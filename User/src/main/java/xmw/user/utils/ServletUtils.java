package xmw.user.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class ServletUtils {
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
}
