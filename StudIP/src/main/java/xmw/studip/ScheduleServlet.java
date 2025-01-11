package xmw.studip;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Serial;
import java.util.List;
import java.util.Map;

@WebServlet("/schedule")
public class ScheduleServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = AuthUtil.getLoggedInUserId(request);
        if (userId == null) {
            // Redirect to login page if user is not logged in
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        XMLDatabase xmlDatabase = (XMLDatabase) getServletContext().getAttribute("xmlDatabase");
        
        // Mock fetching schedule for a student from XML API (via XMLDataImporter)
        List<Map<String, String>> schedule;
        try {
            schedule = xmlDatabase.getScheduleForStudent(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        request.setAttribute("schedules", schedule);
        request.getRequestDispatcher("/schedule.jsp").forward(request, response);
    }
}

