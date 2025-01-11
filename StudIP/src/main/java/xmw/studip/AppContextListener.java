package xmw.studip;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Initialize the XMLDatabase object and store it in the context
        XMLDatabase xmlDatabase = XMLDatabase.getInstance();
        sce.getServletContext().setAttribute("xmlDatabase", xmlDatabase);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Clean up resources if necessary
        XMLDatabase xmlDatabase = (XMLDatabase) sce.getServletContext().getAttribute("xmlDatabase");
        if (xmlDatabase != null) {
            xmlDatabase.close(); // Hypothetical cleanup method
        }
    }
}
