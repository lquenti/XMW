package xmw.exa.models.lectures;

import java.util.List;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.XQuery;

import xmw.exa.db.DB;
import xmw.exa.db.ExaElement;
import xmw.exa.db.repository.BaseXmlRepository;
import xmw.flush.Lecture;
import xmw.flush.Lectures;

public class LectureRepository extends BaseXmlRepository<Lecture> {

    public LectureRepository(Context context) {
        super(context, Lectures.class, Lecture.class);
    }

    @Override
    public List<Lecture> all() {
        try {
            var root = DB.getRootChildMap(context);
            Lectures lectures = (Lectures) root.get(ExaElement.LECTURES);
            return lectures.getLecture();
        } catch (BaseXException e) {
            throw new RuntimeException(e);
        }
    }
}
