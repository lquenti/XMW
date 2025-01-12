package xmw.exa.db;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.Close;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.XQuery;

import xmw.exa.models.courses.CourseRepository;
import xmw.exa.models.exams.ExamRepository;
import xmw.exa.models.lectureres.LecturerRepository;
import xmw.exa.models.lectures.LectureRepository;
import xmw.exa.models.semesters.SemesterRepository;
import xmw.exa.util.Config;

public class DB {
    private static final String[] MOCK_XML_FILES = {
            "courses.xml",
            "exams.xml",
            "lecturers.xml",
            "lectures.xml",
            "semesters.xml",
    };

    private static DB instance;
    private final Context context;
    public static final String DB_NAME = "exa";
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
        this.lecturerRepository = new LecturerRepository(context, DB_NAME);
        this.courseRepository = new CourseRepository(context, DB_NAME);
        this.examRepository = new ExamRepository(context, DB_NAME);
        this.lectureRepository = new LectureRepository(context, DB_NAME);
        this.semesterRepository = new SemesterRepository(context, DB_NAME);
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
            ensureDataDirectoryExists();
            List<String> xmlFilesToLoad = determineXmlFilesToLoad();
            createEmptyDatabase();
            loadXmlFiles(xmlFilesToLoad);
        } catch (IOException e) {
            throw new BaseXException("Failed to initialize database: " + e.getMessage());
        }
    }

    private void ensureDataDirectoryExists() throws IOException {
        File dataDir = new File(Config.XMW_DATA_PATH);
        if (!dataDir.exists() && !dataDir.mkdirs()) {
            throw new IOException("Could not create directory: " + Config.XMW_DATA_PATH);
        }
    }

    private List<String> determineXmlFilesToLoad() {
        File flushFile = new File(Config.FLUSH_FILE_PATH);
        if (flushFile.exists()) {
            return List.of(flushFile.getAbsolutePath());
        }
        return Arrays.asList(MOCK_XML_FILES);
    }

    private void createEmptyDatabase() throws BaseXException {
        new CreateDB(DB_NAME).execute(context);
    }

    private void loadXmlFiles(List<String> xmlFiles) throws BaseXException {
        for (String xmlFile : xmlFiles) {
            try {
                loadXMLFile(xmlFile);
            } catch (IOException e) {
                throw new BaseXException("Failed to load " + xmlFile + ": " + e.getMessage());
            }
        }
    }

    private void loadXMLFile(String xmlFile) throws IOException, BaseXException {
        String xml;
        if (Arrays.asList(MOCK_XML_FILES).contains(xmlFile)) {
            // Handle as resource file
            xml = loadFromResource(xmlFile);
        } else {
            // Handle as direct file path (for flush file)
            xml = new String(java.nio.file.Files.readAllBytes(new File(xmlFile).toPath()), StandardCharsets.UTF_8);
        }

        // Use XQuery to add the document to the database
        String addQuery = String.format("db:add('%s', '%s', '%s')",
                DB_NAME,
                xml.replace("'", "''"), // Escape single quotes in XML
                new File(xmlFile).getName()); // Use just the filename part
        new XQuery(addQuery).execute(context);
    }

    private String loadFromResource(String xmlFile) throws IOException {
        String resourcePath = "mockData/" + xmlFile;
        InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);

        if (is == null) {
            throw new IOException("Could not find resource: " + xmlFile + " in mockData/");
        }

        try (InputStream input = is) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
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

    public void dumpToFile(String outputPath) throws BaseXException, IOException {
        dumpToFile(outputPath, false);
    }

    public void dumpToFlushFile() throws BaseXException, IOException {
        dumpToFile(Config.FLUSH_FILE_PATH, true);
    }

    private void dumpToFile(String outputPath, boolean isFlushFormat) throws BaseXException, IOException {
        // Create a query that combines all collections
        String query;
        if (isFlushFormat) {
            query = String.format(
                    "let $courses := collection('%1$s/courses.xml')/Courses/Course\n" +
                            "let $exams := collection('%1$s/exams.xml')/Exams/Exam\n" +
                            "let $lecturers := collection('%1$s/lecturers.xml')/Lectureres/Lecturer\n" +
                            "let $lectures := collection('%1$s/lectures.xml')/Lectures/Lecture\n" +
                            "let $semesters := collection('%1$s/semesters.xml')/Semesters/Semester\n" +
                            "return\n" +
                            "<root>\n" +
                            "  <Courses>{$courses}</Courses>\n" +
                            "  <Exams>{$exams}</Exams>\n" +
                            "  <Lectureres>{$lecturers}</Lectureres>\n" +
                            "  <Lectures>{$lectures}</Lectures>\n" +
                            "  <Semesters>{$semesters}</Semesters>\n" +
                            "</root>",
                    DB_NAME);
        } else {
            query = String.format(
                    "let $collections := (\n" +
                            "  collection('%1$s/courses.xml')/Courses,\n" +
                            "  collection('%1$s/exams.xml')/Exams,\n" +
                            "  collection('%1$s/lecturers.xml')/Lectureres,\n" +
                            "  collection('%1$s/lectures.xml')/Lectures,\n" +
                            "  collection('%1$s/semesters.xml')/Semesters\n" +
                            ")\n" +
                            "return\n" +
                            "<database>\n" +
                            "{$collections}\n" +
                            "</database>",
                    DB_NAME);
        }

        String result = new XQuery(query).execute(context);

        // Write the result to the specified file
        java.nio.file.Files.writeString(
                new File(outputPath).toPath(),
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + result,
                StandardCharsets.UTF_8);
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
