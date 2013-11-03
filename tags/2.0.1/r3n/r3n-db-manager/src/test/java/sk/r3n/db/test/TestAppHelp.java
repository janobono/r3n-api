package sk.r3n.db.test;

import sk.r3n.app.AppHelp;
import sk.r3n.sw.util.SwingUtil;
import sk.r3n.ui.MessageType;

public class TestAppHelp implements AppHelp {

    @Override
    public void showHelp() {
    }

    @Override
    public void showHelp(String key) {
        SwingUtil.showMessageDialog("", key, MessageType.INFO);
    }

}
