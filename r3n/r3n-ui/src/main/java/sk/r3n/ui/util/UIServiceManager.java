package sk.r3n.ui.util;

import sk.r3n.ui.UIService;

public class UIServiceManager {

    private static UIService uiService;

    public static UIService getDefaultUIService() {
        return uiService;
    }

    public static void setDefaultUIService(UIService uiService) {
        UIServiceManager.uiService = uiService;
    }
}
