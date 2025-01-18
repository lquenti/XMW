package xmw.studip;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.basex.core.BaseXException;
import org.basex.query.QueryException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

@WebListener
public class AppContextListener implements ServletContextListener {
    private static final String USER_HOME = System.getProperty("user.home");
    public static final String BASE_FOLDER = USER_HOME + "/xmw_data";
    public static final String USER_FOLDER = BASE_FOLDER + "/stud";
    public static final String USER_PATH = USER_FOLDER + "/stud.xml";

    public static final String USER_URL = "http://localhost:8080/user/";
    public static final String EXA_URL = "http://localhost:8080/exa/";

    // Define the XML content
    private static final String EXAMPLE_XML =
            """   
                 <StudIP>
                    <Schedules>
                        <Schedule username="hbrosen">
                            <Course id="course-1" semester="ws2425"/>
                        </Schedule>
                    </Schedules>
                    <Exams>
                        <Registration username="hbrosen">
                            <Exam id="exam-2">
                            </Exam>
                        </Registration>
                    </Exams>
                    <Grades>
                        <Grade username="hbrosen">
                            <Exam id="exam-1">1.0</Exam>
                        </Grade>
                    </Grades>
                 </StudIP>
                   \s""";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Initialize the XMLDatabase object and store it in the context

        createDirectoryIfNotExists(BASE_FOLDER);
        createDirectoryIfNotExists(USER_FOLDER);
        createDbFileIfNotExists(USER_PATH, EXAMPLE_XML);

        XMLDatabase xmlDatabase = XMLDatabase.getInstance();

        sce.getServletContext().setAttribute("xmlDatabase", xmlDatabase);

        loadDatabase();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Clean up resources if necessary
        XMLDatabase xmlDatabase = (XMLDatabase) sce.getServletContext().getAttribute("xmlDatabase");
        if (xmlDatabase != null) {
            try {
                XMLDatabase.flushToDisk();
            } catch (QueryException | BaseXException e) {
                throw new RuntimeException(e);
            }
            xmlDatabase.close(); // Hypothetical cleanup method
        }
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
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    XMLDatabase.flushToDisk();
                    System.out.println("DB saved");
                } catch (QueryException e) {
                    e.printStackTrace();
                    // better to just go on... maybe it works next time
                } catch (BaseXException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        long period = 1000 * 60; // every 1 min
        // Start after `period` time
        timer.scheduleAtFixedRate(task, period, period);
    }
}
