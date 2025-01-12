package xmw.user.routes;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.user.db.UserDB;
import xmw.user.utils.ServletUtils;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "deleteUserServlet", value = "/delete/*")
public class DeleteUserServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // Extract <USERNAME> from /delete/<USERNAME>
        Optional<String> maybeUsername = ServletUtils.extractUsernameFromPath(req, res);
        if (maybeUsername.isEmpty()) {
            // resp was already handled in there
            return;
        }
        String username = maybeUsername.get();

        // if username doesnt exist, refuse
        if (!UserDB.usernameExist(username)) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username does not exist, can't delete it.");
            return;
        }

        UserDB.deleteUser(username);

        // return 200
    }
}
