package xmw.user.routes;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xmw.user.db.UserDB;
import xmw.user.utils.DTDValidatorUtils;

@WebServlet(name = "testServlet", value = "/test")
public class TestServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) {
        try {
            System.out.println(UserDB.authenticate("hbrosen", "hunter2"));
            System.out.println(UserDB.authenticate("hbrosen", "hunter3"));
            System.out.println(UserDB.authenticate("hbrosnn", "hunter2"));
            System.out.println(UserDB.getUserByUsername("hbrosen", false));
            System.out.println("Validation:");
            System.out.println(DTDValidatorUtils.validateWithPassword(
                    """
                        <User username="hbrosen2">
                           <name>Brosenne</name>
                           <firstname>Hendrik</firstname>
                           <faculty>Computer Science</faculty>
                           <group id="g_lecturer">Lecturer</group>
                           <group id="g_employee">Employee</group>
                         </User>
                        """
            ));
            System.out.println(DTDValidatorUtils.validateWithPassword(
                    """
                        <User username="hbrosen2">
                           <name>Brosenne</name>
                           <firstname>Hendrik</firstname>
                           <password>hunter2</password>
                           <faculty>Computer Science</faculty>
                           <group id="g_lecturer">Lecturer</group>
                           <group id="g_employee">Employee</group>
                         </User>
                        """
            ));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
