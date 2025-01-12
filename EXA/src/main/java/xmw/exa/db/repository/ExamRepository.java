package xmw.exa.db.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.XQuery;

import xmw.exa.db.Exam;

public class ExamRepository extends BaseXmlRepository<Exam> {

    public ExamRepository(Context context) {
        super(context);
    }

    @Override
    public List<Exam> all() {
        List<Exam> exams = new ArrayList<>();
        String query = String.format(
                "for $e in collection('%s/exams.xml')/Exams/Exam " +
                        "return element exam { " +
                        "  element id { $e/id/text() }, " +
                        "  element course_id { $e/course_id/text() }, " +
                        "  element date { $e/date/text() }, " +
                        "  element is_online { $e/is_online/text() }, " +
                        "  element is_written { $e/is_written/text() }, " +
                        "  element room_or_link { $e/room_or_link/text() } " +
                        "}",
                DB_NAME);

        try {
            String result = new XQuery(query).execute(context);
            String[] examElements = result.split("</exam>");
            for (String element : examElements) {
                if (element.trim().isEmpty())
                    continue;
                Exam exam = parseExamElement(element);
                if (exam != null) {
                    exams.add(exam);
                }
            }
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to query exams: " + e.getMessage(), e);
        }
        return exams;
    }

    @Override
    public Exam getById(long id) {
        String query = String.format(
                "for $e in collection('%s/exams.xml')/Exams/Exam[id = %d] " +
                        "return element exam { " +
                        "  element id { $e/id/text() }, " +
                        "  element course_id { $e/course_id/text() }, " +
                        "  element date { $e/date/text() }, " +
                        "  element is_online { $e/is_online/text() }, " +
                        "  element is_written { $e/is_written/text() }, " +
                        "  element room_or_link { $e/room_or_link/text() } " +
                        "}",
                DB_NAME, id);

        try {
            String result = new XQuery(query).execute(context);
            if (result.trim().isEmpty()) {
                return null;
            }
            return parseExamElement(result);
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to query exam: " + e.getMessage(), e);
        }
    }

    private Exam parseExamElement(String element) {
        try {
            Exam exam = new Exam();
            exam.setId(Integer.parseInt(extractValue(element, "id")));
            exam.setCourseId(Integer.parseInt(extractValue(element, "course_id")));
            exam.setDate(LocalDateTime.parse(extractValue(element, "date")));
            exam.setOnline(Boolean.parseBoolean(extractValue(element, "is_online")));
            exam.setWritten(Boolean.parseBoolean(extractValue(element, "is_written")));
            exam.setRoomOrLink(extractValue(element, "room_or_link"));
            return exam;
        } catch (Exception e) {
            System.err.println("Error processing exam element: " + element);
            e.printStackTrace();
            return null;
        }
    }
}