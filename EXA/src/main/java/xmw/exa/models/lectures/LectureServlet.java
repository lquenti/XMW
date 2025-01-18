package xmw.exa.models.lectures;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

import org.basex.core.BaseXException;
import org.basex.core.cmd.XQuery;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.DB;
import xmw.exa.util.Config;
import xmw.exa.util.ExaServlet;

@WebServlet(name = "lecture", urlPatterns = "/lectures/*")
public class LectureServlet extends ExaServlet {
    private DB db;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void init() {
        db = DB.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var lectureData = db.lectures().get("lecture-1");
        var responseData = DB.marshal(lectureData);
        PrintWriter out = response.getWriter();
        out.println(responseData);
        out.flush();
        return;
//        String pathInfo = request.getPathInfo();
//        if (pathInfo == null || pathInfo.equals("/")) {
//            response.sendRedirect("/lectures");
//            return;
//        }
//
//        // Remove leading slash
//        String lectureId = pathInfo.substring(1);
//
//        // Handle /all endpoint
//        if (lectureId.equals("all")) {
//            String queryString = request.getQueryString();
//            response.sendRedirect(Config.BASE_URL + "/lectures" + (queryString != null ? "?" + queryString : ""));
//            return;
//        }
//
//        // Validate lecture ID
//        if (!lectureId.matches("\\d+")) {
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid lecture ID");
//            return;
//        }
//
//        // Check format parameter
//        boolean isXmlFormat = "xml".equals(request.getParameter("format"));
//
//        try {
//            // Query for the specific lecture
//            String query = String.format(
//                    "let $lecture := /root/Lectures/Lecture[id = %s] " +
//                            "return if ($lecture) then " +
//                            "  serialize(element lectures { " +
//                            "    element lecture { " +
//                            "      element id { $lecture/id/text() }, " +
//                            "      element course_id { $lecture/course_id/text() }, " +
//                            "      element start { $lecture/start/text() }, " +
//                            "      element end { $lecture/end/text() }, " +
//                            "      element room_or_link { $lecture/room_or_link/text() } " +
//                            "    } " +
//                            "  }, map { 'method': 'xml', 'indent': 'yes' }) " +
//                            "else ()",
//                    lectureId);
//
//            String result = new XQuery(query).execute(db.getContext());
//
//            if (result.trim().isEmpty()) {
//                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Lecture not found");
//                return;
//            }
//
//            if (isXmlFormat) {
//                // Return XML response
//                response.setContentType("application/xml");
//                response.setCharacterEncoding("UTF-8");
//                PrintWriter out = response.getWriter();
//                out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//                out.println(result);
//                out.flush();
//            } else {
//                // Return HTML response
//                response.setContentType("text/html");
//                response.setCharacterEncoding("UTF-8");
//                PrintWriter out = response.getWriter();
//
//                int numLectureId = Integer.parseInt(lectureId);
//                var lecture = db.lectures().all().stream()
//                        .filter(l -> l.getId() == numLectureId)
//                        .findFirst()
//                        .orElse(null);
//
//                if (lecture == null) {
//                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Lecture not found");
//                    return;
//                }
//
//                // Find the associated course
//                Course course = lecture.getCourse();
//
//                out.println("<!DOCTYPE html>");
//                out.println("<html>");
//                out.println("<head><title>Lecture Details</title></head>");
//                out.println("<body>");
//                out.println("<h1>Lecture Details</h1>");
//                out.println("<div class='lecture-details'>");
//                out.println("<p><strong>ID:</strong> " + lecture.getId() + "</p>");
//                out.println("<p><strong>Course:</strong> " +
//                        (course != null ? String.format("<a href='%s/courses/%d'>%s</a>",
//                                Config.BASE_URL,
//                                course.getId(),
//                                course.getName())
//                                : "Unknown Course")
//                        + "</p>");
//                out.println("<p><strong>Start:</strong> " + lecture.getStart().format(DATE_FORMATTER) + "</p>");
//                out.println("<p><strong>End:</strong> " + lecture.getEnd().format(DATE_FORMATTER) + "</p>");
//                out.println("<p><strong>Room/Link:</strong> " + lecture.getRoomOrLink() + "</p>");
//                out.println("</div>");
//                out.println(
//                        "<p><a href='" + Config.BASE_URL + "/courses/" + course.getId() + "'>Back to Course</a></p>");
//                out.println("<p><small>View as: <a href='?format=xml'>XML</a></small></p>");
//                out.println("</body>");
//                out.println("</html>");
//                out.flush();
//            }
//
//        } catch (BaseXException e) {
//            throw new IOException("Failed to query lecture: " + e.getMessage(), e);
//        }
    }
}
