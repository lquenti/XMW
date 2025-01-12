package xmw.user.routes;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.user.db.UserDB;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "bulkServlet", value = "/bulk")
public class BulkServlet  extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String result = UserDB.getAllUsers(false);
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