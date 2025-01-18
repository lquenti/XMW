<%@ taglib prefix="c" uri="jakarta.tags.core" %> <%@ taglib prefix="fmt"
uri="jakarta.tags.fmt" %>
<jsp:useBean
  id="courses"
  scope="request"
  type="java.util.List<xmw.exa.models.courses.Course>"
/>
<!DOCTYPE html>
<html xml:lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <script src="https://cdn.tailwindcss.com"></script>
    <title>Course Overview - EXA</title>
  </head>
  <body class="bg-gray-50 min-h-screen">
    <div class="container mx-auto px-4 py-8 max-w-4xl">
      <nav class="mb-8">
        <a
          href="${pageContext.request.contextPath}/"
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
          Back to Home
        </a>
      </nav>

      <div class="bg-white shadow-lg rounded-lg p-6 mb-8">
        <div class="flex justify-between items-center mb-6">
          <h1 class="text-3xl font-bold text-gray-800">Courses</h1>
          <a
            href="?format=xml"
            class="text-sm text-gray-500 hover:text-gray-700"
            >View as XML</a
          >
        </div>

        <div class="grid gap-4">
          <c:choose>
            <c:when test="${empty courses}">
              <div class="text-center py-8 bg-gray-50 rounded-lg">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-12 w-12 mx-auto text-gray-400 mb-3"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"
                  />
                </svg>
                <p class="text-gray-600 italic">No courses available.</p>
              </div>
            </c:when>
            <c:otherwise>
              <c:forEach items="${courses}" var="course">
                <a
                  href="${pageContext.request.contextPath}/courses/${course.id}"
                  class="block bg-white border rounded-lg hover:shadow-md transition-all duration-200"
                >
                  <div class="p-4">
                    <div class="flex items-center justify-between mb-2">
                      <h2 class="text-lg font-semibold text-gray-900">
                        ${course.name}
                      </h2>
                      <span class="text-sm text-gray-500"
                        >ID: ${course.id}</span
                      >
                    </div>
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div class="space-y-2">
                        <p class="text-sm text-gray-600">
                          <span class="font-medium">Faculty:</span>
                          ${course.faculty}
                        </p>
                        <p class="text-sm text-gray-600">
                          <span class="font-medium">Max Students:</span>
                          ${course.maxStudents}
                        </p>
                      </div>
                      <div class="space-y-2">
                        <p class="text-sm text-gray-600">
                          <span class="font-medium">Lecturer:</span>
                          ${course.lecturer.fullName}
                        </p>
                        <p class="text-sm text-gray-600">
                          <span class="font-medium">Semester:</span>
                          ${course.semester.name}
                        </p>
                      </div>
                    </div>
                    <div class="mt-4 flex items-center justify-between">
                      <div class="flex items-center text-sm text-gray-500">
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          class="h-4 w-4 mr-1"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke="currentColor"
                        >
                          <path
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            stroke-width="2"
                            d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
                          />
                        </svg>
                        ${course.lectures.size()} Lecture(s)
                      </div>
                      <div class="text-blue-600">
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          class="h-5 w-5"
                          viewBox="0 0 20 20"
                          fill="currentColor"
                        >
                          <path
                            fill-rule="evenodd"
                            d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z"
                            clip-rule="evenodd"
                          />
                        </svg>
                      </div>
                    </div>
                  </div>
                </a>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>
  </body>
</html>
