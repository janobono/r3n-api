package sk.r3n.action.impl;

import java.util.*;
import sk.r3n.action.R3NActionService;
import sk.r3n.util.ConfigUtil;

public class R3NActionServiceImpl implements R3NActionService, Comparator<String> {
    
    protected Map<String, Map<String, String>> actions;
    
    public R3NActionServiceImpl() {
        super();
        actions = new HashMap<>();
    }
    
    @Override
    public boolean isAction(String groupId, int actionId) {
        return actions.keySet().contains(toString(groupId, actionId));
    }
    
    @Override
    public void add(String groupId, int actionId) {
        String action = toString(groupId, actionId);
        if (!actions.keySet().contains(action)) {
            actions.put(action, new HashMap<String, String>());
        }
    }
    
    @Override
    public void remove(String groupId, int actionId) {
        String action = toString(groupId, actionId);
        actions.remove(action);
    }
    
    @Override
    public int compare(String s1, String s2) {
        Object[] key1 = parseAction(s1);
        Object[] key2 = parseAction(s2);
        if (key1[0].equals(key2[0])) {
            return ((Integer) key1[1]) - ((Integer) key2[1]);
        }
        return ((String) key1[0]).compareTo((String) key2[0]);
    }
    
    @Override
    public List<String> getActions() {
        List<String> result = new ArrayList<>();
        result.addAll(actions.keySet());
        Collections.sort(result, this);
        return result;
    }
    
    @Override
    public List<String> getActions(String groupId) {
        List<String> result = new ArrayList<>();
        for (String action : actions.keySet()) {
            Object[] data = parseAction(action);
            if (data[0].equals(groupId)) {
                result.add(action);
            }
        }
        Collections.sort(result, this);
        return result;
    }
    
    @Override
    public List<String> getGroups() {
        List<String> result = new ArrayList<>();
        for (String key : actions.keySet()) {
            Object[] data = parseAction(key);
            if (!result.contains((String) data[0])) {
                result.add((String) data[0]);
            }
        }
        Collections.sort(result);
        return result;
    }
    
    @Override
    public String getProperty(String groupId, int actionId, String key) {
        String action = toString(groupId, actionId);
        if (!actions.keySet().contains(action)) {
            throw new IllegalArgumentException("Unknown action!");
        }
        return actions.get(action).get(key);
    }
    
    @Override
    public void setProperty(String groupId, int actionId, String key, String value) {
        String action = toString(groupId, actionId);
        if (!actions.keySet().contains(action)) {
            throw new IllegalArgumentException("Unknown action!");
        }
        if (value != null) {
            actions.get(action).put(key, value);
        } else {
            actions.get(action).remove(key);
        }
    }
    
    @Override
    public Object[] parseAction(String action) {
        return ConfigUtil.parseKey(action);
    }
    
    @Override
    public String toString(String groupId, int actionId) {
        return ConfigUtil.createKey(groupId, actionId);
    }
    
}
