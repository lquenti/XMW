<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ page
import="xmw.exa.util.Config" %>
<jsp:useBean id="name" scope="request" type="java.lang.String" />
<jsp:useBean id="message" scope="request" type="java.lang.String" />
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
    </div>
  </body>
</html>
