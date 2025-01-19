package xmw;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.List;
import java.util.Map;

import static xmw.Utils.joinListOfMaps;

@WebServlet("/generateCertificate")
public class CertificateUtil extends HttpServlet {

    private static final String TEMPLATE_DIR = "/templates/";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Utils.log(request, response, (ClientLogger) getServletContext().getAttribute("logger"), "GenerateCertificateEvent", "Generating certificate for user", true);
            XMLDatabase xmlDatabase = (XMLDatabase) getServletContext().getAttribute("xmlDatabase");

            // Fetch user and grade data
            String userId = Utils.getLoggedInUserId(request);
            Map<String, String> userInfo = xmlDatabase.getUserInfo(userId);
            List<Map<String, String>> grades = xmlDatabase.getGrades(userId);
            List<Map<String, String>> exams = xmlDatabase.getExams();
            List<Map<String, String>> courses = xmlDatabase.getCourses();
            List<Map<String, String>> modules = xmlDatabase.getModules();
            joinListOfMaps(grades, exams, "id", "ExamId");
            joinListOfMaps(grades, courses, "CourseID", "CourseID");
            joinListOfMaps(grades, modules, "CourseID", "CourseID");

            // Determine the user's field of study and choose the template
            String fieldOfStudy = userInfo.getOrDefault("faculty", "Default");
            String certificateHtml = generateCertificate(userInfo, grades, fieldOfStudy);

            // Write certificate HTML to response
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.print(certificateHtml);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("<html><body><h1>Error generating certificate</h1></body></html>");
        }
    }

    private String generateCertificate(Map<String, String> userInfo, List<Map<String, String>> grades, String fieldOfStudy) throws Exception {
        // Build XML data
        StringBuilder xmlData = new StringBuilder();
        xmlData.append("<Certificate>");
        xmlData.append("<User>");
        for (Map.Entry<String, String> entry : userInfo.entrySet()) {
            xmlData.append(String.format("<%s>%s</%s>", entry.getKey(), entry.getValue(), entry.getKey()));
        }
        xmlData.append("</User>");
        xmlData.append("<Grades>");
        for (Map<String, String> grade : grades) {
            xmlData.append("<Grade>");
            for (Map.Entry<String, String> entry : grade.entrySet()) {
                xmlData.append(String.format("<%s>%s</%s>", entry.getKey(), entry.getValue(), entry.getKey()));
            }
            xmlData.append("</Grade>");
        }
        xmlData.append("</Grades>");
        xmlData.append("</Certificate>");

        // Load the appropriate XSLT template
        String templatePath = getTemplatePath(fieldOfStudy);
        Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new File(templatePath)));

        // Transform XML using XSLT
        Source xmlSource = new StreamSource(new StringReader(xmlData.toString()));
        StringWriter output = new StringWriter();
        Result result = new StreamResult(output);

        transformer.transform(xmlSource, result);

        return output.toString(); // Return HTML as a String
    }

    private String getTemplatePath(String fieldOfStudy) {
        // Map the field of study to the corresponding template
        String fileName;
        switch (fieldOfStudy) {
            case "Computer Science":
                fileName = "computer_science.xslt";
                break;
            case "Mathematics":
                fileName = "mathematics.xslt";
                break;
            default:
                fileName = "default.xslt";
                break;
        }
        return getServletContext().getRealPath(TEMPLATE_DIR + fileName);
    }
}
