package xmw.exa.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.Close;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.XQuery;

public class DB {
    private static final String DB_NAME = "exa";
    private static final String[] MOCK_XML_FILES = {
            "courses.xml",
            "exams.xml",
            "lecturers.xml",
            "lectures.xml",
            "semesters.xml",
    };

    private static int instances = 0;
    private final Context context;

    public DB() {
        if (instances > 0) {
            throw new IllegalStateException("Cannot create more than one instance of DB");
        }
        instances++;

        context = new Context();
        try {
            initializeDatabase();
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage(), e);
        }
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

    public List<String> getLecturerNames() {
        List<String> names = new ArrayList<>();
        // Use collection to access the database contents
        String query = String.format(
                "for $lecturer in collection('%s/lecturers.xml')/Lectureres/Lecturer " +
                        "return concat($lecturer/firstname/text(), ' ', $lecturer/name/text())",
                DB_NAME);

        try {
            String result = new XQuery(query).execute(context);
            for (String name : result.split("\n")) {
                if (!name.trim().isEmpty()) {
                    names.add(name.trim());
                }
            }
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to query lecturers: " + e.getMessage(), e);
        }
        return names;
    }

    public List<Lecturer> getAllLecturers() {
        List<Lecturer> lecturers = new ArrayList<>();
        String query = String.format(
                "for $l in collection('%s/lecturers.xml')/Lectureres/Lecturer " +
                        "return element lecturer { " +
                        "  attribute username { $l/@username }, " +
                        "  element faculty { if ($l/faculty/@null = 'true') then '' else $l/faculty/text() }, " +
                        "  element name { $l/name/text() }, " +
                        "  element firstname { $l/firstname/text() } " +
                        "}",
                DB_NAME);

        try {
            String result = new XQuery(query).execute(context);
            // Parse the XML result into Lecturer objects
            String[] lecturerElements = result.split("</lecturer>");
            for (String element : lecturerElements) {
                if (element.trim().isEmpty())
                    continue;

                Lecturer lecturer = new Lecturer();

                // Extract username from attribute
                String usernamePattern = "username=\"([^\"]*)\"";
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(usernamePattern);
                java.util.regex.Matcher matcher = pattern.matcher(element);
                if (matcher.find()) {
                    lecturer.setUsername(matcher.group(1));
                }

                // Extract other fields
                lecturer.setFaculty(extractValue(element, "faculty"));
                lecturer.setName(extractValue(element, "name"));
                lecturer.setFirstname(extractValue(element, "firstname"));

                lecturers.add(lecturer);
            }
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to query lecturers: " + e.getMessage(), e);
        }
        return lecturers;
    }

    private String extractValue(String xml, String tag) {
        String pattern = String.format("<%s>([^<]*)</%s>", tag, tag);
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(xml);
        return m.find() ? m.group(1).trim() : "";
    }

    public void close() {
        try {
            new Close().execute(context);
        } catch (BaseXException e) {
            // Ignore close errors
        } finally {
            context.close();
        }
    }
}
