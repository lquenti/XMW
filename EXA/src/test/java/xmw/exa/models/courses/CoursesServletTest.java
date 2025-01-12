package xmw.exa.models.courses;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class CoursesServletTest {
    private CoursesServlet servlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        servlet = new CoursesServlet();
        servlet.init();

        // Set up response writer
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Mock request parameters
        when(request.getServletPath()).thenReturn("/courses");
        when(request.getParameter("format")).thenReturn("xml");
    }

    private String minifyXml(String xml) {
        return xml.replaceAll(">[\\s\n\r]+<", "><")
                .replaceAll("[\\s\n\r]+", " ")
                .replaceAll("\\s+/>", "/>")
                .replaceAll("\\s+>", ">")
                .replaceAll("<?xml\\s+version=\"1.0\"\\s+encoding=\"UTF-8\"\\s*\\?>\\s*", "")
                .trim();
    }

    @Test
    void testCoursesXmlOutput() throws Exception {
        // Execute the servlet
        servlet.doGet(request, response);
        writer.flush();

        // Verify response headers
        verify(response).setContentType("application/xml");
        verify(response).setCharacterEncoding("UTF-8");

        // Get actual output and minify it
        String actualXml = minifyXml(stringWriter.toString());

        // Verify the XML structure contains expected elements
        assertTrue(actualXml.contains("<courses>"), "XML should contain <courses> element");
        assertTrue(actualXml.contains("</courses>"), "XML should contain </courses> element");

        // Extract and verify first course
        int courseStartIndex = actualXml.indexOf("<course");
        int courseEndIndex = actualXml.indexOf("</course>") + 8;
        String firstCourse = actualXml.substring(courseStartIndex, courseEndIndex);

        // Verify course contains required elements
        assertTrue(firstCourse.contains("<course"), "Course should have opening tag");
        assertTrue(firstCourse.contains("id="), "Course should have id attribute");
        assertTrue(firstCourse.contains("semester_id="), "Course should have semester_id attribute");
        assertTrue(firstCourse.contains("<faculty>"), "Course should have faculty element");
        assertTrue(firstCourse.contains("<lecturer"), "Course should have lecturer element");
        assertTrue(firstCourse.contains("<max_students>"), "Course should have max_students element");
        assertTrue(firstCourse.contains("<name>"), "Course should have name element");
        assertTrue(firstCourse.contains("<semester>"), "Course should have semester element");
        assertTrue(firstCourse.contains("<lectures>"), "Course should have lectures element");
        assertTrue(firstCourse.contains("<lecture>"), "Course should have lecture element");
        assertTrue(firstCourse.contains("<start>"), "Course should have start element");
        assertTrue(firstCourse.contains("<end>"), "Course should have end element");
        assertTrue(firstCourse.contains("<room_or_link>"), "Course should have room_or_link element");
        assertTrue(firstCourse.contains("<exams>"), "Course should have exams element");
    }
}