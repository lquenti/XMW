package xmw.exa.models.courses;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.XQuery;

import xmw.exa.db.DB;
import xmw.exa.db.repository.BaseXmlRepository;

public class CourseRepository extends BaseXmlRepository<Course> {

    public CourseRepository(Context context) {
        super(context);
    }

    @Override
    public List<Course> all() {
        List<Course> courses = new ArrayList<>();
        String query = "for $c in /root/Courses/Course " +
                "return element course { " +
                "  attribute id { $c/id/text() }, " +
                "  attribute semester_id { $c/semester_id/text() }, " +
                "  element faculty { $c/faculty/text() }, " +
                "  element lecturer { attribute id { $c/lecturer_id/text() } }, " +
                "  element max_students { $c/max_students/text() }, " +
                "  element name { $c/name/text() } " +
                "}";

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
    public Course get(long id) {
        var all = this.all();
        for (var course : all) {
            if (course.getId() == id) {
                return course;
            }
        }

        return null;
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
                String lecturerId = lecturerIdMatcher.group(1);
                if (!lecturerId.isEmpty()) {
                    course.setLecturerId(Integer.parseInt(lecturerId));
                }
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
            String maxIdQuery = "let $maxId := max(/root/Courses/Course/id/text()) " +
                    "return if ($maxId) then $maxId else 0";
            String maxIdResult = new XQuery(maxIdQuery).execute(context);
            int nextId = Integer.parseInt(maxIdResult.trim()) + 1;

            // if data has id, use it
            if (data.getId() > 0) {
                // ensure the id does not exist olready
                List<Long> allIds = this.all().stream().map(
                        Course::getId
                ).toList();

                if (allIds.contains(data.getId())) {
                    data.setId(nextId);
                }
            } else {
                // Set the new ID
                data.setId(nextId);
            }

            // Create XML representation of the course
            String courseXml = String.format(
                    "<Course>" +
                            "  <faculty>%s</faculty>" +
                            "  <id>%d</id>" +
                            "  <lecturer_id>%d</lecturer_id>" +
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
                    "let $courses := /root/Courses " +
                            "return insert node %s as last into $courses",
                    courseXml);

            new XQuery(query).execute(context);

            try {
                DB.getInstance().dumpToFile();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                return false;
            }
            return true;
        } catch (BaseXException e) {
            System.err.println("Failed to create course: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Course update(Course data) {
        if (!this.all().stream().map(Course::getId).toList().contains(data.getId())) {
            return null;
        }
        delete(data.getId());
        if (create(data)) {
            return this.get(data.getId());
        }
        return null;
    }

    @Override
    public void delete(long id) {
        var res = this.all().stream().map(Course::getId).filter(i -> i == id).toList().size();
        if (res != 1) {
            return;
        }
        try {
            String query = String.format(
                    "delete node /root/Courses/Course[id = %d]",
                    id);
            new XQuery(query).execute(context);
            try {
                DB.getInstance().dumpToFile();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        } catch (BaseXException e) {
            System.err.println("Failed to delete course: " + e.getMessage());
            e.printStackTrace();
        }
    }
}