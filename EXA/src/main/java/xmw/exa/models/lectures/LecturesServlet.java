package xmw.exa.models.lectures;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Util.deleteItem(db.lectures(), request, response);
    }
}