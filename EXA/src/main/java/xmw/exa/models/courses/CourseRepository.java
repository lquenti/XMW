package xmw.exa.models.courses;

import java.util.List;

import org.basex.core.Context;
import org.basex.core.cmd.XQuery;

import xmw.exa.db.repository.Repository;

public class CourseRepository implements Repository<Course> {
    private final Context context;
    private final String dbName;

    public CourseRepository(Context context, String dbName) {
        this.context = context;
        this.dbName = dbName;
        // Initialize the collection if it doesn't exist
        try {
            String initQuery = String.format(
                    "if (not(db:exists('%s'))) then " +
                            "  db:create('%s') " +
                            "else (), " +
                            "if (not(db:exists('%s', 'courses.xml'))) then " +
                            "  db:add('%s', '<Courses/>', 'courses.xml') " +
                            "else ()",
                    dbName, dbName, dbName, dbName);
            new XQuery(initQuery).execute(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Course> all() {
        try {
            String query = String.format(
                    "for $course in collection('%s')/Courses/Course " +
                            "order by $course/id " +
                            "return $course",
                    dbName);
            String result = new XQuery(query).execute(context);
            return Course.parseXmlList(result);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public Course getById(long id) {
        try {
            String query = String.format(
                    "for $course in collection('%s')/Courses/Course[id = %d] " +
                            "return $course",
                    dbName, id);
            String result = new XQuery(query).execute(context);
            if (result.isEmpty()) {
                return null;
            }
            return Course.parseXml(result);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean create(Course course) {
        try {
            // First ensure the collection exists
            String initQuery = String.format(
                    "if (not(db:exists('%s'))) then " +
                            "  db:create('%s') " +
                            "else (), " +
                            "if (not(db:exists('%s', 'courses.xml'))) then " +
                            "  db:add('%s', '<Courses/>', 'courses.xml') " +
                            "else ()",
                    dbName, dbName, dbName, dbName);
            new XQuery(initQuery).execute(context);

            // Get the next available ID
            String maxIdQuery = String.format(
                    "let $maxId := max(collection('%s')/Courses/Course/id) " +
                            "return if ($maxId) then $maxId else 0",
                    dbName);
            String maxIdStr = new XQuery(maxIdQuery).execute(context);
            int nextId = Integer.parseInt(maxIdStr) + 1;
            course.setId(nextId);

            // Insert the new course
            String query = String.format(
                    "let $courses := collection('%s')/Courses " +
                            "return insert node " +
                            "<Course>" +
                            "<faculty>%s</faculty>" +
                            "<id>%d</id>" +
                            "<lecturer_id>%d</lecturer_id>" +
                            "<max_students>%d</max_students>" +
                            "<name>%s</name>" +
                            "<semester_id>%d</semester_id>" +
                            "</Course> " +
                            "into $courses",
                    dbName,
                    course.getFaculty(),
                    course.getId(),
                    course.getLecturerId(),
                    course.getMaxStudents(),
                    course.getName(),
                    course.getSemesterId());

            new XQuery(query).execute(context);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}