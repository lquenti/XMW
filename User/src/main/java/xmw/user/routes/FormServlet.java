package xmw.user.routes;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xmw.ClientLogger;
import xmw.Event;
import xmw.user.db.UserDB;
import xmw.user.utils.DOMUtils;
import xmw.user.utils.GroupMappings;
import xmw.user.utils.Pair;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

@WebServlet(name = "formServlet", value = "/form")
public class FormServlet extends HttpServlet {
    private static Element createCheckbox(Document doc, String id, String labelName) {
        // Create the container div
        Element checkboxDiv = doc.createElement("div");

        // Create the checkbox input element
        Element checkboxInput = doc.createElement("input");
        checkboxInput.setAttribute("type", "checkbox");
        checkboxInput.setAttribute("id", id);
        checkboxInput.setAttribute("name", "group");
        checkboxInput.setAttribute("value", id);
        checkboxDiv.appendChild(checkboxInput);

        // Create the label for the checkbox
        Element checkboxLabel = doc.createElement("label");
        checkboxLabel.setAttribute("for", id);
        checkboxLabel.setTextContent(labelName);
        checkboxDiv.appendChild(checkboxLabel);

        return checkboxDiv;
    }

    private static Document generateDOM(String contextPath) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        // Create the root <div> element
        Element root = doc.createElement("div");
        doc.appendChild(root);

        // Add a header for the form
        Element h2 = doc.createElement("h2");
        h2.appendChild(doc.createTextNode("Creation/Update Form"));
        root.appendChild(h2);

        // Create the <form> element
        Element form = doc.createElement("form");
        form.setAttribute("action", contextPath + "/form");
        form.setAttribute("method", "post");
        root.appendChild(form);

        // Username field
        Element usernameLabel = doc.createElement("label");
        usernameLabel.setAttribute("for", "username");
        usernameLabel.setTextContent("Username:");
        form.appendChild(usernameLabel);

        Element usernameInput = doc.createElement("input");
        usernameInput.setAttribute("type", "text");
        usernameInput.setAttribute("id", "username");
        usernameInput.setAttribute("name", "username");
        usernameInput.setAttribute("required", "true");
        form.appendChild(usernameInput);

        form.appendChild(doc.createElement("br"));

        // Name field
        Element nameLabel = doc.createElement("label");
        nameLabel.setAttribute("for", "name");
        nameLabel.setTextContent("Name:");
        form.appendChild(nameLabel);

        Element nameInput = doc.createElement("input");
        nameInput.setAttribute("type", "text");
        nameInput.setAttribute("id", "name");
        nameInput.setAttribute("name", "name");
        nameInput.setAttribute("required", "true");
        form.appendChild(nameInput);

        form.appendChild(doc.createElement("br"));

        // Firstname field
        Element firstnameLabel = doc.createElement("label");
        firstnameLabel.setAttribute("for", "firstname");
        firstnameLabel.setTextContent("Firstname:");
        form.appendChild(firstnameLabel);

        Element firstnameInput = doc.createElement("input");
        firstnameInput.setAttribute("type", "text");
        firstnameInput.setAttribute("id", "firstname");
        firstnameInput.setAttribute("name", "firstname");
        firstnameInput.setAttribute("required", "true");
        form.appendChild(firstnameInput);

        form.appendChild(doc.createElement("br"));

        // Password field
        Element passwordLabel = doc.createElement("label");
        passwordLabel.setAttribute("for", "password");
        passwordLabel.setTextContent("Password:");
        form.appendChild(passwordLabel);

        Element passwordInput = doc.createElement("input");
        passwordInput.setAttribute("type", "password");
        passwordInput.setAttribute("id", "password");
        passwordInput.setAttribute("name", "password");
        passwordInput.setAttribute("required", "true");
        form.appendChild(passwordInput);

        form.appendChild(doc.createElement("br"));

        // Faculty field
        Element facultyLabel = doc.createElement("label");
        facultyLabel.setAttribute("for", "faculty");
        facultyLabel.setTextContent("Faculty:");
        form.appendChild(facultyLabel);

