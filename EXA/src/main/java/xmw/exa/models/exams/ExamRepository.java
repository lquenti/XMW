package xmw.exa.models.exams;

import java.util.ArrayList;
import java.util.List;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.XQuery;

import xmw.exa.db.DB;
import xmw.exa.db.ExaElement;
import xmw.exa.db.repository.BaseXmlRepository;
import xmw.flush.Exam;
import xmw.flush.Exams;

public class ExamRepository extends BaseXmlRepository<Exam> {
    public ExamRepository(Context context) {
        super(context, Exams.class, Exam.class);
    }

    @Override
    public List<Exam> all() {
        try {
            var root = DB.getRootChildMap(context);
            Exams exams = (Exams) root.get(ExaElement.EXAMS);
            return exams.getExam();
        } catch (BaseXException e) {
            throw new RuntimeException(e);
        }
    }
}
