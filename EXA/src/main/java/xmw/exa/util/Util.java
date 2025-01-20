package xmw.exa.util;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.Nullable;
import xmw.ClientLogger;
import xmw.Event;
import xmw.exa.db.repository.BaseXmlRepository;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;

public class Util {
    private static final char[] CUSTOM_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    private Util() {
    }

    public static String generateId() {
        return NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, CUSTOM_ALPHABET, 21);
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

    @Nullable
    public static Map<String, String> makeUpdatedDto(String[] requiredParams,
                                                     Map<String, String> defaultRawDto,
                                                     HttpServletRequest request,
                                                     HttpServletResponse response) throws IOException {
        try {
            String id = request.getParameter("id");
            if (id == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing id parameter");
                return null;
            }

            // verify that the parameters from requiredParams are present
            for (String requiredParam : requiredParams) {
                if (!defaultRawDto.containsKey(requiredParam)) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameter: " + requiredParam);
                    return null;
                } else {
                    String value = request.getParameter(requiredParam);
                    if (value == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameter: " + requiredParam);
                        return null;
                    }
                    defaultRawDto.put(requiredParam, value);
                }
            }

            // fill in the rest of the parameters
            var keys = defaultRawDto.keySet();
            for (String key : keys) {
                if (defaultRawDto.get(key).isEmpty()
                        && request.getParameter(key) != null
                        && !key.equals("id")
                        && !Arrays.asList(requiredParams).contains(key)) {
                    String value = request.getParameter(key);
                    if (value != null) {
                        defaultRawDto.put(key, value);
                    }
                }
            }

            // add id
            defaultRawDto.put("id", id);

            return defaultRawDto;
        } catch (Exception e) {
            throw new IOException("Failed to update courses: " + e.getMessage(), e);
        }
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
            Event event = new Event("DTO Mapper", "root", "INFO", "Parameter: " + paramName);
            ClientLogger.getInstance().addEvent(event);
            if (paramValues.length == 0) {
                event = new Event("DTO Mapper", "root", "DEBUG", "No values");
                ClientLogger.getInstance().addEvent(event);
                continue;
            }
            if (!defaultRawDto.containsKey(paramName)) {
                event = new Event("DTO Mapper", "root", "ERROR", "Invalid parameter name " + paramName);
                ClientLogger.getInstance().addEvent(event);
            } else {
                event = new Event("DTO Mapper", "root", "DEBUG", "Valid parameter name " + paramName);
                ClientLogger.getInstance().addEvent(event);

                StringBuilder sb = new StringBuilder();
                if (paramValues.length > 1) {
                    event = new Event("DTO Mapper", "root", "DEBUG", "Multiple values");
                    ClientLogger.getInstance().addEvent(event);
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

                event = new Event("DTO Mapper", "root", "DEBUG", "Value: " + sb);
                ClientLogger.getInstance().addEvent(event);

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
