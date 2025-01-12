<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Exam Registration</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f4f4f9;
            color: #333;
        }
        h1 {
            text-align: center;
            color: #444;
        }
        table {
            width: 80%;
            margin: 20px auto;
            border-collapse: collapse;
            background: white;
        }
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #5d8aa8;
            color: white;
        }
        tr:hover {
            background-color: #f1f1f1;
        }
        .form-container {
            width: 80%;
            margin: 20px auto;
            text-align: center;
        }
    </style>
</head>
<body>
<h1>Exam Registration</h1>

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
                        String examId = exam.get("examId");
                        String name = exam.get("courseName");
                        String date = exam.get("date");
            %>
            <option value="<%= examId %>"><%= name + "(" + date + ")" %></option>
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

