<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Available Exams</title>
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
  </style>
</head>
<body>
<h1>Next Exams</h1>
<table>
  <thead>
  <tr>
    <th>Exam ID</th>
    <th>Exam Name</th>
    <th>Date</th>
  </tr>
  </thead>
  <tbody>
  <%
    List<Map<String, String>> exams = (List<Map<String, String>>) request.getAttribute("exams");
    if (exams != null && !exams.isEmpty()) {
      for (Map<String, String> exam : exams) {
        String examId = exam.get("examId");
        String examName = exam.get("courseName");
        String examDate = exam.get("date");
  %>
  <tr>
    <td><%= examId %></td>
    <td><%= examName %></td>
    <td><%= examDate %></td>
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

