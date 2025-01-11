package xmw.user;

import java.io.IOException;
import java.io.StringWriter;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

@WebServlet(name = "indexServlet", value = "/")
public class IndexServlet extends HttpServlet {

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
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            transformer.transform(source, new StreamResult(writer));
            String domHtml = writer.toString();

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
