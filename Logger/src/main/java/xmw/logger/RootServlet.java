package xmw.logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/")
public class RootServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<html><body>");
        out.println("<div id='log-table'></div>");
        out.println("<script src='https://unpkg.com/htmx.org'></script>");
        out.println("<script>setInterval(() => { htmx.ajax('GET', './log', { swap: '#log-table' }) }, 5000);</script>");
        out.println("</body></html>");
    }
}
