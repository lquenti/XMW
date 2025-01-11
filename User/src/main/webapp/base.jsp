<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Auth Server</title>
</head>
<body>
<h1>XMW Auth Server</h1>

<main>
    ${domHtml}
</main>
</body>
<style>
    html {
        background: #3366CC;
    }
    body {
        font-family: 'Trebuchet MS', Arial, sans-serif;
        background-color: #fff;
        border-radius: 20px;
        margin: 40px auto;
        max-width: 1200px;
        line-height: 1.6;
        font-size: 18px;
        color: #333;
        padding: 0 10px;
    }
    h1, h2, h3 {
        line-height: 1.2;
        text-align: center;
    }
    p, pre {
        margin: 0
    }
    .payload, .response {
        padding-left: 5px;
        margin-left: 20px;
        border-left: #3366CC 5px dotted;
    }
    .response + .response {
        margin-top: 5px;
    }
</style>
</html>
