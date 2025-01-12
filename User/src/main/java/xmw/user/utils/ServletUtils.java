package xmw.user.utils;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

public class ServletUtils {
    public static void doGetOnlyAvailableForPOST(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
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


            String innerHtml = DOMUtils.documentToString(doc);
            req.setAttribute("domHtml", innerHtml);
            RequestDispatcher dispatcher = req.getRequestDispatcher("/base.jsp");
            dispatcher.forward(req, res);
        } catch (ParserConfigurationException | TransformerException | ServletException e) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to generate DOM HTML");
        }
    }

    public static String extractPOSTData(HttpServletRequest req) throws IOException {
        StringBuilder postData = new StringBuilder();
        String line;

        // Get the BufferedReader to read the request's input stream
        BufferedReader reader = req.getReader();
        while ((line = reader.readLine()) != null) {
            postData.append(line).append("\n");
        }


        // Convert StringBuilder to String
        return postData.toString();
    }

    // Assumes XML to be well-formatted, see DTDValidatorUtils on how to verify this
    // Expecting sth like
    // <User username="hbrosen2">
    //   <name>Brosenne</name>
    //   <firstname>Hendrik</firstname>
    //   <password>hunter2</password>
    //   <faculty>Computer Science</faculty>
    //   <group id="g_lecturer">Lecturer</group>
    //   <group id="g_employee">Employee</group>
    // </User>
    public static String extractUsernameFromWellFormattedUser(String xml) throws IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // this should never happen
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Document document = builder.parse(new InputSource(new StringReader(xml)));
        document.getDocumentElement().normalize();

        Element root = document.getDocumentElement();

        String username = root.getAttribute("username");
        if (username.isEmpty()) {
            throw new IOException("username is null");
        }
        return username;
    }

    // If None, then we already handled the response
    public static Optional<String> extractUsernameFromPath(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String username = req.getPathInfo();
        if (username == null ||
                username.length() <= 1) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, "Empty Request");
            return Optional.empty();
        }
        // Remove leading slash
        username = username.substring(1);
        if (username.contains("/")) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, "Too many slashes");
            return Optional.empty();
        }
        return Optional.of(username);
    }
}
