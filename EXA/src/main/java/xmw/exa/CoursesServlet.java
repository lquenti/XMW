package xmw.exa;

import java.io.IOException;
import java.io.PrintWriter;

import org.basex.core.BaseXException;
import org.basex.core.cmd.XQuery;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        // Check format parameter
        boolean isXmlFormat = "xml".equals(request.getParameter("format"));

        if (isXmlFormat) {
            try {
                // Query for the complete courses XML with proper indentation
                String query = String.format(
                        "let $courses := collection('%s/courses.xml')/Course " +
                                "return serialize($courses, map { 'method': 'xml', 'indent': 'yes' })",
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
                throw new IOException("Failed to query courses: " + e.getMessage(), e);
            }
        }

        // HTML response
        response.setContentType("text/html");
        request.setAttribute("name", this.name);

        try {
            // Query for all courses with formatted output
            String query = String.format(
                    "for $course in collection('%s/courses.xml')/Course/Course " +
                            "order by xs:integer($course/id) " +
                            "return element course { " +
                            "  $course/id, " +
                            "  $course/n, " +
                            "  $course/faculty, " +
                            "  $course/lecturer_id, " +
                            "  $course/max_students, " +
                            "  $course/semester_id " +
                            "}",
                    "exa");

            String result = new XQuery(query).execute(db.getContext());

            StringBuilder message = new StringBuilder("<ul>");
            String[] courseElements = result.split("</course>");

            for (String element : courseElements) {
                if (element.trim().isEmpty())
                    continue;

                String id = extractValue(element, "id");
                String name = extractValue(element, "n");
                String faculty = extractValue(element, "faculty");
                String maxStudents = extractValue(element, "max_students");

                message.append("<li>")
                        .append("<a href=\"" + HtmlUtil.BASE_URL + "/courses/").append(id).append("\">")
                        .append(name)
                        .append("</a>")
                        .append(" - Faculty: ").append(faculty)
                        .append(" (Max Students: ").append(maxStudents).append(")")
                        .append("</li>");
            }
            message.append("</ul>");
            message.append("<p><small>View as: <a href='?format=xml'>XML</a></small></p>");

            request.setAttribute("message", message.toString());

            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/collection.jsp");
            try {
                dispatcher.forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (BaseXException e) {
            throw new IOException("Failed to query courses: " + e.getMessage(), e);
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
