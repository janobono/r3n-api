package sk.r3n.action;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.swing.KeyStroke;

public interface IdActionService {

	public void add(String groupId, int actionId);

	public void addIdEventListener(String groupId,
			IdEventListener idEventListener);

	public void fireEvent(Object source, boolean multi, String groupId,
			int actionId, Object[] data);

	public void fireEvent(Object source, String groupId, int actionId,
			Object[] data);

	public List<String> getActions();

	public List<String> getActions(Properties properties);

	public List<String> getActions(String groupId);

	public List<String> getActions(String groupId, Properties properties);

	public URL getDisabledIcon(String groupId, int actionId);

	public List<String> getGroups();

	public URL getIcon(String groupId, int actionId);

	public List<IdEventListener> getIdEventListeners(Object source,
			String groupId, Object[] data);

	public KeyStroke getKeyStroke(String groupId, int actionId);

	public String getName(String groupId, int actionId);

	public URL getPressedIcon(String groupId, int actionId);

	public Properties getProperties(String groupId, int actionId);

	public boolean isAction(String groupId, int actionId);

	public boolean isEventEnabled(Object source, String groupId, int actionId,
			Object[] data);

	public void load(InputStream conf, InputStream loc) throws Exception;

	public void processAction(String groupId, int actionId,
			IdActionExecutor idActionExecutor, Object source);

	public void remove(String groupId, int actionId);

	public void removeIdEventListener(String groupId,
			IdEventListener idEventListener);

	public void save(OutputStream conf, OutputStream loc) throws Exception;

	public void setDisabledIcon(String groupId, int actionId, URL url);

	public void setIcon(String groupId, int actionId, URL url);

	public void setIdEventTargetInterface(
			IdEventTargetInterface idEventTargetInterface);

	public void setKeyStroke(String groupId, int actionId, KeyStroke keyStroke);

	public void setName(String groupId, int actionId, String name);

	public void setPressedIcon(String groupId, int actionId, URL url);

	public void setProperties(String groupId, int actionId,
			Properties properties);

	public void sync(InputStream conf, InputStream loc) throws Exception;

}
