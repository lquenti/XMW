<jsp:useBean id="name" scope="request" type="java.lang.String"/>
<jsp:useBean id="message" scope="request" type="java.lang.String"/>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="xmw.exa.util.Config" %>
<html>
  <head>
    <title>${name}</title>
  </head>
  <body>
    <h1>${name}</h1>
    <main>
      <a href="<%= Config.BASE_URL%>"> Back to Exa Home </a>
      <div>${message}</div>
      <a href="<%= Config.BASE_URL%>"> Back to Overview </a>
      <p>
        <small>View as: <a href="?format=xml">XML</a></small>
      </p>
    </main>
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

  main {
    max-width: 1280px;
    margin: 0.5rem 4rem;
  }
  ul > * + * {
    margin-top: 4px;
  }
</style>
</html>
