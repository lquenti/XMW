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
        <th>Room ID</th>
        <th>Time Begin</th>
        <th>Time End</th>
    </tr>
    </thead>
    <tbody>
    <%
        List<Map<String, String>> schedules = (List<Map<String, String>>) request.getAttribute("schedules");
        if (schedules != null) {
            for (Map<String, String> schedule : schedules) {
    %>
    <tr>
        <td><%= schedule.get("room_id") %></td>
        <td><%= schedule.get("time_begin") %></td>
        <td><%= schedule.get("time_end") %></td>
    </tr>
    <%
            }
        }
    %>
    </tbody>
</table></body>
</html>
