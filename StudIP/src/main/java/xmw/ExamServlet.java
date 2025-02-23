package xmw;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static xmw.Utils.joinListOfMaps;

@WebServlet("/flex")
public class ExamServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Utils.log(request, response, (ClientLogger) getServletContext().getAttribute("logger"), "SiteVisitedEvent", "User visiting FlexNever ", true);
            // Fetch exams from XMLDatabase
            XMLDatabase xmlDatabase = (XMLDatabase) getServletContext().getAttribute("xmlDatabase");

            List<Map<String, String>> exams = xmlDatabase.getExams();
            List<Map<String, String>> courses = xmlDatabase.getCourses();
            joinListOfMaps(exams, courses, "CourseID", "CourseID");

            List<String> registeredExamIds = xmlDatabase.getAllExamIDs(Utils.getLoggedInUserId(request));

            List<Map<String, String>> registeredExams = new ArrayList<>();
            for(Map<String, String> exam: exams){
                if(registeredExamIds.contains(exam.get("ExamId")))
                    registeredExams.add(exam);
            }

            // Set exams as a request attribute
            request.setAttribute("exams", registeredExams);

            // Forward to JSP to display exams
            request.getRequestDispatcher("/show_exams.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error fetching exams.");
        }
    }
}

