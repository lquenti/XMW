package xmw.exa;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "lecturers", value = "/lecturers")
public class LecturersServlet extends HttpServlet {
    private String name;

    @Override
    public void init() {
        name = "Lecturers";
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        request.setAttribute("name", this.name);
        request.setAttribute("message", "This is a collection of lecturers.");

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/collection.jsp");
        try {
            dispatcher.forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
    }
}