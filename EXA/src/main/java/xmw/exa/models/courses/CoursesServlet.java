package xmw.exa.models.courses;

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
import xmw.exa.db.DB;
import xmw.exa.models.exams.Exam;
import xmw.exa.models.lecturers.LecturerOld;
import xmw.exa.models.lectures.Lecture;
import xmw.exa.models.semesters.Semester;
import xmw.exa.util.Config;

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
            response.sendRedirect(Config.BASE_URL + "/courses" + (queryString != null ? "?" + queryString : ""));
            return;
        }

        // Check format parameter
        boolean isXmlFormat = "xml".equals(request.getParameter("format"));

        if (isXmlFormat) {
            try {
                // Get all data
                List<Course> courses = db.courses().all();
                List<LecturerOld> allLecturerOlds = db.lecturers().all();
                List<Exam> allExams = db.exams().all();

                // Create XML writer
                StringWriter stringWriter = new StringWriter();
                XMLOutputFactory factory = XMLOutputFactory.newInstance();
                XMLStreamWriter xml = factory.createXMLStreamWriter(stringWriter);

                // Start document
                xml.writeStartDocument("UTF-8", "1.0");
                xml.writeStartElement("courses");

                // Write courses
                for (Course course : courses) {
                    writeCourseToXml(xml, course, allLecturerOlds, allExams);
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
        List<Course> courses = db.courses().all();
        List<LecturerOld> lecturerOlds = db.lecturers().all();
        List<Semester> semesters = db.semesters().all();

        StringBuilder message = new StringBuilder();

        // Group courses by semester
        for (Semester semester : semesters) {
            List<Course> semesterCourses = courses.stream()
                    .filter(c -> c.getSemesterId() == semester.getId())
                    .toList();

            if (!semesterCourses.isEmpty()) {
                message.append("<h2 class=\"text-xl\">").append(semester.getName())
                        .append("</h2><ul class=\"list-disc\">");
                var atagstyles = "class=\"text-blue-600 hover:text-blue-800 flex items-center gap-2\"";

                for (Course course : semesterCourses) {
                    // Find the lecturer for this course
                    String lecturerName = lecturerOlds.stream()
                            .filter(l -> l.getId() == course.getLecturerId())
                            .map(l -> String.format("<a href='%s/lecturers/%s'>%s</a>",
                                    Config.BASE_URL,
                                    l.getUsername(),
                                    l.getFullName()))
                            .findFirst()
                            .orElse("Unknown Lecturer");

                    message.append("<li>")
                            .append("<a href=\"").append(Config.BASE_URL).append("/courses/")
                            .append(course.getId()).append("\" ").append(atagstyles).append(">")
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

    private void writeCourseToXml(XMLStreamWriter xml, Course course, List<LecturerOld> allLecturerOlds, List<Exam> allExams)
            throws Exception {
        xml.writeStartElement("course");
        xml.writeAttribute("id", String.valueOf(course.getId()));
        xml.writeAttribute("semester_id", String.valueOf(course.getSemesterId()));

        writeSimpleElement(xml, "faculty", course.getFaculty());
        writeLecturerElement(xml, course.getLecturerId(), allLecturerOlds);
        writeSimpleElement(xml, "max_students", String.valueOf(course.getMaxStudents()));
        writeSimpleElement(xml, "name", course.getName());
        writeSemesterElement(xml, course.getSemester());

        // Get lectures for this course
        List<Lecture> lectures = course.getLectures();
        writeLecturesElement(xml, lectures);

        writeExamsElement(xml, course.getId(), allExams);

        xml.writeEndElement(); // course
    }

    private void writeSimpleElement(XMLStreamWriter xml, String elementName, String value) throws XMLStreamException {
        xml.writeStartElement(elementName);
        xml.writeCharacters(value);
        xml.writeEndElement();
    }

    private void writeLecturerElement(XMLStreamWriter xml, long lecturerId, List<LecturerOld> allLecturerOlds)
            throws XMLStreamException {
        xml.writeStartElement("lecturer");
        LecturerOld lecturerOld = allLecturerOlds.stream()
                .filter(l -> l.getId() == lecturerId)
                .findFirst()
                .orElse(null);
        if (lecturerOld != null) {
            xml.writeAttribute("id", String.valueOf(lecturerOld.getId()));
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

    private void writeLecturesElement(XMLStreamWriter xml, List<Lecture> lectures) throws Exception {
        xml.writeStartElement("lectures");
        for (Lecture lecture : lectures) {
            xml.writeStartElement("lecture");
            writeSimpleElement(xml, "id", String.valueOf(lecture.getId()));
            writeSimpleElement(xml, "start", lecture.getStart().toString());
            writeSimpleElement(xml, "end", lecture.getEnd().toString());
            writeSimpleElement(xml, "room_or_link", lecture.getRoomOrLink());
            xml.writeEndElement(); // lecture
        }
        xml.writeEndElement(); // lectures
    }

    @Override
    public void destroy() {
        // Don't close the DB here as it's shared
    }
}
