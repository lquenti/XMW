package xmw.exa.db.repository;

import java.io.IOException;

import org.basex.core.Context;
import xmw.exa.util.Config;

public abstract class BaseXmlRepository<T> implements Repository<T> {
    protected final Context context;
    public static final String DB_NAME = Config.BASE_URL.replace("/", "");

    protected BaseXmlRepository(Context context) {
        this.context = context;
    }

    protected String extractValue(String xml, String tag) {
        String pattern = String.format("<%s>([^<]*)</%s>", tag, tag);
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(xml);
        return m.find() ? m.group(1).trim() : "";
    }

    protected void updateXMLFile(String fileName, String parentTag, String childXml) throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

}