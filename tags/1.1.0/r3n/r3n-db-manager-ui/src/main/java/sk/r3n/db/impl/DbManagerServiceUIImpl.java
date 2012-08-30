package sk.r3n.db.impl;

import java.util.Properties;
import org.osgi.service.component.ComponentContext;
import sk.r3n.action.IdActionService;
import sk.r3n.app.AppHelp;
import sk.r3n.db.DbManagerServiceUI;
import sk.r3n.ui.UIService;
import sk.r3n.ui.util.UIServiceManager;

public class DbManagerServiceUIImpl implements DbManagerServiceUI {

    protected static ComponentContext context;
    protected static AppHelp appHelp;
    protected static IdActionService idActionService;

    protected void activate(ComponentContext context) {
        DbManagerServiceUIImpl.context = context;
        DbManagerServiceUIImpl.appHelp = (AppHelp) context.locateService("AppHelp");
        idActionService = (IdActionService) context.locateService("IdActionService");
        idActionService.add(DbManagerServiceUI.class.getCanonicalName(), ACTION_TEST);
    }

    protected void deactivate(ComponentContext context) {
        appHelp = null;
        idActionService.remove(DbManagerServiceUI.class.getCanonicalName(), ACTION_TEST);
        idActionService = null;
        DbManagerServiceUIImpl.context = null;
    }

    @Override
    public Properties edit(Properties properties) {
        Properties result = null;
        ConnectionPropertiesDialog connectionPropertiesDialog;
        connectionPropertiesDialog = new ConnectionPropertiesDialog(
                UIServiceManager.getDefaultUIService().getRootFrame());
        if (connectionPropertiesDialog.init(properties)) {
            result = connectionPropertiesDialog.getProperties();
        }
        return result;
    }

    @Override
    public void showInfo(String title, String message) {
        UIServiceManager.getDefaultUIService().showMessageDialog(title, message, UIService.MESSAGE_ACTION_INFORMATION);
    }

    @Override
    public void showWarning(String title, String message) {
        UIServiceManager.getDefaultUIService().showMessageDialog(title, message, UIService.MESSAGE_ACTION_WARNING);
    }

    @Override
    public void showError(String title, String message) {
        UIServiceManager.getDefaultUIService().showMessageDialog(title, message, UIService.MESSAGE_ACTION_ERROR);
    }

    @Override
    public boolean askWarning(String title, String message) {
        return UIServiceManager.getDefaultUIService().showYesNoDialog(title, message, UIService.MESSAGE_ACTION_WARNING) == UIService.ANSWER_YES;
    }
}
