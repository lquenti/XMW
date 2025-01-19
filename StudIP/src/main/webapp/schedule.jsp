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
        body {
            font-family: 'Courier New', Courier, monospace;
            background-color: #fdfdfe;
            color: #1c1c1d;
        }

        h1 {
            text-align: center;
            font-size: 3em;
            color: #0366d6;
            text-shadow: 3px 3px 0 #ffcc00;
        }

        a {
            display: block;
            text-align: center;
            margin-bottom: 20px;
            font-size: 1.5em;
            color: #03c03c;
            text-decoration: none;
        }

        table {
            width: 80%;
            border-collapse: collapse;
            margin: 0 auto 20px;
            box-shadow: 0 0 10px #ccc;
        }

        th, td {
            border: 3px solid #ff8c00;
            padding: 10px;
            text-align: center;
            font-size: 1.2em;
            background-color: #fffacd;
            color: #1c1c1d;
        }

        th {
            background-color: #ffd700;
            color: #3a3a3a;
            font-weight: bold;
            font-style: italic;
            border-bottom: 5px double #ff4500;
        }

        tr:hover {
            background-color: #afe9af;
            cursor: pointer;
        }

        tbody tr:nth-child(even) {
            background-color: #faebd7;
        }
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
