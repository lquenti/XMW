package xmw.exa.util;

import jakarta.servlet.http.HttpServlet;

public class ExaServlet extends HttpServlet {
    @Override
    public void destroy() {
        // Don't close the DB here as it's shared
    }
}
