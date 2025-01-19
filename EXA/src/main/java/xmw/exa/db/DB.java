package xmw.exa.db;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.Close;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.XQuery;

import xmw.exa.db.repository.BaseXmlRepository;
import xmw.exa.models.courses.CourseRepository;
import xmw.exa.models.courses.CourseUtil;
import xmw.exa.models.exams.ExamRepository;
import xmw.exa.models.lecturers.LecturerRepository;
import xmw.exa.models.lectures.LectureRepository;
import xmw.exa.models.modules.ModuleRepository;
import xmw.exa.models.semesters.SemesterRepository;
import xmw.exa.util.Config;
import xmw.flush.*;


public class DB {
    private static DB instance;
    private final Context context;
    private final LecturerRepository lecturerRepository;
    private final CourseRepository courseRepository;
    private final ExamRepository examRepository;
    private final LectureRepository lectureRepository;
    private final SemesterRepository semesterRepository;
    private final ModuleRepository moduleRepository;
    public static final String XML_NAMESPACE = "http://www.w3.org/namespace/";

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
        this.moduleRepository = new ModuleRepository(context);
    }

    public static synchronized DB getInstance() {
        if (instance == null) {
            instance = new DB();
        }
        return instance;
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
            // Add namespace declaration to the XQuery
            var addQuery = "declare namespace t = '" + XML_NAMESPACE + "'; " + CourseUtil.addQuery(xml);
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

    public ModuleRepository modules() {
        return moduleRepository;
    }

    public void dumpToFile() throws IOException {
        dumpToFile(Config.FLUSH_FILE_PATH);
    }

    public void dumpToFile(String outputPath) throws IOException {
        // Create a query that combines all collections into a single XML document with
        // namespace
        String query = "declare namespace t = '" + XML_NAMESPACE + "'; /root";

        String result = new XQuery(query).execute(context);

        // Write the result to the specified file
        java.nio.file.Files.writeString(
                new File(outputPath).toPath(),
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + result,
                StandardCharsets.UTF_8);
    }

    public static Map<ExaElement, Object> getRootChildMap(Context context) throws BaseXException {
        final String query = "/root";
        String xmlResult = new XQuery(query).execute(context);
        Map<ExaElement, Object> map = new EnumMap<>(ExaElement.class);
        Root root = DB.unmarshal(xmlResult);
        var elements = root.getCoursesOrExamsOrLecturers();
        for (int i = 0; i < elements.size(); i++) {
            switch (i) {
                case 0:
                    map.put(ExaElement.COURSES, elements.get(i));
                    break;
                case 1:
                    map.put(ExaElement.EXAMS, elements.get(i));
                    break;
                case 2:
                    map.put(ExaElement.LECTURERS, elements.get(i));
                    break;
                case 3:
                    map.put(ExaElement.LECTURES, elements.get(i));
                    break;
                case 4:
                    map.put(ExaElement.SEMESTERS, elements.get(i));
                    break;
                case 5:
                    map.put(ExaElement.MODULES, elements.get(i));
                default:
            }
        }
        return map;
    }

    public static Root unmarshal(String xml) {
        return (Root) unmarshal(xml, Root.class);
    }

    public static Object unmarshal(String xml, Class<?> clazz) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            // Add namespace wrapper if not already present
            if (!xml.contains(XML_NAMESPACE)) {
                xml = xml.replaceFirst("<(\\w+)>", "<$1 xmlns=\"" + XML_NAMESPACE + "\">");
            }

            // Create XMLStreamReader with namespace awareness
            XMLInputFactory xif = XMLInputFactory.newInstance();
            XMLStreamReader xsr = xif.createXMLStreamReader(new StringReader(xml));

            return unmarshaller.unmarshal(xsr);
        } catch (JAXBException | XMLStreamException e) {
            throw new RuntimeException("Failed to unmarshal XML: " + e.getMessage(), e);
        }
    }

    public static String marshal(Object object) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Root.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter writer = new StringWriter();
            marshaller.marshal(object, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to marshal object: " + e.getMessage(), e);
        }
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
