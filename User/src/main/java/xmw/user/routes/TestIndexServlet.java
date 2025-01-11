package xmw.user.routes;

import java.io.IOException;
import java.io.StringWriter;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xmw.user.utils.DOMUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

@WebServlet(name = "testIndexServlet", value = "/test")
public class TestIndexServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Create the DOM structure
            Document doc = builder.newDocument();
            Element html = doc.createElement("html");
            doc.appendChild(html);

            Element body = doc.createElement("body");
            html.appendChild(body);

            Element h1 = doc.createElement("h1");
            h1.setTextContent("Hello World from DOM!");
            body.appendChild(h1);

            Element p = doc.createElement("p");
            p.setTextContent("This paragraph is generated programmatically.");
            body.appendChild(p);

            // Serialize DOM to a string
            String domHtml = DOMUtils.documentToString(doc);

            // Pass the generated HTML to the JSP
            req.setAttribute("domHtml", domHtml);

            // Forward to the JSP
            RequestDispatcher dispatcher = req.getRequestDispatcher("/base.jsp");
            dispatcher.forward(req, res);
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to generate DOM HTML");
        }
    }
}
