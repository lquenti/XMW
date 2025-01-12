package xmw.user.routes;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.basex.query.QueryException;
import xmw.user.db.UserDB;

@WebServlet(name = "getSingleUsernameServlet", value = "/id/*")
public class GetSingleUsernameServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        String username = req.getPathInfo();
        if (username == null ||
                username.length() <= 1) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, "Empty Request");
            return;
        }
        // Remove leading slash
        username = username.substring(1);
        if (username.contains("/")) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, "Too many slashes");
            return;
        }
        boolean usernameExists = false;
        try {
            usernameExists = UserDB.usernameExist(username);
        } catch (IOException e) {
            e.printStackTrace();
            // this is basically the same as false
        }
        if (!usernameExists) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }
        String result = UserDB.getUserByUsername(username, false);
        res.setContentType("application/xml");
        try (PrintWriter out = res.getWriter()) {
            out.write(result);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error writing response");

        }
    }
}