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
import xmw.exa.db.Course;
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
        String pathInfo = request.getServletPath();
        if (pathInfo.equals("/courses/all")) {
            String queryString = request.getQueryString();
            response.sendRedirect(HtmlUtil.BASE_URL + "/courses" + (queryString != null ? "?" + queryString : ""));
            return;
        }

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
            // Query for all courses with lecturer information
            var courses = db.getAllCourses();
            var lecturers = db.getAllLecturers();
            var semesters = db.getAllSemesters();

            StringBuilder message = new StringBuilder();

            // Group courses by semester
            for (var semester : semesters) {
                var semesterCourses = courses.stream()
                        .filter(c -> c.getSemesterId() == semester.getId())
                        .toList();

                if (!semesterCourses.isEmpty()) {
                    message.append("<h2>").append(semester.getName()).append("</h2><ul>");

                    for (Course course : semesterCourses) {
                        // Find the lecturer for this course
                        String lecturerName = lecturers.stream()
                                .filter(l -> l.getId() == course.getLecturerId())
                                .map(l -> String.format("<a href='%s/lecturers/%s'>%s</a>",
                                        HtmlUtil.BASE_URL,
                                        l.getUsername(),
                                        l.getFullName()))
                                .findFirst()
                                .orElse("Unknown Lecturer");

                        message.append("<li>")
                                .append("<a href=\"").append(HtmlUtil.BASE_URL).append("/courses/")
                                .append(course.getId())
                                .append("\">")
                                .append(course.getName())
                                .append("</a>")
                                .append(" - Faculty: ")
                                .append(course.getFaculty())
                                .append(" (Max Students: ")
                                .append(course.getMaxStudents())
                                .append(")")
                                .append(" - Held by: ")
                                .append(lecturerName)
                                .append("</li>");
                    }
                    message.append("</ul>");
                }
            }

            request.setAttribute("message", message.toString());

            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/collection.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            throw new IOException("Failed to process courses: " + e.getMessage(), e);
        }
    }

    @Override
    public void destroy() {
        // Don't close the DB here as it's shared
    }
}
