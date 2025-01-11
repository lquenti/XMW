package xmw.user.routes;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.User;
import org.basex.core.cmd.Close;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.Open;
import org.basex.query.QueryException;
import org.basex.query.QueryProcessor;
import org.basex.query.iter.Iter;
import org.basex.query.value.item.Item;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xmw.user.db.UserDB;
import xmw.user.utils.DOMUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

@WebServlet(name = "testAddUserServlet", value = "/testadduser")
public class TestAddUserServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) {
        UserDB.addUserTest();
    }
}
