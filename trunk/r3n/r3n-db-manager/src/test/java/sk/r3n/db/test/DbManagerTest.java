package sk.r3n.db.test;

import java.util.Properties;
import sk.r3n.db.DbManagerService;
import sk.r3n.sw.util.SwingUtil;
import sk.r3n.util.R3NException;

public class DbManagerTest {

    public static void main(String[] args) {
        try {
            DbManagerService dbManagerService = new DbManagerService(new TestAppProperties()) {
                @Override
                protected void checkStructure(Properties properties) throws R3NException {
                }
            };
            dbManagerService.setAppHelp(new TestAppHelp());
            dbManagerService.setHelpKey("helpKey");
            dbManagerService.setDefaultName("testdb");
            dbManagerService.checkDB();
        } catch (R3NException ex) {
            ex.printStackTrace();
        }
        System.out.println("OK");
        SwingUtil.getRootFrame().dispose();
    }
}
