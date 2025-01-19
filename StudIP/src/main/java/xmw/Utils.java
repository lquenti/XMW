package xmw;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class Utils {
    public static void log(HttpServletRequest request, HttpServletResponse response, ClientLogger logger, String Type, String Desc) throws ServletException, IOException {
        log(request, response, logger, Type, Desc, false);
    }

    public static void log(HttpServletRequest request, HttpServletResponse response, ClientLogger logger, String Type, String Desc, Boolean logIn) throws ServletException, IOException {
        String userId = getLoggedInUserId(request);
        if (logIn && userId == null) {
            // Redirect to login page if user is not logged in
            logger.addEvent(new Event("StudIP", "", "ClientNotLoggedInEvent", "Client is not logged in."));
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }
        if(userId == null)
            userId = "";
        logger.addEvent(new Event("StudIP", userId, Type, Desc));
    }

    /**
     * Checks the cookies for a logged-in user and returns the user ID.
     *
     * @param request The HttpServletRequest object containing cookies.
     * @return The user ID if the user is logged in, or null otherwise.
     */
    public static String getLoggedInUserId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("xmw_studip_userid".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null; // for testing purposes
    }
}