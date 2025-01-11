<%@ page contentType="text/html;charset=UTF-8" import="xmw.exa.util.HtmlUtil"
language="java" %>
<html>
  <head>
    <title>${name}</title>
  </head>
  <body>
    <h1>${name}</h1>
    <main>
      <div>${message}</div>
      <a href="<%= HtmlUtil.BASE_URL%>"> Back to Overview </a>
      <p>
        <small>View as: <a href="?format=xml">XML</a></small>
      </p>
    </main>
  </body>
</html>
