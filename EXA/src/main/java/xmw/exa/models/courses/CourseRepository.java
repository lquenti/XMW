package xmw.exa.models.courses;

import java.util.List;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.XQuery;

import xmw.exa.db.DB;
import xmw.exa.db.repository.BaseXmlRepository;
import xmw.flush.Course;
import xmw.flush.Courses;

public class CourseRepository extends BaseXmlRepository<Course> {
    public CourseRepository(Context context) {
        super(context, Courses.class, Course.class);
    }

    @Override
    public List<Course> all() {
        final String query = "/root/Courses";

        try {
            Courses coursesElement = (Courses) DB.unmarshal(new XQuery(query).execute(context), Courses.class);
            return coursesElement.getCourse();
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to query courses: " + e.getMessage(), e);
        }
    }
}
