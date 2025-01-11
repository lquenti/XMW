package xmw.exa.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
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

    private static DB instance;
    private final Context context;

    private DB() {
        context = new Context();
        try {
            initializeDatabase();
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage(), e);
        }
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

    public List<Lecturer> getAllLecturers() {
        List<Lecturer> lecturers = new ArrayList<>();
        String query = String.format(
                "for $l in collection('%s/lecturers.xml')/Lectureres/Lecturer " +
                        "return element lecturer { " +
                        "  attribute username { $l/@username }, " +
                        "  element id { $l/id/text() }, " +
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
                lecturer.setId(Integer.parseInt(extractValue(element, "id")));
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

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String query = String.format(
                "for $c in collection('%s/courses.xml')/Course/Course " +
                        "return element course { " +
                        "  element id { $c/id/text() }, " +
                        "  element name { $c/name/text() }, " +
                        "  element faculty { $c/faculty/text() }, " +
                        "  element lecturerId { $c/lecturer_id/text() }, " +
                        "  element maxStudents { $c/max_students/text() }, " +
                        "  element semesterId { $c/semester_id/text() } " +
                        "}",
                DB_NAME);

        try {
            String result = new XQuery(query).execute(context);
            String[] courseElements = result.split("</course>");
            for (String element : courseElements) {
                if (element.trim().isEmpty())
                    continue;

                Course course = new Course();

                // Extract fields
                course.setId(Integer.parseInt(extractValue(element, "id")));
                course.setName(extractValue(element, "name"));
                course.setFaculty(extractValue(element, "faculty"));
                course.setLecturerId(Integer.parseInt(extractValue(element, "lecturerId")));
                course.setMaxStudents(Integer.parseInt(extractValue(element, "maxStudents")));
                course.setSemesterId(Integer.parseInt(extractValue(element, "semesterId")));

                courses.add(course);
            }
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to query courses: " + e.getMessage(), e);
        }
        return courses;
    }

    public List<Exam> getAllExams() {
        List<Exam> exams = new ArrayList<>();
        String query = String.format(
                "for $e in collection('%s/exams.xml')/Exams/Exam " +
                        "return element exam { " +
                        "  element id { $e/id/text() }, " +
                        "  element courseId { $e/course_id/text() }, " +
                        "  element date { $e/date/text() }, " +
                        "  element isOnline { $e/is_online/text() }, " +
                        "  element isWritten { $e/is_written/text() }, " +
                        "  element roomOrLink { $e/room_or_link/text() } "
                        +
                        "}",
                DB_NAME);

        try {
            String result = new XQuery(query).execute(context);
            String[] examElements = result.split("</exam>");
            for (String element : examElements) {
                if (element.trim().isEmpty())
                    continue;

                Exam exam = new Exam();

                // Extract id from attribute
                String idPattern = "id=\"([^\"]*)\"";

                exam.setId(Integer.parseInt(extractValue(element, "id")));

                // Extract other fields
                exam.setCourseId(Integer.parseInt(extractValue(element, "courseId")));
                exam.setDate(LocalDateTime.parse(extractValue(element, "date")));
                exam.setOnline(Boolean.parseBoolean(extractValue(element, "isOnline")));
                exam.setWritten(Boolean.parseBoolean(extractValue(element, "isWritten")));
                exam.setRoomOrLink(extractValue(element, "roomOrLink"));

                exams.add(exam);
            }
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to query exams: " + e.getMessage(), e);
        }
        return exams;
    }

    public List<Semester> getAllSemesters() {
        List<Semester> semesters = new ArrayList<>();
        String query = String.format(
                "for $s in collection('%s/semesters.xml')/Semesters/Semester " +
                        "return element semester { " +
                        "  element id { $s/id/text() }, " +
                        "  element name { $s/name/text() }, " +
                        "  element start { $s/start/text() }, " +
                        "  element end { $s/end/text() } " +
                        "}",
                DB_NAME);

        try {
            String result = new XQuery(query).execute(context);
            String[] semesterElements = result.split("</semester>");
            for (String element : semesterElements) {
                if (element.trim().isEmpty())
                    continue;

                Semester semester = new Semester();

                // Extract fields
                semester.setId(Integer.parseInt(extractValue(element, "id")));
                semester.setName(extractValue(element, "name"));
                semester.setStart(LocalDateTime.parse(extractValue(element, "start")));
                semester.setEnd(LocalDateTime.parse(extractValue(element, "end")));

                semesters.add(semester);
            }
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to query semesters: " + e.getMessage(), e);
        }
        return semesters;
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            // First try direct parsing
            return LocalDateTime.parse(dateTimeStr);
        } catch (DateTimeParseException e) {
            try {
                // Remove any 'T' that appears in the time portion (after the first T)
                String[] parts = dateTimeStr.split("T", 2);
                if (parts.length == 2) {
                    String datePart = parts[0];
                    // Handle cases where time part has extra T's and leading/trailing zeros
                    String timePart = parts[1]
                            .replace("T", "") // Remove extra T's
                            .replaceFirst("^(\\d)T:", "$1:") // Remove T between hour and colon
                            .replaceFirst("^0(\\d):", "$1:") // Remove leading zero from hour
                            .replaceFirst("^(\\d):", "0$1:") // Add leading zero if hour is single digit
                            .trim();
                    String formattedDateTime = datePart + "T" + timePart;
                    System.out.println("Parsing datetime: " + dateTimeStr + " -> " + formattedDateTime); // Debug print
                    return LocalDateTime.parse(formattedDateTime);
                }
                return LocalDateTime.parse(dateTimeStr);
            } catch (DateTimeParseException e2) {
                System.err.println("Failed to parse datetime: " + dateTimeStr + " - " + e2.getMessage());
                throw e2;
            }
        }
    }

    public List<Lecture> getAllLectures() {
        List<Lecture> lectures = new ArrayList<>();
        String query = String.format(
                "for $l in collection('%s/lectures.xml')/Lectures/Lecture " +
                        "return element lecture { " +
                        "  element id { $l/id/text() }, " +
                        "  element courseId { $l/course_id/text() }, " +
                        "  element start { $l/start/text() }, " +
                        "  element end { $l/end/text() }, " +
                        "  element roomOrLink { $l/room_or_link/text() } " +
                        "}",
                DB_NAME);

        try {
            String result = new XQuery(query).execute(context);
            System.out.println("Raw XML result: " + result); // Debug print

            // Split on complete lecture tag to avoid partial matches
            String[] lectureElements = result.split("</lecture>\\s*(?=<lecture>|$)");
            System.out.println("Found " + lectureElements.length + " lecture elements"); // Debug print

            for (String element : lectureElements) {
                if (element.trim().isEmpty()) {
                    continue;
                }

                try {
                    Lecture lecture = new Lecture();
                    String id = extractValue(element, "id");
                    String courseId = extractValue(element, "courseId");
                    String start = extractValue(element, "start");
                    String end = extractValue(element, "end");
                    String roomOrLink = extractValue(element, "roomOrLink");

                    System.out.println("Processing lecture - ID: " + id + ", CourseID: " + courseId); // Debug print

                    lecture.setId(Integer.parseInt(id));
                    lecture.setCourseId(Integer.parseInt(courseId));
                    try {
                        lecture.setStart(parseDateTime(start));
                        lecture.setEnd(parseDateTime(end));
                    } catch (DateTimeParseException e) {
                        System.err.println("Warning: Failed to parse date for lecture " + id +
                                " - Start: " + start + ", End: " + end +
                                " - Error: " + e.getMessage());
                        continue;
                    }
                    lecture.setRoomOrLink(roomOrLink);
                    lectures.add(lecture);
                } catch (Exception e) {
                    System.err.println("Error processing lecture element: " + element);
                    e.printStackTrace();
                }
            }
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to query lectures: " + e.getMessage(), e);
        }

        System.out.println("Total lectures loaded: " + lectures.size()); // Debug print
        return lectures;
    }

    public void close() {
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
