<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Weekly Schedule</title>
</head>
<br>
<h1>Your Weekly Schedule</h1>
<c:out value="${schedules}" />
<table border="1">
    <thead>
    <tr>
        <th>Name</th>
        <th>Semester</th>
        <th>Faculty</th>
        <th>Time</th>
    </tr>
    </thead>
    <tbody>
    <%
        List<Map<String, String>> schedules = (List<Map<String, String>>) request.getAttribute("schedules");
        if (schedules != null) {
            for (Map<String, String> schedule : schedules) {
    %>
    <tr>
        <td><%= schedule.get("Name") %></td>
        <td><%= schedule.get("Semester") %></td>
        <td><%= schedule.get("Faculty") %></td>
        <td><%= schedule.get("Time") %></td>
    </tr>
    <%
            }
        }
    %>
    </tbody>
</table></body>
</html>
