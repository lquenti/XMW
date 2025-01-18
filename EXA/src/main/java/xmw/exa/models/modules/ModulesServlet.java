package xmw.exa.models.modules;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.DB;
import xmw.exa.util.ExaServlet;
import xmw.exa.util.Util;
import xmw.flush.Modules;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "modules", value = "/modules")
public class ModulesServlet extends ExaServlet {
    private DB db;

    @Override
    public void init() {
        db = DB.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var moduleData = db.modules().all();
        var modules = new Modules();
        modules.getModule().addAll(moduleData);
        var responseData = DB.marshal(modules);
        PrintWriter out = response.getWriter();
        out.println(responseData);
        out.flush();
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Util.deleteItem(db.modules(), request, response);
    }
}
