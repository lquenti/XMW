<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="xmw.studip.StylingConstant" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Grade Submission</title>
    <style>
        <% String css = StylingConstant.CSS; %>
        <%= css %>
    </style>
    <script>
        function filterStudents() {
            const selectedExamId = document.getElementById("examSelect").value;
            const studentRows = document.querySelectorAll(".student-row");

            studentRows.forEach(row => {
                if (row.dataset.examId === selectedExamId || selectedExamId === "") {
                    row.style.display = "";
                } else {
                    row.style.display = "none";
                }
            });
        }
    </script>
</head>
<body>
<h1>Grade Submission</h1>
<a href="index.jsp">Main Site</a>

<!-- Exam Selection -->
<form method="GET">
    <label for="examSelect">Select Exam:</label>
    <select id="examSelect" name="examId" onchange="filterStudents()">
        <option value="">-- Select Exam --</option>
        <%
            List<Map<String, String>> exams = (List<Map<String, String>>) request.getAttribute("exams");
            if (exams != null) {
                for (Map<String, String> exam : exams) {
                    String examId = exam.get("ExamId");
                    String examName = exam.get("CourseName");
                    String examDate = exam.get("date");
        %>
        <option value="<%= examId %>"><%= examName + " - " + examDate %></option>
        <%
                }
            }
        %>
    </select>
</form>

<!-- Students Table -->
<h2>Students</h2>
<form method="POST" action="inputGrades">
    <table border="1" cellpadding="10">
        <thead>
        <tr>
            <th>Exam ID</th>
            <th>Student ID</th>
            <th>Student Name</th>
            <th>Grade</th>
        </tr>
        </thead>
        <tbody>
        <%
            Map<String, List<Map<String, String>>> studentsByExam =
                    (Map<String, List<Map<String, String>>>) request.getAttribute("studentsByExam");
            if (studentsByExam != null && exams != null && !exams.isEmpty()) {
                for (Map.Entry<String, List<Map<String, String>>> entry : studentsByExam.entrySet()) {
                    String examId = entry.getKey();
                    List<Map<String, String>> studentsForExam = entry.getValue();

                    for (Map<String, String> student : studentsForExam) {
                        String studentId = student.get("username");
                        String studentName = student.get("name");
        %>
        <tr class="student-row" data-exam-id="<%= examId %>">
            <td><%= examId %></td>
            <td><%= studentId %></td>
            <td><%= studentName %></td>
            <td>
                <input type="text" name="grades[<%= examId %>][<%= studentId %>]" placeholder="Enter grade">
            </td>
        </tr>
        <%
                    }
                }
            }
        %>
        </tbody>
    </table>
    <button type="submit">Submit Grades</button>
</form>

</body>
</html>

