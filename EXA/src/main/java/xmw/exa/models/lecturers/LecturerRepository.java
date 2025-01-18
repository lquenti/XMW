package xmw.exa.models.lecturers;

import java.util.ArrayList;
import java.util.List;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.XQuery;

import xmw.exa.db.repository.BaseXmlRepository;

public class LecturerRepository extends BaseXmlRepository<LecturerOld> {
    public LecturerRepository(Context context) {
        super(context);
    }

    @Override
    public List<LecturerOld> all() {
        List<LecturerOld> lecturerOlds = new ArrayList<>();
        String query = String.format(
                "for $l in /root/Lecturers/Lecturer " +
                        "return element lecturer { " +
                        "  attribute id { $l/id/text() }, " +
                        "  attribute username { $l/@username }, " +
                        "  element faculty { if ($l/faculty/@null = 'true') then '' else $l/faculty/text() }, " +
                        "  element first_name { $l/firstname/text() }, " +
                        "  element last_name { $l/name/text() } " +
                        "}",
                DB_NAME);

        try {
            String result = new XQuery(query).execute(context);
            String[] lecturerElements = result.split("</lecturer>");

            for (String element : lecturerElements) {
                if (element.trim().isEmpty())
                    continue;
                LecturerOld lecturerOld = parseLecturerElement(element);
                if (lecturerOld != null) {
                    lecturerOlds.add(lecturerOld);
                }
            }
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to query lecturers: " + e.getMessage(), e);
        }
        return lecturerOlds;
    }

    @Override
    public LecturerOld get(long id) {
        String query = String.format(
                "for $l in /root/Lecturers/Lecturer[id = %d] " +
                        "return element lecturer { " +
                        "  attribute username { $l/@username }, " +
                        "  element id { $l/id/text() }, " +
                        "  element faculty { if ($l/faculty/@null = 'true') then '' else $l/faculty/text() }, " +
                        "  element first_name { $l/firstname/text() }, " +
                        "  element last_name { $l/name/text() } " +
                        "}",
                id);

        try {
            String result = new XQuery(query).execute(context);
            if (result.trim().isEmpty()) {
                return null;
            }
            return parseLecturerElement(result);
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to query lecturer: " + e.getMessage(), e);
        }
    }

    private LecturerOld parseLecturerElement(String element) {
        try {
            LecturerOld lecturerOld = new LecturerOld();

            // Extract attributes
            String usernamePattern = "username=\"([^\"]*)\"";
            String idPattern = "id=\"([^\"]*)\"";
            java.util.regex.Pattern usernameRegex = java.util.regex.Pattern.compile(usernamePattern);
            java.util.regex.Pattern idRegex = java.util.regex.Pattern.compile(idPattern);

            java.util.regex.Matcher usernameMatcher = usernameRegex.matcher(element);
            java.util.regex.Matcher idMatcher = idRegex.matcher(element);

            if (usernameMatcher.find()) {
                lecturerOld.setUsername(usernameMatcher.group(1));
            }
            if (idMatcher.find()) {
                lecturerOld.setId(Integer.parseInt(idMatcher.group(1)));
            }

            // Extract other fields
            lecturerOld.setFaculty(extractValue(element, "faculty"));
            lecturerOld.setName(extractValue(element, "last_name"));
            lecturerOld.setFirstname(extractValue(element, "first_name"));

            return lecturerOld;
        } catch (Exception e) {
            System.err.println("Error processing lecturer element: " + element);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean create(LecturerOld data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public LecturerOld update(LecturerOld data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
}