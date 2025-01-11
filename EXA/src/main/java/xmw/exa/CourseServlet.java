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
import xmw.exa.db.DB;
import xmw.exa.util.HtmlUtil;

@WebServlet(name = "course", urlPatterns = "/courses/*")
public class CourseServlet extends HttpServlet {
    private String name;
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
            response.sendRedirect("/courses");
            return;
        }

        // Remove leading slash
        String courseId = pathInfo.substring(1);

        // Handle /all endpoint
        if (courseId.equals("all")) {
            String queryString = request.getQueryString();
            response.sendRedirect(HtmlUtil.BASE_URL + "/courses" + (queryString != null ? "?" + queryString : ""));
            return;
        }

        // Validate course ID
        if (!courseId.matches("\\d+")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
            return;
        }

        // Check format parameter
        boolean isXmlFormat = "xml".equals(request.getParameter("format"));

        try {
            // Query for the specific course
            String query = String.format(
                    "let $course := collection('%s/courses.xml')/Courses/Course[id = %s] " +
                            "return if ($course) then " +
                            "  serialize(element Courses { $course }, map { 'method': 'xml', 'indent': 'yes' }) " +
                            "else ()",
                    "exa", courseId);

            String result = new XQuery(query).execute(db.getContext());

            if (result.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Course not found");
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

                int numCourseId = Integer.parseInt(courseId);
                var course = db.getAllCourses().stream()
                        .filter(c -> c.getId() == numCourseId)
                        .findFirst()
                        .orElse(null);

                if (course == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Course not found");
                    return;
                }

                // Find the lecturer
                var lecturer = db.getAllLecturers().stream()
                        .filter(l -> l.getId() == course.getLecturerId())
                        .findFirst()
                        .orElse(null);

                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head><title>Course Details</title></head>");
                out.println("<body>");
                out.println("<h1>Course Details</h1>");
                out.println("<div class='course-details'>");
                out.println("<p><strong>ID:</strong> " + course.getId() + "</p>");
                out.println("<p><strong>Name:</strong> " + course.getName() + "</p>");
                out.println("<p><strong>Faculty:</strong> " + course.getFaculty() + "</p>");
                out.println("<p><strong>Lecturer:</strong> " +
                        (lecturer != null ? String.format("<a href='%s/lecturers/%s'>%s</a>",
                                HtmlUtil.BASE_URL,
                                lecturer.getUsername(),
                                lecturer.getFullName())
                                : "Unknown Lecturer")
                        + "</p>");
                out.println("<p><strong>Max Students:</strong> " + course.getMaxStudents() + "</p>");
                out.println("<p><strong>Semester ID:</strong> " + course.getSemesterId() + "</p>");

                // Add lectures section
                out.println("<h2>Lectures</h2>");
                var lectures = course.getLectures();
                if (!lectures.isEmpty()) {
                    out.println("<ul>");
                    for (var lecture : lectures) {
                        out.println("<li>");
                        out.println(String.format("<a href='%s/lectures/%d'>%s - %s</a>",
                                HtmlUtil.BASE_URL,
                                lecture.getId(),
                                lecture.getStart().format(DATE_FORMATTER),
                                lecture.getRoomOrLink()));
                        out.println("</li>");
                    }
                    out.println("</ul>");
                } else {
                    out.println("<p>No lectures scheduled</p>");
                }

                // Add exams section
                out.println("<h2>Exams</h2>");
                var exams = course.getExams().stream()
                        .sorted((e1, e2) -> e1.getDate().compareTo(e2.getDate()))
                        .toList();
                if (!exams.isEmpty()) {
                    out.println("<ul>");
                    for (var exam : exams) {
                        out.println("<li>");
                        out.println(String.format("<a href='%s/exams/%d'>%s - %s</a> (%s%s)",
                                HtmlUtil.BASE_URL,
                                exam.getId(),
                                exam.getDate().format(DATE_FORMATTER),
                                exam.getRoomOrLink(),
                                exam.isOnline() ? "Online" : "In-person",
                                exam.isWritten() ? ", Written" : ""));
                        out.println("</li>");
                    }
                    out.println("</ul>");
                } else {
                    out.println("<p>No exams scheduled</p>");
                }

                out.println("</div>");
                out.println("<p><a href='" + HtmlUtil.BASE_URL + "/courses'>Back to Courses List</a></p>");
                out.println("<p><small>View as: <a href='?format=xml'>XML</a></small></p>");
                out.println("</body>");
                out.println("</html>");
                out.flush();
            }

        } catch (BaseXException e) {
            throw new IOException("Failed to query course: " + e.getMessage(), e);
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