package xmw.exa.models.lecturers;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.XQuery;
import xmw.exa.db.DB;
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
        final String query = "/root/Lecturers";

        try {
            Lecturers lecturersElement = (Lecturers) DB.unmarshal(new XQuery(query).execute(context), Lecturers.class);
            return lecturersElement.getLecturer();
        } catch (BaseXException e) {
            throw new RuntimeException(e);
        }
    }
}