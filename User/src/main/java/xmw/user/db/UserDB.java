package xmw.user.db;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.XQuery;
import org.basex.query.QueryException;
import org.basex.query.QueryProcessor;
import xmw.ClientLogger;
import xmw.Event;
import xmw.user.utils.UserContextListener;

import java.io.IOException;
import java.util.List;

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
        ClientLogger.getInstance().addEvent(new Event("UserDB", "root", "FlushToDisk", "Flush XML database to disk"));
    }

    public static boolean authenticate(String username, String password) throws QueryException {
        String authQuery = "//User[@username = '" + username + "' and password/text() = '" + password + "']";
        ClientLogger.getInstance().addEvent(new Event("UserDB", "root", "AuthenticateQuery", "Authenticating user " + username));
        synchronized (lock) {
            QueryProcessor proc = new QueryProcessor(authQuery, instance.ctx);
            return !proc.value().isEmpty();
        }
    }

    public static boolean usernameExist(String username) throws IOException {
        String authQuery = "//User[@username = '" + username + "']";
        ClientLogger.getInstance().addEvent(new Event("UserDB", "root", "UsernameExistQuery", "Checking whether " + username + " exists"));
        synchronized (lock) {
            QueryProcessor proc = new QueryProcessor(authQuery, instance.ctx);
            try {
                return !proc.value().isEmpty();
            } catch (QueryException e) {
                throw new IOException(e);
            }
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
        ClientLogger.getInstance().addEvent(new Event("UserDB", "root", "GetUserByUsername", "Getting " + username + " object"));
        synchronized (lock) {
            return new XQuery(authQuery).execute(instance.ctx);
        }
    }

    // expects to be checked before that we have no duplication
    public static void addUserNode(String xml) {
        String query = "insert node " + xml + " into /Users";
        ClientLogger.getInstance().addEvent(new Event("UserDB", "root", "AddUser", "Adding new User object"));
        synchronized (lock) {
            QueryProcessor proc = new QueryProcessor(query, instance.ctx);
            try {
                proc.execute();
            } catch (QueryException e) {
                throw new RuntimeException(e);
            }
            proc.close();
        }
    }

    // expects to be checked before that it exists
    public static void deleteUser(String username) {
        String query = "delete node /Users/User[@username = '" + username + "']";
        ClientLogger.getInstance().addEvent(new Event("UserDB", "root", "DeleteUser", "Deleting user " + username));
        synchronized (lock) {
            QueryProcessor proc = new QueryProcessor(query, instance.ctx);
            try {
                proc.execute();
            } catch (QueryException e) {
                throw new RuntimeException(e);
            }
            proc.close();
        }
    }

    public static String getAllUsers(boolean with_password) throws BaseXException {
        String authQuery;
        if (with_password) {
            authQuery = "/";
        } else {
            authQuery = """
                    <Users>
                    {
                    for $user in /Users/User
                    return
                    <User>
                    { for $attr in $user/@* return $attr }
                    {
                      for $child in $user/*
                      where not(local-name() = "password")
                      return $child
                    }
                    </User>
                    }
                    </Users>
                    """;
        }
        ClientLogger.getInstance().addEvent(new Event("UserDB", "root", "GetAllUsers", "Getting all users" + (with_password ? " with password" : " without password")));
        synchronized (lock) {
            return new XQuery(authQuery).execute(instance.ctx);
        }
    }

    public static String getAllUsersOfGroup(String groupname, boolean with_password) throws BaseXException {
        String authQuery;
        if (with_password) {
            authQuery = "<Users>{/Users/User[./group/text() = '" + groupname + "' or ./group/@id = '" + groupname + "']}</Users>";
        } else {
            authQuery = "<Users>"
                    + "{"
                    + "for $user in /Users/User[./group/text() = '" + groupname + "' or ./group/@id = '" + groupname + "']"
                    + " return"
                    + " <User>"
                    + "{ for $attr in $user/@* return $attr }"
                    + "{"
                    + "  for $child in $user/*"
                    + "  where not(local-name() = \"password\")"
                    + "  return $child"
                    + "}"
                    + " </User>"
                    + "}"
                    + "</Users>";
        }
        ClientLogger.getInstance().addEvent(new Event("UserDB", "root", "GetAllUsersOfGroup", "Getting all users of group " + groupname + (with_password ? " with password" : " without password")));
        synchronized (lock) {
            return new XQuery(authQuery).execute(instance.ctx);
        }
    }

    public static String getSpecificBulkUsers(List<String> usernames, boolean with_password) throws BaseXException {
        String formattedUsernames = String.join("\", \"", usernames);
        formattedUsernames = "(\"" + formattedUsernames + "\")";
        // ("username1", "username2", ..., "usernameN")
        ClientLogger.getInstance().addEvent(new Event("UserDB", "root", "GetAllUsersOfGroup", "Running selected bulk of " + usernames.size() + " users"));

        String authQuery = "declare variable $usernames := " + formattedUsernames + ";\n";
        if (with_password) {
            authQuery += """
                    /Users/User[@username = $usernames]
                    """;
        } else {
            authQuery += """
                    <Users>
                    {
                    for $user in /Users/User[@username = $usernames]
                    return
                    <User>
                    { $user/@* }
                    { $user/*[not(local-name() = "password")] }
                    </User>
                    }
                    </Users>
                    """;
        }
        synchronized (lock) {
            return new XQuery(authQuery).execute(instance.ctx);
        }
    }

}
