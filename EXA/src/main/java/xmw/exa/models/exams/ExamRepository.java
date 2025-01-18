package xmw.exa.models.exams;

import java.util.List;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.XQuery;

import xmw.exa.db.DB;
import xmw.exa.db.repository.BaseXmlRepository;
import xmw.flush.Exam;
import xmw.flush.Exams;

public class ExamRepository extends BaseXmlRepository<Exam> {
    public ExamRepository(Context context) {
        super(context, Exams.class, Exam.class);
    }

    @Override
    public List<Exam> all() {
        final String query = "/root/Exams";
        try {
            Exams examsElement = (Exams) DB.unmarshal(new XQuery(query).execute(context), Exams.class);
            return examsElement.getExam();
        } catch (BaseXException e) {
            throw new RuntimeException(e);
        }
    }
}
