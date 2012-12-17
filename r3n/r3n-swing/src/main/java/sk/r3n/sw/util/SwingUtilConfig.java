package sk.r3n.sw.util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import sk.r3n.util.ConfigUtil;

public class SwingUtilConfig {

    private Dimension max;

    private float coefficient;

    private Dimension defaultIconDimension;

    private Map<String, Dimension> dimensionMap;

    private Map<String, Boolean> recountMap;

    private Map<String, KeyStroke> keyStrokeMap;

    private Map<String, URL> urlMap;

    public SwingUtilConfig() {
        super();
        coefficient = 1.0f;
        dimensionMap = new HashMap<>();
        recountMap = new HashMap<>();
        keyStrokeMap = new HashMap<>();
        urlMap = new HashMap<>();

        ResourceBundle bundle = null;
        try {
            bundle = ResourceBundle.getBundle("sk.r3n.sw.util.JFileChooser");
        } catch (Exception e) {
        }
        if (bundle != null) {
            UIManager.put("FileChooser.lookInLabelText", bundle.getString("lookInLabelText"));
            UIManager.put("FileChooser.saveInLabelText", bundle.getString("saveInLabelText"));
            UIManager.put("FileChooser.filesOfTypeLabelText", bundle.getString("filesOfTypeLabelText"));
            UIManager.put("FileChooser.upFolderToolTipText", bundle.getString("upFolderToolTipText"));
            UIManager.put("FileChooser.fileNameLabelText", bundle.getString("fileNameLabelText"));
            UIManager.put("FileChooser.homeFolderToolTipText", bundle.getString("homeFolderToolTipText"));
            UIManager.put("FileChooser.newFolderToolTipText", bundle.getString("newFolderToolTipText"));
            UIManager.put("FileChooser.listViewButtonToolTipTextlist", bundle.getString("listViewButtonToolTipTextlist"));
            UIManager.put("FileChooser.detailsViewButtonToolTipText", bundle.getString("detailsViewButtonToolTipText"));
            UIManager.put("FileChooser.openButtonText", bundle.getString("openButtonText"));
            UIManager.put("FileChooser.saveButtonText", bundle.getString("saveButtonText"));
            UIManager.put("FileChooser.cancelButtonText", bundle.getString("cancelButtonText"));
            UIManager.put("FileChooser.updateButtonText", bundle.getString("updateButtonText"));
            UIManager.put("FileChooser.helpButtonText", bundle.getString("helpButtonText"));
            UIManager.put("FileChooser.saveButtonToolTipText", bundle.getString("saveButtonToolTipText"));
            UIManager.put("FileChooser.openButtonToolTipText", bundle.getString("openButtonToolTipText"));
            UIManager.put("FileChooser.cancelButtonToolTipText", bundle.getString("cancelButtonToolTipText"));
            UIManager.put("FileChooser.updateButtonToolTipText", bundle.getString("updateButtonToolTipText"));
            UIManager.put("FileChooser.helpButtonToolTipText", bundle.getString("helpButtonToolTipText"));
        }
    }

    public float getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(float coefficient) {
        this.coefficient = coefficient;
    }

    public Dimension getDefaultIconDimension() {
        if (defaultIconDimension == null) {
            defaultIconDimension = new Dimension(20, 20);
        }
        return defaultIconDimension;
    }

    public void setDefaultIconDimension(Dimension defaultDimension) {
        this.defaultIconDimension = defaultDimension;
    }

    public Dimension getDimension(String key) {
        return dimensionMap.get(key);
    }

    public void setDimension(String key, Dimension dimension) {
        if (dimension == null) {
            dimensionMap.remove(key);
        } else {
            dimensionMap.put(key, dimension);
        }
    }

    public boolean getRecount(String key) {
        Boolean result = recountMap.get(key);
        if (result == null) {
            result = false;
        }
        return result;
    }

    public void setRecount(String key, Boolean recount) {
        if (recount == null) {
            recountMap.remove(key);
        } else {
            recountMap.put(key, recount);
        }
    }

    public URL getURL(UIActionKey actionKey, IconType iconType) {
        String key = ConfigUtil.createKey(actionKey.group(), actionKey.code()) + iconType.name();
        return urlMap.get(key);
    }

    public void setURL(UIActionKey actionKey, IconType iconType, URL url) {
        String key = ConfigUtil.createKey(actionKey.group(), actionKey.code()) + iconType.name();
        if (url != null) {
            urlMap.put(key, url);
        } else {
            urlMap.remove(key);
        }
    }

    public Dimension getMaxDimension() {
        if (max == null) {
            max = Toolkit.getDefaultToolkit().getScreenSize();
        } else {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (screenSize.height < max.height) {
                max.height = screenSize.height;
            }
            if (screenSize.width < max.width) {
                max.width = screenSize.width;
            }
        }
        return max;
    }

    public void setMaxDimension(Dimension max) {
        this.max = max;
    }

    public KeyStroke getKeyStroke(UIActionKey actionKey) {
        String key = ConfigUtil.createKey(actionKey.group(), actionKey.code());
        return keyStrokeMap.get(key);
    }

    public void setKeyStroke(UIActionKey actionKey, KeyStroke keyStroke) {
        String key = ConfigUtil.createKey(actionKey.group(), actionKey.code());
        if (keyStroke != null) {
            keyStrokeMap.put(key, keyStroke);
        } else {
            keyStrokeMap.remove(key);
        }
    }

}
