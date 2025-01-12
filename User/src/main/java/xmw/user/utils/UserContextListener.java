package xmw.user.utils;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.basex.query.QueryException;
import xmw.user.db.UserDB;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

@WebListener
public class UserContextListener implements ServletContextListener {
    private static final String USER_HOME = System.getProperty("user.home");
    public static final String BASE_FOLDER = USER_HOME + "/xmw_data";
    public static final String USER_FOLDER = BASE_FOLDER + "/user";
    public static final String USER_PATH = USER_FOLDER + "/user.xml";

    private static final String USER_DB = """
            <Users>
              <User username="hbrosen">
                <name>Brosenne</name>
                <firstname>Hendrik</firstname>
                <password>hunter2</password>
                <faculty>Computer Science</faculty>
                <group id="g_lecturer">Lecturer</group>
                <group id="g_employee">Employee</group>
              </User>
              <User username="wmay">
                <name>May</name>
                <firstname>Wolfgang</firstname>
                <password>m0nd14l</password>
                <faculty>Computer Science</faculty>
                <group id="g_lecturer">Lecturer</group>
                <group id="g_employee">Employee</group>
                <group id="g_professor">Professor</group>
              </User>
              <User username="lars.quentin">
                <name>Quentin</name>
                <firstname>Lars</firstname>
                <password>bobbydocuments</password>
                <faculty>Computer Science</faculty>
                <group id="g_student">Student</group>
              </User>
              <User username="frederik.hennecke">
                <name>Hennecke</name>
                <firstname>Frederik</firstname>
                <password>1234</password>
                <faculty>Computer Science</faculty>
                <group id="g_student">Student</group>
              </User>
              <User username="v.mattfeld">
                <name>Mattfeld</name>
                <firstname>Valerius Albert Gongjus</firstname>
                <password>hunter3</password>
                <faculty>Computer Science</faculty>
                <group id="g_student">Student</group>
              </User>
            </Users>
            """;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        createDirectoryIfNotExists(BASE_FOLDER);
        createDirectoryIfNotExists(USER_FOLDER);
        createDbFileIfNotExists(USER_PATH, USER_DB);
        loadDatabase();
    }
    private void createDirectoryIfNotExists(String directoryPath) {
        Path path = Paths.get(directoryPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                System.err.println("Failed to create directory: " + path.toAbsolutePath());
                e.printStackTrace();
            }
        } else {
            System.out.println("Directory already exists: " + path.toAbsolutePath());
        }
    }
    private void createDbFileIfNotExists(String filePath, String initialData) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            try {
                Files.writeString(path, initialData, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadDatabase() {
        UserDB.init();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    UserDB.flushToDisk();
                    System.out.println("DB saved");
                } catch (QueryException e) {
                    e.printStackTrace();
                    // better to just go on... maybe it works next time
                }
            }
        };
        long period = 1000 * 10 * 60; // every 10 min
        //period = 1000 * 5;
        // Start after `period` time
        timer.scheduleAtFixedRate(task, period, period);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            UserDB.flushToDisk();
        } catch (QueryException e) {
            e.printStackTrace();
            // better to just go on... maybe it works next time
        }
    }
}