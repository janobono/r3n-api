package sk.r3n.action.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;
import javax.swing.KeyStroke;
import javax.swing.event.EventListenerList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.osgi.service.component.ComponentContext;
import org.w3c.dom.*;
import sk.r3n.action.*;
import sk.r3n.util.ConfigUtil;

public class IdActionServiceImpl implements IdActionService, Comparator<String> {

    protected class FireThread implements Runnable {

        private boolean multi;
        private IdEvent idEvent;

        public FireThread(boolean multi, IdEvent idEvent) {
            super();
            this.multi = multi;
            this.idEvent = idEvent;
        }

        @Override
        public void run() {
            try {
                fireIdEvent(idEvent);
            } finally {
                if (!multi) {
                    eventsList.remove(ConfigUtil.createKey(
                            idEvent.getGroupId(), idEvent.getActionId()));
                }
            }
        }
    }
    protected Map<String, EventListenerList> eventListenerMap;
    protected List<String> eventsList;
    protected List<String> actions;
    protected Properties names;
    protected Map<String, KeyStroke> actionKeyStroke;
    protected Map<String, URL> actionIcon;
    protected Map<String, URL> disabledActionIcon;
    protected Map<String, URL> pressedActionIcon;
    protected Map<String, Properties> actionProperties;
    protected IdEventTargetInterface idEventTargetInterface;

    public IdActionServiceImpl() {
        super();
        eventsList = new ArrayList<>();
        eventListenerMap = new HashMap<>();

        actions = new ArrayList<>();
        names = new Properties();
        actionKeyStroke = new HashMap<>();
        actionIcon = new HashMap<>();
        disabledActionIcon = new HashMap<>();
        pressedActionIcon = new HashMap<>();
        actionProperties = new HashMap<>();
    }

    protected void activate(ComponentContext context) {
        IdAction.idActionService = this;
        IdActionListener.idActionService = this;
    }

    @Override
    public void add(String groupId, int actionId) {
        String key = ConfigUtil.createKey(groupId, actionId);
        if (!actions.contains(key)) {
            actions.add(key);
        }
    }

    @Override
    public void addIdEventListener(String groupId,
            IdEventListener idEventListener) {
        if (!eventListenerMap.containsKey(groupId)) {
            eventListenerMap.put(groupId, new EventListenerList());
        }
        eventListenerMap.get(groupId).add(IdEventListener.class,
                idEventListener);
    }

    @Override
    public int compare(String s1, String s2) {
        Object[] key1 = ConfigUtil.parseKey(s1);
        Object[] key2 = ConfigUtil.parseKey(s2);
        if (key1[0].equals(key2[0])) {
            return ((Integer) key1[1]) - ((Integer) key2[1]);
        }
        return ((String) key1[0]).compareTo((String) key2[0]);
    }

    protected void deactivate(ComponentContext context) {
        IdAction.idActionService = null;
        IdActionListener.idActionService = null;
    }

    @Override
    public void fireEvent(Object source, boolean multi, String groupId,
            int actionId, Object[] data) {
        String key = ConfigUtil.createKey(groupId, actionId);
        if (!actions.contains(key)) {
            throw new IllegalArgumentException("Unknown action!");
        }
        if (!eventsList.contains(key) || multi) {
            if (!multi) {
                eventsList.add(key);
            }
            IdEvent idEvent = new IdEvent(source, groupId, actionId, data);
            Thread thread = new Thread(new FireThread(multi, idEvent), getName(
                    groupId, actionId));
            thread.start();
        }
    }

    @Override
    public void fireEvent(Object source, String groupId, int actionId,
            Object[] data) {
        fireEvent(source, false, groupId, actionId, data);
    }

