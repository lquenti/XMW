package xmw.exa.models.courses;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.DB;
import xmw.exa.util.ExaServlet;
import xmw.exa.util.Util;
import xmw.flush.Course;
import xmw.flush.Courses;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "courses", value = "/courses")
public class CoursesServlet extends ExaServlet {
    private String name;
    private DB db;

    @Override
    public void init() {
        name = "Courses";
        db = DB.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            // Get all data
            List<Course> courses = db.courses().all();
            Courses coursesElement = new Courses();
            coursesElement.getCourse().addAll(courses);
            var responseData = DB.marshal(coursesElement);
            PrintWriter out = response.getWriter();
            out.println(responseData);
            out.flush();
        } catch (Exception e) {
            throw new IOException("Failed to generate courses XML: " + e.getMessage(), e);
        }
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Util.deleteItem(db.courses(), request, response);
    }
}
