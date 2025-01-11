package xmw.exa;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.Course;
import xmw.exa.db.DB;
import xmw.exa.db.Exam;
import xmw.exa.db.Lecturer;
import xmw.exa.db.Semester;
import xmw.exa.util.HtmlUtil;

@WebServlet(name = "courses", value = "/courses")
public class CoursesServlet extends HttpServlet {
    private String name;
    private DB db;

    @Override
    public void init() {
        name = "Courses";
        db = DB.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String pathInfo = request.getServletPath();
        if (pathInfo.equals("/courses/all")) {
            String queryString = request.getQueryString();
            response.sendRedirect(HtmlUtil.BASE_URL + "/courses" + (queryString != null ? "?" + queryString : ""));
            return;
        }

        // Check format parameter
        boolean isXmlFormat = "xml".equals(request.getParameter("format"));

        if (isXmlFormat) {
            try {
                // Get all data
                List<Course> courses = db.getAllCourses();
                List<Lecturer> allLecturers = db.getAllLecturers();
                List<Exam> allExams = db.getAllExams();

                // Create XML writer
                StringWriter stringWriter = new StringWriter();
                XMLOutputFactory factory = XMLOutputFactory.newInstance();
                XMLStreamWriter xml = factory.createXMLStreamWriter(stringWriter);

                // Start document
                xml.writeStartDocument("UTF-8", "1.0");
                xml.writeStartElement("courses");

                // Write courses
                for (Course course : courses) {
                    writeCourseToXml(xml, course, allLecturers, allExams);
                }

                xml.writeEndElement(); // courses
                xml.writeEndDocument();
                xml.close();

                // Return XML response
                response.setContentType("application/xml");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.println(stringWriter.toString());
                out.flush();
                return;
            } catch (Exception e) {
                throw new IOException("Failed to generate courses XML: " + e.getMessage(), e);
            }
        }

        // HTML response
        response.setContentType("text/html");
        request.setAttribute("name", this.name);

        // Query for all courses with lecturer information
        List<Course> courses = db.getAllCourses();
        List<Lecturer> lecturers = db.getAllLecturers();
        List<Semester> semesters = db.getAllSemesters();

        StringBuilder message = new StringBuilder();

        // Group courses by semester
        for (Semester semester : semesters) {
            List<Course> semesterCourses = courses.stream()
                    .filter(c -> c.getSemesterId() == semester.getId())
                    .toList();

            if (!semesterCourses.isEmpty()) {
                message.append("<h2>").append(semester.getName()).append("</h2><ul>");

                for (Course course : semesterCourses) {
                    // Find the lecturer for this course
                    String lecturerName = lecturers.stream()
                            .filter(l -> l.getId() == course.getLecturerId())
                            .map(l -> String.format("<a href='%s/lecturers/%s'>%s</a>",
                                    HtmlUtil.BASE_URL,
                                    l.getUsername(),
                                    l.getFullName()))
                            .findFirst()
                            .orElse("Unknown Lecturer");

                    message.append("<li>")
                            .append("<a href=\"").append(HtmlUtil.BASE_URL).append("/courses/")
                            .append(course.getId())
                            .append("\">")
                            .append(course.getName())
                            .append("</a>")
                            .append(" - Faculty: ")
                            .append(course.getFaculty())
                            .append(" (Max Students: ")
                            .append(course.getMaxStudents())
                            .append(")")
                            .append(" - Held by: ")
                            .append(lecturerName)
                            .append("</li>");
                }
                message.append("</ul>");
            }
        }

        request.setAttribute("message", message.toString());

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/collection.jsp");
        dispatcher.forward(request, response);
    }

    private void writeCourseToXml(XMLStreamWriter xml, Course course, List<Lecturer> allLecturers, List<Exam> allExams)
            throws Exception {
        xml.writeStartElement("course");
        xml.writeAttribute("id", String.valueOf(course.getId()));
        xml.writeAttribute("semester_id", String.valueOf(course.getSemesterId()));

        writeSimpleElement(xml, "faculty", course.getFaculty());
        writeLecturerElement(xml, course.getLecturerId(), allLecturers);
        writeSimpleElement(xml, "max_students", String.valueOf(course.getMaxStudents()));
        writeSimpleElement(xml, "name", course.getName());
        writeSemesterElement(xml, course.getSemester());
        writeExamsElement(xml, course.getId(), allExams);

        xml.writeEndElement(); // course
    }

    private void writeSimpleElement(XMLStreamWriter xml, String elementName, String value) throws Exception {
        xml.writeStartElement(elementName);
        xml.writeCharacters(value);
        xml.writeEndElement();
    }

    private void writeLecturerElement(XMLStreamWriter xml, long lecturerId, List<Lecturer> allLecturers)
            throws XMLStreamException {
        xml.writeStartElement("lecturer");
        Lecturer lecturer = allLecturers.stream()
                .filter(l -> l.getId() == lecturerId)
                .findFirst()
                .orElse(null);
        if (lecturer != null) {
            xml.writeAttribute("id", String.valueOf(lecturer.getId()));
        }
        xml.writeEndElement();
    }

    private void writeSemesterElement(XMLStreamWriter xml, Semester semester) throws Exception {
        xml.writeStartElement("semester");
        if (semester != null) {
            writeSimpleElement(xml, "id", String.valueOf(semester.getId()));
            writeSimpleElement(xml, "name", semester.getName());
            writeSimpleElement(xml, "start", semester.getStart().toString());
            writeSimpleElement(xml, "end", semester.getEnd().toString());
        }
        xml.writeEndElement();
    }

    private void writeExamsElement(XMLStreamWriter xml, long courseId, List<Exam> allExams) throws Exception {
        xml.writeStartElement("exams");
        List<Exam> courseExams = allExams.stream()
                .filter(e -> e.getCourseId() == courseId)
                .toList();
        for (Exam exam : courseExams) {
            xml.writeStartElement("exam");
            writeSimpleElement(xml, "id", String.valueOf(exam.getId()));
            writeSimpleElement(xml, "date", exam.getDate().toString());
            writeSimpleElement(xml, "is_online", String.valueOf(exam.isOnline()));
            writeSimpleElement(xml, "is_written", String.valueOf(exam.isWritten()));
            writeSimpleElement(xml, "room_or_link", exam.getRoomOrLink());
            xml.writeEndElement(); // exam
        }
        xml.writeEndElement(); // exams
    }

    @Override
    public void destroy() {
        // Don't close the DB here as it's shared
    }
}
