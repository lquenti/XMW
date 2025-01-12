package xmw.exa.util;

public class Config {
    private Config() {
    }

    public static final String XMW_DATA_PATH = System.getProperty("user.home") + "/xmw_data";
    public static final String BASE_URL = "/exa";
    public static final String FLUSH_FILE = "flush.xml";
    public static final String FLUSH_FILE_PATH = String.format("%s/%s", Config.XMW_DATA_PATH, Config.FLUSH_FILE);
}
