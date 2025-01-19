package xmw.exa.models.courses;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.DB;
import xmw.exa.util.ExaServlet;
import xmw.exa.util.Util;
import xmw.flush.Course;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

@WebServlet(name = "course", urlPatterns = "/courses/*")
public class CourseServlet extends ExaServlet {
    private DB db;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void init() {
        db = DB.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String courseId = Util.getPathParameter("courses", request, response);

        // Fetch course data
        Course courseData = db.courses().get(courseId);
        if (courseData == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Course not found");
            return;
        }

        // Marshal course data to XML
        String responseData = DB.marshal(courseData);

        // Write XML response
        Util.writeXmlResponse(responseData, response);
    }
}