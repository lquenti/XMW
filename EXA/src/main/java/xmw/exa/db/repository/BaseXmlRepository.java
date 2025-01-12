package xmw.exa.db.repository;

import org.basex.core.Context;

public abstract class BaseXmlRepository<T> implements Repository<T> {
    protected final Context context;
    protected static final String DB_NAME = "exa";

    protected BaseXmlRepository(Context context) {
        this.context = context;
    }

    protected String extractValue(String xml, String tag) {
        String pattern = String.format("<%s>([^<]*)</%s>", tag, tag);
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(xml);
        return m.find() ? m.group(1).trim() : "";
    }
}