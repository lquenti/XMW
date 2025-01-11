package xmw.exa;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

import org.basex.core.BaseXException;
import org.basex.core.cmd.XQuery;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.Course;
import xmw.exa.db.DB;
import xmw.exa.util.HtmlUtil;

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
            response.sendRedirect(HtmlUtil.BASE_URL + "/lectures" + (queryString != null ? "?" + queryString : ""));
            return;
        }

        // Check format parameter
        boolean isXmlFormat = "xml".equals(request.getParameter("format"));

        if (isXmlFormat) {
            try {
                // Query for the complete lectures XML with proper indentation
                String query = String.format(
                        "let $lectures := collection('%s/lectures.xml')/Lectures " +
                                "return serialize(element lectures { " +
                                "  for $l in $lectures/Lecture " +
                                "  return element lecture { " +
                                "    element id { $l/id/text() }, " +
                                "    element course_id { $l/course_id/text() }, " +
                                "    element start { $l/start/text() }, " +
                                "    element end { $l/end/text() }, " +
                                "    element room_or_link { $l/room_or_link/text() } " +
                                "  } " +
                                "}, map { 'method': 'xml', 'indent': 'yes' })",
                        "exa");

                String result = new XQuery(query).execute(db.getContext());

                // Return XML response
                response.setContentType("application/xml");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                out.println(result);
                out.flush();
                return;
            } catch (BaseXException e) {
                throw new IOException("Failed to query lectures: " + e.getMessage(), e);
            }
        }

        // HTML response
        response.setContentType("text/html");
        request.setAttribute("name", this.name);

        // Get all lectures and courses
        var lectures = db.getAllLectures();
        var courses = db.getAllCourses();
        var semesters = db.getAllSemesters();

        StringBuilder message = new StringBuilder();

        // Group lectures by semester through their courses
        for (var semester : semesters) {
            var semesterLectures = lectures.stream()
                    .filter(l -> {
                        var course = courses.stream()
                                .filter(c -> c.getId() == l.getCourseId())
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
                            .append("<a href=\"" + HtmlUtil.BASE_URL + "/lectures/").append(lecture.getId())
                            .append("\">")
                            .append(lecture.getStart().format(DATE_FORMATTER))
                            .append(" - ")
                            .append(lecture.getRoomOrLink())
                            .append("</a>")
                            .append(" for ")
                            .append("<a href=\"" + HtmlUtil.BASE_URL + "/courses/").append(course.getId()).append("\">")
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