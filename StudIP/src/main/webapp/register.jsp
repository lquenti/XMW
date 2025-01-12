<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register for Courses</title>
</head>
<body>
<h1>Register for Courses</h1>
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
        %>
        <option value="<%= courseId %>"><%= faculty + ": " + name %></option>
        <%
                }
            }
        %>
    </select><br><br>

    <button type="submit">Register</button>
</form>

<p>${message}</p>
</body>
</html>
