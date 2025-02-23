<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="xmw.StylingConstant" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Available Exams</title>
  <style>
    <% String css = StylingConstant.CSS; %>
    <%= css %>
  </style>
</head>
<body>
<h1>Next Exams</h1>
<a href="index.jsp">Main Site</a>
<table>
  <thead>
  <tr>
    <th>Exam ID</th>
    <th>Exam Name</th>
    <th>Date</th>
    <th>Location</th>
  </tr>
  </thead>
  <tbody>
  <%
    List<Map<String, String>> exams = (List<Map<String, String>>) request.getAttribute("exams");
    if (exams != null && !exams.isEmpty()) {
      for (Map<String, String> exam : exams) {
        String examId = exam.get("ExamId");
        String examName = exam.get("CourseName");
        String examDate = exam.get("date");
        String roomOrLink = exam.get("roomOrLink");
  %>
  <tr>
    <td><%= examId %></td>
    <td><%= examName %></td>
    <td><%= examDate %></td>
    <td><%= roomOrLink %></td>
  </tr>
  <%
    }
  } else {
  %>
  <tr>
    <td colspan="3" style="text-align: center;">No exams available.</td>
  </tr>
  <% } %>
  </tbody>
</table>
</body>
</html>

