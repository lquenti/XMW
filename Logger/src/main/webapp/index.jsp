<%--
  Created by IntelliJ IDEA.
  User: lquenti
  Date: 1/19/25
  Time: 11:23 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<body>
<div id='log-table'>Initializing Logger... This takes up to 5 Seconds</div>
<script src='https://unpkg.com/htmx.org'></script>
<script>
    setInterval(() => {
        htmx.ajax('GET', './log', { swap: '#log-table' });
    }, 5000, true);
</script>
</body>
</html>