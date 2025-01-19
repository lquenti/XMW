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

import static xmw.Utils.joinListOfMaps;

@WebServlet("/grades")
public class GradeServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Utils.log(request, response, (ClientLogger) getServletContext().getAttribute("logger"), "SiteVisitedEvent", "User visiting grades", true);
            // Fetch exams from XMLDatabase
            XMLDatabase xmlDatabase = (XMLDatabase) getServletContext().getAttribute("xmlDatabase");
            List<Map<String, String>> grades = xmlDatabase.getGrades(Utils.getLoggedInUserId(request));
            List<Map<String, String>> exams = xmlDatabase.getExams();
            List<Map<String, String>> courses = xmlDatabase.getCourses();
            List<Map<String, String>> modules = xmlDatabase.getModules();
            joinListOfMaps(grades, exams, "id", "ExamId");
            joinListOfMaps(grades, courses, "CourseID", "CourseID");
            joinListOfMaps(grades, modules, "CourseID", "CourseID");
            // Set exams as a request attribute
            request.setAttribute("grades", grades);
            // Forward to JSP to display exams
            request.getRequestDispatcher("/grades.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error fetching exams.");
        }
    }
}
