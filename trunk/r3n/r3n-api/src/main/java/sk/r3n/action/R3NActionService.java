package sk.r3n.action;

import java.util.List;

public interface R3NActionService {

    public boolean isAction(String groupId, int actionId);

    public void add(String groupId, int actionId);

    public void remove(String groupId, int actionId);

    public List<String> getActions();

    public List<String> getActions(String groupId);

    public List<String> getGroups();

    public String getProperty(String groupId, int actionId, String key);

    public void setProperty(String groupId, int actionId, String key, String value);

    public Object[] parseAction(String action);

    public String toString(String groupId, int actionId);

}
