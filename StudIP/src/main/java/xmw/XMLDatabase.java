package xmw;

import org.basex.core.BaseXException;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.XQuery;
import org.basex.core.Context;
import org.basex.query.QueryException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static xmw.Utils.joinListOfMaps;

public class XMLDatabase {
    private static XMLDatabase instance;
    private static final String DB_NAME = "StudDB";
    private static final Object lock = new Object();

    // Initialize BaseX context
    private Context context;

    private XMLDatabase() {
        try {
            // Connect to BaseX server
            synchronized (lock) {
                context = new Context();
                new CreateDB(DB_NAME, AppContextListener.USER_PATH).execute(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void init() {
        instance = new XMLDatabase();
    }

    public static void flushToDisk() throws QueryException, BaseXException {
        String query = "file:write(\"" + AppContextListener.USER_PATH + "\", /)";
        synchronized (lock) {
            XQuery proc = new XQuery(query);
            proc.execute(instance.context);
        }
    }

    public static synchronized XMLDatabase getInstance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new XMLDatabase();
            }
        }
        return instance;
    }

    public boolean registerStudentToCourse(String userId, String courseId, String semester) {
        try {
            // XQuery to check if the user already has a schedule
            String checkSchedulesQuery = String.format(
                    "let $schedules := /StudIP/Schedules/Schedule[@username='%s']\n" +
                            "return exists($schedules)",
                    userId
            );
            boolean userHasSchedules;
            synchronized (lock) {
                userHasSchedules = Boolean.parseBoolean(new XQuery(checkSchedulesQuery).execute(context));
            }

            String xquery;

            if (userHasSchedules) {
                // XQuery to check if the user already has a schedule
                String checkScheduleQuery = String.format(
                        "let $course := /StudIP/Schedules/Schedule[@username='%s']/Course[@id='%s', semester='%s']\n" +
                                "return exists(course)",
                        userId, courseId, semester
                );
                boolean userHasSchedule;
                synchronized (lock) {
                    userHasSchedule = Boolean.parseBoolean(new XQuery(checkScheduleQuery).execute(context));
                }
                if(userHasSchedule)
                    return false;
                // User has an existing <Schedule>, add the course to it
                xquery = String.format(
                        "let $schedule := /StudIP/Schedules/Schedule[@username='%s']\n" +
                                "return insert node <Course id='%s' semester='%s'/> into $schedule",
                        userId, courseId, semester
                );
            } else {
                // User does not have a <Schedule>, create a new one with the course
                xquery = String.format(
                        "insert node <Schedule username='%s'><Course id='%s' semester='%s'/></Schedule> into /StudIP/Schedules",
                        userId, courseId, semester
                );
            }

            // Execute the XQuery
            synchronized (lock) {
                new XQuery(xquery).execute(context);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deregisterStudentFromCourse(String userId, String courseId, String semester) {
        try {
            // XQuery to find and remove the specific exam for the user
            String xquery = String.format(
                    "let $course := /StudIP/Schedules/Schedule[@username='%s']/Course[@id='%s' and @semester='%s']\n" +
                            "return delete node $course",
                    userId, courseId, semester
            );

            // Execute the XQuery
            synchronized (lock) {
                new XQuery(xquery).execute(context);
            }

            return true;
        } catch (BaseXException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getContentFromUrl(String contentUrl) throws IOException {
        URL url = new URL(contentUrl);
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
        return content.toString();
    }

    public List<Map<String, String>> getLectures() throws Exception {
        String content = getContentFromUrl(AppContextListener.EXA_URL + "lectures?format=xml");
        return processLectures(content);
    }

    public List<Map<String, String>> processLectures(String xmlString) throws Exception {
        List<Map<String, String>> lectures = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xmlString.getBytes()));

        Element tmp = (Element) doc.getElementsByTagName("Lectures").item(0);
        NodeList nodeList = tmp.getElementsByTagName("Lecture");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element lecture = (Element) nodeList.item(i);// XQuery to check if the user already has a schedule

            Map<String, String> lectureMap = new HashMap<>();
            lectureMap.put("CourseID", lecture.getAttribute("course"));
            lectureMap.put("lectureId", lecture.getAttribute("id"));

            lectureMap.put("Begin", lecture.getElementsByTagName("start").item(0).getTextContent());
            lectureMap.put("End", lecture.getElementsByTagName("end").item(0).getTextContent());
            lectureMap.put("Location", lecture.getElementsByTagName("room_or_link").item(0).getTextContent());

            lectures.add(lectureMap);
        }

        return lectures;
    }


    public List<Map<String, String>> getCourses() throws Exception {
        String content = getContentFromUrl(AppContextListener.EXA_URL + "courses?format=xml");
        return processCourses(content);
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
            courseMap.put("LecturerID", course.getAttribute("lecturer"));
            courseMap.put("CourseName", course.getElementsByTagName("name").item(0).getTextContent());
            courseMap.put("Faculty", course.getElementsByTagName("faculty").item(0).getTextContent());

            if (course.getElementsByTagName("max_students").getLength() > 0) {
                courseMap.put("MaxStudentCount", course.getElementsByTagName("max_students").item(0).getTextContent());
            }

            courses.add(courseMap);
        }

        return courses;
    }

    public List<Map<String,String>> getScheduleForStudent(String userId) throws Exception {
        List<Map<String, String>> schedule = new ArrayList<>();
        try {
            String query = String.format(
                    """
                            for $course in /StudIP/Schedules/Schedule[@username = '%s']/Course
                            return
                              <CourseDetail>
                                <CourseID>{$course/@id/string()}</CourseID>
                                <Semester>{$course/@semester/string()}</Semester>
                              </CourseDetail>""", userId);
            String result;
            synchronized (lock) {
                result = new XQuery(query).execute(context);
            }
            for(String r: result.split("\n")) {
                if(!r.isEmpty())
                    schedule.add(parseLecturersults(r));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, String>> courses = mergeCoursesAndLectures(getCourses(), getLectures());

        for(Map<String, String> s: schedule){
            for(Map<String, String> c: courses){
                if(s.get("CourseID").equals(c.get("CourseID"))){
                    s.put("CourseName", c.get("CourseName"));
                }
            }
        }

        return schedule;
    }

    List<Map<String, String>> mergeCoursesAndLectures(List<Map<String, String>> courses, List<Map<String, String>> lectures) {
        List<Map<String, String>> mergeList = new ArrayList<>();
        for(Map<String, String> course: courses){
            int cnt = 0;
            for(Map<String, String> lecture: lectures){
                if(course.get("CourseID").equals(lecture.get("CourseID"))){
                    course.put("Begin"+cnt, lecture.get("Begin"));
                    course.put("End"+cnt, lecture.get("End"));
                    course.put("Location"+cnt, lecture.get("Location"));
                    cnt++;
                }
            }
            mergeList.add(course);
        }
        return mergeList;
    }

    public Map<String, String> parseLecturersults(String xmlResult) {
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

    public List<Map<String, String>> getExams() throws Exception {
        String content = getContentFromUrl(AppContextListener.EXA_URL + "exams?format=xml");
        return processExams(content);
    }

    private List<Map<String, String>> processExams(String xmlString) {
        List<Map<String, String>> examsList = new ArrayList<>();

        try {
            // Parse the XML string into a DOM document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new java.io.ByteArrayInputStream(xmlString.getBytes()));

            // Normalize the document
            document.getDocumentElement().normalize();

            // Get all <exam> elements
            Element tmp = (Element) document.getElementsByTagName("Exams").item(0);
            NodeList examNodes = tmp.getElementsByTagName("Exam");

            for (int i = 0; i < examNodes.getLength(); i++) {
                Element examElement = (Element) examNodes.item(i);

                Map<String, String> examData = new HashMap<>();

                // Extract data from the <exam> element and its children
                examData.put("ExamId", examElement.getAttribute("id"));
                examData.put("CourseID", examElement.getAttribute("course"));
                examData.put("date", examElement.getElementsByTagName("date").item(0).getTextContent());
                examData.put("isOnline", examElement.getElementsByTagName("is_online").item(0).getTextContent());
                examData.put("isWritten", examElement.getElementsByTagName("is_written").item(0).getTextContent());
                examData.put("roomOrLink", examElement.getElementsByTagName("room_or_link").item(0).getTextContent());

                examsList.add(examData);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return examsList;
    }

    public boolean registerStudentToExam(String userId, String examId) {
        try {
            // XQuery to check if the user already has an <Exams> entry
            String checkExamsQuery = String.format(
                    "declare namespace ns = 'http://example.com/schema';\n" +
                            "let $exams := /StudIP/Exams/Registration[@username='%s']\n" +
                            "return exists($exams)",
                    userId
            );

            boolean examExists;

            synchronized (lock) {
                examExists = Boolean.parseBoolean(new XQuery(checkExamsQuery).execute(context));
            }

            String xquery;

            if (examExists) {
                // XQuery to check if the user already has a schedule
                String checkScheduleQuery = String.format(
                        "let $course := /StudIP/Exams/Registration[@username='%s']/Exam[@id='%s']\n" +
                                "return exists(course)",
                        userId, examId
                );
                boolean userIsRegistered;
                synchronized (lock) {
                    userIsRegistered = Boolean.parseBoolean(new XQuery(checkScheduleQuery).execute(context));
                }
                if(userIsRegistered)
                    return false;
                // If the exam exists, register the user for the exam
                xquery = String.format(
                        "let $exam := /StudIP/Exams/Registration[@username='%s']\n" +
                                "return insert node <Exam id='%s'/>into $exam",
                        userId, examId
                        );
            } else {
                // If the exam does not exist, create a new exam entry and register the user
                xquery = String.format(
                        "return insert node <Registration username='%s'><Exam id='%s'/></Registration> into /StudIP/Exams",
                        userId, examId
                        );
            }

            // Execute the XQuery
            synchronized (lock) {
                new XQuery(xquery).execute(context);
            }

            return true;
        } catch (BaseXException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deregisterStudentFromExam(String userId, String examId) {
        try {
            // XQuery to find and remove the specific exam for the user
            String xquery = String.format(
                    "let $exam := /StudIP/Exams/Registration[@username='%s']/Exam[@id='%s']\n" +
                            "return delete node $exam",
                    userId, examId
            );

            // Execute the XQuery
            synchronized (lock) {
                new XQuery(xquery).execute(context);
            }

            return true;
        } catch (BaseXException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getAllExamIDs(String userId) {
        try {
            // XQuery to retrieve all Exam IDs for the given user
            String xquery = String.format(
                    "for $exam in /StudIP/Exams/Registration[@username='%s']/Exam\n" +
                            "return data($exam/@id)",
                    userId
            );

            // Execute the XQuery and process the results
            String result;
            synchronized (lock) {
                result = new XQuery(xquery).execute(context);
            }
            String[] ids = result.split("\\n");
            List<String> examIds = new ArrayList<>();

            for (String id : ids) {
                if (!id.trim().isEmpty()) {
                    examIds.add(id.trim());
                }
            }

            return examIds;
        } catch (BaseXException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Map<String, String>> getStudents() throws IOException, ParserConfigurationException {
        String content = getContentFromUrl(AppContextListener.USER_URL + "bulk");

        Document doc;
        ArrayList<Map<String, String>> users = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
            try {
                doc = builder.parse(new java.io.ByteArrayInputStream(content.getBytes()));
            } catch (SAXException | IOException e) {
                throw new RuntimeException(e);
            }

            NodeList nodeList = doc.getElementsByTagName("User");
            for(int i=0;i<nodeList.getLength();i++) {
                Map<String, String> tmp = new HashMap<>();
                Element currentUser = (Element) nodeList.item(i);
                tmp.put("username", currentUser.getAttribute("username"));
                tmp.put("name", currentUser.getElementsByTagName("name").item(0).getTextContent());
                tmp.put("firstname", currentUser.getElementsByTagName("firstname").item(0).getTextContent());
                tmp.put("faculty", currentUser.getElementsByTagName("faculty").item(0).getTextContent());

                users.add(tmp);
            }

        return users;
    }

    public boolean inputGrade(String examId, String studentId, String grade) {
        try {
            // XQuery to check if the user already has an <Exams> entry
            String checkGradeQuery = String.format(
                    "let $exams := /StudIP/Grades/Grade[@username='%s']/Exam[@id='%s']\n" +
                            "return exists($exams)",
                    studentId, examId
            );

            boolean gradeExists;
            synchronized (lock) {
                gradeExists = Boolean.parseBoolean(new XQuery(checkGradeQuery).execute(context));
            }

            String xquery;

            if (gradeExists) {
                // If the exam exists, register the user for the exam
                xquery = String.format(
                        "let $exam := /StudIP/Grades/Grade[@username='%s']\n" +
                                "return insert node <Exam id='%s'>%s</Exam>into $exam",
                        studentId, examId, grade
                );
            } else {
                // If the exam does not exist, create a new exam entry and register the user
                try {
                    xquery = String.format(
                            "let $exam := /StudIP/Grades/Grade[@username='%s']/Exam[@id='%s']\n" +
                                    "delete node $exam",
                            studentId, examId
                    );
                    synchronized (lock) {
                        new XQuery(xquery).execute(context);
                    }
                }catch (Exception _){}

                xquery = String.format(
                        "insert node <Grade username='%s'><Exam id='%s'>%s</Exam></Grade> into /StudIP/Grades",
                        studentId, examId, grade
                );
            }

            // Execute the XQuery
            synchronized (lock) {
                new XQuery(xquery).execute(context);
            }

            return true;
        } catch (BaseXException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<Map<String, String>> getGrades(String userId) {
        try {
            // XQuery to retrieve all grades for the given user
            String xquery = String.format(
                    "for $exam in /StudIP/Grades/Grade[@username='%s']/Exam\n" +
                            "return map { 'id': data($exam/@id), 'grade': data($exam/text()) }",
                    userId
            );

            // Execute the XQuery and process the results
            String result;
            synchronized (lock) {
                result = new XQuery(xquery).execute(context);
            }
            String[] entries = result.split("\\n");
            List<Map<String, String>> grades = new ArrayList<>();

            for (String entry : entries) {
                if (!entry.trim().isEmpty()) {
                    Map<String, String> grade = new HashMap<>();
                    String[] keyValuePairs = entry.replace("{", "").replace("}", "").split(",");
                    for (String pair : keyValuePairs) {
                        String[] keyValue = pair.split(":");
                        grade.put(keyValue[0].replace("'", "").replace("\"", ""), keyValue[1].replace("'", "").replace("\"", ""));
                    }
                    grades.add(grade);
                }
            }

            return grades;
        } catch (BaseXException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<String> getCurrentRole(String loggedInUserId) throws IOException {
        String content = getContentFromUrl(AppContextListener.USER_URL + "id/" + loggedInUserId);
        ArrayList<String> roles = new ArrayList<>();

        Document doc = getDocument(content);

        NodeList nodeList = doc.getElementsByTagName("User");
        Element currentUser = (Element) nodeList.item(0);
        Element currentGroup = (Element) currentUser.getElementsByTagName("group").item(0);
        roles.add(currentGroup.getAttribute("id"));
        return roles;
    }

    public Map<String, String> getUserInfo(String UserId) throws IOException {
        String content = getContentFromUrl(AppContextListener.USER_URL + "id/" + UserId);

        Map<String, String> userInfo = new HashMap<>();
        Document doc = getDocument(content);

        NodeList nodeList = doc.getElementsByTagName("User");
        Element currentUser = (Element) nodeList.item(0);
        userInfo.put("username", currentUser.getAttribute("username"));
        userInfo.put("name", currentUser.getElementsByTagName("name").item(0).getTextContent());
        userInfo.put("firstname", currentUser.getElementsByTagName("firstname").item(0).getTextContent());
        userInfo.put("faculty", currentUser.getElementsByTagName("faculty").item(0).getTextContent());
        return userInfo;
    }

    public List<Map<String, String>> getExamsAsLecturer(String LecturerId) throws Exception {
        List<Map<String, String>> exams = getExams();
        List<Map<String, String>> courses = getCourses();
        joinListOfMaps(exams, courses, "CourseID", "CourseID");

        List<Map<String, String>> currentExams = new ArrayList<>();
        for(Map<String, String> exam: exams){
            if(getUserNameFromID(exam.get("LecturerID")).equals(LecturerId))
                currentExams.add(exam);
        }
        return currentExams;
    }

    public String getRegistrations(String userName) throws Exception {
        try {
            String xquery = String.format("/StudIP/Exams/Registration[@username='%s']/Exam/@id/string()", userName);

            // Execute the XQuery and process the results
            String result;
            synchronized (lock) {
                result = new XQuery(xquery).execute(context);
            }

            return result;
        } catch (BaseXException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getUserNameFromID(String lecturerID) throws IOException {
        String content = getContentFromUrl(AppContextListener.EXA_URL + "lecturers/" + lecturerID);

        Document doc;
        doc = getDocument(content);

        NodeList nodeList = doc.getElementsByTagName("Lecturer");
        Element currentUser = (Element) nodeList.item(0);
        return currentUser.getAttribute("username");
    }

    private Document getDocument(String content) {
        Document doc;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(new ByteArrayInputStream(content.getBytes()));
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new RuntimeException(e);
        }
        return doc;
    }

    protected void registerLecturersToCourse() {
        try {
            List<Map<String, String>> courses = getCourses();
            for (Map<String, String> course : courses) {
                String lecturerId = course.get("LecturerID");
                String lecturerUsername = getUserNameFromID(lecturerId);
                if(lecturerUsername.isEmpty())
                    continue;
                registerStudentToCourse(lecturerUsername, course.get("CourseID"), course.get("Semester"));
            }
        }catch (Exception _){}
    }

    public List<Map<String, String>> getModules() throws IOException {
        String content = getContentFromUrl(AppContextListener.EXA_URL + "modules?format=xml");
        return processModules(content);
    }

    private List<Map<String, String>> processModules(String xmlString) {
        List<Map<String, String>> moduleList = new ArrayList<>();

        try {
            // Parse the XML string into a DOM document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new java.io.ByteArrayInputStream(xmlString.getBytes()));

            // Normalize the document
            document.getDocumentElement().normalize();

            // Get all <exam> elements
            Element tmp = (Element) document.getElementsByTagName("Modules").item(0);
            NodeList moduleNodes = tmp.getElementsByTagName("Module");

            for (int i = 0; i < moduleNodes.getLength(); i++) {
                Element moduleElement = (Element) moduleNodes.item(i);

                Map<String, String> moduleData = new HashMap<>();

                // Extract data from the <exam> element and its children
                moduleData.put("ModuleName", moduleElement.getElementsByTagName("name").item(0).getTextContent());
                moduleData.put("ModuleID", moduleElement.getAttribute("id"));
                moduleData.put("Credits", moduleElement.getAttribute("credits"));
                moduleData.put("CourseID", moduleElement.getAttribute("course"));
                moduleData.put("Description", moduleElement.getElementsByTagName("Description").item(0).getTextContent());
                NodeList studies = moduleElement.getElementsByTagName("Studies");
                StringBuilder studyString = new StringBuilder();
                for(int j=0; j< studies.getLength(); j++){
                    Element e = (Element) studies.item(j);
                    studyString.append(e.getTextContent());
                    if(j+1< studies.getLength())
                        studyString.append("\n");
                }
                moduleData.put("Studies", studyString.toString());
                moduleList.add(moduleData);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return moduleList;
    }
}
