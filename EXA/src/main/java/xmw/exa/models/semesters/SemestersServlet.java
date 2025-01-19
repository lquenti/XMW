package xmw.exa.models.semesters;

import jakarta.servlet.annotation.WebServlet;
import java.util.Enumeration;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.DB;
import xmw.exa.util.ExaServlet;
import xmw.exa.util.Util;
import xmw.flush.Semesters;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

@WebServlet(name = "semesters", value = "/semesters")
public class SemestersServlet extends ExaServlet {
    private String name;
    private DB db;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void init() {
        name = "Semesters";
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
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Util.deleteItem(db.semesters(), request, response);
    }
}