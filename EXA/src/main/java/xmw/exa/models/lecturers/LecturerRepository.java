package xmw.exa.models.lecturers;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.XQuery;
import xmw.exa.db.DB;
import xmw.exa.db.ExaElement;
import xmw.exa.db.repository.BaseXmlRepository;
import xmw.flush.Lecturer;
import xmw.flush.Lecturers;

import java.util.List;

public class LecturerRepository extends BaseXmlRepository<Lecturer> {
    public LecturerRepository(Context context) {
        super(context, Lecturers.class, Lecturer.class);
    }

    @Override
    public List<Lecturer> all() {
        try {
            var root = DB.getRootChildMap(context);
            Lecturers lecturers = (Lecturers) root.get(ExaElement.LECTURERS);
            return lecturers.getLecturer();
        } catch (BaseXException e) {
            throw new RuntimeException(e);
        }
    }
}