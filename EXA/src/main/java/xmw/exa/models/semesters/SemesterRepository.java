package xmw.exa.models.semesters;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.XQuery;
import xmw.exa.db.DB;
import xmw.exa.db.repository.BaseXmlRepository;
import xmw.flush.Semesters;

import java.util.List;

public class SemesterRepository extends BaseXmlRepository<SemesterModel> {

    public SemesterRepository(Context context) {
        super(context, Semesters.class, SemesterModel.class);
    }

    @Override
    public List<SemesterModel> all() {
        final String query = "/root/Semesters";

        try {
            Semesters semestersElement = (Semesters) DB.unmarshal(new XQuery(query).execute(context), Semesters.class);
            return semestersElement.getSemester().stream().map(e -> (SemesterModel) e).toList();
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to query semesters: " + e.getMessage(), e);
        }
    }
}