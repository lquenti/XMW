package xmw.exa.db;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
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
    private static final String XML_NAMESPACE = "http://www.w3.org/namespace/";

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

    public static Object unmarshal(String xml, Class<?> clazz) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            // Create XMLStreamReader with namespace awareness
            XMLInputFactory xif = XMLInputFactory.newInstance();
            XMLStreamReader xsr = xif.createXMLStreamReader(new StringReader(xml));

            // Create a namespace aware reader that adds the required namespace
            XMLStreamReader nsReader = new NamespaceAwareXMLStreamReader(xsr, XML_NAMESPACE);

            return unmarshaller.unmarshal(nsReader);
        } catch (JAXBException | XMLStreamException e) {
            throw new RuntimeException("Failed to unmarshal XML: " + e.getMessage(), e);
        }
    }

    public static String marshal(Object object) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
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

// Helper class to add namespace to XML stream
class NamespaceAwareXMLStreamReader implements XMLStreamReader {
    private final XMLStreamReader reader;
    private final String namespace;

    public NamespaceAwareXMLStreamReader(XMLStreamReader reader, String namespace) {
        this.reader = reader;
        this.namespace = namespace;
    }

    @Override
    public String getNamespaceURI() {
        String uri = reader.getNamespaceURI();
        return uri == null || uri.isEmpty() ? namespace : uri;
    }

    // Delegate all other methods to the wrapped reader
    @Override
    public Object getProperty(String name) {
        return reader.getProperty(name);
    }

    @Override
    public int next() throws XMLStreamException {
        return reader.next();
    }

    @Override
    public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        reader.require(type, namespaceURI, localName);
    }

    @Override
    public String getElementText() throws XMLStreamException {
        return reader.getElementText();
    }

    @Override
    public int nextTag() throws XMLStreamException {
        return reader.nextTag();
    }

    @Override
    public boolean hasNext() throws XMLStreamException {
        return reader.hasNext();
    }

    @Override
    public void close() throws XMLStreamException {
        reader.close();
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return reader.getNamespaceURI(prefix);
    }

    @Override
    public boolean isStartElement() {
        return reader.isStartElement();
    }

    @Override
    public boolean isEndElement() {
        return reader.isEndElement();
    }

    @Override
    public boolean isCharacters() {
        return reader.isCharacters();
    }

    @Override
    public boolean isWhiteSpace() {
        return reader.isWhiteSpace();
    }

    @Override
    public String getAttributeValue(String namespaceURI, String localName) {
        return reader.getAttributeValue(namespaceURI, localName);
    }

    @Override
    public int getAttributeCount() {
        return reader.getAttributeCount();
    }

    @Override
    public QName getAttributeName(int index) {
        return reader.getAttributeName(index);
    }

    @Override
    public String getAttributeNamespace(int index) {
        return reader.getAttributeNamespace(index);
    }

    @Override
    public String getAttributeLocalName(int index) {
        return reader.getAttributeLocalName(index);
    }

    @Override
    public String getAttributePrefix(int index) {
        return reader.getAttributePrefix(index);
    }

    @Override
    public String getAttributeType(int index) {
        return reader.getAttributeType(index);
    }

    @Override
    public String getAttributeValue(int index) {
        return reader.getAttributeValue(index);
    }

    @Override
    public boolean isAttributeSpecified(int index) {
        return reader.isAttributeSpecified(index);
    }

    @Override
    public int getNamespaceCount() {
        return reader.getNamespaceCount();
    }

    @Override
    public String getNamespacePrefix(int index) {
        return reader.getNamespacePrefix(index);
    }

    @Override
    public String getNamespaceURI(int index) {
        return reader.getNamespaceURI(index);
    }

    @Override
    public int getEventType() {
        return reader.getEventType();
    }

    @Override
    public String getText() {
        return reader.getText();
    }

    @Override
    public char[] getTextCharacters() {
        return reader.getTextCharacters();
    }

    @Override
    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length)
            throws XMLStreamException {
        return reader.getTextCharacters(sourceStart, target, targetStart, length);
    }

    @Override
    public int getTextStart() {
        return reader.getTextStart();
    }

    @Override
    public int getTextLength() {
        return reader.getTextLength();
    }

    @Override
    public String getEncoding() {
        return reader.getEncoding();
    }

    @Override
    public boolean hasText() {
        return reader.hasText();
    }

    @Override
    public Location getLocation() {
        return reader.getLocation();
    }

    @Override
    public String getLocalName() {
        return reader.getLocalName();
    }

    @Override
    public boolean hasName() {
        return reader.hasName();
    }

    @Override
    public String getPrefix() {
        return reader.getPrefix();
    }

    @Override
    public String getVersion() {
        return reader.getVersion();
    }

    @Override
    public boolean isStandalone() {
        return reader.isStandalone();
    }

    @Override
    public boolean standaloneSet() {
        return reader.standaloneSet();
    }

    @Override
    public String getCharacterEncodingScheme() {
        return reader.getCharacterEncodingScheme();
    }

    @Override
    public String getPITarget() {
        return reader.getPITarget();
    }

    @Override
    public String getPIData() {
        return reader.getPIData();
    }

    @Override
    public javax.xml.namespace.NamespaceContext getNamespaceContext() {
        return reader.getNamespaceContext();
    }

    @Override
    public javax.xml.namespace.QName getName() {
        return reader.getName();
    }
}
