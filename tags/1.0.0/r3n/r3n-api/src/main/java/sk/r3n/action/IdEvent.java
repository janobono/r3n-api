package sk.r3n.action;

import java.util.EventObject;

public class IdEvent extends EventObject {

    private String groupId;
    private int actionId;
    private Object[] data;

    public IdEvent(Object source, String groupId, int actionId, Object[] data) {
        super(source);
        this.groupId = groupId;
        this.actionId = actionId;
        this.data = data;
    }

    public int getActionId() {
        return actionId;
    }

    public Object[] getData() {
        return data;
    }

    public String getGroupId() {
        return groupId;
    }
}
