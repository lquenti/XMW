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
                            <Course id="c1" semester="ws2425">
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

    public boolean registerStudentToCourse(String userId, String courseId) {
        try {
            String query = String.format("... XQuery to register student ...", userId, courseId);
            String result = new XQuery(query).execute(context);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Map<String, String>> getCourses() throws Exception {
        URL url = new URL("http://localhost:8080/courses/all");
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

        NodeList nodeList = doc.getElementsByTagName("Course");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element course = (Element) nodeList.item(i);

            Map<String, String> courseMap = new HashMap<>();
            courseMap.put("CourseID", course.getAttribute("id"));
            courseMap.put("Semester", course.getAttribute("semester"));
            courseMap.put("Name", course.getElementsByTagName("Name").item(0).getTextContent());
            courseMap.put("Faculty", course.getElementsByTagName("Faculty").item(0).getTextContent());

            // MaxStudentCount is optional
            if (course.getElementsByTagName("MaxStudentCount").getLength() > 0) {
                courseMap.put("MaxStudentCount", course.getElementsByTagName("MaxStudentCount").item(0).getAttributes().getNamedItem("count").getTextContent());
            }

            courseMap.put("Time", getLecturesString(course.getElementsByTagName("Lectures")));

            courses.add(courseMap);
        }

        return courses;
    }

    private String getLecturesString(NodeList lectures) {
        String time = String.format("Begin: %s \t End: %s \t Room: %s",
                lectures.item(0).getAttributes().getNamedItem("time_begin"),
                lectures.item(0).getAttributes().getNamedItem("time_end"),
                lectures.item(0).getAttributes().getNamedItem("room_id"));
        for(int i=1;i<lectures.getLength();i++){
            time += String.format("\nBegin: %s \t End: %s \t Room: %s",
                    lectures.item(i).getAttributes().getNamedItem("time_begin"),
                    lectures.item(i).getAttributes().getNamedItem("time_end"),
                    lectures.item(i).getAttributes().getNamedItem("room_id"));
        }
        return time;
    }

    public List<Map<String,String>> getScheduleForStudent(String userId) throws Exception {
        List<Map<String, String>> schedule = new ArrayList<>();
        try {
            String query = String.format(
                    """
                            for $course in /Schedules/Schedule[%s = $username]/Course
                            return
                              <CourseDetail>
                                <CourseID>{$course/@id}</CourseID>
                                <Semester>{$course/@semester}</Semester>
                              </CourseDetail>""", userId);
            String result = new XQuery(query).execute(context);
            for(String r: result.split("\n"))
                schedule.add(parseLectureResults(r));
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, String>> courses = getCourses();

        for(Map<String, String> s: schedule){
            for(Map<String, String> c: courses){
                if(s.get("CourseID").equals(c.get("CourseID"))){
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
                    lectureMap.put("Semester", lectureElement.getAttribute("Semester"));
                    lectureMap.put("CourseID", lectureElement.getAttribute("CourseID"));

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
}
