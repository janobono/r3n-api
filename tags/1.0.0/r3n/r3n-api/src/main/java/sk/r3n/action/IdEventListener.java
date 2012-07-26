package sk.r3n.action;

import java.util.EventListener;

public interface IdEventListener extends EventListener {

    public void idEvent(IdEvent idEvent);

    public boolean isEnabled(IdEvent idEvent);
}
