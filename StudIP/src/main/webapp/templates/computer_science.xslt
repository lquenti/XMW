<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" encoding="UTF-8" indent="yes"/>
    <xsl:template match="/Certificate">
        <html>
            <head>
                <title>Certificate of Grades for CS</title>
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
                    <h1><u>Certificate of Academic Performance</u></h1>
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