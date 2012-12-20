package sk.r3n.db;

import java.util.Properties;

public interface DbManagerServiceUI {

    public int ACTION_TEST = 10;

    public Properties edit(Properties properties);
    
    public void showInfo(String title, String message);

    public void showWarning(String title, String message);
    
    public void showError(String title, String message);

    public boolean askWarning(String title, String message);
}
