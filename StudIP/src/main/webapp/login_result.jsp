<%@ page import="xmw.studip.StylingConstant" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login Result</title>
    <style>
        <% String css = StylingConstant.CSS; %>
        <%= css %>
    </style>
</head>
<body>
<h1>Login Successful</h1>
<pre>${loginResponse}</pre>
<a href="index.jsp">Go back to Home</a>
</body>
</html>
