package xmw.exa.models.exams;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.Nullable;
import xmw.exa.db.DB;
import xmw.exa.util.ExaServlet;
import xmw.exa.util.Util;
import xmw.flush.*;

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

        var exam = makeExam(request, response, defaultRawDto, requiredParams);
        if (exam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Add the exam to the database
        boolean success = db.exams().create(exam);
        if(!success) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create exam");
            return;
        }

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.setContentType("application/xml");
        // Create PrintWriter for response
        PrintWriter out = response.getWriter();
        out.println(DB.marshal(exam));
        out.close();
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String[] requiredParams = {"course", "date"};
         Map<String, String> defaultRawDto = new HashMap<>();
        defaultRawDto.put("course", "");
        defaultRawDto.put("date", "");
        defaultRawDto.put("is_online", "false");
        defaultRawDto.put("is_written", "false");
        defaultRawDto.put("room_or_link", "No room or link provided");

        defaultRawDto = Util.makeUpdatedDto(requiredParams, defaultRawDto, request, response);

        if (defaultRawDto == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // verify that the exam exists
        var exam = db.exams().get(defaultRawDto.get("id"));
        if (exam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Exam does not exist");
            return;
        }

        // verify that the course exists
        var course = db.courses().get(defaultRawDto.get("course"));
        if (course == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Course does not exist");
            return;
        }

        // Create the exam
        exam = makeExam(request, response, defaultRawDto, requiredParams);
        if (exam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Update the exam
        exam = db.exams().update(exam);

        if (exam == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update exam");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/xml");
        PrintWriter out = response.getWriter();
        out.println(DB.marshal(exam));
        out.flush();
    }

    @Nullable
    private Exam makeExam(HttpServletRequest request, HttpServletResponse response, Map<String, String> defaultRawDto, String[] requiredParams) throws IOException {
        // Get the raw DTO
        Map<String, String> rawDto = Util.getRawDto(defaultRawDto, requiredParams, request, response);

        if (rawDto == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters");
            return null;
        }

        // Verify the date
        if (!Util.verifyDate(rawDto.get("date"))) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid date format");
            return null;
        }

        // Verify course exists
        var course = db.courses().get(rawDto.get("course").strip());
        if (course == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Course not found");
            return null;
        }

        // Create the exam
        var exam = new Exam();

        if (rawDto.containsKey("id")) {
            exam.setId(rawDto.get("id"));
        }

        exam.setCourse(course);

        Date date = new Date();
        IsWritten isWritten = new IsWritten();
        IsOnline isOnline = new IsOnline();
        RoomOrLink roomOrLink = new RoomOrLink();

        date.setContent(rawDto.get("date"));
        isOnline.setContent(rawDto.get("is_online"));
        isWritten.setContent(rawDto.get("is_written"));
        roomOrLink.setContent(rawDto.get("room_or_link"));
        exam.getDateOrIsOnlineOrIsWritten().add(date);
        exam.getDateOrIsOnlineOrIsWritten().add(isOnline);
        exam.getDateOrIsOnlineOrIsWritten().add(isWritten);
        exam.getDateOrIsOnlineOrIsWritten().add(roomOrLink);
        return exam;
    }


    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Util.deleteItem(db.exams(), request, response);
    }
}