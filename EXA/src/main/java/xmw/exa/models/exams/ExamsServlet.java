package xmw.exa.models.exams;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

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
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Util.deleteItem(db.exams(), request, response);
    }
}