package xmw.user.db;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.basex.query.QueryException;

import java.util.Timer;
import java.util.TimerTask;

@WebListener
public class DBContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        UserDB.init();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    UserDB.flushToDisk();
                    System.out.println("DB saved");
                } catch (QueryException e) {
                    e.printStackTrace();
                    // better to just go on... maybe it works next time
                }
            }
        };
        long delay = 5;
        long period = 1000 * 10 * 60; // every 10 min
        //period = 1000 * 5;
        timer.scheduleAtFixedRate(task, delay, period);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            UserDB.flushToDisk();
        } catch (QueryException e) {
            e.printStackTrace();
            // better to just go on... maybe it works next time
        }
    }
}