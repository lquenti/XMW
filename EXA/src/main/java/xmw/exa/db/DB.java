package xmw.exa.db;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.Close;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.XQuery;

import xmw.exa.db.repository.BaseXmlRepository;
import xmw.exa.models.courses.CourseRepository;
import xmw.exa.models.courses.CourseUtil;
import xmw.exa.models.exams.ExamRepository;
import xmw.exa.models.lectureres.LecturerRepository;
import xmw.exa.models.lectures.LectureRepository;
import xmw.exa.models.semesters.SemesterRepository;
import xmw.exa.util.Config;

public class DB {
    private static DB instance;
    private final Context context;
    private final LecturerRepository lecturerRepository;
    private final CourseRepository courseRepository;
    private final ExamRepository examRepository;
    private final LectureRepository lectureRepository;
    private final SemesterRepository semesterRepository;

    private DB() {
        context = new Context();
        try {
            initializeDatabase();
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage(), e);
        }

        // Initialize repositories
        this.lecturerRepository = new LecturerRepository(context);
        this.courseRepository = new CourseRepository(context);
        this.examRepository = new ExamRepository(context);
        this.lectureRepository = new LectureRepository(context);
        this.semesterRepository = new SemesterRepository(context);
    }

    public static synchronized DB getInstance() {
        if (instance == null) {
            instance = new DB();
        }
        return instance;
    }

    public Context getContext() {
        return context;
    }

    private void initializeDatabase() throws BaseXException {
        try {
            // Close any existing context first
            if (context != null) {
                try {
                    new Close().execute(context);
                } catch (Exception e) {
                    // Ignore close errors
                }
            }

            var xml = Files.readString(new File(Config.FLUSH_FILE_PATH).toPath());
            new CreateDB(BaseXmlRepository.DB_NAME).execute(context);
            var addQuery = CourseUtil.addQuery(xml);
            new XQuery(addQuery).execute(context);
        } catch (IOException e) {
            throw new BaseXException("Failed to initialize database: " + e.getMessage());
        }
    }

    public CourseRepository courses() {
        return courseRepository;
    }

    public LecturerRepository lecturers() {
        return lecturerRepository;
    }

    public ExamRepository exams() {
        return examRepository;
    }

    public LectureRepository lectures() {
        return lectureRepository;
    }

    public SemesterRepository semesters() {
        return semesterRepository;
    }

    public void reinitialize() throws BaseXException {
        try {
            // Close existing context
            if (context != null) {
                try {
                    new Close().execute(context);
                } catch (Exception e) {
                    // Ignore close errors
                }
            }
            initializeDatabase();
        } catch (Exception e) {
            throw new BaseXException("Failed to reinitialize database: " + e.getMessage());
        }
    }

    public void dumpToFile() throws IOException {
        dumpToFile(Config.FLUSH_FILE_PATH);
    }

    public void dumpToFile(String outputPath) throws IOException {
        // Create a query that combines all collections into a single XML document
        String query = "/root";

        String result = new XQuery(query).execute(context);

        // Write the result to the specified file
        java.nio.file.Files.writeString(
                new File(outputPath).toPath(),
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + result,
                StandardCharsets.UTF_8);
    }

    public static void close(Context context) {
        if (context != null) {
            try {
                new Close().execute(context);
            } catch (Exception e) {
                // Ignore close errors
            }
            context.close();
        }
        instance = null; // Allow recreation after close
    }
}
