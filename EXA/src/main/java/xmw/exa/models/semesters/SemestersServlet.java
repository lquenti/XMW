package xmw.exa.models.semesters;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

import org.basex.core.BaseXException;
import org.basex.core.cmd.XQuery;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.DB;
import xmw.exa.util.Config;
import xmw.flush.Semesters;

@WebServlet(name = "semesters", value = "/semesters")
public class SemestersServlet extends HttpServlet {
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
        return;
//        String pathInfo = request.getServletPath();
//        if (pathInfo.equals("/semesters/all")) {
//            String queryString = request.getQueryString();
//            response.sendRedirect(Config.BASE_URL + "/semesters" + (queryString != null ? "?" + queryString : ""));
//            return;
//        }
//
//        // Check format parameter
//        boolean isXmlFormat = "xml".equals(request.getParameter("format"));
//
//        if (isXmlFormat) {
//            try {
//                // Query for the complete semesters XML with proper indentation
//                String query = String.format(
//                        "let $semesters := /root/Semesters " +
//                                "return serialize(element semesters { " +
//                                "  for $s in $semesters/Semester " +
//                                "  return element semester { " +
//                                "    element id { $s/id/text() }, " +
//                                "    element name { $s/name/text() }, " +
//                                "    element start { $s/start/text() }, " +
//                                "    element end { $s/end/text() } " +
//                                "  } " +
//                                "}, map { 'method': 'xml', 'indent': 'yes' })",
//                        "exa");
//
//                String result = new XQuery(query).execute(db.getContext());
//
//                // Return XML response
//                response.setContentType("application/xml");
//                response.setCharacterEncoding("UTF-8");
//                PrintWriter out = response.getWriter();
//                out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//                out.println(result);
//                out.flush();
//                return;
//            } catch (BaseXException e) {
//                throw new IOException("Failed to query semesters: " + e.getMessage(), e);
//            }
//        }
//
//        // HTML response
//        response.setContentType("text/html");
//        request.setAttribute("name", this.name);
//
//        // Get all semesters
//        var semesters = db.semesters().all();
//
//        StringBuilder message = new StringBuilder();
//        message.append("<ul>");
//
//        for (var semester : semesters) {
//            message.append("<li>")
//                    .append("<strong>").append(semester.getName()).append("</strong>")
//                    .append(" (")
//                    .append(semester.getStart().format(DATE_FORMATTER))
//                    .append(" - ")
//                    .append(semester.getEnd().format(DATE_FORMATTER))
//                    .append(")")
//                    .append("<br>")
//                    .append("Courses: ")
//                    .append(semester.getCourses().size())
//                    .append("</li>");
//        }
//
//        message.append("</ul>");
//        request.setAttribute("message", message.toString());
//
//        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/collection.jsp");
//        try {
//            dispatcher.forward(request, response);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}