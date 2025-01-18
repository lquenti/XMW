package xmw.exa.tools;

import xmw.exa.db.DB;
import xmw.exa.util.Config;

public class DumpDatabase {
    public static void main(String[] args) {
        try {
            String outputPath = args.length > 0 ? args[0] : Config.FLUSH_FILE_PATH;
            DB.getInstance().dumpToFile(outputPath);
            System.out.println("Database successfully dumped to: " + outputPath);
        } catch (Exception e) {
            System.err.println("Error dumping database: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}