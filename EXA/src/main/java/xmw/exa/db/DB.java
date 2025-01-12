package xmw.exa.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.Close;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.XQuery;

import xmw.exa.db.repository.CourseRepository;
import xmw.exa.db.repository.ExamRepository;
import xmw.exa.db.repository.LectureRepository;
import xmw.exa.db.repository.LecturerRepository;
import xmw.exa.db.repository.SemesterRepository;

public class DB {
    private static final String DB_NAME = "exa";
    private static final String[] MOCK_XML_FILES = {
            "courses.xml",
            "exams.xml",
            "lecturers.xml",
            "lectures.xml",
            "semesters.xml",
    };

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
        // Create a new empty database
        new CreateDB(DB_NAME).execute(context);

        // Load each XML file from resources
        for (String xmlFile : MOCK_XML_FILES) {
            try {
                loadXMLFile(xmlFile);
            } catch (IOException e) {
                throw new BaseXException("Failed to load " + xmlFile + ": " + e.getMessage());
            }
        }
    }

    private void loadXMLFile(String xmlFile) throws IOException, BaseXException {
        String resourcePath = "/mockData/" + xmlFile;
        InputStream is = DB.class.getResourceAsStream(resourcePath);

        if (is == null) {
            // Try without leading slash if first attempt fails
            resourcePath = "mockData/" + xmlFile;
            is = DB.class.getResourceAsStream(resourcePath);
        }

        if (is == null) {
            throw new IOException("Could not find resource: " + xmlFile + " (tried with and without leading slash)");
        }

        try (InputStream input = is) {
            // Read the XML content
            String xml = new String(input.readAllBytes(), StandardCharsets.UTF_8);

            // Use XQuery to add the document to the database
            String addQuery = String.format("db:add('%s', '%s', '%s')",
                    DB_NAME,
                    xml.replace("'", "''"), // Escape single quotes in XML
                    xmlFile);
            new XQuery(addQuery).execute(context);
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
        DB.close(context);
        initializeDatabase();
    }

    // Alias methods that delegate to repositories
    @Deprecated
    public List<Lecturer> getAllLecturers() {
        return lecturerRepository.all();
    }

    @Deprecated
    public List<Course> getAllCourses() {
        return courseRepository.all();
    }

    @Deprecated
    public List<Exam> getAllExams() {
        return examRepository.all();
    }

    @Deprecated
    public List<Semester> getAllSemesters() {
        return semesterRepository.all();
    }

    @Deprecated
    public List<Lecture> getAllLectures() {
        return lectureRepository.all();
    }

    public static void close(Context context) {
        try {
            new Close().execute(context);
        } catch (BaseXException e) {
            // Ignore close errors
        } finally {
            context.close();
            instance = null; // Allow recreation after close
        }
    }
}