        Element facultyInput = doc.createElement("input");
        facultyInput.setAttribute("type", "text");
        facultyInput.setAttribute("id", "faculty");
        facultyInput.setAttribute("name", "faculty");
        facultyInput.setAttribute("required", "true");
        form.appendChild(facultyInput);

        form.appendChild(doc.createElement("br"));

        // Groups (Checkboxes)
        Element groupsLabel = doc.createElement("label");
        groupsLabel.setTextContent("Groups:");
        form.appendChild(groupsLabel);

        // Add checkboxes using the helper function
        form.appendChild(createCheckbox(doc, "g_lecturer", "Lecturer"));
        form.appendChild(createCheckbox(doc, "g_employee", "Employee"));
        form.appendChild(createCheckbox(doc, "g_professor", "Professor"));
        form.appendChild(createCheckbox(doc, "g_student", "Student"));

        form.appendChild(doc.createElement("br"));

        // Submit button
        Element submitButton = doc.createElement("button");
        submitButton.setAttribute("type", "submit");
        submitButton.setTextContent("Create User");
        form.appendChild(submitButton);
        ClientLogger.getInstance().addEvent(new Event("User", "root", "Form", "FormDOM generated"));

        return doc;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String innerHtml;
        String contextPath = req.getContextPath();
        try {
            Document doc = generateDOM(contextPath);
            innerHtml = DOMUtils.documentToString(doc);
            req.setAttribute("domHtml", innerHtml);
            RequestDispatcher dispatcher = req.getRequestDispatcher("/base.jsp");
            dispatcher.forward(req, res);
        } catch (ParserConfigurationException | TransformerException | ServletException e) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to generate DOM HTML");
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        // extract params
        String username = req.getParameter("username");
        String name = req.getParameter("name");
        String firstname = req.getParameter("firstname");
        String password = req.getParameter("password");
        String faculty = req.getParameter("faculty");
        String[] groups = req.getParameterValues("group"); // Can be null if no group is selected

        // Convert groups into group pairs
        ArrayList<Pair<String, String>> groupPairs = new ArrayList<>();
        if (groups != null) {
            for (String key : groups) {
                String val = GroupMappings.GROUP_MAP.get(key);
                groupPairs.add(new Pair<>(key, val));
            }
        }

        // Create XML element
        // Expected:
        /*
        <User username="hbrosen2">
        <name>Brosenne</name>
        <firstname>Hendrik</firstname>
        <faculty>Computer Science</faculty>
        <group id="g_lecturer">Lecturer</group>
        <group id="g_employee">Employee</group>
        </User>
         */
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // this should never happen
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Document doc = dBuilder.newDocument();

        Element userElement = doc.createElement("User");
        userElement.setAttribute("username", username);
        doc.appendChild(userElement);

        Element nameElement = doc.createElement("name");
        nameElement.appendChild(doc.createTextNode(name));
        userElement.appendChild(nameElement);

        Element firstnameElement = doc.createElement("firstname");
        firstnameElement.appendChild(doc.createTextNode(firstname));
        userElement.appendChild(firstnameElement);

        Element passwordElement = doc.createElement("password");
        passwordElement.appendChild(doc.createTextNode(password));
        userElement.appendChild(passwordElement);

        Element facultyElement = doc.createElement("faculty");
        facultyElement.appendChild(doc.createTextNode(faculty));
        userElement.appendChild(facultyElement);

        for (Pair<String, String> groupPair : groupPairs) {
            Element groupElement = doc.createElement("group");
            groupElement.setAttribute("id", groupPair.fst);
            groupElement.appendChild(doc.createTextNode(groupPair.snd));
            userElement.appendChild(groupElement);
        }

        // Convert XML Document to String
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        // TODO refactor out try
        try {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
        DOMSource source = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        // TODO refactor out try
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        String xml = writer.toString();

        // check if it is already here
        if (UserDB.usernameExist(username)) {
            UserDB.deleteUser(username);
        }
        CreateNewUserServlet.doAdd(xml, req, res);



        // if we got here we succeeded
        res.setContentType("application/xml");
        ClientLogger.getInstance().addEvent(new Event("User", "root", "Form", "Form successfully saved"));
        try (PrintWriter out = res.getWriter()) {
            out.write(xml);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error writing response");

        }
    }
}