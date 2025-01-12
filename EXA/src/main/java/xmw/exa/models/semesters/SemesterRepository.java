package xmw.exa.models.semesters;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.XQuery;

import xmw.exa.db.repository.BaseXmlRepository;

public class SemesterRepository extends BaseXmlRepository<Semester> {

    public SemesterRepository(Context context) {
        super(context);
    }

    @Override
    public List<Semester> all() {
        List<Semester> semesters = new ArrayList<>();
        String query = String.format(
                "for $s in /root/Semesters/Semester " +
                        "return element semester { " +
                        "  element id { $s/id/text() }, " +
                        "  element name { $s/name/text() }, " +
                        "  element start { $s/start/text() }, " +
                        "  element end { $s/end/text() } " +
                        "}");

        try {
            String result = new XQuery(query).execute(context);
            String[] semesterElements = result.split("</semester>");
            for (String element : semesterElements) {
                if (element.trim().isEmpty())
                    continue;
                Semester semester = parseSemesterElement(element);
                if (semester != null) {
                    semesters.add(semester);
                }
            }
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to query semesters: " + e.getMessage(), e);
        }
        return semesters;
    }

    @Override
    public Semester get(long id) {
        String query = String.format(
                "for $s in /root/Semesters/Semester[id = %d] " +
                        "return element semester { " +
                        "  element id { $s/id/text() }, " +
                        "  element name { $s/name/text() }, " +
                        "  element start { $s/start/text() }, " +
                        "  element end { $s/end/text() } " +
                        "}",
                id);

        try {
            String result = new XQuery(query).execute(context);
            if (result.trim().isEmpty()) {
                return null;
            }
            return parseSemesterElement(result);
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to query semester: " + e.getMessage(), e);
        }
    }

    private Semester parseSemesterElement(String element) {
        try {
            Semester semester = new Semester();
            semester.setId(Integer.parseInt(extractValue(element, "id")));
            semester.setName(extractValue(element, "name"));
            semester.setStart(LocalDateTime.parse(extractValue(element, "start")));
            semester.setEnd(LocalDateTime.parse(extractValue(element, "end")));
            return semester;
        } catch (Exception e) {
            System.err.println("Error processing semester element: " + element);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean create(Semester data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public Semester update(Semester data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
}