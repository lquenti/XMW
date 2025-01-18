package xmw.exa.models.lectures;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.DB;
import xmw.exa.util.ExaServlet;
import xmw.exa.util.Util;
import xmw.flush.Lecture;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

@WebServlet(name = "lecture", urlPatterns = "/lectures/*")
public class LectureServlet extends ExaServlet {
    private DB db;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void init() {
        db = DB.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String lectureId = Util.getPathParameter("lectures", request, response);

        // Fetch lecture data
        Lecture lectureData = db.lectures().get(lectureId);
        if (lectureData == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Lecture not found");
            return;
        }

        // Marshal lecture data to XML
        String responseData = DB.marshal(lectureData);

        // Write XML response
        Util.writeXmlResponse(responseData, response);
    }
}
