<%@ page import="xmw.studip.StylingConstant" %>
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
  <li><a href="register">Register for Courses</a></li>
  <li><a href="schedule">View Weekly Schedule</a></li>
  <li><a href="flex">FlexNever</a></li>
</ul>
</body>
</html>
