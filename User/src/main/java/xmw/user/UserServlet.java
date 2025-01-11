package xmw.user;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import org.basex.core.*;
import org.basex.core.cmd.*;
import org.basex.query.*;

@WebServlet(name="userServlet", value="/UserServlet")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Define the database name
    private static final String DB_NAME = "UserDB";

    // Define the XML content
    private static final String USER_XML =
            "<User username=\"hbrosen\">" +
                    "<name>Brosenne</name>" +
                    "<firstname>Hendrik</firstname>" + // Corrected closing tag
                    "<faculty>Computer Science</faculty>" +
                    "<group id=\"g_lecturer\">Lecturer</group>" +
                    "<group id=\"g_employee\">Employee</group>" +
                    "</User>";

    // Initialize BaseX context
    private Context context;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            // Initialize BaseX context
            context = new Context();

            new CreateDB(DB_NAME, USER_XML).execute(context);

            /*
            // Check if the database already exists to avoid duplication
            if(!new Open(DB_NAME).execute(context).equals("true")) {
                // Create the database with the USER_XML
                new CreateDB(DB_NAME, USER_XML).execute(context);
                System.out.println("Database created successfully.");
            } else {
                System.out.println("Database already exists.");
            }
             */

        } catch (Exception e) {
            throw new ServletException("Error initializing BaseX database", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Set response type to text/plain
        response.setContentType("text/plain");

        // Prepare the XPath query to retrieve firstname where username="hbrosen"
        String xpath = "/User[@username='hbrosen']/firstname/text()";

        // Execute the XQuery
        String result = new XQuery(xpath).execute(context);

        if (result != null && !result.isEmpty()) {
            // Write the firstname to the response
            response.getWriter().write(result.trim());
        } else {
            // If no result found, send a 404 status
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
        }

    }

    @Override
    public void destroy() {
        // Close the BaseX context upon servlet destruction
        if(context != null) {
            context.close();
        }
        super.destroy();
    }
}
