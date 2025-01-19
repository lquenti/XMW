package xmw.exa.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import xmw.ClientLogger;

@WebListener
public class ExaContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ClientLogger.getInstance();
    }
}
