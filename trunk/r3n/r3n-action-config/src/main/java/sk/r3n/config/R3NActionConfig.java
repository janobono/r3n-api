package sk.r3n.config;

import org.osgi.service.component.ComponentContext;
import sk.r3n.action.IdActionConfig;
import sk.r3n.action.IdActionService;
import sk.r3n.ui.util.UIServiceManager;

public class R3NActionConfig implements IdActionConfig {

    protected static IdActionService idActionService;
    private IdActionEditor idActionEditor;

    public R3NActionConfig() {
        super();
    }

    protected void activate(ComponentContext context) {
        idActionService = (IdActionService) context.locateService("IdActionService");
    }

    protected void deactivate(ComponentContext context) {
        if (idActionEditor != null) {
            idActionEditor.dispose();
        }
        idActionEditor = null;
    }

    @Override
    public void edit() {
        if (idActionEditor == null) {
            idActionEditor = new IdActionEditor();
            idActionEditor.setSize(800, 600);
            UIServiceManager.getDefaultUIService().positionCenterScreen(idActionEditor);
        }
        idActionEditor.setVisible(true);
    }
}
