package xmw.user.routes;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.xml.sax.SAXException;
import xmw.user.db.UserDB;
import xmw.user.utils.DTDValidatorUtils;
import xmw.user.utils.ServletUtils;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "updateUserServlet", value = "/update/*")
public class UpdateUserServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        ServletUtils.doGetOnlyAvailableForPOST(req, res);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // Extract <USERNAME> from /update/<USERNAME>
        Optional<String> maybeUsername = ServletUtils.extractUsernameFromPath(req, res);
        if (maybeUsername.isEmpty()) {
            // resp was already handled in there
            return;
        }
        String username = maybeUsername.get();

        // Extract XML from Post
        String xml;
        try {
            xml = ServletUtils.extractPOSTData(req);
        } catch (IOException e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error reading POST data");
            return;
        }

        // validate dtd
        if (!DTDValidatorUtils.validateWithPassword(xml)) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid XML sent");
            return;
        }

        // Make sure the username in the XML is the same as in the URL parameter
        String xmlUsername;
        try {
            xmlUsername = ServletUtils.extractUsernameFromWellFormattedUser(xml);
        } catch (IOException | SAXException e) {
            // this should never happen since it passed the DTD validation
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not find username. This should never happen");
            return;
        }
        if (!username.strip().equals(xmlUsername.strip())) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Usernames in Path and XML differ!");
            return;
        }

        // if username doesnt exist, refuse
        if (!UserDB.usernameExist(username)) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username does not exist, consider using create");
            return;
        }

        // delete user
        UserDB.deleteUser(username);

        // create user
        UserDB.addUserNode(xml);

        // return 200

    }
}
