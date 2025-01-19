package xmw.exa.models.courses;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.DB;
import xmw.exa.util.ExaServlet;
import xmw.exa.util.Util;
import xmw.flush.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "courses", value = "/courses")
public class CoursesServlet extends ExaServlet {
    private DB db;

    @Override
    public void init() {
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
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String[] requiredParams = {"name", "lecturer", "faculty", "semester"};
        final Map<String, String> defaultRawDto = new HashMap<>();
        defaultRawDto.put("name", "");
        defaultRawDto.put("lecturer", "");
        defaultRawDto.put("faculty", "");
        defaultRawDto.put("semester", "");
        defaultRawDto.put("max_students", "0");

        // Get the raw DTO
        Map<String, String> rawDto = Util.getRawDto(defaultRawDto, requiredParams, request, response);

        if (rawDto == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Verify references exist
        var lecturer = db.lecturers().get(rawDto.get("lecturer"));
        if (lecturer == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Lecturer does not exist");
            return;
        }
        var semester = db.semesters().get(rawDto.get("semester"));
        if (semester == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Semester does not exist");
            return;
        }

        // Create the course
        var course = new Course();
        course.setLecturer(lecturer);
        course.setSemester(semester);
        // Name, Faculty, MaxStudents
        Name name = new Name();
        name.setContent(rawDto.get("name"));
        course.getNameOrFacultyOrMaxStudents().add(name);
        Faculty faculty = new Faculty();
        faculty.setContent(rawDto.get("faculty"));
        course.getNameOrFacultyOrMaxStudents().add(faculty);
        MaxStudents maxStudents = new MaxStudents();
        maxStudents.setContent(rawDto.get("max_students"));
        course.getNameOrFacultyOrMaxStudents().add(maxStudents);

        // Add the course
        boolean success = db.courses().create(course);

        if (!success) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failed to create course");
            return;
        }

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.setContentType("application/xml");

        // Return the course
        PrintWriter out = response.getWriter();
        out.println(DB.marshal(course));
        out.flush();
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Util.deleteItem(db.courses(), request, response);
    }
}
