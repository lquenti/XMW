package xmw.exa.models.courses;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.Nullable;
import xmw.ClientLogger;
import xmw.Event;
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
//            Event event = new Event("Exa", "root", "GET", "Requested All Courses");
//            ClientLogger.getInstance().addEvent(event);
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

        var course = makeCourse(request, response, defaultRawDto, requiredParams);
        if (course == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

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
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String[] requiredParams = {"name", "lecturer", "faculty", "semester"};
        Map<String, String> defaultRawDto = new HashMap<>();
        defaultRawDto.put("name", "");
        defaultRawDto.put("lecturer", "");
        defaultRawDto.put("faculty", "");
        defaultRawDto.put("semester", "");
        defaultRawDto.put("max_students", "0");

        defaultRawDto = Util.makeUpdatedDto(requiredParams, defaultRawDto, request, response);

        if (defaultRawDto == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // verify that the course exists
        var course = db.courses().get(defaultRawDto.get("id"));
        if (course == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Course does not exist");
            return;
        }

        // Create the course
        course = makeCourse(request, response, defaultRawDto, requiredParams);
        if (course == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Update the course
        course = db.courses().update(course);

        if (course == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update course");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/xml");
        PrintWriter out = response.getWriter();
        out.println(DB.marshal(course));
        out.flush();
    }

    @Nullable
    private Course makeCourse(HttpServletRequest request, HttpServletResponse response, Map<String, String> defaultRawDto, String[] requiredParams) throws IOException {
        // Get the raw DTO
        Map<String, String> rawDto = Util.getRawDto(defaultRawDto, requiredParams, request, response);

        if (rawDto == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        // Verify references exist
        var lecturer = db.lecturers().get(rawDto.get("lecturer"));
        if (lecturer == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Lecturer does not exist");
            return null;
        }
        var semester = db.semesters().get(rawDto.get("semester"));
        if (semester == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Semester does not exist");
            return null;
        }

        // Create the course
        var course = new Course();

        if (rawDto.containsKey("id")) {
            course = db.courses().get(rawDto.get("id"));
        }

        course.getNameOrFacultyOrMaxStudents().clear();

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
        return course;
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Util.deleteItem(db.courses(), request, response);
    }
}
