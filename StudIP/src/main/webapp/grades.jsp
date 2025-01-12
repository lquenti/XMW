<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Grades Overview</title>
</head>
<body>
<h1>Your Grades</h1>
<table border="1" cellpadding="10">
  <thead>
  <tr>
    <th>Exam ID</th>
    <th>Lecture</th>
    <th>Grade</th>
    <th>Date</th>
  </tr>
  </thead>
  <tbody>
  <%
    List<Map<String, String>> grades = (List<Map<String, String>>) request.getAttribute("grades");
    if (grades != null && !grades.isEmpty()) {
      for (Map<String, String> grade : grades) {
        String examId = grade.get("id");
        String courseName = grade.get("courseName");
        String gradeValue = grade.get("grade");
        String date = grade.get("date");
  %>
  <tr>
    <td><%= examId %></td>
    <td><%= courseName %></td>
    <td><%= gradeValue %></td>
    <td><%= date %></td>
  </tr>
  <%
    }
  } else {
  %>
  <tr>
    <td colspan="2">No grades available</td>
  </tr>
  <%
    }
  %>
  </tbody>
</table>
</body>
</html>

