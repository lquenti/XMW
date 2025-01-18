package xmw.exa.models.semesters;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.DB;
import xmw.exa.util.ExaServlet;
import xmw.exa.util.Util;
import xmw.flush.Semester;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

@WebServlet(name = "semester", urlPatterns = "/semesters/*")
public class SemesterServlet extends ExaServlet {
    private DB db;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void init() {
        db = DB.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String semesterId = Util.getPathParameter("semesters", request, response);

        // Fetch semester data
        Semester semesterData = db.semesters().get(semesterId);
        if (semesterData == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Semester not found");
            return;
        }

        // Marshal semester data to XML
        String responseData = DB.marshal(semesterData);

        // Write XML response
        Util.writeXmlResponse(responseData, response);
    }
}

