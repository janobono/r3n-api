package sk.r3n.config;

import org.osgi.service.component.ComponentContext;
import sk.r3n.action.IdActionConfig;
import sk.r3n.action.IdActionService;
import sk.r3n.ui.UIService;

public class R3NActionConfig implements IdActionConfig {

    protected static IdActionService idActionService;
    protected static UIService uiService;
    private IdActionEditor idActionEditor;

    public R3NActionConfig() {
        super();
    }

    protected void activate(ComponentContext context) {
        idActionService = (IdActionService) context.locateService("IdActionService");
        uiService = (UIService) context.locateService("UIService");
    }

    protected void deactivate(ComponentContext context) {
        if (idActionEditor != null) {
            idActionEditor.dispose();
        }
        idActionEditor = null;
        uiService = null;
    }

    @Override
    public void edit() {
        if (idActionEditor == null) {
            idActionEditor = new IdActionEditor();
            idActionEditor.setSize(800, 600);
            uiService.positionCenterScreen(idActionEditor);
        }
        idActionEditor.setVisible(true);
    }
}
