package xmw.exa.models.courses;

import java.util.ArrayList;
import java.util.List;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.XQuery;

import xmw.exa.db.repository.BaseXmlRepository;

public class CourseRepository extends BaseXmlRepository<Course> {

    public CourseRepository(Context context) {
        super(context);
    }

    @Override
    public List<Course> all() {
        List<Course> courses = new ArrayList<>();
        String query = String.format(
                "for $c in collection('%s/courses.xml')/Courses/Course " +
                        "return element course { " +
                        "  attribute id { $c/id/text() }, " +
                        "  attribute semester_id { $c/semester_id/text() }, " +
                        "  element faculty { $c/faculty/text() }, " +
                        "  element lecturer { attribute id { $c/lecturer_id/text() } }, " +
                        "  element max_students { $c/max_students/text() }, " +
                        "  element name { $c/name/text() } " +
                        "}",
                DB_NAME);

        try {
            String result = new XQuery(query).execute(context);
            String[] courseElements = result.split("</course>");

            for (String element : courseElements) {
                if (element.trim().isEmpty())
                    continue;
                Course course = parseCourseElement(element);
                if (course != null) {
                    courses.add(course);
                }
            }
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to query courses: " + e.getMessage(), e);
        }
        return courses;
    }

    @Override
    public Course getById(long id) {
        String query = String.format(
                "for $c in collection('%s/courses.xml')/Courses/Course[id = %d] " +
                        "return element course { " +
                        "  attribute id { $c/id/text() }, " +
                        "  attribute semester_id { $c/semester_id/text() }, " +
                        "  element faculty { $c/faculty/text() }, " +
                        "  element lecturer { attribute id { $c/lecturer_id/text() } }, " +
                        "  element max_students { $c/max_students/text() }, " +
                        "  element name { $c/name/text() } " +
                        "}",
                DB_NAME, id);

        try {
            String result = new XQuery(query).execute(context);
            if (result.trim().isEmpty()) {
                return null;
            }
            return parseCourseElement(result);
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to query course: " + e.getMessage(), e);
        }
    }

    private Course parseCourseElement(String element) {
        try {
            Course course = new Course();

            // Extract attributes
            String idPattern = "id=\"([^\"]*)\"";
            String semesterIdPattern = "semester_id=\"([^\"]*)\"";
            java.util.regex.Pattern idRegex = java.util.regex.Pattern.compile(idPattern);
            java.util.regex.Pattern semesterIdRegex = java.util.regex.Pattern.compile(semesterIdPattern);

            java.util.regex.Matcher idMatcher = idRegex.matcher(element);
            java.util.regex.Matcher semesterIdMatcher = semesterIdRegex.matcher(element);

            if (idMatcher.find()) {
                course.setId(Integer.parseInt(idMatcher.group(1)));
            }
            if (semesterIdMatcher.find()) {
                course.setSemesterId(Integer.parseInt(semesterIdMatcher.group(1)));
            }

            // Extract lecturer ID from lecturer element's id attribute
            String lecturerIdPattern = "<lecturer[^>]*id=\"([^\"]*)\"";
            java.util.regex.Pattern lecturerIdRegex = java.util.regex.Pattern.compile(lecturerIdPattern);
            java.util.regex.Matcher lecturerIdMatcher = lecturerIdRegex.matcher(element);
            if (lecturerIdMatcher.find()) {
                course.setLecturerId(Integer.parseInt(lecturerIdMatcher.group(1)));
            }

            // Extract other fields
            course.setName(extractValue(element, "name"));
            course.setFaculty(extractValue(element, "faculty"));
            course.setMaxStudents(Integer.parseInt(extractValue(element, "max_students")));
            return course;
        } catch (Exception e) {
            System.err.println("Error processing course element: " + element);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean create(Course data) {
        try {
            // Find the highest existing ID
            String maxIdQuery = String.format(
                    "let $maxId := max(collection('%s/courses.xml')/Courses/Course/id/text()) " +
                            "return if ($maxId) then $maxId else 0",
                    DB_NAME);
            String maxIdResult = new XQuery(maxIdQuery).execute(context);
            int nextId = Integer.parseInt(maxIdResult.trim()) + 1;

            // Set the new ID
            data.setId(nextId);

            // Create XML representation of the course
            String courseXml = String.format(
                    "<Course>" +
                            "  <faculty>%s</faculty>" +
                            "  <id>%d</id>" +
                            "  <lecturer id=\"%d\"/>" +
                            "  <max_students>%d</max_students>" +
                            "  <name>%s</name>" +
                            "  <semester_id>%d</semester_id>" +
                            "</Course>",
                    data.getFaculty(),
                    data.getId(),
                    data.getLecturerId(),
                    data.getMaxStudents(),
                    data.getName(),
                    data.getSemesterId());

            // Add the new course to the existing courses
            String query = String.format(
                    "let $courses := collection('%s/courses.xml')/Courses " +
                            "return insert node %s as last into $courses",
                    DB_NAME,
                    courseXml);

            new XQuery(query).execute(context);
            return true;
        } catch (BaseXException e) {
            System.err.println("Failed to create course: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}