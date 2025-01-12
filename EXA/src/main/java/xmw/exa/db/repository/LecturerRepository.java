package xmw.exa.db.repository;

import java.util.ArrayList;
import java.util.List;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.XQuery;

import xmw.exa.db.Lecturer;

public class LecturerRepository extends BaseXmlRepository<Lecturer> {

    public LecturerRepository(Context context) {
        super(context);
    }

    @Override
    public List<Lecturer> all() {
        List<Lecturer> lecturers = new ArrayList<>();
        String query = String.format(
                "for $l in collection('%s/lecturers.xml')/Lectureres/Lecturer " +
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
                Lecturer lecturer = parseLecturerElement(element);
                if (lecturer != null) {
                    lecturers.add(lecturer);
                }
            }
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to query lecturers: " + e.getMessage(), e);
        }
        return lecturers;
    }

    @Override
    public Lecturer getById(long id) {
        String query = String.format(
                "for $l in collection('%s/lecturers.xml')/Lectureres/Lecturer[id = %d] " +
                        "return element lecturer { " +
                        "  attribute username { $l/@username }, " +
                        "  element id { $l/id/text() }, " +
                        "  element faculty { if ($l/faculty/@null = 'true') then '' else $l/faculty/text() }, " +
                        "  element first_name { $l/firstname/text() }, " +
                        "  element last_name { $l/name/text() } " +
                        "}",
                DB_NAME, id);

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

    private Lecturer parseLecturerElement(String element) {
        try {
            Lecturer lecturer = new Lecturer();

            // Extract attributes
            String usernamePattern = "username=\"([^\"]*)\"";
            String idPattern = "id=\"([^\"]*)\"";
            java.util.regex.Pattern usernameRegex = java.util.regex.Pattern.compile(usernamePattern);
            java.util.regex.Pattern idRegex = java.util.regex.Pattern.compile(idPattern);

            java.util.regex.Matcher usernameMatcher = usernameRegex.matcher(element);
            java.util.regex.Matcher idMatcher = idRegex.matcher(element);

            if (usernameMatcher.find()) {
                lecturer.setUsername(usernameMatcher.group(1));
            }
            if (idMatcher.find()) {
                lecturer.setId(Integer.parseInt(idMatcher.group(1)));
            }

            // Extract other fields
            lecturer.setFaculty(extractValue(element, "faculty"));
            lecturer.setName(extractValue(element, "last_name"));
            lecturer.setFirstname(extractValue(element, "first_name"));

            return lecturer;
        } catch (Exception e) {
            System.err.println("Error processing lecturer element: " + element);
            e.printStackTrace();
            return null;
        }
    }
}