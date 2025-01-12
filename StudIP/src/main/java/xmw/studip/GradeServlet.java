package xmw.studip;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/grades")
public class GradeServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Fetch exams from XMLDatabase
            XMLDatabase xmlDatabase = (XMLDatabase) getServletContext().getAttribute("xmlDatabase");
            List<Map<String, String>> grades = xmlDatabase.getGrades(AuthUtil.getLoggedInUserId(request));
            List<Map<String, String>> exams = xmlDatabase.getExams();

            List<Map<String, String>> registeredExams = new ArrayList<>();
            for(Map<String, String> exam: exams){
                for(Map<String, String> grade: grades)
                    if(grade.get("id").contains(exam.get("examId")))
                        grade.putAll(exam);
            }

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
