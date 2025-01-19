package xmw.user.routes;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.basex.query.QueryException;
import xmw.ClientLogger;
import xmw.Event;
import xmw.user.db.UserDB;
import xmw.user.utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "authServlet", value = "/auth")
public class AuthServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        ServletUtils.doGetOnlyAvailableForPOST(req, res);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
            return;
        }

        boolean authenticated;
        try {
            authenticated = UserDB.authenticate(username, password);
        } catch (QueryException e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Internal Error while trying to authenticate");
            return;
        }
        if (authenticated) {
            ClientLogger.getInstance().addEvent(new Event("User", "root", "Authenticate", "User " + username + " successfully authenticated"));
            String result = UserDB.getUserByUsername(username, false);
            res.setContentType("application/xml");
            try (PrintWriter out = res.getWriter()) {
                out.write(result);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error writing response");

            }
        } else {
            ClientLogger.getInstance().addEvent(new Event("User", "root", "Authenticate", "User " + username + " failed to authenticate"));
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Wrong Username or Password");
        }
    }
}
