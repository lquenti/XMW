<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="xmw.StylingConstant" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Schedule</title>
    <style>
        <% String css = StylingConstant.CSS; %>
        <%= css %>
    </style>
</head>
<br>
<h1>Your Schedule</h1>
<a href="index.jsp">Main Site</a>
<c:out value="${schedules}" />
<table border="1" style="margin: 0 auto">
    <thead>
    <tr>
        <th>Name</th>
        <th>Semester</th>
        <th>Faculty</th>
        <th>Begin</th>
        <th>End</th>
        <th>Location</th>
    </tr>
    </thead>
    <tbody>
    <%
        List<Map<String, String>> schedules = (List<Map<String, String>>) request.getAttribute("schedules");
        if (schedules != null) {
            for (Map<String, String> schedule : schedules) {
    %>
    <tr>
        <td><%= schedule.get("CourseName") %></td>
        <td><%= schedule.get("Semester") %></td>
        <td><%= schedule.get("Faculty") %></td>
        <td><%= schedule.get("Begin") %></td>
        <td><%= schedule.get("End") %></td>
        <td><%= schedule.get("Location") %></td>
    </tr>
    <%
            }
        }
    %>
    </tbody>
</table></body>
</html>
