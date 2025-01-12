<%@ page import="xmw.studip.StylingConstant" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>
    <style>
        <% String css = StylingConstant.CSS; %>
        <%= css %>
    </style>
</head>
<body>
<h1>Login</h1>
<a href="index.jsp">Main Site</a>
<form method="POST" action="login">
    <label for="username">Username:</label>
    <input type="text" id="username" name="username" required><br><br>

    <label for="password">Password:</label>
    <input type="password" id="password" name="password" required><br><br>

    <button type="submit">Login</button>
</form>

<p style="color: red;">${errorMessage}</p>
</body>
</html>

