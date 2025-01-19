package xmw.exa.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.repository.BaseXmlRepository;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Enumeration;
import java.util.Map;
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

    public static void deleteItem(BaseXmlRepository<?> repository, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String requestParameter = request.getParameter("id");
            if (requestParameter == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing courseId parameter");
                return;
            }
            // Delete all data
            boolean success = repository.delete(requestParameter);
            if (!success) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Course not found/failed to delete");
                return;
            }
            PrintWriter out = response.getWriter();
            out.println("Item deleted");
            out.flush();
        } catch (Exception e) {
            throw new IOException("Failed to delete courses: " + e.getMessage(), e);
        }
    }

    public static Map<String, String> getRawDto(Map<String, String> defaultRawDto,
                                                String[] requiredParams, HttpServletRequest request,
                                                HttpServletResponse response) {
        // Set response content type
        response.setContentType("text/plain");

        // Get all parameter names from the request
        Enumeration<String> parameterNames = request.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);

            // Print parameter name and its values
            System.out.println("Parameter: " + paramName);
            if (paramValues.length == 0) {
                System.out.println("No values");
                continue;
            }
            if (!defaultRawDto.containsKey(paramName)) {
                System.err.println("Invalid parameter name " + paramName);
            } else {
                System.out.println("Valid parameter name " + paramName);

                StringBuilder sb = new StringBuilder();
                if (paramValues.length > 1) {
                    System.out.println("Multiple values");
                    for (int i = 0; i < paramValues.length; i++) {
                        String value = paramValues[i];
                        sb.append(value);
                        if (i < paramValues.length - 1) {
                            sb.append(";");
                        }
                    }
                } else {
                    sb.append(paramValues[0]);
                }

                System.out.println("Value: " + sb);

                defaultRawDto.put(paramName, sb.toString());
            }
        }

        return validateParams(defaultRawDto, requiredParams, response);
    }

    public static Map<String, String> validateParams(Map<String, String> defaultRawDto, String[] requiredParams,
                                                     HttpServletResponse response) {
        // validate the data
        for (String requiredParam : requiredParams) {
            if (defaultRawDto.get(requiredParam).isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return null;
            }
        }
        return defaultRawDto;
    }

    // verifies iso date string without timezone
    public static boolean verifyDate(String date) {
        date = date.strip();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        try {
            formatter.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            try {
                formatter.parse(date);
                return true;
            } catch (DateTimeParseException f) {
                System.err.println("Invalid date format: " + date + " " + e.getMessage());
                System.err.println("Invalid date format: " + date + " " + f.getMessage());
                return false;
            }
        }
    }
}
