package xmw.studip;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serial;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("errorMessage", "Username and password are required.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        String loginApiUrl = AppContextListener.USER_URL + "auth"; // Replace with actual API URL
        URL url = new URL(loginApiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        String requestBody = "username=" + username + "&password=" + password;
        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestBody.getBytes());
            os.flush();
        }

        Document doc;
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            String xmlResponse = new String(connection.getInputStream().readAllBytes());

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            try {
                builder = factory.newDocumentBuilder();
                doc = builder.parse(new ByteArrayInputStream(xmlResponse.getBytes()));
            } catch (SAXException | ParserConfigurationException e) {
                throw new RuntimeException(e);
            }

            NodeList nodeList = doc.getElementsByTagName("User");
            Map<String, String> userMap = new HashMap<>();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element course = (Element) nodeList.item(i);

                userMap.put("userID", course.getAttribute("username"));
                userMap.put("Name", course.getElementsByTagName("Name").item(0).getTextContent());
                userMap.put("Faculty", course.getElementsByTagName("Faculty").item(0).getTextContent());
            }

            // Set a cookie with the user ID
            Cookie userCookie = new Cookie("xmw_studip_userid", username);
            userCookie.setPath("/");
            response.addCookie(userCookie);

            request.setAttribute("loginResponse", xmlResponse);
            request.getRequestDispatcher("/login_result.jsp").forward(request, response);
        } else if (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
            request.setAttribute("errorMessage", "Invalid login parameters. Please try again.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "Login failed. Please try again later.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
