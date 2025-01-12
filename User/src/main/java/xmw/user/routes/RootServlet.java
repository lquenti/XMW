package xmw.user.routes;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xmw.user.utils.DOMUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.ArrayList;

class Payload {
    public String mimeType;
    public String payload;

    public Element toHTML(Document doc) {
        Element div = doc.createElement("div");
        div.setAttribute("class", "Payload");

        Element mimeTypeElem = doc.createElement("b");
        mimeTypeElem.setTextContent("Mime Type: ");
        div.appendChild(mimeTypeElem);
        div.appendChild(doc.createTextNode(mimeType));
        div.appendChild(doc.createElement("br"));

        Element payloadElem = doc.createElement("b");
        payloadElem.setTextContent("Payload: ");
        div.appendChild(payloadElem);

        Element pre = doc.createElement("pre");
        Element code = doc.createElement("code");
        code.setTextContent(payload);
        pre.appendChild(code);
        div.appendChild(pre);

        return div;
    }
}
class Response {
    public int returnCode;
    public String reason;
    public Payload payload; // possibly null

    public Element toHTML(Document doc) {
        Element div = doc.createElement("div");
        div.setAttribute("class", "Response");

        Element returnCodeElem = doc.createElement("b");
        returnCodeElem.setTextContent("Return Code: ");
        div.appendChild(returnCodeElem);
        div.appendChild(doc.createTextNode(String.valueOf(returnCode)));
        div.appendChild(doc.createElement("br"));

        Element reasonElem = doc.createElement("b");
        reasonElem.setTextContent("Reason: ");
        div.appendChild(reasonElem);
        div.appendChild(doc.createTextNode(reason));
        div.appendChild(doc.createElement("br"));

        Element payloadElem = doc.createElement("b");
        payloadElem.setTextContent("Payload: ");
        div.appendChild(payloadElem);
        if (payload != null) {
            div.appendChild(payload.toHTML(doc));
        } else {
            div.appendChild(doc.createTextNode("null"));
        }

        return div;
    }
}
class Endpoint {
    public enum Methods {
        GET, POST, PUT, DELETE
    }

    public String endpoint;
    public Methods method;
    public Payload requestPayload; // Possibly null
    public ArrayList<Response> responses;

    public Element toHTML(Document doc) {
        Element div = doc.createElement("div");
        div.setAttribute("class", "Endpoint");

        Element endpointElem = doc.createElement("h2");
        endpointElem.setTextContent("Endpoint: " + endpoint);
        div.appendChild(endpointElem);

        Element methodElem = doc.createElement("p");
        Element methodElem1 = doc.createElement("b");
        methodElem1.setTextContent("Method: ");
        methodElem.appendChild(methodElem1);
        methodElem.appendChild(doc.createTextNode(method.toString()));
        div.appendChild(methodElem);

        Element requestPayloadElem = doc.createElement("div");
        Element requestPayloadElem1 = doc.createElement("b");
        requestPayloadElem1.setTextContent("Request Payload: ");
        requestPayloadElem.appendChild(requestPayloadElem1);
        if (requestPayload != null) {
            requestPayloadElem.appendChild(requestPayload.toHTML(doc));
        } else {
            requestPayloadElem.appendChild(doc.createTextNode("null"));
        }
        div.appendChild(requestPayloadElem);

        Element responsesElem = doc.createElement("div");
        responsesElem.setAttribute("class", "Responses");
        Element responsesElem1 = doc.createElement("b");
        responsesElem1.setTextContent("Responses: ");
        responsesElem.appendChild(responsesElem1);
        for (Response response : responses) {
            responsesElem.appendChild(response.toHTML(doc));
        }
        div.appendChild(responsesElem);

        return div;
    }
}

@WebServlet(name = "rootServlet", value = "/")
public class RootServlet extends HttpServlet {

    private static Document generateDOM() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement("div");
        doc.appendChild(root);

