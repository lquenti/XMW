package xmw.user.db;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.CreateDB;
import org.basex.query.QueryException;
import org.basex.query.QueryProcessor;
import org.basex.query.value.Value;

public class UserDB {
    private static UserDB instance;
    private Context ctx;
    private final String db_path = "/home/lquenti/code/XMW/test.xml";
    private static final Object lock = new Object();

    private UserDB() {
        ctx = new Context();
        try {
            new CreateDB("UserDB", db_path).execute(ctx);
        } catch (BaseXException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // NOTE: This always gets called first i DBContextListener.contextInitialized
    public static void init() {
        instance = new UserDB();
    }

    public static void flushToDisk() throws QueryException {
        String query = "file:write(\"" + instance.db_path + "\", /)";
        synchronized (lock) {
            QueryProcessor proc = new QueryProcessor(query, instance.ctx);
            proc.execute();
            proc.close();
        }
    }

    // TODO remove
    public static void addUserTest() {
        String add = "insert node <User>\n" +
                "  <Username>hehe</Username>\n" +
                "  <Password>haha</Password>\n" +
                "</User> into /Users";
        synchronized (lock) {
            QueryProcessor proc = new QueryProcessor(add, instance.ctx);
            try {
                proc.execute();
            } catch (QueryException e) {
                throw new RuntimeException(e);
            }
            // Close the query processor
            proc.close();
        }
    }

    public static boolean authenticate(String username, String password) throws QueryException {
        String authQuery = "//User[@username = '" + username + "' and password/text() = '" + password + "']";
        QueryProcessor proc = new QueryProcessor(authQuery, instance.ctx);
        return !proc.value().isEmpty();
    }
}
