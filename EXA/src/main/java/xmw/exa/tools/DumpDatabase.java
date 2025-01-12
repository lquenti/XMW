package xmw.exa.tools;

import xmw.exa.db.DB;
import xmw.exa.util.Config;

public class DumpDatabase {
    public static void main(String[] args) {
        try {
            if (args.length > 0 && args[0].equals("--flush")) {
                // Create flush.xml
                DB.getInstance().dumpToFlushFile();
                System.out.println("Database successfully dumped to flush file: " + Config.FLUSH_FILE_PATH);
            } else {
                // Regular dump to file
                String outputPath = args.length > 0 ? args[0] : Config.XMW_DATA_PATH + "/dump.xml";
                DB.getInstance().dumpToFile(outputPath);
                System.out.println("Database successfully dumped to: " + outputPath);
            }
        } catch (Exception e) {
            System.err.println("Error dumping database: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}