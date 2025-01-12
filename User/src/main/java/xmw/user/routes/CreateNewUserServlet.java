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

@WebServlet(name = "createNewUserServlet", value = "/create")
public class CreateNewUserServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        ServletUtils.doGetOnlyAvailableForPOST(req, res);
    }

    public static void doAdd(String xml, HttpServletRequest req, HttpServletResponse res) throws IOException {

        System.out.println(xml);
        // validate dtd
        if (!DTDValidatorUtils.validateWithPassword(xml)) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid XML sent");
            return;
        }

        // extract username
        String username;
        try {
            username = ServletUtils.extractUsernameFromWellFormattedUser(xml);
        } catch (IOException | SAXException e) {
            // this should never happen since it passed the DTD validation
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not find username. This should never happen");
            return;
        }

        // if user exist, return with bad request
        if (UserDB.usernameExist(username)) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username already exist, consider using update");
            return;
        }

        // add user
        UserDB.addUserNode(xml);

        // return 200
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // Extract XML
        String xml;
        try {
            xml = ServletUtils.extractPOSTData(req);
        } catch (IOException e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error reading POST data");
            return;
        }

        CreateNewUserServlet.doAdd(xml, req, res);
    }
}

