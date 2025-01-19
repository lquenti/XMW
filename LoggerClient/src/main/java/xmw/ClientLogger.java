package xmw;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ClientLogger {
    private final List<Event> eventQueue = Collections.synchronizedList(new LinkedList<>());
    private static final String SERVER_URL = "http://localhost:8080/Logger_war_exploded/log";
    private HttpURLConnection connection;
    private final Object lock = new Object();

    private static final int KEEPALVE_ALEEP_MS = 5000;
    private static final int QUEUE_FLUSH_MS = 5000;

    public static void main(String[] args) throws InterruptedException {
        ClientLogger logger = new ClientLogger();
        logger.run();
        int cnt = 0;
        // Example of adding a real event
        while (true) {
            logger.addEvent(new Event("user", "hbrosen", "UserDeletedEvent", "delete lars.quentin" + cnt));
            Thread.sleep(1000);
            cnt++;
        }
    }

    public void run() {
        // Keepalive thread
        new Thread(() -> {
            while (true) {
                try {
                    sendEvent("<Keepalive />");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Thread.sleep(KEEPALVE_ALEEP_MS);
                } catch (InterruptedException ignored) {
                }
            }
        }).start();

        // Event sender thread
        new Thread(() -> {
            while (true) {
                try {
                    sendPendingEvents();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Thread.sleep(QUEUE_FLUSH_MS);
                } catch (InterruptedException ignored) {
                }
            }
        }).start();
    }

    private void setupConnection() throws IOException {
        URL url = new URL(SERVER_URL);
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/xml");
        connection.setRequestProperty("Accept", "application/xml");
        connection.setChunkedStreamingMode(0); // equivalent to multipart

        OutputStream os = connection.getOutputStream();
        synchronized (lock) {
            String input = "<Events>"; // restart with new root elem
            os.write(input.getBytes(StandardCharsets.UTF_8), 0, input.length());
            os.flush();
        }
    }

    private void sendEvent(String xmlData) throws IOException {
        try {
            if (connection == null) {
                setupConnection();
            }
            OutputStream os = connection.getOutputStream();
            System.out.println("writing..." + xmlData);
            byte[] input = xmlData.getBytes("utf-8");
            synchronized (lock) {
                os.write(input, 0, input.length);
                os.flush();
            }
        } catch (Exception e) {
            // Conn probably dead, restart next event
            System.err.println("Failed to send event: Retrying..." + e.getMessage());
            connection = null;
        }
    }

    private void sendPendingEvents() throws IOException {
        synchronized (eventQueue) {
            for (Event evt : eventQueue) {
                sendEvent(evt.toXML());
            }
            eventQueue.clear();
        }
    }

    public void addEvent(Event event) {
        eventQueue.add(event);
    }
}

