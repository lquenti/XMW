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
        // Check format parameter
        boolean isXmlFormat = "xml".equals(request.getParameter("format"));

        if (isXmlFormat) {
            try {
                // Query for the complete exams XML with proper indentation
                String query = String.format(
                        "let $exams := collection('%s/exams.xml')/Exams " +
                                "return serialize($exams, map { 'method': 'xml', 'indent': 'yes' })",
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
                throw new IOException("Failed to query exams: " + e.getMessage(), e);
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
        for (var semester : semesters) {
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

                for (var exam : semesterExams) {
                    Course course = courses.stream()
                            .filter(c -> c.getId() == exam.getCourseId())
                            .findFirst().orElse(null);

                    message.append("<li>")
                            .append("<a href=\"" + HtmlUtil.BASE_URL + "/exams/").append(exam.getId()).append("\">")
                            .append("Exam ").append(exam.getId())
                            .append("</a>")
                            .append(" on ")
                            .append(exam.getDate().format(DATE_FORMATTER))
                            .append(" for ")
                            .append("<a href=\"" + HtmlUtil.BASE_URL + "/courses/").append(course.getId()).append("\">")
                            .append(course.getName())
                            .append("</a>");
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