        // Auth Endpoint
        Endpoint auth = new Endpoint();
        auth.endpoint = "/auth";
        auth.method = Endpoint.Methods.POST;
        auth.requestPayload = new Payload();
        auth.requestPayload.mimeType = "x-www-form-urlencoded";
        auth.requestPayload.payload = """
                username: <USERNAME>
                password: <PASSWORD>""";
        auth.responses = new ArrayList<>();

        Response authMissing = new Response();
        authMissing.returnCode = 400;
        authMissing.reason = "Missing Parameter";
        auth.responses.add(authMissing);

        Response authDenied = new Response();
        authDenied.returnCode = 403;
        authDenied.reason = "Wrong Username or Password";
        auth.responses.add(authDenied);

        Response authSuccess = new Response();
        authSuccess.returnCode = 200;
        authSuccess.reason = "Successfully Authenticated";
        authSuccess.payload = new Payload();
        authSuccess.payload.mimeType = "application/xml";
        authSuccess.payload.payload = """
                <User username="hbrosen">
                  <name>Brosenne</name>
                  <firstname>Hendrik</firstname>
                  <faculty>Computer Science</faculty>
                  <group id="g_lecturer">Lecturer</group>
                  <group id="g_employee">Employee</group>
                </User>
                """;
        auth.responses.add(authSuccess);

        // CRUD Read
        Endpoint getUserById = new Endpoint();
        getUserById.endpoint = "/id/<USERNAME>";
        getUserById.method = Endpoint.Methods.GET;
        getUserById.requestPayload = null;
        getUserById.responses = new ArrayList<>();

        Response getUserSuccess = new Response();
        getUserSuccess.returnCode = 200;
        getUserSuccess.reason = "Successfully Retrieved User";
        getUserSuccess.payload = new Payload();
        getUserSuccess.payload.mimeType = "application/xml";
        getUserSuccess.payload.payload = """
                <User username="hbrosen">
                  <name>Brosenne</name>
                  <firstname>Hendrik</firstname>
                  <faculty>Computer Science</faculty>
                  <group id="g_lecturer">Lecturer</group>
                  <group id="g_employee">Employee</group>
                </User>
                """;
        getUserById.responses.add(getUserSuccess);

        Response getUserNotFound = new Response();
        getUserNotFound.returnCode = 404;
        getUserNotFound.reason = "User Not Found";
        getUserById.responses.add(getUserNotFound);

        // CRUD Create
        Endpoint createUser = new Endpoint();
        createUser.endpoint = "/create";
        createUser.method = Endpoint.Methods.POST;
        createUser.requestPayload = new Payload();
        createUser.requestPayload.mimeType = "text/xml";
        createUser.requestPayload.payload = """
                <User username="hbrosen">
                  <name>Brosenne</name>
                  <firstname>Hendrik</name>
                  <password>hunter2</password> <!-- NOTE THE PASSWORD HERE -->
                  <faculty>Computer Science</faculty>
                  <group id="g_lecturer">Lecturer</group>
                  <group id="g_employee">Employee</group>
                </User>
                """;
        createUser.responses = new ArrayList<>();


        Response createSuccess = new Response();
        createSuccess.returnCode = 200;
        createSuccess.reason = "User Created Successfully";
        createUser.responses.add(createSuccess);

        Response createBadRequest = new Response();
        createBadRequest.returnCode = 400;
        createBadRequest.reason = "Bad Request";
        createBadRequest.payload = new Payload();
        createBadRequest.payload.mimeType = "text/plain";
        createBadRequest.payload.payload = "REASON...";
        createUser.responses.add(createBadRequest);

        // CRUD Update
        Endpoint updateUser = new Endpoint();
        updateUser.endpoint = "/update/<USERNAME>";
        updateUser.method = Endpoint.Methods.POST;
        updateUser.requestPayload = new Payload();
        updateUser.requestPayload.mimeType = "text/xml";
        updateUser.requestPayload.payload = """
        <User username="hbrosen">
          <name>Brosenne</name>
          <firstname>Hendrik</firstname>
          <password>hunter2</password> <!-- NOTE THE PASSWORD HERE -->
          <faculty>Computer Science</faculty>
          <group id="g_lecturer">Lecturer</group>
          <group id="g_employee">Employee</group>
        </User>
        """;
        updateUser.responses = new ArrayList<>();

