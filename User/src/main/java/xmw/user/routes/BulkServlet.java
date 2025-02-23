package xmw.user.routes;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.ClientLogger;
import xmw.Event;
import xmw.user.db.UserDB;
import xmw.user.utils.BulkUsernameExtractor;
import xmw.user.utils.DTDValidatorUtils;
import xmw.user.utils.ServletUtils;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "bulkServlet", value = "/bulk")
public class BulkServlet  extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String result = UserDB.getAllUsers(false);
        res.setContentType("application/xml");
        ClientLogger.getInstance().addEvent(new Event("User", "root", "Bulk", "Bulk returning all users"));
        try (PrintWriter out = res.getWriter()) {
            out.write(result);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error writing response");
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // exract xml from POST
        String xml;
        try {
            xml = ServletUtils.extractPOSTData(req);
        } catch (IOException e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error reading POST data");
            return;
        }

        // validate via dtd
        /*
        if (!DTDValidatorUtils.validateBulkRequest(xml)) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid XML sent");
        }
         */

        // extract all usernames (lazy, tolerant validation (as long as it **also** contains our information))
        List<String> usernames;
        try {
            usernames = BulkUsernameExtractor.extractUsernames(xml);
        } catch (XMLStreamException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid XML sent");
            return;
        }

        // query for the usernames
        String result = UserDB.getSpecificBulkUsers(usernames, false);
        res.setContentType("application/xml");
        ClientLogger.getInstance().addEvent(new Event("User", "root", "Bulk", "Bulk returning " + usernames.size() + " users"));
        try (PrintWriter out = res.getWriter()) {
            out.write(result);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error writing response");
        }
    }
}