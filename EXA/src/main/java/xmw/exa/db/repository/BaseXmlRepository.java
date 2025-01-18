package xmw.exa.db.repository;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.XQuery;
import xmw.exa.db.DB;
import xmw.exa.util.Config;
import xmw.exa.util.Util;

import java.io.IOException;
import java.util.List;

public abstract class BaseXmlRepository<T extends BaseOperations> implements Repository<T> {
    protected final Context context;
    public static final String DB_NAME = Config.BASE_URL.replace("/", "");
    public final String classNameSingular;
    public final String classNamePlural;

    protected BaseXmlRepository(Context context, Class<?> classList, Class<T> classElement) {
        this.context = context;
        this.classNameSingular = classElement.getSimpleName();
        this.classNamePlural = classList.getSimpleName();
    }

    @Override
    public boolean create(T data) {
        String nextId = Util.generateId();
        // if data has id, use it
        if (data.getId() != null && !data.getId().isBlank()) {
            // ensure the id does not exist already
            List<String> allIds = this.all().stream().map(
                    T::getId).toList();

            if (allIds.contains(data.getId())) {
                data.setId(nextId);
            }
        } else {
            // Set the new ID
            data.setId(nextId);
        }

        // Create XML representation of the course
        String courseXml = DB.marshal(data);
        // remove first line, if it contains <?xml...
        courseXml = courseXml.replaceFirst("^<\\?xml.*\\?>", "");
        courseXml = courseXml.replace("xmlns=\"http://www.w3.org/namespace/\"", "");

        // Add the new course to the existing courses
        String query = String.format(
                "let $elements := /root/%s " +
                        "return insert node %s as last into $elements ",
                this.classNamePlural, courseXml);

        try {
            new XQuery(query).execute(context);
        } catch (BaseXException e) {
            throw new RuntimeException(e);
        }
        try {
            DB.getInstance().dumpToFile();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public T get(String id) {
        List<T> all = this.all();
        for (T element : all) {
            if (element.getId().equalsIgnoreCase(id)) {
                return element;
            }
        }
        // TODO: throw exception
        return null;
    }


    @Override
    public T update(T data) {
        if (!this.all().stream().map(T::getId).toList().contains(data.getId())) {
            return null;
        }
        boolean success = this.delete(data.getId());
        if (!success) {
            return null;
        }
        if (create(data)) {
            return this.get(data.getId());
        }
        return null;
    }

    @Override
    public boolean delete(String id) {
        var res = this.all().stream().map(T::getId).filter(i -> i.equalsIgnoreCase(id)).toList().size();
        if (res != 1) {
            return false;
        }
        try {
            String query = String.format("let $elements := /root/%s " +
                    "return delete node $elements/%s[@id='%s']", this.classNamePlural, this.classNameSingular, id);
            new XQuery(query).execute(context);
            try {
                DB.getInstance().dumpToFile();
            } catch (IOException e) {
                System.err.println("Failed to delete " + this.classNameSingular + ": " + e.getMessage());
            }
            return true;
        } catch (BaseXException e) {
            System.err.println("Failed to delete " + this.classNameSingular + ": " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}

