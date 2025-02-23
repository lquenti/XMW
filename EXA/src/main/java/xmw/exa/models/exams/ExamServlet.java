package xmw.exa.models.exams;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.DB;
import xmw.exa.util.ExaServlet;
import xmw.exa.util.Util;
import xmw.flush.Exam;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

@WebServlet(name = "exam", urlPatterns = "/exams/*")
public class ExamServlet extends ExaServlet {
    private DB db;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void init() {
        db = DB.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String examId = Util.getPathParameter("exams", request, response);

        // Fetch exam data
        Exam examData = db.exams().get(examId);
        if (examData == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Exam not found");
            return;
        }

        // Marshal exam data to XML
        String responseData = DB.marshal(examData);

        // Write XML response
        Util.writeXmlResponse(responseData, response);
    }
}
