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
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/generateCertificate")
public class CertificateUtil extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            XMLDatabase xmlDatabase = (XMLDatabase) getServletContext().getAttribute("xmlDatabase");

            // Fetch user and grade data
            String userId = AuthUtil.getLoggedInUserId(request);
            Map<String, String> userInfo = xmlDatabase.getUserInfo(userId);
            List<Map<String, String>> grades = xmlDatabase.getGrades(userId);
            List<Map<String, String>> exams = xmlDatabase.getExams();

            for (Map<String, String> map1 : grades) {
                for (Map<String, String> map2 : exams) {
                    if (map1.containsKey("id") && map2.containsKey("examId") &&
                            map1.get("id").equals(map2.get("examId"))) {

                        map1.putAll(map2);
                    }
                }
            }

            // Generate certificate
            String certificateHtml = generateCertificate(userInfo, grades);

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

    public static String generateCertificate(Map<String, String> userInfo, List<Map<String, String>> grades) {
        try {
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

            // XSLT to transform XML into HTML
            String xslt = """
                <xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                    <xsl:output method="html" encoding="UTF-8" indent="yes"/>
                    <xsl:template match="/Certificate">
                        <html>
                            <head>
                                <title>Certificate of Grades</title>
                                <link rel="stylesheet" href="https://latex.vercel.app/style.css"/>
                                <style>
                                    body {
                                        text-align: center;
                                        background-color: #3366CC;
                                    }
                                    .certificate-container {
                                        max-width: 800px;
                                        margin: auto;
                                        background: #ffffff;
                                        padding: 20px;
                                        border-radius: 10px;
                                        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                                    }
                                    h1, h2 {
                                        color: #333;
                                    }
                                    table {
                                        margin: auto;
                                        width: 90%;
                                        border-collapse: collapse;
                                    }
                                    th, td {
                                        border: 1px solid #ddd;
                                        padding: 10px;
                                        text-align: left;
                                    }
                                    th {
                                        background-color: #f2f2f2;
                                    }
                                </style>
                            </head>
                            <body>
                                <div class="certificate-container">
                                    <h1>Certificate of Academic Performance</h1>
                                    <p>This document certifies the academic achievements of the student listed below:</p>
                                    <h2>Student Information</h2>
                                    <table>
                                        <tr><th>Field</th><th>Value</th></tr>
                                        <xsl:for-each select="User/*">
                                            <tr>
                                                <td><xsl:value-of select="name()"/></td>
                                                <td><xsl:value-of select="."/></td>
                                            </tr>
                                        </xsl:for-each>
                                    </table>
                                    <h2>Grades</h2>
                                    <p>The following table lists the courses completed along with the grades achieved:</p>
                                    <table>
                                        <tr>
                                            <th>Course</th>
                                            <th>Grade</th>
                                            <th>Exam ID</th>
                                        </tr>
                                        <xsl:for-each select="Grades/Grade">
                                            <tr>
                                                <td><xsl:value-of select="courseName"/></td>
                                                <td><xsl:value-of select="grade"/></td>
                                                <td><xsl:value-of select="id"/></td>
                                            </tr>
                                        </xsl:for-each>
                                    </table>
                                    <p>Issued on: 2025-01-01</p>
                                    <p>Certified by: <strong>University Administration</strong></p>
                                </div>
                            </body>
                        </html>
                    </xsl:template>
                </xsl:stylesheet>
                """;

            // Transform XML using XSLT
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(new java.io.StringReader(xslt)));

            Source xmlSource = new StreamSource(new java.io.StringReader(xmlData.toString()));
            StringWriter output = new StringWriter();
            Result result = new StreamResult(output);

            transformer.transform(xmlSource, result);

            return output.toString(); // Return HTML as a String
        } catch (Exception e) {
            e.printStackTrace();
            return "<html><body><h1>Error generating certificate</h1></body></html>";
        }
    }

}
