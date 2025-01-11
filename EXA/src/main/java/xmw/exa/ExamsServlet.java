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

        try {
            // Query for all exams with formatted output
            String query = String.format(
                    "for $exam in collection('%s/exams.xml')/Exams/Exam " +
                            "order by xs:dateTime($exam/date) " +
                            "return element exam { " +
                            "  $exam/id, " +
                            "  $exam/course_id, " +
                            "  $exam/date, " +
                            "  $exam/is_online, " +
                            "  $exam/is_written, " +
                            "  $exam/room_or_link " +
                            "}",
                    "exa");

            String result = new XQuery(query).execute(db.getContext());

            StringBuilder message = new StringBuilder("<ul>");
            String[] examElements = result.split("</exam>");

            for (String element : examElements) {
                if (element.trim().isEmpty())
                    continue;

                String id = extractValue(element, "id");
                String date = extractValue(element, "date");
                String location = extractValue(element, "room_or_link");
                boolean isOnline = "1".equals(extractValue(element, "is_online"));
                boolean isWritten = "1".equals(extractValue(element, "is_written"));

                message.append("<li>")
                        .append("<a href=\"" + HtmlUtil.BASE_URL + "/exams/").append(id).append("\">")
                        .append("Exam ").append(id)
                        .append(" (").append(date).append(")")
                        .append("</a>")
                        .append(" - ")
                        .append(isOnline ? "Online" : "On-site")
                        .append(", ")
                        .append(isWritten ? "Written" : "Oral")
                        .append(" @ ")
                        .append(location)
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
            throw new IOException("Failed to query exams: " + e.getMessage(), e);
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