package xmw.exa.models.modules;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.DB;
import xmw.exa.util.ExaServlet;
import xmw.exa.util.Util;
import xmw.flush.Module;

import java.io.IOException;

@WebServlet(name = "module", urlPatterns = "/modules/*")
public class ModuleServlet extends ExaServlet {
    private DB db;

    @Override
    public void init() {
        db = DB.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String moduleId = Util.getPathParameter("modules", request, response);

        // Fetch module data
        Module moduleData = db.modules().get(moduleId);
        if (moduleData == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Module not found");
            return;
        }

        // Marshal module data to XML
        String responseData = DB.marshal(moduleData);

        // Write XML response
        Util.writeXmlResponse(responseData, response);
    }
}
