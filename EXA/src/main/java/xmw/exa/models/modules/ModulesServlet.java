package xmw.exa.models.modules;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.exa.db.DB;
import xmw.exa.util.ExaServlet;
import xmw.exa.util.Util;
import xmw.flush.*;
import xmw.flush.Module;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

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
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String[] requiredParams = {"credits", "course", "name", "studies"};
        final Map<String, String> defaultRawDto = new HashMap<>();
        defaultRawDto.put("credits", "");
        defaultRawDto.put("course", "");
        defaultRawDto.put("name", "");
        defaultRawDto.put("studies", "");
        defaultRawDto.put("description", "No description provided");

        Map<String, String> rawDto = Util.getRawDto(defaultRawDto, requiredParams, request, response);
        if (rawDto == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Verify that credits is a valid number
        try {
            Integer.parseInt(rawDto.get("credits"));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Verify that course exists
        var course = db.courses().get(rawDto.get("course"));
        if (course == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Split studies into a list
        var studies = rawDto.get("studies").split(";");
        // verify that at least one study exists
        if (studies.length == 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Create the module
        Module module = new Module();
        module.setCredits(rawDto.get("credits"));
        module.setCourse(course);

        Name name = new Name();
        name.setContent(rawDto.get("name"));
        module.getNameOrStudiesOrDescription().add(name);
        Studies studiesElement = new Studies();
        for (String study : studies) {
            Study studyName = new Study();
            studyName.setContent(study);
            studiesElement.getStudy().add(studyName);
        }
        module.getNameOrStudiesOrDescription().add(studiesElement);
        Description description = new Description();
        description.setContent(rawDto.get("description"));
        module.getNameOrStudiesOrDescription().add(description);

        // Add the module
        boolean success = db.modules().create(module);

        if (!success) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.setContentType("application/xml");

        // Return the module
        PrintWriter out = response.getWriter();
        out.println(DB.marshal(module));
        out.flush();
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Util.deleteItem(db.modules(), request, response);
    }
}
