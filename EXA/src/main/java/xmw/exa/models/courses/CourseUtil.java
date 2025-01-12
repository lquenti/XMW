package xmw.exa.models.courses;

import org.basex.core.Context;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.XQuery;
import xmw.exa.db.repository.BaseXmlRepository;
import xmw.exa.util.Config;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class CourseUtil {
    private CourseUtil() {
    }

    public static final String allQuery = "for $c in /root/Courses/Course " +
            "return element course { " +
            "  attribute id { $c/id/text() }, " +
            "  attribute semester_id { $c/semester_id/text() }, " +
            "  element faculty { $c/faculty/text() }, " +
            "  element lecturer { attribute id { $c/lecturer_id/text() } }, " +
            "  element max_students { $c/max_students/text() }, " +
            "  element name { $c/name/text() } " +
            "}";



    public static String addQuery(String xml) {
        return String.format("db:add('%s', '%s', '%s')",
                BaseXmlRepository.DB_NAME,
                xml.replace("'", "''"),
                "root"
        );
    }

    public static void main(String[] args) throws Exception {

        var xml = new String(Files.readAllBytes(new File(Config.FLUSH_FILE_PATH).toPath()), StandardCharsets.UTF_8);

        var context = new Context();
        new CreateDB(BaseXmlRepository.DB_NAME).execute(context);

        var addQueryString = CourseUtil.addQuery(xml);
        new XQuery(addQueryString).execute(context);

        var result = new XQuery(allQuery).execute(context);
        System.out.println(result);
    }
}
