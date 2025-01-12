package xmw.exa.models.lectures;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.XQuery;

import xmw.exa.db.repository.BaseXmlRepository;

public class LectureRepository extends BaseXmlRepository<Lecture> {

    public LectureRepository(Context context) {
        super(context);
    }

    @Override
    public List<Lecture> all() {
        List<Lecture> lectures = new ArrayList<>();
        String query = String.format(
                "for $l in /root/Lectures/Lecture " +
                        "return element lecture { " +
                        "  element id { $l/id/text() }, " +
                        "  element course_id { $l/course_id/text() }, " +
                        "  element start { $l/start/text() }, " +
                        "  element room_or_link { $l/room_or_link/text() } " +
                        "}",
                DB_NAME);

        try {
            String result = new XQuery(query).execute(context);
            String[] lectureElements = result.split("</lecture>");
            for (String element : lectureElements) {
                if (element.trim().isEmpty())
                    continue;
                Lecture lecture = parseLectureElement(element);
                if (lecture != null) {
                    lectures.add(lecture);
                }
            }
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to query lectures: " + e.getMessage(), e);
        }
        return lectures;
    }

    @Override
    public Lecture get(long id) {
        String query = String.format(
                "for $l in /root/Lectures/Lecture[id = %d] " +
                        "return element lecture { " +
                        "  element id { $l/id/text() }, " +
                        "  element course_id { $l/course_id/text() }, " +
                        "  element start { $l/start/text() }, " +
                        "  element room_or_link { $l/room_or_link/text() } " +
                        "}",
                id);

        try {
            String result = new XQuery(query).execute(context);
            if (result.trim().isEmpty()) {
                return null;
            }
            return parseLectureElement(result);
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to query lecture: " + e.getMessage(), e);
        }
    }

    private Lecture parseLectureElement(String element) {
        try {
            Lecture lecture = new Lecture();
            lecture.setId(Integer.parseInt(extractValue(element, "id")));
            lecture.setCourseId(Integer.parseInt(extractValue(element, "course_id")));

            // Fix malformed datetime strings by replacing any single digit followed by 'T:'
            // e.g., "T0T:" -> "T00:", "T1T:" -> "T01:", etc.
            String startStr = extractValue(element, "start");

            for (int i = 0; i < 10; i++) {
                startStr = startStr.replace(String.format("T%dT:", i), String.format("T0%d:", i));
            }
            for (int i = 10; i < 24; i++) {
                startStr = startStr.replace(String.format("T%dT:", i), String.format("T%d:", i));
            }

            lecture.setStart(LocalDateTime.parse(startStr));
            lecture.setEnd(lecture.getStart()); // Set end time same as start time if not specified
            lecture.setRoomOrLink(extractValue(element, "room_or_link"));
            return lecture;
        } catch (Exception e) {
            System.err.println("Error processing lecture element: " + element);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean create(Lecture data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public Lecture update(Lecture data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public Lecture delete(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
}
