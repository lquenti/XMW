package xmw;

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
        Utils.log(request, response, (ClientLogger) getServletContext().getAttribute("logger"), "GradeSubmissionEvent", "User trying to submit grades ", true);
        Map<String, Map<String, String>> grades = new HashMap<>();

        // Extract parameter map
        Map<String, String[]> parameterMap = request.getParameterMap();

        for (String param : parameterMap.keySet()) {
            // Check if the parameter corresponds to a grade entry
            if (param.startsWith("grades[")) {
                // Extract the examId and studentId from the parameter name
                String[] parts = param.substring(7, param.length() - 1).split("\\]\\[");
                if (parts.length == 2) {
                    String examId = parts[0];
                    String studentId = parts[1];

                    // Get the grade value
                    String grade = request.getParameter(param);

                    // Add the grade to the map
                    grades.putIfAbsent(examId, new HashMap<>());
                    grades.get(examId).put(studentId, grade);
                }
            }
        }

        List<Map<String, String>> submittedGrades = new ArrayList<>();
        for(String examId: grades.keySet()){
            if(examId.equals(""))
                continue;
            Map<String, String> grade = grades.get(examId);
            for(String u: grade.keySet()){
                if(grade.get(u).equals(""))
                    continue;
                Map<String, String> s = new HashMap<>();
                s.put("username", u);
                s.put("grade", grade.get(u));
                s.put("examId", examId);
                submittedGrades.add(s);
            }
        }

        for (Map<String, String> grade : submittedGrades) {
            try {
                XMLDatabase xmlDatabase = (XMLDatabase) getServletContext().getAttribute("xmlDatabase");
                boolean success = xmlDatabase.inputGrade(grade.get("examId"), grade.get("username"), grade.get("grade"));
                if (success) {
                    request.setAttribute("message", "Grade input successfully.");
                } else {
                    request.setAttribute("message", "Failed to input grade. Please try again.");
                }

                request.getRequestDispatcher("/grade_submission.jsp").forward(request, response);
            } catch(Exception e){
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                request.setAttribute("message", "An error occurred: " + e.getMessage());
                request.getRequestDispatcher("/grade_submission.jsp").forward(request, response);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Utils.log(request, response, (ClientLogger) getServletContext().getAttribute("logger"), "SiteVisitedEvent", "User visiting for grade submission", true);
        try {
            XMLDatabase xmlDatabase = (XMLDatabase) getServletContext().getAttribute("xmlDatabase");
            List<String> currentRole = xmlDatabase.getCurrentRole(Utils.getLoggedInUserId(request));
            if (!currentRole.contains("g_lecturer")) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                request.setAttribute("message", "An error occurred: You don't have the rights to access this content.");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
                return;
            }
            
            Utils.log(request, response, (ClientLogger) getServletContext().getAttribute("logger"), "SiteVisitedEvent", "Updating lecturer courses", true);
            xmlDatabase.registerLecturersToCourse();

            List<Map<String, String>> exams = xmlDatabase.getExamsAsLecturer(Utils.getLoggedInUserId(request));
            List<Map<String, String>> courses = xmlDatabase.getCourses();
            for (Map<String, String> exam : exams) {
                for (Map<String, String> course : courses){
                    if (exam.get("CourseID").equals(course.get("CourseID"))) {
                        exam.putAll(course);
                    }
                }
            }

            List<Map<String, String>> students = xmlDatabase.getStudents();
            for(Map<String, String> student: students){
                student.put("examId", xmlDatabase.getRegistrations(student.get("username")));
            }

            // Group students by exam ID for easy filtering
            Map<String, List<Map<String, String>>> studentsByExam = new HashMap<>();
            for (Map<String, String> student : students) {
                String[] examIds = student.get("examId").split("\n");
                for(String examId: examIds) {
                    if(examId==null || examId.isEmpty() || examId.equals("null"))
                        continue;
                    studentsByExam.computeIfAbsent(examId, k -> new ArrayList<>()).add(student);
                }
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