        Response updateSuccess = new Response();
        updateSuccess.returnCode = 200;
        updateSuccess.reason = "User Updated Successfully";
        updateUser.responses.add(updateSuccess);

        Response updateNotFound = new Response();
        updateNotFound.returnCode = 404;
        updateNotFound.reason = "User Not Found";
        updateUser.responses.add(updateNotFound);

        Response updateBadRequest = new Response();
        updateBadRequest.returnCode = 400;
        updateBadRequest.reason = "Bad Request";
        updateBadRequest.payload = new Payload();
        updateBadRequest.payload.mimeType = "text/plain";
        updateBadRequest.payload.payload = "REASON...   ";
        updateUser.responses.add(updateBadRequest);

        // CRUD Delete
        Endpoint deleteUser = new Endpoint();
        deleteUser.endpoint = "/delete/<USERNAME>";
        deleteUser.method = Endpoint.Methods.DELETE;
        deleteUser.requestPayload = null;
        deleteUser.responses = new ArrayList<>();

        Response deleteSuccess = new Response();
        deleteSuccess.returnCode = 200;
        deleteSuccess.reason = "User Deleted Successfully";
        deleteUser.responses.add(deleteSuccess);

        Response deleteNotFound = new Response();
        deleteNotFound.returnCode = 404;
        deleteNotFound.reason = "User Not Found";
        deleteUser.responses.add(deleteNotFound);

        // Bulk POST
        Endpoint bulkOperation = new Endpoint();
        bulkOperation.endpoint = "/bulk";
        bulkOperation.method = Endpoint.Methods.POST;
        bulkOperation.requestPayload = new Payload();
        bulkOperation.requestPayload.mimeType = "application/xml";
        bulkOperation.requestPayload.payload = """
        <Users>
          <User username="hbrosen" />
          <User username="wmay" />
          <User username="lars.quentin" />
          <!-- ... -->
        </Users>
        """;
        bulkOperation.responses = new ArrayList<>();

        Response bulkSuccess = new Response();
        bulkSuccess.returnCode = 200;
        bulkSuccess.reason = "All Users Exist";
        bulkSuccess.payload = new Payload();
        bulkSuccess.payload.mimeType = "application/xml";
        bulkSuccess.payload.payload = """
        <Users>
          <User username="hbrosen">
            <name>Brosenne</name>
            <firstname>Hendrik</firstname>
            <faculty>Computer Science</faculty>
            <group id="g_lecturer">Lecturer</group>
            <group id="g_employee">Employee</group>
          </User>
          <User username="wmay">
            <name>May</name>
            <firstname>Wolfgang</firstname>
            <faculty>Computer Science</faculty>
            <group id="g_lecturer">Lecturer</group>
            <group id="g_employee">Employee</group>
          </User>
          <!-- ... -->
        </Users>
        """;
        bulkOperation.responses.add(bulkSuccess);

        Response bulkPartialContent = new Response();
        bulkPartialContent.returnCode = 206;
        bulkPartialContent.reason = "Partial Content: Some Users Not Found";
        bulkPartialContent.payload = new Payload();
        bulkPartialContent.payload.mimeType = "application/xml";
        bulkPartialContent.payload.payload = """
        <Users>
          <User username="hbrosen">
            <name>Brosenne</name>
            <firstname>Hendrik</firstname>
            <faculty>Computer Science</faculty>
            <group id="g_lecturer">Lecturer</group>
            <group id="g_employee">Employee</group>
          </User>
          <!-- Users that were not found are omitted -->
        </Users>
        """;
        bulkOperation.responses.add(bulkPartialContent);

        Response bulkNotFound = new Response();
        bulkNotFound.returnCode = 404;
        bulkNotFound.reason = "None of the Users were found";
        bulkOperation.responses.add(bulkNotFound);

