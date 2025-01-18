package xmw.exa.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

public class Util {
    private Util() {
    }

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    public static String getPathParameter(final String redirectURL, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();

        // Handle root path (course overview)
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(Config.BASE_URL + "/" + redirectURL);
            return "";
        }

        // Remove leading slash
        String courseId = pathInfo.substring(1);

        // Handle /all endpoint
        if (courseId.equals("all")) {
            response.sendRedirect(Config.BASE_URL + "/" + redirectURL);
            return "";
        }
        return courseId;
    }

    public static void writeXmlResponse(String xmlString, HttpServletResponse response) throws IOException {
        // Set response content type
        response.setContentType("application/xml");

        // Write response data
        PrintWriter out = response.getWriter();
        out.println(xmlString);
        out.flush();
    }
}
