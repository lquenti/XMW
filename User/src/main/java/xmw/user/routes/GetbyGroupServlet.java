package xmw.user.routes;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.user.db.UserDB;
import xmw.user.utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@WebServlet(name = "getByGroupServlet", value = "/group/*")
public class GetbyGroupServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Optional<String> maybeGroupname = ServletUtils.extractUsernameFromPath(req, res);
        if (maybeGroupname.isEmpty()) {
            // resp was already handled in there
            return;
        }
        String groupname = maybeGroupname.get();

        String result = UserDB.getAllUsersOfGroup(groupname, false);
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
