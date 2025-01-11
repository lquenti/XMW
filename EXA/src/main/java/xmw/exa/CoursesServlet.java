package xmw.exa;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.Course;
import xmw.exa.db.DB;
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
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                var courses = db.getAllCourses();
                var allLecturers = db.getAllLecturers();
                var allExams = db.getAllExams();

                // Create XML writer
                StringWriter stringWriter = new StringWriter();
                XMLOutputFactory factory = XMLOutputFactory.newInstance();
                XMLStreamWriter xml = factory.createXMLStreamWriter(stringWriter);

                // Start document
                xml.writeStartDocument("UTF-8", "1.0");
                xml.writeStartElement("courses");

                // Write lecturers section
                xml.writeStartElement("lecturers");
                for (var course : courses) {
                    var lecturer = allLecturers.stream()
                            .filter(l -> l.getId() == course.getLecturerId())
                            .findFirst()
                            .orElse(null);
                    if (lecturer != null) {
                        xml.writeStartElement("lecturer");
                        xml.writeStartElement("id");
                        xml.writeCharacters(String.valueOf(lecturer.getId()));
                        xml.writeEndElement();
                        xml.writeStartElement("username");
                        xml.writeCharacters(lecturer.getUsername());
                        xml.writeEndElement();
                        xml.writeStartElement("faculty");
                        xml.writeCharacters(lecturer.getFaculty());
                        xml.writeEndElement();
                        xml.writeStartElement("name");
                        xml.writeCharacters(lecturer.getName());
                        xml.writeEndElement();
                        xml.writeStartElement("firstname");
                        xml.writeCharacters(lecturer.getFirstname());
                        xml.writeEndElement();
                        xml.writeEndElement(); // lecturer
                    }
                }
                xml.writeEndElement(); // lecturers

                // Write courses
                for (var course : courses) {
                    xml.writeStartElement("course");

                    xml.writeStartElement("faculty");
                    xml.writeCharacters(course.getFaculty());
                    xml.writeEndElement();

                    xml.writeStartElement("id");
                    xml.writeCharacters(String.valueOf(course.getId()));
                    xml.writeEndElement();

                    // Write lecturer details
                    xml.writeStartElement("lecturer");
                    var lecturer = allLecturers.stream()
                            .filter(l -> l.getId() == course.getLecturerId())
                            .findFirst()
                            .orElse(null);
                    if (lecturer != null) {
                        xml.writeStartElement("id");
                        xml.writeCharacters(String.valueOf(lecturer.getId()));
                        xml.writeEndElement();
                        xml.writeStartElement("username");
                        xml.writeCharacters(lecturer.getUsername());
                        xml.writeEndElement();
                        xml.writeStartElement("faculty");
                        xml.writeCharacters(lecturer.getFaculty());
                        xml.writeEndElement();
                        xml.writeStartElement("name");
                        xml.writeCharacters(lecturer.getName());
                        xml.writeEndElement();
                        xml.writeStartElement("firstname");
                        xml.writeCharacters(lecturer.getFirstname());
                        xml.writeEndElement();
                    }
                    xml.writeEndElement(); // lecturer

                    xml.writeStartElement("max_students");
                    xml.writeCharacters(String.valueOf(course.getMaxStudents()));
                    xml.writeEndElement();

                    xml.writeStartElement("name");
                    xml.writeCharacters(course.getName());
                    xml.writeEndElement();

                    // Write semester details
                    var semester = course.getSemester();
                    xml.writeStartElement("semester");
                    if (semester != null) {
                        xml.writeStartElement("id");
                        xml.writeCharacters(String.valueOf(semester.getId()));
                        xml.writeEndElement();
                        xml.writeStartElement("name");
                        xml.writeCharacters(semester.getName());
                        xml.writeEndElement();
                        xml.writeStartElement("start");
                        xml.writeCharacters(semester.getStart().toString());
                        xml.writeEndElement();
                        xml.writeStartElement("end");
                        xml.writeCharacters(semester.getEnd().toString());
                        xml.writeEndElement();
                    }
                    xml.writeEndElement(); // semester

                    // Write exams
                    xml.writeStartElement("exams");
                    var courseExams = allExams.stream()
                            .filter(e -> e.getCourseId() == course.getId())
                            .toList();
                    for (var exam : courseExams) {
                        xml.writeStartElement("exam");
                        xml.writeStartElement("id");
                        xml.writeCharacters(String.valueOf(exam.getId()));
                        xml.writeEndElement();
                        xml.writeStartElement("date");
                        xml.writeCharacters(exam.getDate().toString());
                        xml.writeEndElement();
                        xml.writeStartElement("is_online");
                        xml.writeCharacters(String.valueOf(exam.isOnline()));
                        xml.writeEndElement();
                        xml.writeStartElement("is_written");
                        xml.writeCharacters(String.valueOf(exam.isWritten()));
                        xml.writeEndElement();
                        xml.writeStartElement("room_or_link");
                        xml.writeCharacters(exam.getRoomOrLink());
                        xml.writeEndElement();
                        xml.writeEndElement(); // exam
                    }
                    xml.writeEndElement(); // exams

                    xml.writeEndElement(); // course
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

        try {
            // Query for all courses with lecturer information
            var courses = db.getAllCourses();
            var lecturers = db.getAllLecturers();
            var semesters = db.getAllSemesters();

            StringBuilder message = new StringBuilder();

            // Group courses by semester
            for (var semester : semesters) {
                var semesterCourses = courses.stream()
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
        } catch (Exception e) {
            throw new IOException("Failed to process courses: " + e.getMessage(), e);
        }
    }

    @Override
    public void destroy() {
        // Don't close the DB here as it's shared
    }
}
