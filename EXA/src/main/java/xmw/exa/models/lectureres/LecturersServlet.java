package xmw.exa.models.Lecturers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.basex.core.BaseXException;
import org.basex.core.cmd.XQuery;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.DB;
import xmw.exa.util.Config;

@WebServlet(name = "lecturers", value = "/lecturers")
public class LecturersServlet extends HttpServlet {
    private String name;
    private DB db;
    private static final String DB_NAME = "exa";

    @Override
    public void init() {
        name = "Lecturers";
        db = DB.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Check format parameter
        boolean isXmlFormat = "xml".equals(request.getParameter("format"));

        if (isXmlFormat) {
            try {
                // Query for the complete lecturers XML with proper indentation and snake case
                String query = String.format(
                        "let $lecturers := /root/Lecturers " +
                                "return element lecturers { " +
                                "  for $l in $lecturers/Lecturer " +
                                "  return element lecturer { " +
                                "    attribute id { $l/id/text() }, " +
                                "    attribute username { $l/@username }, " +
                                "    element faculty { if ($l/faculty/@null = 'true') then '' else $l/faculty/text() }, "
                                +
                                "    element first_name { $l/firstname/text() }, " +
                                "    element last_name { $l/name/text() } " +
                                "  } " +
                                "}",
                        DB_NAME);

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
                throw new IOException("Failed to query lecturers: " + e.getMessage(), e);
            }
        }

        // HTML response (existing code)
        response.setContentType("text/html");
        request.setAttribute("name", this.name);

        List<Lecturer> lecturers = db.lecturers().all();
        StringBuilder message = new StringBuilder("<ul>");
        for (Lecturer lecturer : lecturers) {
            message
                    .append("<li>")
                    .append("<a href=\"" + Config.BASE_URL + "/lecturers/")
                    .append(lecturer.getUsername())
                    .append("\">")
                    .append(lecturer.getFirstname()).append(" ").append(lecturer.getName())
                    .append("</a>")
                    .append("</li>");
        }
        message.append("</ul>");

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
