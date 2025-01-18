package xmw.studip;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/generateCertificate")
public class CertificateUtil extends HttpServlet {

    private static final String TEMPLATE_DIR = "/templates/";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            XMLDatabase xmlDatabase = (XMLDatabase) getServletContext().getAttribute("xmlDatabase");

            // Fetch user and grade data
            String userId = AuthUtil.getLoggedInUserId(request);
            Map<String, String> userInfo = xmlDatabase.getUserInfo(userId);
            List<Map<String, String>> grades = xmlDatabase.getGrades(userId);
            List<Map<String, String>> exams = xmlDatabase.getExams();

            // Merge exam details into grades
            for (Map<String, String> grade : grades) {
                for (Map<String, String> exam : exams) {
                    if (grade.get("id").equals(exam.get("examId"))) {
                        grade.putAll(exam);
                    }
                }
            }

            // Determine the user's field of study and choose the template
            String fieldOfStudy = userInfo.getOrDefault("faculty", "default").toLowerCase();
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
            case "computer science":
                fileName = "computer_science.xslt";
                break;
            case "mathematics":
                fileName = "mathematics.xslt";
                break;
            default:
                fileName = "default.xslt";
                break;
        }
        return getServletContext().getRealPath(TEMPLATE_DIR + fileName);
    }
}
