package xmw.exa.models.exams;

import java.util.List;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.XQuery;

import xmw.exa.db.DB;
import xmw.exa.db.repository.BaseXmlRepository;
import xmw.flush.Exams;

public class ExamRepository extends BaseXmlRepository<ExamModel> {
    public ExamRepository(Context context) {
        super(context, Exams.class, ExamModel.class);
    }

    @Override
    public List<ExamModel> all() {
        final String query = "/root/Exams";
        try {
            Exams examsElement = (Exams) DB.unmarshal(new XQuery(query).execute(context), Exams.class);
            return examsElement.getExam().stream().map(e -> (ExamModel) e).toList();
        } catch (BaseXException e) {
            throw new RuntimeException(e);
        }
    }
}
