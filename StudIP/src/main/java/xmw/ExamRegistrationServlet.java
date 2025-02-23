package xmw;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static xmw.Utils.joinListOfMaps;

// Servlet to handle exam registration and deregistration
@WebServlet("/examRegistration")
public class ExamRegistrationServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = Utils.getLoggedInUserId(request);
        String examId = request.getParameter("examId");
        String action = request.getParameter("action"); // "register" or "deregister"

        try {
            XMLDatabase xmlDatabase = (XMLDatabase) getServletContext().getAttribute("xmlDatabase");
            boolean success;

            if ("register".equalsIgnoreCase(action)) {
                Utils.log(request, response, (ClientLogger) getServletContext().getAttribute("logger"), "RegistrationEvent", "User registration for exam " + examId, true);
                success = xmlDatabase.registerStudentToExam(userId, examId);
            } else if ("deregister".equalsIgnoreCase(action)) {
                Utils.log(request, response, (ClientLogger) getServletContext().getAttribute("logger"), "DeregistrationEvent", "User deregistration for exam " + examId, true);
                success = xmlDatabase.deregisterStudentFromExam(userId, examId);
            } else {
                throw new IllegalArgumentException("Invalid action: " + action);
            }

            if (success) {
                request.setAttribute("message", "Action completed successfully.");
            } else {
                request.setAttribute("message", "Action failed. Please try again.");
            }

            // Redirect back to the registration page with feedback
            request.getRequestDispatcher("/exam_registration.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("message", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/exam_registration.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Utils.log(request, response, (ClientLogger) getServletContext().getAttribute("logger"), "SiteVisitedEvent", "User visiting for exam registration ", true);
        try {
            XMLDatabase xmlDatabase = (XMLDatabase) getServletContext().getAttribute("xmlDatabase");
            List<Map<String, String>> exams = xmlDatabase.getExams();
            List<Map<String, String>> courses = xmlDatabase.getCourses();
            joinListOfMaps(exams, courses, "CourseID", "CourseID");

            request.setAttribute("exams", exams);
            request.getRequestDispatcher("/exam_registration.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("message", "Unable to load exams.");
            request.getRequestDispatcher("/exam_registration.jsp").forward(request, response);
        }
    }
}


