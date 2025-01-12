package xmw.exa.models.exams;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.XQuery;

import xmw.exa.db.DB;
import xmw.exa.db.repository.BaseXmlRepository;

public class ExamRepository extends BaseXmlRepository<Exam> {

    public ExamRepository(Context context) {
        super(context);
    }

    @Override
    public List<Exam> all() {
        List<Exam> exams = new ArrayList<>();
        String query = "for $e in /root/Exams/Exam " +
                "return element exam { " +
                "  element id { $e/id/text() }, " +
                "  element course_id { $e/course_id/text() }, " +
                "  element date { $e/date/text() }, " +
                "  element is_online { $e/is_online/text() }, " +
                "  element is_written { $e/is_written/text() }, " +
                "  element room_or_link { $e/room_or_link/text() } " +
                "}";

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
    public Exam get(long id) {
        var all = all();
        for (var exam : all) {
            if (exam.getId() == id)
                return exam;
        }
        return null;
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

    @Override
    public boolean create(Exam data) {
        try {

            // Find the highest existing ID
            String maxIdQuery = "let $maxId := max(/root/Exams/Exam/id/text()) " +
                    "return if ($maxId) then $maxId else 0";
            String maxIdResult = new XQuery(maxIdQuery).execute(context);
            int nextId = Integer.parseInt(maxIdResult.trim()) + 1;

            if (data.getId() > 0) {
                List<Integer> allIds = all().stream().map(Exam::getId).toList();

                // TODO: check w/ tests!!
                if (!allIds.contains(data.getId())) {
                    data.setId(nextId);
                }
            } else {
                data.setId(nextId);
            }


            String examXML = String.format(
                    "<Exam>\n" +
                            "      <course_id>%d</course_id>\n" +
                            "      <date>%s</date>\n" +
                            "      <id>%d</id>\n" +
                            "      <is_online>%d</is_online>\n" +
                            "      <is_written>%d</is_written>\n" +
                            "      <room_or_link>%s</room_or_link>\n" +
                            "   </Exam>",
                    data.getCourseId(),
                    data.getDate().toString(),
                    data.getId(),
                    data.isOnline() ? 1 : 0,
                    data.isWritten() ? 1 : 0,
                    data.getRoomOrLink()
            );

            String query = String.format("let $exams := /root/Exams" + "return insert node %s as last in $exams", examXML);

            new XQuery(query).execute(context);

            try {
                DB.getInstance().dumpToFile();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                return false;
            }
            return true;
        } catch (BaseXException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Exam update(Exam data) {
        if (!this.all().stream().map(Exam::getId).toList().contains(data.getId())) {
            return null;
        }
        delete(data.getId());
        if (create(data)) {
            return this.get(data.getId());
        }
        return null;
    }

    @Override
    public void delete(long id) {
        var res = this.all().stream().map(Exam::getId).filter(i -> i == id).toList().size();
        if (res != 1) {
            return;
        }
        try {
            String query = String.format("delete node /root/Exams/Exam/[id = %d]", id);
            new XQuery(query).execute(context);

            try {
                DB.getInstance().dumpToFile();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        } catch (BaseXException e) {
            System.err.println("Failed to delete Exam: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
