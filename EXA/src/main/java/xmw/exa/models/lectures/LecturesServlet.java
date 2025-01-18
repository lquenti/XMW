package xmw.exa.models.lectures;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.DB;
import xmw.exa.models.semesters.Semester;
import xmw.exa.util.Config;
import xmw.flush.*;

@WebServlet(name = "lectures", value = "/lectures")
public class LecturesServlet extends HttpServlet {
    private String name;
    private DB db;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void init() {
        name = "Lectures";
        db = DB.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getServletPath();
        if (pathInfo.equals("/lectures/all")) {
            String queryString = request.getQueryString();
            response.sendRedirect(Config.BASE_URL + "/lectures" + (queryString != null ? "?" + queryString : ""));
            return;
        }

        // Check format parameter
        boolean isXmlFormat = "xml".equals(request.getParameter("format"));

        if (isXmlFormat) {
            try {
                // Get all lectures using the DB class
                var lectures = db.lectures().all();
                var courses = db.courses().all();

                // Build XML response using StringBuilder for better control
                StringBuilder xmlBuilder = new StringBuilder();
                xmlBuilder.append("<lectures>\n");

                for (var lecture : lectures) {
                    Course course = courses.stream()
                            .filter(c -> c.getId() == lecture.getCourseId())
                            .findFirst()
                            .orElse(null);

                    xmlBuilder.append("  <lecture>\n")
                            .append("    <id>").append(lecture.getId()).append("</id>\n")
                            .append("    <course>\n");

                    if (course != null) {
                        xmlBuilder.append("      <id>").append(course.getId()).append("</id>\n")
                                .append("      <name>").append(course.getName()).append("</name>\n")
                                .append("      <faculty>").append(course.getFaculty()).append("</faculty>\n");

                        // TODO: fix this
                        Lecturer lecturer = null; //course.getLecturer();
                        xmlBuilder.append("      <lecturer>\n");
                        // TODO: fix this
//                        if (lecturer != null) {
//                            xmlBuilder.append("        <id>").append(lecturer.getId()).append("</id>\n")
//                                    .append("        <username>").append(lecturer.getUsername()).append("</username>\n")
//                                    .append("        <name>").append(lecturer.getName()).append("</name>\n")
//                                    .append("        <firstname>").append(lecturer.getFirstname())
//                                    .append("</firstname>\n");
//                        }
                        xmlBuilder.append("      </lecturer>\n");

                        Semester semester = course.getSemester();
                        xmlBuilder.append("      <semester>\n");
                        if (semester != null) {
                            xmlBuilder.append("        <id>").append(semester.getId()).append("</id>\n")
                                    .append("        <name>").append(semester.getName()).append("</name>\n");
                        }
                        xmlBuilder.append("      </semester>\n");
                    }

                    xmlBuilder.append("    </course>\n")
                            .append("    <start>").append(lecture.getStart()).append("</start>\n")
                            .append("    <end>").append(lecture.getEnd()).append("</end>\n")
                            .append("    <room_or_link>").append(lecture.getRoomOrLink()).append("</room_or_link>\n")
                            .append("  </lecture>\n");
                }

                xmlBuilder.append("</lectures>");

                // Return XML response
                response.setContentType("application/xml");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                out.println(xmlBuilder.toString());
                out.flush();
                return;
            } catch (Exception e) {
                throw new IOException("Failed to generate lectures XML: " + e.getMessage(), e);
            }
        }

        // HTML response
        response.setContentType("text/html");
        request.setAttribute("name", this.name);

        // Get all lectures and courses
        var lectures = db.lectures().all();
        var courses = db.courses().all();
        var semesters = db.semesters().all();

        StringBuilder message = new StringBuilder();

        // Group lectures by semester through their courses
        for (var semester : semesters) {
            var semesterLectures = lectures.stream()
                    .filter(lecture -> {
                        var course = courses.stream()
                                .filter(c -> c.getId() == lecture.getCourseId())
                                .findFirst()
                                .orElse(null);
                        return course != null && course.getSemesterId() == semester.getId();
                    })
                    .sorted((l1, l2) -> l1.getStart().compareTo(l2.getStart()))
                    .toList();

            if (!semesterLectures.isEmpty()) {
                message.append("<h2>").append(semester.getName()).append("</h2><ul>");

                for (var lecture : semesterLectures) {
                    Course course = courses.stream()
                            .filter(c -> c.getId() == lecture.getCourseId())
                            .findFirst().orElse(null);

                    message.append("<li>")
                            .append("<a href=\"" + Config.BASE_URL + "/lectures/").append(lecture.getId())
                            .append("\">")
                            .append(lecture.getStart().format(DATE_FORMATTER))
                            .append(" - ")
                            .append(lecture.getRoomOrLink())
                            .append("</a>")
                            .append(" for ")
                            .append("<a href=\"" + Config.BASE_URL + "/courses/").append(course.getId()).append("\">")
                            .append(course.getName())
                            .append("</a>");
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

    @Override
    public void destroy() {
        // Don't close the DB here as it's shared
    }
}