<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="xmw.StylingConstant" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Exam Registration</title>
    <style>
        <% String css = StylingConstant.CSS; %>
        <%= css %>
    </style>
</head>
<body>
<h1>Exam Registration</h1>
<a href="index.jsp">Main Site</a>

<p style="text-align: center; color: green;">
    <% String message = (String) request.getAttribute("message");
        if (message != null) { %>
    <%= message %>
    <% } %>
</p>

<div class="form-container">
    <form method="POST" action="examRegistration">
        <label for="examId">Select Exam:</label>
        <select id="examId" name="examId">
            <%
                List<Map<String, String>> exams = (List<Map<String, String>>) request.getAttribute("exams");
                if (exams != null) {
                    for (Map<String, String> exam : exams) {
                        String examId = exam.get("ExamId");
                        String name = exam.get("CourseName");
            %>
            <option value="<%= examId %>"><%= name %></option>
            <%
                }
            } else {
            %>
            <option value="">No exams available</option>
            <% } %>
        </select><br><br>

        <label for="action">Action:</label>
        <select id="action" name="action">
            <option value="register">Register</option>
            <option value="deregister">Deregister</option>
        </select><br><br>

        <input type="hidden" name="userId" value="<%= request.getSession().getAttribute("userId") %>">

        <button type="submit">Submit</button>
    </form>
</div>
</body>
</html>

