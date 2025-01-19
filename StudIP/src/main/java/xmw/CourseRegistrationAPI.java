package xmw;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/course")
public class CourseRegistrationAPI extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response, true); // Register operation
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response, false); // Deregister operation
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response, boolean isRegister) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Parse request parameters
            String userId = request.getParameter("userId");
            String courseId = request.getParameter("courseId");
            String semester = request.getParameter("semester");

            if (userId == null || courseId == null || semester == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\":\"Missing required parameters: userId, courseId, semester\"}");
                return;
            }

            // Access the database
            XMLDatabase xmlDatabase = (XMLDatabase) getServletContext().getAttribute("xmlDatabase");
            if (xmlDatabase == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write("{\"error\":\"Database access error\"}");
                return;
            }



            // Perform the operation
            boolean success;
            if (isRegister) {
                Utils.log(request, response, (ClientLogger) getServletContext().getAttribute("logger"), "RegistrationApiEvent", "Api call to register student "+userId+" to course" + courseId, false);
                success = xmlDatabase.registerStudentToCourse(userId, courseId, semester);
            } else {
                Utils.log(request, response, (ClientLogger) getServletContext().getAttribute("logger"), "DeregistrationApiEvent", "Api call to deregister student "+userId+" to course" + courseId, false);

                success = xmlDatabase.deregisterStudentFromCourse(userId, courseId, semester);
            }

            // Respond to the client
            if (success) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"status\":\"success\", \"operation\":\"" + (isRegister ? "register" : "deregister") + "\", \"userId\":\"" + userId + "\", \"courseId\":\"" + courseId + "\", \"semester\":\"" + semester + "\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"status\":\"failure\", \"operation\":\"" + (isRegister ? "register" : "deregister") + "\", \"userId\":\"" + userId + "\", \"courseId\":\"" + courseId + "\", \"semester\":\"" + semester + "\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\":\"An unexpected error occurred\"}");
        } finally {
            out.flush();
            out.close();
        }
    }
}

