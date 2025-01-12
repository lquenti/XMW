package xmw.studip;

import org.basex.api.client.ClientQuery;
import org.basex.api.client.ClientSession;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.XQuery;
import org.basex.core.Context;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLDatabase {
    private static XMLDatabase instance;
    private static final String DB_NAME = "ScheduleDB";

    // Initialize BaseX context
    private Context context;

    // Define the XML content
    private static final String EXAMPLE_XML =
            """
                    <Schedules>
                        <Schedule username="hbrosen">
                            <Course id="1" semester="ws2425">
                            </Course>
                        </Schedule>
                    </Schedules>
                   \s""";

    private XMLDatabase() {
        try {
            // Connect to BaseX server
            context = new Context();
            new CreateDB(DB_NAME, EXAMPLE_XML).execute(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized XMLDatabase getInstance() {
        if (instance == null) {
            instance = new XMLDatabase();
        }
        return instance;
    }

    public boolean registerStudentToCourse(String userId, String courseId, String semester) {
        try {
            // XQuery to check if the user already has a schedule
            String checkScheduleQuery = String.format(
                    "declare namespace ns = 'http://example.com/schema';\n" +
                            "let $schedules := /Schedules/Schedule[@username='%s']\n" +
                            "return exists($schedules)",
                    userId
            );

            boolean userHasSchedule = Boolean.parseBoolean(new XQuery(checkScheduleQuery).execute(context));

            String xquery;

            if (userHasSchedule) {
                // User has an existing <Schedule>, add the course to it
                xquery = String.format(
                        "declare namespace ns = 'http://example.com/schema';\n" +
                                "let $schedule := /Schedules/Schedule[@username='%s']\n" +
                                "return insert node <Course id='%s' semester='%s'/> into $schedule",
                        userId, courseId, semester
                );
            } else {
                // User does not have a <Schedule>, create a new one with the course
                xquery = String.format(
                        "declare namespace ns = 'http://example.com/schema';\n" +
                                "return insert node <Schedule username='%s'><Course id='%s' semester='%s'/></Schedule> into /Schedules",
                        userId, courseId, semester
                );
            }

            // Execute the XQuery
            new XQuery(xquery).execute(context);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Map<String, String>> getCourses() throws Exception {
        URL url = new URL("http://localhost:8080/exa/courses?format=xml");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        conn.disconnect();

        return processCourses(content.toString());
    }

    public List<Map<String, String>> processCourses(String xmlString) throws Exception {
        List<Map<String, String>> courses = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xmlString.getBytes()));

        NodeList nodeList = doc.getElementsByTagName("course");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element course = (Element) nodeList.item(i);

            Map<String, String> courseMap = new HashMap<>();
            courseMap.put("CourseID", course.getAttribute("id"));
            courseMap.put("Semester", course.getAttribute("semester_id"));
            courseMap.put("Name", course.getElementsByTagName("name").item(0).getTextContent());
            courseMap.put("Faculty", course.getElementsByTagName("faculty").item(0).getTextContent());
            Element tmp = (Element) course.getElementsByTagName("lecturer").item(0);
            courseMap.put("LecturerID", tmp.getAttribute("id"));

            // MaxStudentCount is optional
            if (course.getElementsByTagName("max_students").getLength() > 0) {
                courseMap.put("MaxStudentCount", course.getElementsByTagName("max_students").item(0).getTextContent());
            }
            if (course.getElementsByTagName("lectures").getLength() > 0) {
                Element lectures = (Element) course.getElementsByTagName("lectures").item(0);
                courseMap.put("Time", getLecturesString(lectures.getElementsByTagName("lecture")));
            }
            else
                courseMap.put("Time", "-");

            courses.add(courseMap);
        }

        return courses;
    }

    private String getLecturesString(NodeList lectures) {
        Element tmp = (Element) lectures.item(0);
        StringBuilder time = new StringBuilder(String.format("Begin: %s \t End: %s \t Room: %s",
                tmp.getElementsByTagName("start").item(0).getTextContent(),
                tmp.getElementsByTagName("end").item(0).getTextContent(),
                tmp.getElementsByTagName("room_or_link").item(0).getTextContent()));
        for(int i=1;i<lectures.getLength();i++){
            tmp = (Element) lectures.item(i);
            time.append(String.format("\nBegin: %s \t End: %s \t Room: %s",
                    tmp.getElementsByTagName("start").item(0).getTextContent(),
                    tmp.getElementsByTagName("end").item(0).getTextContent(),
                    tmp.getElementsByTagName("room_or_link").item(0).getTextContent()));
        }
        return time.toString();
    }

    public List<Map<String,String>> getScheduleForStudent(String userId) throws Exception {
        List<Map<String, String>> schedule = new ArrayList<>();
        try {
            String query = String.format(
                    """
                            for $course in /Schedules/Schedule[@username = \"%s\"]/Course
                            return
                              <CourseDetail>
                                <CourseID>{$course/@id/string()}</CourseID>
                                <Semester>{$course/@semester/string()}</Semester>
                              </CourseDetail>""", userId);
            String result = new XQuery(query).execute(context);
            for(String r: result.split("\n")) {
                if(!r.isEmpty())
                    schedule.add(parseLectureResults(r));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, String>> courses = getCourses();

        for(Map<String, String> s: schedule){
            for(Map<String, String> c: courses){
                if(s.getOrDefault("CourseID", "-").equals(c.getOrDefault("CourseID", "-"))){
                    s.putAll(c);
                }
            }
        }

        return schedule;
    }

    public Map<String, String> parseLectureResults(String xmlResult) {
        Map<String, String> lecture = new HashMap<>();
        try {
            // Create a document from the XML string
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlResult)));

            // Normalize the XML structure; it's just good practice
            document.getDocumentElement().normalize();

            // Get all Lecture elements
            NodeList lectureNodes = document.getElementsByTagName("CourseDetail");

            // Iterate through each Lecture element
            for (int i = 0; i < lectureNodes.getLength(); i++) {
                Node lectureNode = lectureNodes.item(i);

                if (lectureNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element lectureElement = (Element) lectureNode;

                    // Create a map for this Lecture's attributes
                    Map<String, String> lectureMap = new HashMap<>();
                    lectureMap.put("Semester", lectureElement.getElementsByTagName("Semester").item(0).getTextContent());
                    lectureMap.put("CourseID", lectureElement.getElementsByTagName("CourseID").item(0).getTextContent());

                    // Add the map to the list
                    lecture.putAll(lectureMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lecture;
    }

    public void close() {
        try {
            if (context != null) {
                context.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getThing() {
        return "";
    }
}
