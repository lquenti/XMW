package xmw.exa.models.courses;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import xmw.exa.db.DB;
import xmw.exa.db.ExaElement;
import xmw.exa.db.repository.BaseXmlRepository;
import xmw.flush.Course;
import xmw.flush.Courses;

import java.util.List;

public class CourseRepository extends BaseXmlRepository<Course> {
    public CourseRepository(Context context) {
        super(context, Courses.class, Course.class);
    }

    @Override
    public List<Course> all() {
        try {
            var root = DB.getRootChildMap(context);
            Courses courses = (Courses) root.get(ExaElement.COURSES);
            return courses.getCourse();
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to query courses: " + e.getMessage(), e);
        }
    }
}
