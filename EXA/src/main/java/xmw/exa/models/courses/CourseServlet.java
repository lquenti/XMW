package xmw.exa.models.courses;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import org.basex.core.cmd.XQuery;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.DB;
import xmw.exa.util.Config;

@WebServlet(name = "course", urlPatterns = "/courses/*")
public class CourseServlet extends HttpServlet {
    private DB db;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void init() {
        db = DB.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect("/courses");
            return;
        }

        // Remove leading slash
        String courseId = pathInfo.substring(1);

        // Handle /all endpoint
        if (courseId.equals("all")) {
            String queryString = request.getQueryString();
            response.sendRedirect(Config.BASE_URL + "/courses" + (queryString != null ? "?" + queryString : ""));
            return;
        }

        // Validate course ID
        if (!courseId.matches("\\d+")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
            return;
        }

        // Check format parameter
        boolean isXmlFormat = "xml".equals(request.getParameter("format"));

        // Query for the specific course
        String query = String.format(
                "let $course := /root/Courses/Course[id = %s] " +
                        "let $lectures := /root/Lectures/Lecture[course_id = %s] " +
                        "return if ($course) then " +
                        "  element course { " +
                        "    attribute id { $course/id/text() }, " +
                        "    attribute semester_id { $course/semester_id/text() }, " +
                        "    element faculty { $course/faculty/text() }, " +
                        "    element lecturer { attribute id { $course/lecturer_id/text() } }, " +
                        "    element max_students { $course/max_students/text() }, " +
                        "    element name { $course/name/text() }, " +
                        "    element lectures { " +
                        "      for $lecture in $lectures " +
                        "      return element lecture { " +
                        "        element id { $lecture/id/text() }, " +
                        "        element start { $lecture/start/text() }, " +
                        "        element room_or_link { $lecture/room_or_link/text() } " +
                        "      } " +
                        "    } " +
                        "  } " +
                        "else ()",
                 courseId, courseId);

        String result = new XQuery(query).execute(db.getContext());

        if (result.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Course not found");
            return;
        }

        if (isXmlFormat) {
            response.setContentType("application/xml");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();

            // Transform using StAX
            try {
                XMLInputFactory inputFactory = XMLInputFactory.newInstance();
                XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
                XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(result));
                StringWriter stringWriter = new StringWriter();
                XMLStreamWriter writer = outputFactory.createXMLStreamWriter(stringWriter);

                writer.writeStartDocument("UTF-8", "1.0");

                while (reader.hasNext()) {
                    int event = reader.next();
                    switch (event) {
                        case XMLStreamConstants.START_ELEMENT:
                            String elementName = reader.getLocalName();
                            writer.writeStartElement(elementName);

                            // Copy all attributes
                            for (int i = 0; i < reader.getAttributeCount(); i++) {
                                writer.writeAttribute(
                                        reader.getAttributeLocalName(i),
                                        reader.getAttributeValue(i));
                            }
                            break;

                        case XMLStreamConstants.CHARACTERS:
                            if (!reader.isWhiteSpace()) {
                                writer.writeCharacters(reader.getText());
                            }
                            break;

                        case XMLStreamConstants.END_ELEMENT:
                            writer.writeEndElement();
                            break;
                    }
                }

                writer.writeEndDocument();
                writer.flush();
                reader.close();
                writer.close();

                out.println(stringWriter.toString());
            } catch (XMLStreamException e) {
                throw new IOException("Error transforming XML", e);
            }

            out.flush();
        } else {
            // Return HTML response
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            int numCourseId = Integer.parseInt(courseId);
            var course = db.courses().all().stream()
                    .filter(c -> c.getId() == numCourseId)
                    .findFirst()
                    .orElse(null);

            if (course == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Course not found");
                return;
            }

            request.setAttribute("course", course);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/course-details.jsp");
            dispatcher.forward(request, response);
        }
    }

    @Override
    public void destroy() {
        // Don't close the DB here as it's shared
    }
}