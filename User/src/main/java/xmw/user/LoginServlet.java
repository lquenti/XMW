package xmw.user;

import java.io.IOException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

@WebServlet(name = "loginServlet", value = "/login")
public class LoginServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // Set response content type
        res.setContentType("application/xml");

        try {
            // Create a new Document to build the XML
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // Create root element <response>
            Element rootElement = doc.createElement("response");
            doc.appendChild(rootElement);

            // Create <message> element to inform the user
            Element message = doc.createElement("message");
            message.appendChild(doc.createTextNode("Please use the POST method to submit login parameters."));
            rootElement.appendChild(message);

            // Create <example> element with a sample curl command
            Element example = doc.createElement("example");
            example.appendChild(doc.createTextNode(
                    "curl -X POST -d \"username=yourusername&password=yourpassword\" http://localhost:8080/login"
            ));
            rootElement.appendChild(example);

            // Transform the DOM Document to XML and write to response output
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // Optional: Add indentation to the output for readability
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(res.getOutputStream());
            transformer.transform(source, result);

        } catch (ParserConfigurationException | TransformerException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // Set response content type
        res.setContentType("application/xml");

        // Get parameters from request
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        try {
            // Create a new Document to build the XML
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // Create root element <response>
            Element rootElement = doc.createElement("response");
            doc.appendChild(rootElement);

            if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
                res.setStatus(HttpServletResponse.SC_OK); // 200 OK

                // Create <message> element
                Element message = doc.createElement("message");
                message.appendChild(doc.createTextNode("Login parameters received successfully."));
                rootElement.appendChild(message);
            } else {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request

                // Create <error> element
                Element error = doc.createElement("error");
                error.appendChild(doc.createTextNode("Missing username or password."));
                rootElement.appendChild(error);
            }

            // Transform the DOM Document to XML and write to response output
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // Optional: Add indentation to the output for readability
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(res.getOutputStream());
            transformer.transform(source, result);

        } catch (ParserConfigurationException | TransformerException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}
