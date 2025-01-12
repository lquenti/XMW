package xmw.user.routes;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.*;
import xmw.user.utils.DOMUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.StringReader;

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
}

