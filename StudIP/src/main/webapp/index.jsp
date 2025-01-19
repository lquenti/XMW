<%@ page import="xmw.StylingConstant" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Welcome to StudIP</title>
  <style>
    <% String css = StylingConstant.CSS; %>
    <%= css %>
  </style>
</head>
<body>
<h1>Welcome to StudIP</h1>
<ul>
  <li><a href="login">Login</a></li>
  <li><a href="register">Register for Courses</a></li>
  <li><a href="schedule">View Schedule</a></li>
  <li><a href="flex">FlexNever</a></li>
  <li><a href="examRegistration">Register for Exams</a></li>
  <li><a href="grades">Show Grades</a></li>
  <li><a href="inputGrades">Submit Grades</a></li>
</ul>
</body>
</html>
