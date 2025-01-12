package xmw.exa.models.exams;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.DB;
import xmw.exa.models.courses.Course;
import xmw.exa.models.lectureres.Lecturer;
import xmw.exa.models.semesters.Semester;
import xmw.exa.util.Config;

@WebServlet(name = "exams", value = "/exams")
public class ExamsServlet extends HttpServlet {
    private String name;
    private DB db;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void init() {
        name = "Exams";
        db = DB.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getServletPath();
        if (pathInfo.equals("/exams/all")) {
            String queryString = request.getQueryString();
            response.sendRedirect(Config.BASE_URL + "/exams" + (queryString != null ? "?" + queryString : ""));
            return;
        }

        // Check format parameter
        boolean isXmlFormat = "xml".equals(request.getParameter("format"));

        if (isXmlFormat) {
            try {
                // Get all exams using the DB class
                var exams = db.getAllExams();

                // Build XML response using StringBuilder for better control
                StringBuilder xmlBuilder = new StringBuilder();
                xmlBuilder.append("<exams>\n");

                for (var exam : exams) {
                    var course = exam.getCourse();

                    xmlBuilder.append("  <exam>\n")
                            .append("    <id>").append(exam.getId()).append("</id>\n")
                            .append("    <course>\n");

                    if (course != null) {
                        xmlBuilder.append("      <id>").append(course.getId()).append("</id>\n")
                                .append("      <name>").append(course.getName()).append("</name>\n")
                                .append("      <faculty>").append(course.getFaculty()).append("</faculty>\n");

                        var lecturer = course.getLecturer();
                        xmlBuilder.append("      <lecturer>\n");
                        if (lecturer != null) {
                            xmlBuilder.append("        <id>").append(lecturer.getId()).append("</id>\n")
                                    .append("        <username>").append(lecturer.getUsername()).append("</username>\n")
                                    .append("        <name>").append(lecturer.getName()).append("</name>\n")
                                    .append("        <firstname>").append(lecturer.getFirstname())
                                    .append("</firstname>\n");
                        }
                        xmlBuilder.append("      </lecturer>\n");

                        var semester = course.getSemester();
                        xmlBuilder.append("      <semester>\n");
                        if (semester != null) {
                            xmlBuilder.append("        <id>").append(semester.getId()).append("</id>\n")
                                    .append("        <name>").append(semester.getName()).append("</name>\n");
                        }
                        xmlBuilder.append("      </semester>\n");
                    }

                    xmlBuilder.append("    </course>\n")
                            .append("    <date>").append(exam.getDate()).append("</date>\n")
                            .append("    <is_online>").append(exam.isOnline()).append("</is_online>\n")
                            .append("    <is_written>").append(exam.isWritten()).append("</is_written>\n")
                            .append("    <room_or_link>").append(exam.getRoomOrLink()).append("</room_or_link>\n")
                            .append("  </exam>\n");
                }

                xmlBuilder.append("</exams>");

                // Return XML response
                response.setContentType("application/xml");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                out.println(xmlBuilder.toString());
                out.flush();
                return;
            } catch (Exception e) {
                throw new IOException("Failed to generate exams XML: " + e.getMessage(), e);
            }
        }

        // HTML response
        response.setContentType("text/html");
        request.setAttribute("name", this.name);

        // Get all exams and courses
        var exams = db.getAllExams();
        var courses = db.getAllCourses();
        var semesters = db.getAllSemesters();

        StringBuilder message = new StringBuilder();

        // Group exams by semester through their courses
        for (Semester semester : semesters) {
            var semesterExams = exams.stream()
                    .filter(e -> {
                        var course = courses.stream()
                                .filter(c -> c.getId() == e.getCourseId())
                                .findFirst()
                                .orElse(null);
                        return course != null && course.getSemesterId() == semester.getId();
                    })
                    .sorted((e1, e2) -> e1.getDate().compareTo(e2.getDate()))
                    .toList();

            if (!semesterExams.isEmpty()) {
                message.append("<h2>").append(semester.getName()).append("</h2><ul>");

                for (Exam exam : semesterExams) {
                    Course course = courses.stream()
                            .filter(c -> c.getId() == exam.getCourseId())
                            .findFirst().orElse(null);

                    // Get lecturer information
                    Lecturer lecturer = course != null ? course.getLecturer() : null;

                    message.append("<li>")
                            .append("<a href=\"" + Config.BASE_URL + "/exams/").append(exam.getId()).append("\">")
                            .append(exam.getDate().format(DATE_FORMATTER))
                            .append(" - ")
                            .append(exam.getRoomOrLink())
                            .append("</a>")
                            .append(" for ")
                            .append("<a href=\"" + Config.BASE_URL + "/courses/").append(course.getId()).append("\">")
                            .append(course.getName())
                            .append("</a>")
                            .append(" by ")
                            .append(lecturer != null ? String.format("<a href='%s/lecturers/%s'>%s</a>",
                                    Config.BASE_URL,
                                    lecturer.getUsername(),
                                    lecturer.getFullName())
                                    : "Unknown Lecturer");
                    message.append("</li>");
                }
                message.append("</ul>");
            }
        }

        request.setAttribute("message", message.toString());

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/collection.jsp");
        try {
            dispatcher.forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extractValue(String xml, String tag) {
        String pattern = String.format("<%s>([^<]*)</%s>", tag, tag);
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(xml);
        return m.find() ? m.group(1).trim() : "";
    }

    @Override
    public void destroy() {
        // Don't close the DB here as it's shared
    }
}