        Response bulkBadRequest = new Response();
        bulkBadRequest.returnCode = 400;
        bulkBadRequest.reason = "Bad Request";
        bulkBadRequest.payload = new Payload();
        bulkBadRequest.payload.mimeType = "text/plain";
        bulkBadRequest.payload.payload = "REASON...";
        bulkOperation.responses.add(bulkBadRequest);

        // Bulk GET ALL
        Endpoint bulkGetUsers = new Endpoint();
        bulkGetUsers.endpoint = "/bulk";
        bulkGetUsers.method = Endpoint.Methods.GET;
        bulkGetUsers.requestPayload = null;
        bulkGetUsers.responses = new ArrayList<>();

        Response bulkGetSuccess = new Response();
        bulkGetSuccess.returnCode = 200;
        bulkGetSuccess.reason = "Successfully Retrieved All Users";
        bulkGetSuccess.payload = new Payload();
        bulkGetSuccess.payload.mimeType = "text/xml";
        bulkGetSuccess.payload.payload = """
        <Users>
          <User username="hbrosen">
            <name>Brosenne</name>
            <firstname>Hendrik</firstname>
            <faculty>Computer Science</faculty>
            <group id="g_lecturer">Lecturer</group>
            <group id="g_employee">Employee</group>
          </User>
          <User username="wmay">
            <name>May</name>
            <firstname>Wolfgang</firstname>
            <faculty>Computer Science</faculty>
            <group id="g_lecturer">Lecturer</group>
            <group id="g_employee">Employee</group>
          </User>
          <!-- ... -->
        </Users>
        """;
        bulkGetUsers.responses.add(bulkGetSuccess);

        // GET by Group
        Endpoint getUsersByGroup = new Endpoint();
        getUsersByGroup.endpoint = "/group/<GROUP_ID> (case sensitive)";
        getUsersByGroup.method = Endpoint.Methods.GET;
        getUsersByGroup.requestPayload = null;
        getUsersByGroup.responses = new ArrayList<>();

        Response getUsersByGroupSuccess = new Response();
        getUsersByGroupSuccess.returnCode = 200;
        getUsersByGroupSuccess.reason = "Successfully Retrieved Users for Group";
        getUsersByGroupSuccess.payload = new Payload();
        getUsersByGroupSuccess.payload.mimeType = "text/xml";
        getUsersByGroupSuccess.payload.payload = """
        <Users>
          <User username="hbrosen">
            <name>Brosenne</name>
            <firstname>Hendrik</firstname>
            <faculty>Computer Science</faculty>
            <group id="g_lecturer">Lecturer</group>
            <group id="g_employee">Employee</group>
          </User>
          <User username="wmay">
            <name>May</name>
            <firstname>Wolfgang</firstname>
            <faculty>Computer Science</faculty>
            <group id="g_lecturer">Lecturer</group>
            <group id="g_employee">Employee</group>
          </User>
          <!-- ... -->
        </Users>
        """;
        getUsersByGroup.responses.add(getUsersByGroupSuccess);

        Response getUsersByGroupNotFound = new Response();
        getUsersByGroupNotFound.returnCode = 404;
        getUsersByGroupNotFound.reason = "Group Not Found";
        getUsersByGroupNotFound.payload = new Payload();
        getUsersByGroupNotFound.payload.mimeType = "text/plain";
        getUsersByGroupNotFound.payload.payload = "The specified group does not exist.";
        getUsersByGroup.responses.add(getUsersByGroupNotFound);

        root.appendChild(auth.toHTML(doc));
        root.appendChild(getUserById.toHTML(doc));
        root.appendChild(createUser.toHTML(doc));
        root.appendChild(updateUser.toHTML(doc));
        root.appendChild(deleteUser.toHTML(doc));
        root.appendChild(bulkOperation.toHTML(doc));
        root.appendChild(bulkGetUsers.toHTML(doc));
        root.appendChild(getUsersByGroup.toHTML(doc));

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
