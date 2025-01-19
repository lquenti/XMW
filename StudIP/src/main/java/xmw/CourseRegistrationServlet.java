package xmw;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Serial;
import java.util.List;
import java.util.Map;

@WebServlet("/register")
public class CourseRegistrationServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Utils.log(request, response, (ClientLogger) getServletContext().getAttribute("logger"), "SiteVisitedEvent", "User visiting registration site", true);

        XMLDatabase xmlDatabase = (XMLDatabase) getServletContext().getAttribute("xmlDatabase");

        // Mock fetching course list from XML API (via XMLDataImporter)
        List<Map<String, String>> courses;
        try {
            courses = xmlDatabase.getCourses();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        request.setAttribute("courses", courses);
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        XMLDatabase xmlDatabase = (XMLDatabase) getServletContext().getAttribute("xmlDatabase");

        String userId = Utils.getLoggedInUserId(request);
        String courseId = request.getParameter("courseId");
        String semesterId = request.getParameter("semesterId");
        String action = request.getParameter("action");

        // Mock course registration logic
        if("register".equalsIgnoreCase(action)){
            Utils.log(request, response, (ClientLogger) getServletContext().getAttribute("logger"), "RegistrationEvent", "User "+ userId+ " registration for course "+courseId, true);
            if (xmlDatabase.registerStudentToCourse(userId, courseId, semesterId)) {
                request.setAttribute("message", "Registration successful!");
            } else {
                request.setAttribute("message", "Registration failed! Course not found.");
            }
        } else if ("deregister".equalsIgnoreCase(action)) {
            Utils.log(request, response, (ClientLogger) getServletContext().getAttribute("logger"), "DeregistrationEvent", "User "+ userId+ " registration for course "+courseId, true);

            if (xmlDatabase.deregisterStudentFromCourse(userId, courseId, semesterId)) {
                request.setAttribute("message", "Deregistration successful!");
            } else {
                request.setAttribute("message", "Deregistration failed! Course not found.");
            }
        }
        doGet(request, response);
    }
}

