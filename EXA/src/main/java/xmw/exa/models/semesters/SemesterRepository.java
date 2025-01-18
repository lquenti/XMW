package xmw.exa.models.semesters;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import xmw.exa.db.DB;
import xmw.exa.db.ExaElement;
import xmw.exa.db.repository.BaseXmlRepository;
import xmw.flush.Semester;
import xmw.flush.Semesters;

import java.util.List;

public class SemesterRepository extends BaseXmlRepository<Semester> {

    public SemesterRepository(Context context) {
        super(context, Semesters.class, Semester.class);
    }

    @Override
    public List<Semester> all() {
        try {
            var root = DB.getRootChildMap(context);
            Semesters semesters = (Semesters) root.get(ExaElement.SEMESTERS);
            return semesters.getSemester();
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to query semesters: " + e.getMessage(), e);
        }
    }
}