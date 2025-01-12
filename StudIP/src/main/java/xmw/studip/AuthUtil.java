package xmw.studip;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class AuthUtil {
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
