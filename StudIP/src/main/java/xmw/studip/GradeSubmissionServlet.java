package xmw.studip;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Servlet to handle grade input by lecturers
@WebServlet("/inputGrades")
public class GradeSubmissionServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String examId = request.getParameter("examId");
        String studentId = request.getParameter("studentId");
        String grade = request.getParameter("grade");

        try {
            XMLDatabase xmlDatabase = (XMLDatabase) getServletContext().getAttribute("xmlDatabase");
            boolean success = xmlDatabase.inputGrade(examId, studentId, grade);

            if (success) {
                request.setAttribute("message", "Grade input successfully.");
            } else {
                request.setAttribute("message", "Failed to input grade. Please try again.");
            }

            request.getRequestDispatcher("/grade_submission.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("message", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/grade_submission.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            XMLDatabase xmlDatabase = (XMLDatabase) getServletContext().getAttribute("xmlDatabase");
            List<String> currentRole = xmlDatabase.getCurrentRole(AuthUtil.getLoggedInUserId(request));
            if (!currentRole.contains("g_lecturer")) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                request.setAttribute("message", "An error occurred: You don't have the rights to access this content.");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
                return;
            }

            List<Map<String, String>> exams = xmlDatabase.getExamsAsLecturer(AuthUtil.getLoggedInUserId(request));
            List<Map<String, String>> students = xmlDatabase.getStudents();

            // Group students by exam ID for easy filtering
            Map<String, List<Map<String, String>>> studentsByExam = new HashMap<>();
            for (Map<String, String> student : students) {
                String examId = student.get("examId");
                studentsByExam.computeIfAbsent(examId, k -> new ArrayList<>()).add(student);
            }

            request.setAttribute("exams", exams);
            if(!studentsByExam.isEmpty())
                request.setAttribute("studentsByExam", studentsByExam);
            request.getRequestDispatcher("/grade_submission.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("message", "Unable to load data.");
            request.getRequestDispatcher("/grade_submission.jsp").forward(request, response);
        }
    }
}
