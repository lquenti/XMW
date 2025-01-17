package xmw.exa.models.exams;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.DB;
import xmw.exa.models.courses.Course;
import xmw.exa.models.courses.CourseRepository;
import xmw.exa.models.Lecturers.Lecturer;
import xmw.exa.models.semesters.Semester;
import xmw.exa.util.Config;

@WebServlet(name = "exams", value = "/exams")
@MultipartConfig
public class ExamsServlet extends HttpServlet {
    private String name;
    private DB db;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Override
    public void init() {
        name = "Exams";
        db = DB.getInstance();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Parse parameters
            int courseId = Integer.parseInt(request.getParameter("course_id"));
            String dateTimeStr = request.getParameter("date");
            boolean isOnline = request.getParameter("is_online") != null;
            boolean isWritten = request.getParameter("is_written") != null;
            String roomOrLink = request.getParameter("room_or_link");

            // Validate required fields
            if (dateTimeStr == null || roomOrLink == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required fields");
                return;
            }

            // Create new exam object
            Exam exam = new Exam();
            exam.setCourseId(courseId);
            exam.setDate(LocalDateTime.parse(dateTimeStr, DATE_FORMATTER));
            exam.setOnline(isOnline);
            exam.setWritten(isWritten);
            exam.setRoomOrLink(roomOrLink);

            // Create the exam in the database
            boolean success = db.exams().create(exam);

            if (success) {
                // Return success response
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.sendRedirect(Config.BASE_URL + "/exams");
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create exam");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID format");
        } catch (DateTimeParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid date format. Expected format: yyyy-MM-ddTHH:mm");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating exam: " + e.getMessage());
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getServletPath();
        if (pathInfo.equals("/exams/all")) {
            String queryString = request.getQueryString();
            response.sendRedirect(Config.BASE_URL + "/exams" + (queryString != null ? "?" + queryString : ""));
            return;
        }

        // Check format parameter
        boolean isXmlFormat = "xml".equals(request.getParameter("format"));

        if (isXmlFormat) {
            try {
                // Get all exams using the DB class
                var exams = db.exams().all();

                // Build XML response using StringBuilder for better control
                StringBuilder xmlBuilder = new StringBuilder();
                xmlBuilder.append("<exams>\n");

                for (var exam : exams) {
                    var course = exam.getCourse();

                    xmlBuilder.append("  <exam>\n")
                            .append("    <id>").append(exam.getId()).append("</id>\n")
                            .append("    <course>\n");

                    if (course != null) {
                        xmlBuilder.append("      <id>").append(course.getId()).append("</id>\n")
                                .append("      <name>").append(course.getName()).append("</name>\n")
                                .append("      <faculty>").append(course.getFaculty()).append("</faculty>\n");

                        var lecturer = course.getLecturer();
                        xmlBuilder.append("      <lecturer>\n");
                        if (lecturer != null) {
                            xmlBuilder.append("        <id>").append(lecturer.getId()).append("</id>\n")
                                    .append("        <username>").append(lecturer.getUsername()).append("</username>\n")
                                    .append("        <name>").append(lecturer.getName()).append("</name>\n")
                                    .append("        <firstname>").append(lecturer.getFirstname())
                                    .append("</firstname>\n");
                        }
                        xmlBuilder.append("      </lecturer>\n");

                        var semester = course.getSemester();
                        xmlBuilder.append("      <semester>\n");
                        if (semester != null) {
                            xmlBuilder.append("        <id>").append(semester.getId()).append("</id>\n")
                                    .append("        <name>").append(semester.getName()).append("</name>\n");
                        }
                        xmlBuilder.append("      </semester>\n");
                    }

                    xmlBuilder.append("    </course>\n")
                            .append("    <date>").append(exam.getDate()).append("</date>\n")
                            .append("    <is_online>").append(exam.isOnline()).append("</is_online>\n")
                            .append("    <is_written>").append(exam.isWritten()).append("</is_written>\n")
                            .append("    <room_or_link>").append(exam.getRoomOrLink()).append("</room_or_link>\n")
                            .append("  </exam>\n");
                }

                xmlBuilder.append("</exams>");

                // Return XML response
                response.setContentType("application/xml");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                out.println(xmlBuilder.toString());
                out.flush();
                return;
            } catch (Exception e) {
                throw new IOException("Failed to generate exams XML: " + e.getMessage(), e);
            }
        }

        // HTML response
        response.setContentType("text/html");
        request.setAttribute("name", this.name);
        request.setAttribute("courses", new CourseRepository(DB.getInstance().getContext()).all());

        // Get all exams and courses
        var exams = db.exams().all();
        var courses = db.courses().all();
        var semesters = db.semesters().all();

        StringBuilder message = new StringBuilder();

        // Group exams by semester through their courses
        for (Semester semester : semesters) {
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

                for (Exam exam : semesterExams) {
                    Course course = courses.stream()
                            .filter(c -> c.getId() == exam.getCourseId())
                            .findFirst().orElse(null);

                    // Get lecturer information
                    Lecturer lecturer = course != null ? course.getLecturer() : null;

                    message.append("<li>")
                            .append("<a href=\"" + Config.BASE_URL + "/exams/").append(exam.getId()).append("\">")
                            .append(exam.getDate().format(DATE_FORMATTER))
                            .append(" - ")
                            .append(exam.getRoomOrLink())
                            .append("</a>")
                            .append(" for ")
                            .append("<a href=\"" + Config.BASE_URL + "/courses/").append(course.getId()).append("\">")
                            .append(course.getName())
                            .append("</a>")
                            .append(" by ")
                            .append(lecturer != null ? String.format("<a href='%s/lecturers/%s'>%s</a>",
                                    Config.BASE_URL,
                                    lecturer.getUsername(),
                                    lecturer.getFullName())
                                    : "Unknown Lecturer");
                    message.append("</li>");
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