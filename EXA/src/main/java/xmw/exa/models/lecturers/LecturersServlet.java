package xmw.exa.models.lecturers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.basex.core.BaseXException;
import org.basex.core.cmd.XQuery;

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

@WebServlet(name = "lecturers", value = "/lecturers")
public class LecturersServlet extends ExaServlet {
    private String name;
    private DB db;
    private static final String DB_NAME = "exa";

    @Override
    public void init() {
        name = "Lecturers";
        db = DB.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var lecturerData = db.lecturers().all();
        var lecturers = new Lecturers();
        lecturers.getLecturer().addAll(lecturerData);
        var responseData = DB.marshal(lecturers);
        PrintWriter out = response.getWriter();
        out.println(responseData);
        out.flush();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // /users handles lecturer creation
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Util.deleteItem(db.lecturers(), request, response);
    }
}

