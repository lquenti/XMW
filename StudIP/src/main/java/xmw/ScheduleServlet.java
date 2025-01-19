package xmw;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Serial;
import java.util.*;

import static xmw.Utils.joinListOfMaps;

@WebServlet("/schedule")
public class ScheduleServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Utils.log(request, response, (ClientLogger) getServletContext().getAttribute("logger"), "SiteVisitedEvent", "User visiting schedule", true);
        String userId = Utils.getLoggedInUserId(request);

        XMLDatabase xmlDatabase = (XMLDatabase) getServletContext().getAttribute("xmlDatabase");
        
        // Mock fetching schedule for a student from XML API (via XMLDataImporter)
        List<Map<String, String>> schedule;
        try {
            schedule = xmlDatabase.getScheduleForStudent(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<Map<String, String>> courses;
        try {
            courses = xmlDatabase.mergeCoursesAndLectures(xmlDatabase.getCourses(), xmlDatabase.getLectures());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        joinListOfMaps(schedule, courses, "CourseID", "CourseID");
        
        List<Map<String, String>> completeSchedule = new ArrayList<>();
        for(Map<String, String> s: schedule){
            List<Map<String, String>> tmp = convertSchedule(s);
            tmp.removeFirst();
            completeSchedule.addAll(convertSchedule(s));
        }
        completeSchedule.removeFirst();
        completeSchedule.sort(mapComparator);

        request.setAttribute("schedules", completeSchedule);
        request.getRequestDispatcher("/schedule.jsp").forward(request, response);
    }

    public Comparator<Map<String, String>> mapComparator =
            Comparator.comparing(m -> m.getOrDefault("Begin", ""));

    public static List<Map<String, String>> convertSchedule(Map<String, String> schedule) {
        List<Map<String, String>> result = new ArrayList<>();

        // Extract shared fields from the input map
        Map<String, String> sharedFields = new HashMap<>();
        for (Map.Entry<String, String> entry : schedule.entrySet()) {
            if (!entry.getKey().matches("(Begin\\d+|End\\d+|Location\\d+)")) {
                sharedFields.put(entry.getKey(), entry.getValue());
            }
        }

        // Identify the unique suffixes (e.g., 0, 1, 2) for Begin, End, Location
        Set<String> suffixes = new HashSet<>();
        for (String key : schedule.keySet()) {
            if (key.startsWith("Begin") || key.startsWith("End") || key.startsWith("Location")) {
                suffixes.add(key.replaceAll("(Begin|End|Location)", ""));
            }
        }

        // Build individual maps for each session
        for (String suffix : suffixes) {
            Map<String, String> sessionMap = new HashMap<>(sharedFields); // Include shared fields

            // Add Begin, End, and Location values (if present)
            sessionMap.put("Begin", schedule.getOrDefault("Begin" + suffix, ""));
            sessionMap.put("End", schedule.getOrDefault("End" + suffix, ""));
            sessionMap.put("Location", schedule.getOrDefault("Location" + suffix, ""));

            result.add(sessionMap);
        }

        return result;
    }

}

