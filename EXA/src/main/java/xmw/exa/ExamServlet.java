package xmw.exa;

import java.io.IOException;
import java.io.PrintWriter;

import org.basex.core.BaseXException;
import org.basex.core.cmd.XQuery;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.DB;
import xmw.exa.util.HtmlUtil;

@WebServlet(name = "exam", urlPatterns = "/exams/*")
public class ExamServlet extends HttpServlet {
    private DB db;

    @Override
    public void init() {
        db = DB.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect("/exams");
            return;
        }

        // Extract exam ID from path (remove leading slash)
        String examId = pathInfo.substring(1);
        if (!examId.matches("\\d+")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid exam ID");
            return;
        }

        // Check format parameter
        boolean isXmlFormat = "xml".equals(request.getParameter("format"));

        try {
            // Query for the specific exam
            String query = String.format(
                    "let $exam := collection('%s/exams.xml')/Exams/Exam[id = %s] " +
                            "return if ($exam) then " +
                            "  serialize($exam, map { 'method': 'xml', 'indent': 'yes' }) " +
                            "else ()",
                    "exa", examId);

            String result = new XQuery(query).execute(db.getContext());

            if (result.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Exam not found");
                return;
            }

            if (isXmlFormat) {
                // Return XML response
                response.setContentType("application/xml");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                out.println(result);
                out.flush();
            } else {
                // Return HTML response
                response.setContentType("text/html");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();

                // Query for a more readable format
                query = String.format(
                        "let $exam := collection('%s/exams.xml')/Exams/Exam[id = %s] " +
                                "return map { " +
                                "  'id': $exam/id/string(), " +
                                "  'course_id': $exam/course_id/string(), " +
                                "  'date': $exam/date/string(), " +
                                "  'is_online': $exam/is_online/string(), " +
                                "  'is_written': $exam/is_written/string(), " +
                                "  'room_or_link': $exam/room_or_link/string() " +
                                "}",
                        "exa", examId);

                result = new XQuery(query).execute(db.getContext());

                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head><title>Exam Details</title></head>");
                out.println("<body>");
                out.println("<h1>Exam Details</h1>");
                out.println("<div class='exam-details'>");
                out.println("<p><strong>ID:</strong> " + extractValue(result, "id") + "</p>");
                out.println("<p><strong>Course ID:</strong> " + extractValue(result, "course_id") + "</p>");
                out.println("<p><strong>Date:</strong> " + extractValue(result, "date") + "</p>");

                boolean isOnline = "1".equals(extractValue(result, "is_online"));
                boolean isWritten = "1".equals(extractValue(result, "is_written"));
                String location = extractValue(result, "room_or_link");

                out.println("<p><strong>Type:</strong> " + (isOnline ? "Online" : "On-site") + ", " +
                        (isWritten ? "Written" : "Oral") + "</p>");
                out.println("<p><strong>" + (isOnline ? "Link" : "Room") + ":</strong> " + location + "</p>");
                out.println("</div>");
                out.println("<p><a href='" + HtmlUtil.BASE_URL + "/exams'>Back to Exams List</a></p>");
                out.println("<p><small>View as: <a href='?format=xml'>XML</a></small></p>");
                out.println("</body>");
                out.println("</html>");
                out.flush();
            }

        } catch (BaseXException e) {
            throw new IOException("Failed to query exam: " + e.getMessage(), e);
        }
    }

    private String extractValue(String result, String key) {
        String pattern = "'" + key + "': '([^']*)'";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(result);
        return m.find() ? m.group(1) : "";
    }

    @Override
    public void destroy() {
        // Don't close the DB here as it's shared
    }
}