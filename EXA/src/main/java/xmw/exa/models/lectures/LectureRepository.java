package xmw.exa.models.lectures;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.XQuery;

import xmw.exa.db.DB;
import xmw.exa.db.repository.BaseXmlRepository;
import xmw.flush.Lectures;

public class LectureRepository extends BaseXmlRepository<LectureModel> {

    public LectureRepository(Context context) {
        super(context, Lectures.class, LectureModel.class);
    }

    @Override
    public List<LectureModel> all() {
        final String query = "/root/Lectures";
        try {
            Lectures lecturesElement = (Lectures) DB.unmarshal(new XQuery(query).execute(context), Lectures.class);
            return lecturesElement.getLecture().stream().map(e -> (LectureModel) e).toList();
        } catch (BaseXException e) {
            throw new RuntimeException(e);
        }
    }
}
