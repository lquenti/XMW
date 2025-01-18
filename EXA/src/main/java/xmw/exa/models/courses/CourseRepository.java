package xmw.exa.models.courses;

import java.util.List;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.XQuery;

import xmw.exa.db.DB;
import xmw.exa.db.repository.BaseXmlRepository;
import xmw.flush.Courses;

public class CourseRepository extends BaseXmlRepository<CourseModel> {
    public CourseRepository(Context context) {
        super(context, Courses.class, CourseModel.class);
    }

    @Override
    public List<CourseModel> all() {
        final String query = "/root/Courses";

        try {
            Courses coursesElement = (Courses) DB.unmarshal(new XQuery(query).execute(context), Courses.class);
            return coursesElement.getCourse().stream().map(c -> (CourseModel) c).toList();
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to query courses: " + e.getMessage(), e);
        }
    }
}
