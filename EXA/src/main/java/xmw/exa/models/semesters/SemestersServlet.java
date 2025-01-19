package xmw.exa.models.semesters;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.Nullable;
import xmw.exa.db.DB;
import xmw.exa.util.ExaServlet;
import xmw.exa.util.Util;
import xmw.flush.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "semesters", value = "/semesters")
public class SemestersServlet extends ExaServlet {
    private DB db;

    @Override
    public void init() {
        db = DB.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var semesterData = db.semesters().all();
        var semesters = new Semesters();
        semesters.getSemester().addAll(semesterData);
        var responseData = DB.marshal(semesters);
        PrintWriter out = response.getWriter();
        out.println(responseData);
        out.flush();
    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String[] requiredParams = {"name", "start", "end"};
        final Map<String, String> defaultRawDto = new HashMap<>();
        defaultRawDto.put("name", "");
        defaultRawDto.put("start", "");
        defaultRawDto.put("end", "");

        Semester semester = makeSemester(request, response, defaultRawDto, requiredParams);
        if (semester == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        };

        // Add the semester
        boolean success = db.semesters().create(semester);
        if (!success) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.setContentType("application/xml");
        PrintWriter out = response.getWriter();
        out.println(DB.marshal(semester));
        out.flush();
    }

    @Nullable
    private Semester makeSemester(HttpServletRequest request, HttpServletResponse response, Map<String, String> defaultRawDto, String[] requiredParams) {
        var rawDto = Util.getRawDto(defaultRawDto, requiredParams, request, response);
        if (rawDto == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        // Verify that start and end are valid dates
        if (!Util.verifyDate(rawDto.get("start")) || !Util.verifyDate(rawDto.get("end"))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        // Verify that the semester does not already exist
        boolean exists = false;

        for (var semester : db.semesters().all()) {
            if (semester.getNameOrStartOrEnd().getFirst().equals(rawDto.get("name"))) {
                exists = true;
                break;
            }
            if (semester.getNameOrStartOrEnd().get(1).equals(rawDto.get("name"))) {
                exists = true;
                break;
            }
            if (semester.getNameOrStartOrEnd().get(2).equals(rawDto.get("name"))) {
                exists = true;
                break;
            }
        }

        if (exists) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return null;
        }

        // Create the semester
        Semester semester = new Semester();
        Name name = new Name();
        name.setContent(rawDto.get("name"));
        semester.getNameOrStartOrEnd().add(name);
        Start start = new Start();
        start.setContent(rawDto.get("start"));
        semester.getNameOrStartOrEnd().add(start);
        End end = new End();
        end.setContent(rawDto.get("end"));
        semester.getNameOrStartOrEnd().add(end);
        return semester;
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Util.deleteItem(db.semesters(), request, response);
    }
}