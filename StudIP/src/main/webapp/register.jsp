<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="xmw.StylingConstant" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register for Courses</title>
    <style>
        <% String css = StylingConstant.CSS; %>
        <%= css %>
    </style>
</head>
<body>
<h1>Register for Courses</h1>
<a href="index.jsp">Main Site</a>
<form method="POST" action="register">
    <label for="courseId">Select Course:</label>
    <select id="courseId" name="courseId">
        <%
            List<Map<String, String>> courses = (List<Map<String, String>>) request.getAttribute("courses");
            if (courses != null) {
                for (Map<String, String> course : courses) {
                    String courseId = course.get("CourseID");
                    String name = course.get("Name");
                    String faculty = course.get("Faculty");
                    String semester = course.get("Semester");
        %>
        <option value="<%= courseId %>"><%= faculty + ": " + name + " (" + semester + ")" %></option>
        <%
                }
            }
        %>
    </select><br><br>

    <label for="action">Action:</label>
    <select id="action" name="action">
        <option value="register">Register</option>
        <option value="deregister">Deregister</option>
    </select><br><br>

    <button type="submit">Register</button>
</form>

<p>${message}</p>
</body>
</html>
