package xmw.user.routes;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import xmw.user.db.UserDB;
import xmw.user.utils.DOMUtils;
import xmw.user.utils.DTDValidatorUtils;
import xmw.user.utils.ServletUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = "createNewUserServlet", value = "/create")
public class CreateNewUserServlet extends HttpServlet {
    private static Document generateDOM() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement("div");
        doc.appendChild(root);
        root.appendChild(doc.createTextNode("Only available for "));
        Element method = doc.createElement("code");
        method.appendChild(doc.createTextNode("POST"));
        root.appendChild(method);
        root.appendChild(doc.createTextNode(". See the documentation"));

        return doc;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String innerHtml;
        try {
            Document doc = generateDOM();
            innerHtml = DOMUtils.documentToString(doc);
            req.setAttribute("domHtml", innerHtml);
            RequestDispatcher dispatcher = req.getRequestDispatcher("/base.jsp");
            dispatcher.forward(req, res);
        } catch (ParserConfigurationException | TransformerException | ServletException e) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to generate DOM HTML");
        }
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
}

