package xmw.exa;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

import org.basex.core.BaseXException;
import org.basex.core.cmd.XQuery;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.Course;
import xmw.exa.db.DB;
import xmw.exa.db.Lecturer;
import xmw.exa.util.HtmlUtil;

@WebServlet(name = "exam", urlPatterns = "/exams/*")
public class ExamServlet extends HttpServlet {
    private DB db;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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

                int numExamId = Integer.parseInt(examId);
                var exam = db.getAllExams().stream()
                        .filter(e -> numExamId == e.getId())
                        .findFirst()
                        .orElse(null);

                if (exam == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Exam not found");
                    return;
                }

                // Get associated course and lecturer
                Course course = exam.getCourse();
                Lecturer lecturer = course != null ? course.getLecturer() : null;

                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head><title>Exam Details</title></head>");
                out.println("<body>");
                out.println("<h1>Exam Details</h1>");
                out.println("<div class='exam-details'>");
                out.println("<p><strong>ID:</strong> " + exam.getId() + "</p>");
                out.println("<p><strong>Course:</strong> "
                        + (course != null ? String.format("<a href='%s/courses/%d'>%s</a>",
                                HtmlUtil.BASE_URL,
                                course.getId(),
                                course.getName())
                                : "Unknown Course")
                        + "</p>");
                out.println("<p><strong>Lecturer:</strong> "
                        + (lecturer != null ? String.format("<a href='%s/lecturers/%s'>%s</a>",
                                HtmlUtil.BASE_URL,
                                lecturer.getUsername(),
                                lecturer.getFullName())
                                : "Unknown Lecturer")
                        + "</p>");
                out.println("<p><strong>Date:</strong> " + exam.getDate().format(DATE_FORMATTER) + "</p>");

                boolean isOnline = exam.isOnline();
                boolean isWritten = exam.isWritten();
                String location = exam.getRoomOrLink();

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

    @Override
    public void destroy() {
        // Don't close the DB here as it's shared
    }
}
