package xmw.user.routes;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.Close;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.Open;
import org.basex.query.QueryException;
import org.basex.query.QueryProcessor;
import org.basex.query.iter.Iter;
import org.basex.query.value.item.Item;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xmw.user.utils.DOMUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

@WebServlet(name = "authServlet", value = "/auth")
public class AuthServlet extends HttpServlet {
    private static Document generateDOM() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement("div");
        doc.appendChild(root);
        root.appendChild(doc.createTextNode("Please use "));
        Element method = doc.createElement("code");
        method.appendChild(doc.createTextNode("POST"));
        root.appendChild(method);
        root.appendChild(doc.createTextNode(" for checking auth. Example:"));

        Element pre = doc.createElement("pre");
        Element code = doc.createElement("code");
        code.setTextContent("""
                curl -X POST -d "username=yourusername&password=yourpassword" http://URL_OF_SERVICE/auth    
                """);
        pre.appendChild(code);
        root.appendChild(pre);

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
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
        }
    }
}
