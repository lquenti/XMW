package xmw.exa.models.lecturers;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.DB;
import xmw.exa.util.ExaServlet;
import xmw.exa.util.Util;
import xmw.flush.Lecturer;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "lecturer", urlPatterns = "/lecturers/*")
public class LecturerServlet extends ExaServlet {
    private DB db;

    @Override
    public void init() {
        db = DB.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String lecturerId = Util.getPathParameter("lecturers", request, response);

        // Fetch lecturer data
        Lecturer lecturerData = db.lecturers().get(lecturerId);
        if (lecturerData == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Lecturer not found");
            return;
        }

        // Marshal lecturer data to XML
        String responseData = DB.marshal(lecturerData);

        // Write XML response
        Util.writeXmlResponse(responseData, response);
    }
}
