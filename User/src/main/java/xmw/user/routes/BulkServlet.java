package xmw.user.routes;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        try (PrintWriter out = res.getWriter()) {
            out.write(result);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error writing response");
        }
    }

    // TODO find out whether partial or not
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
        if (!DTDValidatorUtils.validateBulkRequest(xml)) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid XML sent");
        }

        // extract all usernames
        List<String> usernames;
        try {
            usernames = BulkUsernameExtractor.extractUsernames(xml);
        } catch (XMLStreamException e) {
            // This should never happen since we already successfully validated it
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // query for the usernames
        String result = UserDB.getSpecificBulkUsers(usernames, false);
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