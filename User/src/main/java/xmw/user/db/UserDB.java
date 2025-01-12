package xmw.user.db;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.XQuery;
import org.basex.data.Result;
import org.basex.query.QueryException;
import org.basex.query.QueryProcessor;
import org.basex.query.value.Value;
import xmw.user.utils.UserContextListener;

public class UserDB {
    private static UserDB instance;
    private Context ctx;
    private static final Object lock = new Object();

    private UserDB() {
        ctx = new Context();
        try {
            new CreateDB("UserDB", UserContextListener.USER_PATH).execute(ctx);
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
        String query = "file:write(\"" + UserContextListener.USER_PATH + "\", /)";
        synchronized (lock) {
            QueryProcessor proc = new QueryProcessor(query, instance.ctx);
            proc.execute();
            proc.close();
        }
    }

//    public static void addUserTest() {
//        String add = "insert node <User>\n" +
//                "  <Username>hehe</Username>\n" +
//                "  <Password>haha</Password>\n" +
//                "</User> into /Users";
//        synchronized (lock) {
//            QueryProcessor proc = new QueryProcessor(add, instance.ctx);
//            try {
//                proc.execute();
//            } catch (QueryException e) {
//                throw new RuntimeException(e);
//            }
//            // Close the query processor
//            proc.close();
//        }
//    }

    public static boolean authenticate(String username, String password) throws QueryException {
        String authQuery = "//User[@username = '" + username + "' and password/text() = '" + password + "']";
        synchronized (lock) {
            QueryProcessor proc = new QueryProcessor(authQuery, instance.ctx);
            return !proc.value().isEmpty();
        }
    }

    public static boolean usernameExist(String username) throws QueryException {
        String authQuery = "//User[@username = '" + username + "']";
        synchronized (lock) {
            QueryProcessor proc = new QueryProcessor(authQuery, instance.ctx);
            return !proc.value().isEmpty();
        }
    }

    public static String getUserByUsername(String username, boolean with_password) throws BaseXException {

        String authQuery;

        if (with_password) {
            authQuery = "//User[@username = '" + username + "']";
        } else {
            authQuery = "for $user in //User[@username = '" + username + "']" + """
                    return
                      <User>
                        { for $attr in $user/@* return $attr }
                        {
                          for $child in $user/*
                          where not(local-name() = "password")
                          return $child
                        }
                      </User>
                    """;
        }
        synchronized (lock) {
            return new XQuery(authQuery).execute(instance.ctx);
        }
    }
}
