package xmw.exa.models.lectures;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.time.format.SignStyle;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.Nullable;
import xmw.exa.db.DB;
import xmw.exa.util.Config;
import xmw.exa.util.ExaServlet;
import xmw.exa.util.Util;
import xmw.flush.*;

@WebServlet(name = "lectures", value = "/lectures")
public class LecturesServlet extends ExaServlet {
    private String name;
    private DB db;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void init() {
        name = "Lectures";
        db = DB.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var lectureData = db.lectures().all();
        var lectures = new Lectures();
        lectures.getLecture().addAll(lectureData);
        var responseData = DB.marshal(lectures);
        PrintWriter out = response.getWriter();
        out.println(responseData);
        out.flush();
    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String[] requiredParams = {"course", "start", "end", "room"};
        final Map<String, String> defaultRawDto = new HashMap<>();
        defaultRawDto.put("course", "");
        defaultRawDto.put("start", "");
        defaultRawDto.put("end", "");
        defaultRawDto.put("room", "");

        var lecture = makeLecture(request, response, defaultRawDto, requiredParams);
        if (lecture == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Add the lecture to the database
        db.lectures().create(lecture);

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.setContentType("application/xml");
        PrintWriter out = response.getWriter();
        out.println(DB.marshal(lecture));
        out.flush();
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String[] requiredParams = {"course", "start", "end", "room"};
        Map<String, String> defaultRawDto = new HashMap<>();
        defaultRawDto.put("course", "");
        defaultRawDto.put("start", "");
        defaultRawDto.put("end", "");
        defaultRawDto.put("room", "");

        defaultRawDto = Util.makeUpdatedDto(requiredParams, defaultRawDto, request, response);

        if (defaultRawDto == null) {
            return;
        }

        // verify that the lecture exists
        var lecture = db.lectures().get(defaultRawDto.get("id"));
        if (lecture == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Lecture does not exist");
            return;
        }

        // Create the lecture
        lecture = makeLecture(request, response, defaultRawDto, requiredParams);
        if (lecture == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Update the lecture
        lecture = db.lectures().update(lecture);
        if (lecture == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update lecture");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/xml");
        PrintWriter out = response.getWriter();
        out.println(DB.marshal(lecture));
        out.flush();
    }

    @Nullable
    private Lecture makeLecture(HttpServletRequest request, HttpServletResponse response, Map<String, String> defaultRawDto, String[] requiredParams) {
        Map<String, String> rawDto = Util.getRawDto(defaultRawDto, requiredParams, request, response);
        if (rawDto == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        // Verify that start and end are valid dates
        if (!Util.verifyDate(rawDto.get("start")) || !Util.verifyDate(rawDto.get("end"))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        // Verify that the course exists
        var course = db.courses().get(rawDto.get("course"));
        if (course == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        // Create the lecture
        var lecture = new Lecture();

        if (rawDto.containsKey("id")) {
            lecture = db.lectures().get(rawDto.get("id"));
        }
        lecture.getStartOrEndOrRoomOrLink().clear();

        lecture.setCourse(course);
        Start start = new Start();
        start.setContent(rawDto.get("start"));
        lecture.getStartOrEndOrRoomOrLink().add(start);
        End end = new End();
        end.setContent(rawDto.get("end"));
        lecture.getStartOrEndOrRoomOrLink().add(end);
        RoomOrLink room = new RoomOrLink();
        room.setContent(rawDto.get("room"));
        lecture.getStartOrEndOrRoomOrLink().add(room);
        return lecture;
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Util.deleteItem(db.lectures(), request, response);
    }
}