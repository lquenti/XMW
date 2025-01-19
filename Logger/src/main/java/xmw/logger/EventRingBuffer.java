package xmw.logger;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class EventRingBuffer {
    public static class Event {
        public final String service;
        public final String user;
        public final String type;
        public final String desc;
        public final String timestamp;

        public Event(String service, String user, String type, String desc, String timestamp) {
            this.service = service;
            this.user = user;
            this.type = type;
            this.desc = desc;
            this.timestamp = timestamp;
        }
    }

    private static EventRingBuffer instance;
    private final Event[] buffer;
    private final int size = 30;
    private int start = 0;
    private int end = 0;


    private EventRingBuffer() {
        buffer = new Event[size];
    }

    public static synchronized EventRingBuffer getInstance() {
        if (instance == null) {
            instance = new EventRingBuffer();
        }
        return instance;
    }

    public synchronized void push(Event event) {
        System.out.println(event.desc);
        buffer[end] = event;
        end = (end + 1) % size;
        if (end == start) {  // Buffer full, overwrite oldest
            start = (start + 1) % size;
        }
    }

    public static String convertIsoToFormattedString(String isoTimestamp) {
        Instant instant = Instant.parse(isoTimestamp);
        // assume our time zone
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return dateTime.format(formatter);
    }


    public synchronized String toHTMLTableString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table><tr><th>Service</th><th>User</th><th>Type</th><th>Description</th><th>Timestamp</th></tr>");

        // Check if buffer is not empty
        if (start != end || buffer[start] != null) {
            // Calculate the index of the last valid entry
            // We subtract one from `end` because it points to the next insertion spot
            int currentIndex = (end - 1 + size) % size;
            do {
                Event event = buffer[currentIndex];
                if (event != null) {
                    sb.append("<tr><td>").append(event.service)
                            .append("</td><td>")
                            .append(event.user)
                            .append("</td><td>")
                            .append(event.type)
                            .append("</td><td>")
                            .append(event.desc)
                            .append("</td><td>")
                            .append(convertIsoToFormattedString(event.timestamp))
                            .append("</td></tr>");
                }

                // Move to the previous index, wrapping around if necessary
                currentIndex = (currentIndex - 1 + size) % size;
                // Continue until we circle back to the end
            } while (currentIndex != (end - 1 + size) % size);
        }

        sb.append("</table>");
        return sb.toString();
    }

}