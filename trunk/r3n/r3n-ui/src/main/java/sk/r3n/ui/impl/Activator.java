package sk.r3n.ui.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import sk.r3n.action.IdActionService;
import sk.r3n.ui.UIService;
import sk.r3n.ui.util.UIServiceManager;

public class Activator implements BundleActivator, ServiceListener {

    private BundleContext bc;

    @Override
    public void start(BundleContext bc) throws Exception {
        this.bc = bc;
        bc.addServiceListener(this);

        UIServiceManager manager = new UIServiceManager();
        UIServiceManager.setDefaultUIService(manager);

        ServiceReference sr = bc.getServiceReference(IdActionService.class.getCanonicalName());
        if (sr != null) {
            registerIdActionInstance((IdActionService) bc.getService(sr));
        }
    }

    @Override
    public void stop(BundleContext bc) throws Exception {
        UIServiceManager.setIdActionService(null);
    }

    @Override
    public void serviceChanged(ServiceEvent se) {
        switch (se.getType()) {
            case ServiceEvent.REGISTERED:
                ServiceReference sr = se.getServiceReference();
                Object service = bc.getService(sr);
                if (service instanceof IdActionService) {
                    registerIdActionInstance((IdActionService) service);
                }
                break;
            case ServiceEvent.UNREGISTERING:
                sr = se.getServiceReference();
                service = bc.getService(sr);
                if (service instanceof IdActionService) {
                    unregisterIdActionInstance((IdActionService) service);
                }
                break;
        }
    }

    private void registerIdActionInstance(IdActionService idActionService) {
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_CLOSE);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_RESTART);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_REFRESH);

        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_OK);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_CANCEL);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_YES);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_NO);

        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_UP);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_DOWN);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_LEFT);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_RIGHT);

        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_MOVE_UP);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_MOVE_DOWN);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_MOVE_LEFT);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_MOVE_RIGHT);

        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_FIRST);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_PREVIOUS);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_NEXT);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_LAST);

        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_PREVIOUS_ROWS);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_NEXT_ROWS);

        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_BUTTON);

        idActionService.add(UIService.class.getCanonicalName(), UIService.FOCUS_FORWARD);
        idActionService.add(UIService.class.getCanonicalName(), UIService.FOCUS_BACKWARD);

        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_ADD);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_COPY);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_EDIT);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_REMOVE);

        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_ADD_TO_LIST);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_EDIT_ON_LIST);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_REMOVE_FROM_LIST);

        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_CELL_EDIT);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_CELL_OK);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_CELL_CANCEL);

        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_INFO);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_WARNING);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_ERROR);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_QUESTION);

        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_SELECT);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_PREVIEW);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_PRINT);

        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_PROPERTIES);

        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_SEARCH);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_SWITCH_SEARCH_KEY);

        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_FILE_NEW);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_FILE_OPEN);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_FILE_SAVE);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_FILE_SAVE_AS);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_FILE_DELETE);

        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_DIR_NEW);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_DIR_OPEN);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_DIR_DELETE);

        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_DEFAULT);

        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_ENABLE);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_DISABLE);

        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_ABOUT);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_HELP);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_LICENSE);
        idActionService.add(UIService.class.getCanonicalName(), UIService.ACTION_LOG_PREVIEW);
        UIServiceManager.setIdActionService(idActionService);
    }

    private void unregisterIdActionInstance(IdActionService idActionService) {
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_CLOSE);

        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_OK);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_CANCEL);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_YES);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_NO);

        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_UP);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_DOWN);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_LEFT);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_RIGHT);

        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_MOVE_UP);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_MOVE_DOWN);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_MOVE_LEFT);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_MOVE_RIGHT);

        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_FIRST);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_PREVIOUS);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_NEXT);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_LAST);

        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_PREVIOUS_ROWS);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_NEXT_ROWS);

        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_BUTTON);

        idActionService.remove(UIService.class.getCanonicalName(), UIService.FOCUS_FORWARD);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.FOCUS_BACKWARD);

        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_ADD);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_COPY);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_EDIT);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_REMOVE);

        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_ADD_TO_LIST);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_EDIT_ON_LIST);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_REMOVE_FROM_LIST);

        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_CELL_EDIT);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_CELL_OK);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_CELL_CANCEL);

        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_INFO);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_WARNING);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_ERROR);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_QUESTION);

        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_SELECT);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_PREVIEW);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_PRINT);

        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_PROPERTIES);

        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_SEARCH);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_SWITCH_SEARCH_KEY);

        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_FILE_NEW);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_FILE_OPEN);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_FILE_SAVE);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_FILE_SAVE_AS);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_FILE_DELETE);

        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_DIR_NEW);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_DIR_OPEN);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_DIR_DELETE);

        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_DEFAULT);

        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_ENABLE);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_DISABLE);

        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_ABOUT);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_HELP);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_LICENSE);
        idActionService.remove(UIService.class.getCanonicalName(), UIService.ACTION_LOG_PREVIEW);
        UIServiceManager.setIdActionService(null);
    }
}
