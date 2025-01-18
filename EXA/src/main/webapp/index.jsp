<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html xml:lang="en">
<head>
    <title>Exa Service</title>
</head>
<body>
<h1>
    <%= "EXA Services" %>
</h1>
<br/>
<nav>
    <a href="courses"> Courses </a>
    <a href="lecturerOlds"> Lecturers </a>
    <a href="exams"> Exams </a>
    <a href="courses/new">New Course</a>
</nav>
</body>
<style>
    *, :root {
        max-width: 1280px;
        font-family: ui-sans-serif, system-ui, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol", "Noto Color Emoji";
    }

    h1 {
        padding-top: 1rem;
        margin: 0.5rem 4rem;
    }

    nav {
        margin: 1rem 4rem;
        display: flex;
        justify-content: space-between;
    }
</style>
</html>
