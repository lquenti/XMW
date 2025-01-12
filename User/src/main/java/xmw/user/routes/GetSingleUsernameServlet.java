package xmw.user.routes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.basex.query.QueryException;
import xmw.user.db.UserDB;
import xmw.user.utils.ServletUtils;

@WebServlet(name = "getSingleUsernameServlet", value = "/id/*")
public class GetSingleUsernameServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        Optional<String> maybeUsername = ServletUtils.extractUsernameFromPath(req, res);
        if (maybeUsername.isEmpty()) {
            // resp was already handled in there
            return;
        }
        String username = maybeUsername.get();

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