    protected void fireIdEvent(IdEvent idEvent) {
        if (eventListenerMap.containsKey(idEvent.getGroupId())) {
            Object[] listeners = eventListenerMap.get(idEvent.getGroupId()).getListenerList();
            for (int i = 0; i < listeners.length; i += 2) {
                if (listeners[i] == IdEventListener.class) {
                    if (listeners[i] == IdEventListener.class) {
                        IdEventListener idEventListener = ((IdEventListener) listeners[i + 1]);
                        if (idEventListener.isEnabled(idEvent)) {
                            if (idEventTargetInterface != null) {
                                idEventTargetInterface.before(idEvent);
                            }
                            idEventListener.idEvent(idEvent);
                            if (idEventTargetInterface != null) {
                                idEventTargetInterface.after(idEvent);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<String> getActions() {
        List<String> result = new ArrayList<>();
        result.addAll(actions);
        Collections.sort(result, this);
        return result;
    }

    @Override
    public List<String> getActions(Properties properties) {
        List<String> result = new ArrayList<>();
        for (String key : actions) {
            Properties actProp = this.actionProperties.get(key);
            if (actProp != null) {
                boolean equals = true;
                for (Object propKey : properties.keySet()) {
                    if (!actProp.containsKey(propKey)) {
                        equals = false;
                        break;
                    }
                    if (!properties.getProperty(propKey.toString()).equals(
                            actProp.getProperty(propKey.toString()))) {
                        equals = false;
                        break;
                    }
                }
                if (equals) {
                    result.add(key);
                }
            }
        }
        Collections.sort(result, this);
        return result;
    }

    @Override
    public List<String> getActions(String groupId) {
        List<String> result = new ArrayList<>();
        for (String key : actions) {
            Object[] data = ConfigUtil.parseKey(key);
            if (data[0].equals(groupId)) {
                result.add(key);
            }
        }
        Collections.sort(result, this);
        return result;
    }

    @Override
    public List<String> getActions(String groupId, Properties properties) {
        List<String> result = new ArrayList<>();
        for (String key : actions) {
            Object[] data = ConfigUtil.parseKey(key);
            if (data[0].equals(groupId)) {
                Properties actProp = this.actionProperties.get(key);
                if (actProp != null) {
                    boolean equals = true;
                    for (Object propKey : properties.keySet()) {
                        if (!actProp.containsKey(propKey)) {
                            equals = false;
                            break;
                        }
                        if (!properties.getProperty(propKey.toString()).equals(actProp.getProperty(propKey.toString()))) {
                            equals = false;
                            break;
                        }
                    }
                    if (equals) {
                        result.add(key);
                    }
                }
            }
        }
        Collections.sort(result, this);
        return result;
    }

    @Override
    public URL getDisabledIcon(String groupId, int actionId) {
        return disabledActionIcon.get(ConfigUtil.createKey(groupId, actionId));
    }

    @Override
    public List<String> getGroups() {
        List<String> result = new ArrayList<>();
        for (String key : actions) {
            Object[] data = ConfigUtil.parseKey(key);
            if (!result.contains(data[0])) {
                result.add((String) data[0]);
            }
        }
        Collections.sort(result);
        return result;
    }

    private String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "?";
    }

    @Override
    public URL getIcon(String groupId, int actionId) {
        return actionIcon.get(ConfigUtil.createKey(groupId, actionId));
    }

    @Override
    public List<IdEventListener> getIdEventListeners(Object source,
            String groupId, Object[] data) {
        List<IdEventListener> result = new ArrayList<>();
        if (eventListenerMap.containsKey(groupId)) {
            Object[] listeners = eventListenerMap.get(groupId).getListenerList();
            for (int i = 0; i < listeners.length; i += 2) {
                if (listeners[i] == IdEventListener.class) {
                    IdEventListener idEventListener = ((IdEventListener) listeners[i + 1]);
                    result.add(idEventListener);
                }
            }
        }
        return result;
    }

    @Override
    public KeyStroke getKeyStroke(String groupId, int actionId) {
        return actionKeyStroke.get(ConfigUtil.createKey(groupId, actionId));
    }

    @Override
    public String getName(String groupId, int actionId) {
        String key = ConfigUtil.createKey(groupId, actionId);
        if (names.containsKey(key)) {
            return names.getProperty(key);
        }
        return key;
    }

    @Override
    public URL getPressedIcon(String groupId, int actionId) {
        return pressedActionIcon.get(ConfigUtil.createKey(groupId, actionId));
    }

    @Override
    public Properties getProperties(String groupId, int actionId) {
        return actionProperties.get(ConfigUtil.createKey(groupId, actionId));
    }

    @Override
    public boolean isAction(String groupId, int actionId) {
        return actions.contains(ConfigUtil.createKey(groupId, actionId));
    }

    @Override
    public boolean isEventEnabled(Object source, String groupId, int actionId,
            Object[] data) {
        if (eventListenerMap.containsKey(groupId)) {
            IdEvent idEvent = new IdEvent(source, groupId, actionId, data);
            Object[] listeners = eventListenerMap.get(groupId).getListenerList();
            for (int i = 0; i < listeners.length; i += 2) {
                if (listeners[i] == IdEventListener.class) {
                    IdEventListener idEventListener = ((IdEventListener) listeners[i + 1]);
                    return idEventListener.isEnabled(idEvent);
                }
            }
        }
        return false;
    }

    @Override
    public void load(InputStream conf, InputStream loc) throws Exception {
        List<String> actions = new ArrayList<>();
        Map<String, KeyStroke> actionKeyStroke = new HashMap<>();
        Map<String, URL> actionIcon = new HashMap<>();
        Map<String, URL> disabledActionIcon = new HashMap<>();
        Map<String, URL> pressedActionIcon = new HashMap<>();
        Map<String, Properties> actionProperties = new HashMap<>();

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(conf);
        // action
        NodeList nodes = doc.getElementsByTagName("action");
        for (int i = 0; i < nodes.getLength(); i++) {
            String groupId;
            int actionId;
            Element action = (Element) nodes.item(i);
            // groupId
            NodeList groupIdNode = action.getElementsByTagName("groupId");
            Element line = (Element) groupIdNode.item(0);
            groupId = getCharacterDataFromElement(line);
            // actionId
            NodeList actionIdNode = action.getElementsByTagName("actionId");
            line = (Element) actionIdNode.item(0);
            actionId = Integer.parseInt(getCharacterDataFromElement(line));
            // Add action
            String key = ConfigUtil.createKey(groupId, actionId);
            if (!actions.contains(key)) {
                actions.add(key);
            }
            // icon
            NodeList iconNode = action.getElementsByTagName("icon");
            if (iconNode.getLength() > 0) {
                line = (Element) iconNode.item(0);
                URL url = new URL(getCharacterDataFromElement(line));
                actionIcon.put(key, url);
            }
            // disabled icon
            iconNode = action.getElementsByTagName("disabled-icon");
            if (iconNode.getLength() > 0) {
                line = (Element) iconNode.item(0);
                URL url = new URL(getCharacterDataFromElement(line));
                disabledActionIcon.put(key, url);
            }
            // pressed icon
            iconNode = action.getElementsByTagName("pressed-icon");
            if (iconNode.getLength() > 0) {
                line = (Element) iconNode.item(0);
                URL url = new URL(getCharacterDataFromElement(line));
                pressedActionIcon.put(key, url);
            }
            // keyCode
            NodeList keyCodeNode = action.getElementsByTagName("keyCode");
            if (keyCodeNode.getLength() > 0) {
                Element keyCode = (Element) keyCodeNode.item(0);
                // key
                NodeList keyNode = keyCode.getElementsByTagName("key");
                // modifiers
                NodeList modifiersNode = keyCode.getElementsByTagName("modifiers");
                KeyStroke keyStroke = KeyStroke.getKeyStroke(
                        Integer.parseInt(getCharacterDataFromElement((Element) keyNode.item(0))),
                        Integer.parseInt(getCharacterDataFromElement((Element) modifiersNode.item(0))));
                actionKeyStroke.put(key, keyStroke);
            }
            // property
            NodeList propertyNode = action.getElementsByTagName("property");
            for (int j = 0; j < propertyNode.getLength(); j++) {
                Element property = (Element) propertyNode.item(j);
                // key
                NodeList keyNode = property.getElementsByTagName("key");
                // value
                NodeList valueNode = property.getElementsByTagName("value");
                if (!actionProperties.containsKey(key)) {
                    actionProperties.put(key, new Properties());
                }
                actionProperties.get(key).put(getCharacterDataFromElement((Element) keyNode.item(0)),
                        getCharacterDataFromElement((Element) valueNode.item(0)));
            }
        }
        Properties saved = new Properties();
        saved.load(loc);
        this.actions = actions;
        this.names = saved;
        this.actionKeyStroke = actionKeyStroke;
        this.actionIcon = actionIcon;
        this.disabledActionIcon = disabledActionIcon;
        this.pressedActionIcon = pressedActionIcon;
        this.actionProperties = actionProperties;
    }

    @Override
    public void processAction(String groupId, int actionId,
            IdActionExecutor idActionExecutor, Object source) {
        String key = ConfigUtil.createKey(groupId, actionId);
        if (!actions.contains(key)) {
            throw new IllegalArgumentException("Unknown action!");
        }
        if (!eventsList.contains(key)) {
            idActionExecutor.execute(groupId, actionId, source);
        }
    }

    @Override
    public void remove(String groupId, int actionId) {
        String key = ConfigUtil.createKey(groupId, actionId);
        actions.remove(key);
        actionKeyStroke.remove(key);
        actionIcon.remove(key);
        disabledActionIcon.remove(key);
        pressedActionIcon.remove(key);
        actionProperties.remove(key);
    }

    @Override
    public void removeIdEventListener(String groupId,
            IdEventListener idEventListener) {
        if (eventListenerMap.containsKey(groupId)) {
            eventListenerMap.get(groupId).remove(IdEventListener.class,
                    idEventListener);
        }
    }

    @Override
    public void save(OutputStream conf, OutputStream loc) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        // actions
        Element actions = doc.createElement("actions");
        doc.appendChild(actions);
        // action
        for (String key : this.actions) {
            Object[] data = ConfigUtil.parseKey(key);
            String groupId = (String) data[0];
            int actionId = (Integer) data[1];
            Element action = doc.createElement("action");
            actions.appendChild(action);
            // groupId
            Element groupIdElement = doc.createElement("groupId");
            groupIdElement.appendChild(doc.createTextNode(groupId));
            action.appendChild(groupIdElement);
            // actionId
            Element actionIdElement = doc.createElement("actionId");
            actionIdElement.appendChild(doc.createTextNode(Integer.toString(actionId)));
            action.appendChild(actionIdElement);
            // icon
            URL url = getIcon(groupId, actionId);
            if (url != null) {
                Element iconElement = doc.createElement("icon");
                iconElement.appendChild(doc.createTextNode(url.toString()));
                action.appendChild(iconElement);
            }
            // disabled icon
            url = getDisabledIcon(groupId, actionId);
            if (url != null) {
                Element iconElement = doc.createElement("disabled-icon");
                iconElement.appendChild(doc.createTextNode(url.toString()));
                action.appendChild(iconElement);
            }
            // pressed icon
            url = getPressedIcon(groupId, actionId);
            if (url != null) {
                Element iconElement = doc.createElement("pressed-icon");
                iconElement.appendChild(doc.createTextNode(url.toString()));
                action.appendChild(iconElement);
            }
            // keyCode
            KeyStroke keyStroke = getKeyStroke(groupId, actionId);
            if (keyStroke != null) {
                Element keyCodeElement = doc.createElement("keyCode");
                // key
                Element keyElement = doc.createElement("key");
                keyElement.appendChild(doc.createTextNode(Integer.toString(keyStroke.getKeyCode())));
                keyCodeElement.appendChild(keyElement);
                // modifiers
                Element modifiersElement = doc.createElement("modifiers");
                modifiersElement.appendChild(doc.createTextNode(Integer.toString(keyStroke.getModifiers())));
                keyCodeElement.appendChild(modifiersElement);
                action.appendChild(keyCodeElement);
            }
            // property
            Properties properties = actionProperties.get(key);
            if (properties != null) {
                for (Object propKey : properties.keySet()) {
                    Element propertyElement = doc.createElement("property");
                    // key
                    Element keyElement = doc.createElement("key");
                    keyElement.appendChild(doc.createTextNode((String) propKey));
                    propertyElement.appendChild(keyElement);
                    // value
                    Element valueElement = doc.createElement("value");
                    valueElement.appendChild(doc.createTextNode(properties.getProperty((String) propKey)));
                    propertyElement.appendChild(valueElement);
                    action.appendChild(propertyElement);
                }
            }
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(conf);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
        Properties saved = new Properties();
        saved.putAll(names);
        saved.store(loc, IdActionService.class.getCanonicalName());
    }

    @Override
    public void setDisabledIcon(String groupId, int actionId, URL url) {
        String key = ConfigUtil.createKey(groupId, actionId);
        if (!actions.contains(key)) {
            throw new IllegalArgumentException("Unknown action!");
        }
        if (url != null) {
            disabledActionIcon.put(key, url);
        } else {
            disabledActionIcon.remove(key);
        }
    }

    public void setIcon(String groupId, int actionId, URL url) {
        String key = ConfigUtil.createKey(groupId, actionId);
        if (!actions.contains(key)) {
            throw new IllegalArgumentException("Unknown action!");
        }
        if (url != null) {
            actionIcon.put(key, url);
        } else {
            actionIcon.remove(key);
        }
    }

    @Override
    public void setIdEventTargetInterface(
            IdEventTargetInterface idEventTargetInterface) {
        this.idEventTargetInterface = idEventTargetInterface;
    }

    @Override
    public void setKeyStroke(String groupId, int actionId, KeyStroke keyStroke) {
        String key = ConfigUtil.createKey(groupId, actionId);
        if (!actions.contains(key)) {
            throw new IllegalArgumentException("Unknown action!");
        }
        if (keyStroke != null) {
            actionKeyStroke.put(key, keyStroke);
        } else {
            actionKeyStroke.remove(key);
        }
    }

    @Override
    public void setName(String groupId, int actionId, String name) {
        String key = ConfigUtil.createKey(groupId, actionId);
        if (!actions.contains(key)) {
            throw new IllegalArgumentException("Unknown action!");
        }
        names.setProperty(key, name);
    }

    @Override
    public void setPressedIcon(String groupId, int actionId, URL url) {
        String key = ConfigUtil.createKey(groupId, actionId);
        if (!actions.contains(key)) {
            throw new IllegalArgumentException("Unknown action!");
        }
        if (url != null) {
            pressedActionIcon.put(key, url);
        } else {
            pressedActionIcon.remove(key);
        }
    }

    @Override
    public void setProperties(String groupId, int actionId,
            Properties properties) {
        String key = ConfigUtil.createKey(groupId, actionId);
        if (!actions.contains(key)) {
            throw new IllegalArgumentException("Unknown action!");
        }
        if (properties != null) {
            actionProperties.put(key, properties);
        } else {
            actionProperties.remove(key);
        }
    }

    @Override
    public void sync(InputStream conf, InputStream loc) throws Exception {
        List<String> actions = new ArrayList<>();
        Map<String, KeyStroke> actionKeyStroke = new HashMap<>();
        Map<String, URL> actionIcon = new HashMap<>();
        Map<String, URL> disabledActionIcon = new HashMap<>();
        Map<String, URL> pressedActionIcon = new HashMap<>();
        Map<String, Properties> actionProperties = new HashMap<>();

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(conf);
        // action
        NodeList nodes = doc.getElementsByTagName("action");
        for (int i = 0; i < nodes.getLength(); i++) {
            String groupId;
            int actionId;
            Element action = (Element) nodes.item(i);
            // groupId
            NodeList groupIdNode = action.getElementsByTagName("groupId");
            Element line = (Element) groupIdNode.item(0);
            groupId = getCharacterDataFromElement(line);
            // actionId
            NodeList actionIdNode = action.getElementsByTagName("actionId");
            line = (Element) actionIdNode.item(0);
            actionId = Integer.parseInt(getCharacterDataFromElement(line));
            // Add action
            String key = ConfigUtil.createKey(groupId, actionId);
            if (!actions.contains(key)) {
                actions.add(key);
            }
            // icon
            NodeList iconNode = action.getElementsByTagName("icon");
            if (iconNode.getLength() > 0) {
                line = (Element) iconNode.item(0);
                URL url = new URL(getCharacterDataFromElement(line));
                actionIcon.put(key, url);
            }
            // disabled icon
            iconNode = action.getElementsByTagName("disabled-icon");
            if (iconNode.getLength() > 0) {
                line = (Element) iconNode.item(0);
                URL url = new URL(getCharacterDataFromElement(line));
                disabledActionIcon.put(key, url);
            }
            // pressed icon
            iconNode = action.getElementsByTagName("pressed-icon");
            if (iconNode.getLength() > 0) {
                line = (Element) iconNode.item(0);
                URL url = new URL(getCharacterDataFromElement(line));
                pressedActionIcon.put(key, url);
            }
            // keyCode
            NodeList keyCodeNode = action.getElementsByTagName("keyCode");
            if (keyCodeNode.getLength() > 0) {
                Element keyCode = (Element) keyCodeNode.item(0);
                // key
                NodeList keyNode = keyCode.getElementsByTagName("key");
                // modifiers
                NodeList modifiersNode = keyCode.getElementsByTagName("modifiers");
                KeyStroke keyStroke = KeyStroke.getKeyStroke(
                        Integer.parseInt(getCharacterDataFromElement((Element) keyNode.item(0))),
                        Integer.parseInt(getCharacterDataFromElement((Element) modifiersNode.item(0))));
                actionKeyStroke.put(key, keyStroke);
            }
            // property
            NodeList propertyNode = action.getElementsByTagName("property");
            for (int j = 0; j < propertyNode.getLength(); j++) {
                Element property = (Element) propertyNode.item(j);
                // key
                NodeList keyNode = property.getElementsByTagName("key");
                // value
                NodeList valueNode = property.getElementsByTagName("value");
                if (!actionProperties.containsKey(key)) {
                    actionProperties.put(key, new Properties());
                }
                actionProperties.get(key).put(getCharacterDataFromElement((Element) keyNode.item(0)),
                        getCharacterDataFromElement((Element) valueNode.item(0)));
            }
        }
        Properties saved = new Properties();
        saved.load(loc);
        for (String key : this.actions) {
            if (actions.contains(key)) {
                names.remove(key);
                if (saved.containsKey(key)) {
                    names.setProperty(key, saved.getProperty(key));
                }
                this.actionKeyStroke.remove(key);
                if (actionKeyStroke.containsKey(key)) {
                    this.actionKeyStroke.put(key, actionKeyStroke.get(key));
                }
                this.actionIcon.remove(key);
                if (actionIcon.containsKey(key)) {
                    this.actionIcon.put(key, actionIcon.get(key));
                }
                this.disabledActionIcon.remove(key);
                if (disabledActionIcon.containsKey(key)) {
                    this.disabledActionIcon.put(key,
                            disabledActionIcon.get(key));
                }
                this.pressedActionIcon.remove(key);
                if (pressedActionIcon.containsKey(key)) {
                    this.pressedActionIcon.put(key, pressedActionIcon.get(key));
                }
                this.actionProperties.remove(key);
                if (actionProperties.containsKey(key)) {
                    this.actionProperties.put(key, actionProperties.get(key));
                }
            }
        }
    }
}
