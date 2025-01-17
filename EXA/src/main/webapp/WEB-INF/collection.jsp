<%@ taglib prefix="c" uri="jakarta.tags.core" %> <%@ taglib prefix="fmt"
uri="jakarta.tags.fmt" %> <%@ page contentType="text/html;charset=UTF-8"
language="java" %> <%@ page import="xmw.exa.util.Config" %>
<jsp:useBean id="name" scope="request" type="java.lang.String" />
<jsp:useBean id="message" scope="request" type="java.lang.String" />
<jsp:useBean id="courses" scope="request" type="java.util.List" />
<!DOCTYPE html>
<html xml:lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <script src="https://cdn.tailwindcss.com"></script>
    <title>${name}</title>
  </head>
  <body class="bg-gray-50 min-h-screen">
    <div class="container mx-auto px-4 py-8 max-w-4xl">
      <nav class="mb-8">
        <a
          href="<%= Config.BASE_URL%>"
          class="text-blue-600 hover:text-blue-800 flex items-center gap-2"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5"
            viewBox="0 0 20 20"
            fill="currentColor"
          >
            <path
              fill-rule="evenodd"
              d="M9.707 16.707a1 1 0 01-1.414 0l-6-6a1 1 0 010-1.414l6-6a1 1 0 011.414 1.414L5.414 9H17a1 1 0 110 2H5.414l4.293 4.293a1 1 0 010 1.414z"
              clip-rule="evenodd"
            />
          </svg>
          Back to Exa Home
        </a>
      </nav>

      <div class="bg-white shadow-lg rounded-lg p-6 mb-8">
        <h1 class="text-3xl font-bold text-gray-800 mb-6">${name}</h1>

        <div class="prose max-w-none">${message}</div>
      </div>

      <div class="flex justify-between items-center text-sm">
        <a
          href="<%= Config.BASE_URL%>"
          class="text-blue-600 hover:text-blue-800"
        >
          Back to Overview
        </a>
        <a href="?format=xml" class="text-gray-500 hover:text-gray-700">
          View as XML
        </a>
      </div>

      <c:if test="${pageContext.request.servletPath eq '/exams'}">
        <div class="create-form">
          <h3>Create New Exam</h3>
          <form
            action="${pageContext.request.contextPath}/exams"
            method="post"
            enctype="multipart/form-data"
          >
            <div class="form-group">
              <label for="course_id">Course:</label>
              <select name="course_id" id="course_id" required>
                <c:forEach var="course" items="${courses}">
                  <option value="${course.id}">${course.name}</option>
                </c:forEach>
              </select>
            </div>

            <div class="form-group">
              <label for="date">Date and Time:</label>
              <input type="datetime-local" name="date" id="date" required />
            </div>

            <div class="form-group">
              <label for="is_online">Online Exam:</label>
              <input type="checkbox" name="is_online" id="is_online" />
            </div>

            <div class="form-group">
              <label for="is_written">Written Exam:</label>
              <input type="checkbox" name="is_written" id="is_written" />
            </div>

            <div class="form-group">
              <label for="room_or_link">Room/Link:</label>
              <input
                type="text"
                name="room_or_link"
                id="room_or_link"
                required
              />
            </div>

            <button type="submit">Create Exam</button>
          </form>
        </div>
      </c:if>

      <style>
        .create-form {
          max-width: 500px;
          margin: 20px 0;
          padding: 20px;
          border: 1px solid #ddd;
          border-radius: 4px;
        }

        .form-group {
          margin-bottom: 15px;
        }

        .form-group label {
          display: block;
          margin-bottom: 5px;
          font-weight: bold;
        }

        .form-group input[type="text"],
        .form-group input[type="datetime-local"],
        .form-group select {
          width: 100%;
          padding: 8px;
          border: 1px solid #ddd;
          border-radius: 4px;
        }

        .form-group input[type="checkbox"] {
          margin-right: 5px;
        }

        button[type="submit"] {
          background-color: #4caf50;
          color: white;
          padding: 10px 15px;
          border: none;
          border-radius: 4px;
          cursor: pointer;
        }

        button[type="submit"]:hover {
          background-color: #45a049;
        }
      </style>
    </div>
  </body>
</html>
