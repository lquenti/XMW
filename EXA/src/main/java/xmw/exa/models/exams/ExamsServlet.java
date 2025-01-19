package xmw.exa.models.exams;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.DB;
import xmw.exa.util.ExaServlet;
import xmw.exa.util.Util;

@WebServlet(name = "exams", value = "/exams")
@MultipartConfig
public class ExamsServlet extends ExaServlet {
    private String name;
    private DB db;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Override
    public void init() {
        name = "Exams";
        db = DB.getInstance();
    }


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var examData = db.exams().all();
        var exams = new xmw.flush.Exams();
        exams.getExam().addAll(examData);
        var responseData = DB.marshal(exams);
        PrintWriter out = response.getWriter();
        out.println(responseData);
        out.flush();
    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String[] requiredParams = {"course", "date"};
        final Map<String, String> defaultRawDto = new HashMap<>();
        defaultRawDto.put("course", "");
        defaultRawDto.put("date", "");
        defaultRawDto.put("is_online", "false");
        defaultRawDto.put("is_written", "false");
        defaultRawDto.put("room_or_link", "No room or link provided");

        // Get the raw DTO
        Map<String, String> rawDto = Util.getRawDto(defaultRawDto, requiredParams, request, response);

        if (rawDto == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters");
            return;
        }

        // Create PrintWriter for response
        PrintWriter out = response.getWriter();

        // Print the RawDto
        out.println("Course: " + rawDto.get("course"));
        out.println("Date: " + rawDto.get("date"));
        out.println("Is online: " + rawDto.get("is_online"));
        out.println("Is written: " + rawDto.get("is_written"));
        out.println("Room or link: " + rawDto.get("room_or_link"));

        // Close the PrintWriter
        out.close();
    }


    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Util.deleteItem(db.exams(), request, response);
    }
}