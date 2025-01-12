package xmw.exa.models.lectureres;

import java.io.IOException;
import java.io.PrintWriter;

import org.basex.core.BaseXException;
import org.basex.core.cmd.XQuery;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.DB;
import xmw.exa.util.Config;

@WebServlet(name = "lecturer", urlPatterns = "/lecturers/*")
public class LecturerServlet extends HttpServlet {
    private DB db;

    @Override
    public void init() {
        db = DB.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect("/lecturers");
            return;
        }

        // Extract username from path (remove leading slash)
        String username = pathInfo.substring(1);

        // Check format parameter
        boolean isXmlFormat = "xml".equals(request.getParameter("format"));

        try {
            // Query for the specific lecturer's XML with proper indentation
            String query = String.format(
                    "let $lecturer := collection('%s/lecturers.xml')/Lectureres/Lecturer[@username = '%s'] " +
                            "return if ($lecturer) then " +
                            "  serialize($lecturer, map { 'method': 'xml', 'indent': 'yes' }) " +
                            "else ()",
                    "exa", username.replace("'", "''"));

            String result = new XQuery(query).execute(db.getContext());

            if (result.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Lecturer not found");
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
                Lecturer lecturer = getLecturer(username);

                if (lecturer == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Lecturer not found");
                    return;
                }

                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head><title>Lecturer Details</title></head>");
                out.println("<body>");
                out.println("<h1>Lecturer Details</h1>");
                out.println("<div class='lecturer-details'>");
                out.println("<p><strong>ID:</strong> " + lecturer.getId() + "</p>");
                out.println("<p><strong>Username:</strong> " + lecturer.getUsername() + "</p>");
                out.println("<p><strong>Name:</strong> "
                        + lecturer.getFullName() + "</p>");
                out.println("<p><strong>Faculty:</strong> "
                        + (lecturer.getFaculty() != null ? lecturer.getFaculty() : "No Faculty") + "</p>");
                out.println("</div>");
                out.println("<p><a href='" + Config.BASE_URL + "/lecturers'>Back to Lecturers List</a></p>");
                out.println("<p><small>View as: <a href='?format=xml'>XML</a></small></p>");
                out.println("</body>");
                out.println("</html>");
                out.flush();
            }

        } catch (BaseXException e) {
            throw new IOException("Failed to query lecturer: " + e.getMessage(), e);
        }
    }

    private Lecturer getLecturer(String username) {
        return db.getAllLecturers().stream()
                .filter(l -> l.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void destroy() {
        // Don't close the DB here as it's shared
    }
}