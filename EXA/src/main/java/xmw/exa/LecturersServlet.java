package xmw.exa;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.DB;
import xmw.exa.db.Lecturer;

@WebServlet(name = "lecturers", value = "/lecturers")
public class LecturersServlet extends HttpServlet {
    private String name;
    private DB db;

    @Override
    public void init() {
        name = "Lecturers";
        db = new DB();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        request.setAttribute("name", this.name);

        List<Lecturer> lecturers = db.getAllLecturers();
        StringBuilder message = new StringBuilder("<ul>");
        for (Lecturer lecturer : lecturers) {
            message
                    .append("<li>")
                    .append("<a href=\"/lecturers/")
                    .append(lecturer.getUsername())
                    .append("\">")
                    .append(lecturer.getFirstname()).append(" ").append(lecturer.getName())
                    .append("</a>")
                    .append("</li>");
        }
        message.append("</ul>");

        request.setAttribute("message", message.toString());

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/collection.jsp");
        try {
            dispatcher.forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        if (db != null) {
            db.close();
        }
    }
}