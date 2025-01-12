<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt"
           uri="jakarta.tags.fmt" %>
<jsp:useBean id="course" scope="request" type="xmw.exa.models.courses.Course"/>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE >
<html xml:lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <script src="https://cdn.tailwindcss.com"></script>
    <title>Course Details - ${course.name}</title>
</head>
<body class="bg-gray-50 min-h-screen">
<div class="container mx-auto px-4 py-8 max-w-4xl">
    <nav class="mb-8">
        <a
                href="${pageContext.request.contextPath}/courses"
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
            Back to Courses List
        </a>
    </nav>

    <div class="bg-white shadow-lg rounded-lg p-6 mb-8">
        <h1 class="text-3xl font-bold text-gray-800 mb-6">${course.name}</h1>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
            <div class="space-y-4">
                <div>
                    <h2 class="text-sm font-semibold text-gray-600">Course ID</h2>
                    <p class="text-gray-800">${course.id}</p>
                </div>
                <div>
                    <h2 class="text-sm font-semibold text-gray-600">Faculty</h2>
                    <p class="text-gray-800">${course.faculty}</p>
                </div>
                <div>
                    <h2 class="text-sm font-semibold text-gray-600">Max Students</h2>
                    <p class="text-gray-800">${course.maxStudents}</p>
                </div>
            </div>

            <div class="space-y-4">
                <div>
                    <h2 class="text-sm font-semibold text-gray-600">Lecturer</h2>
                    <a
                            href="${pageContext.request.contextPath}/lecturers/${course.lecturer.username}"
                            class="text-blue-600 hover:text-blue-800"
                    >
                        ${course.lecturer.fullName}
                    </a>
                </div>
                <div>
                    <h2 class="text-sm font-semibold text-gray-600">Semester</h2>
                    <p class="text-gray-800">${course.semester.name}</p>
                </div>
            </div>
        </div>

        <div class="border-t pt-6">
            <h2 class="text-xl font-bold text-gray-800 mb-4">Lectures</h2>

            <c:choose>
                <c:when test="${empty course.lectures}">
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
                                    d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
                            />
                        </svg>
                        <p class="text-gray-600 italic">No lectures scheduled.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="grid gap-4">
                        <c:forEach items="${course.lectures}" var="item">
                            <a
                                    href="${pageContext.request.contextPath}/lectures/${item.id}"
                                    class="block bg-white border rounded-lg hover:shadow-md transition-all duration-200"
                            >
                                <div class="p-4 flex items-center gap-4">
                                    <div class="bg-blue-50 rounded-full p-3 flex-shrink-0">
                                        <svg
                                                xmlns="http://www.w3.org/2000/svg"
                                                class="h-6 w-6 text-blue-600"
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
                                    </div>
                                    <div class="flex-grow min-w-0">
                                        <div class="flex items-center gap-2 mb-1">
                          <span class="text-sm font-medium text-gray-900">
                                  ${item.start.toLocalDate()}
                          </span>
                                            <span class="text-sm font-medium text-gray-900">
                                                    ${item.start.toLocalTime()}
                                            </span>
                                        </div>
                                        <div class="flex items-center text-gray-600">
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
                                                        d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"
                                                />
                                                <path
                                                        stroke-linecap="round"
                                                        stroke-linejoin="round"
                                                        stroke-width="2"
                                                        d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"
                                                />
                                            </svg>
                                            <span class="text-sm truncate"
                                            >${item.roomOrLink}</span
                                            >
                                        </div>
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
                            </a>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div class="text-right">
        <a href="?format=xml" class="text-sm text-gray-500 hover:text-gray-700"
        >View as XML</a
        >
    </div>
</div>
</body>
</html>
