package sk.r3n.ui.util;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import org.apache.batik.transcoder.TranscoderInput;
import sk.r3n.action.IdActionExecutor;
import sk.r3n.action.IdActionService;
import sk.r3n.ui.Buzzer;
import sk.r3n.ui.IdAction;
import sk.r3n.ui.UIService;
import sk.r3n.ui.component.R3NFileFilter;
import sk.r3n.ui.component.R3NInputComponent;
import sk.r3n.ui.dialog.R3NOkDialog;
import sk.r3n.ui.dialog.R3NPasswordDialog;
import sk.r3n.ui.dialog.R3NStatusDialog;
import sk.r3n.ui.dialog.R3NYesNoCancelDialog;
import sk.r3n.ui.dialog.R3NYesNoDialog;
import sk.r3n.ui.panel.MessagePanel;

public final class UIServiceManager implements UIService {

    private static UIService uiService;
    private static IdActionService idActionService;

    public static UIService getDefaultUIService() {
        return uiService;
    }

    public static void setDefaultUIService(UIService uiService) {
        UIServiceManager.uiService = uiService;
    }

    public static void setIdActionService(IdActionService idActionService) {
        UIServiceManager.idActionService = idActionService;
    }
    
    protected float coefficient;
    protected Dimension defaultDimension;
    protected long statusId;
    protected Map<Long, R3NStatusDialog> statusDialogs;
    protected Map<String, Dimension> dimension;
    protected Map<String, Boolean> recount;
    protected Dimension max;
    protected Frame frame;
    protected Buzzer buzzer;

    public UIServiceManager() {
        super();
        coefficient = 1.0f;
        defaultDimension = new Dimension(20, 20);
        statusId = 0;
        statusDialogs = new HashMap<>();

        dimension = new HashMap<>();
        recount = new HashMap<>();
        setMaxDimension(null);
        setRootFrame(null);

        ResourceBundle bundle = null;
        try {
            bundle = ResourceBundle.getBundle("sk.r3n.ui.util.JFileChooser");
        } catch (Exception e) {
        }
        if (bundle != null) {
            UIManager.put("FileChooser.lookInLabelText",
                    bundle.getString("lookInLabelText"));
            UIManager.put("FileChooser.saveInLabelText",
                    bundle.getString("saveInLabelText"));
            UIManager.put("FileChooser.filesOfTypeLabelText",
                    bundle.getString("filesOfTypeLabelText"));
            UIManager.put("FileChooser.upFolderToolTipText",
                    bundle.getString("upFolderToolTipText"));
            UIManager.put("FileChooser.fileNameLabelText",
                    bundle.getString("fileNameLabelText"));
            UIManager.put("FileChooser.homeFolderToolTipText",
                    bundle.getString("homeFolderToolTipText"));
            UIManager.put("FileChooser.newFolderToolTipText",
                    bundle.getString("newFolderToolTipText"));
            UIManager.put("FileChooser.listViewButtonToolTipTextlist",
                    bundle.getString("listViewButtonToolTipTextlist"));
            UIManager.put("FileChooser.detailsViewButtonToolTipText",
                    bundle.getString("detailsViewButtonToolTipText"));
            UIManager.put("FileChooser.openButtonText",
                    bundle.getString("openButtonText"));
            UIManager.put("FileChooser.saveButtonText",
                    bundle.getString("saveButtonText"));
            UIManager.put("FileChooser.cancelButtonText",
                    bundle.getString("cancelButtonText"));
            UIManager.put("FileChooser.updateButtonText",
                    bundle.getString("updateButtonText"));
            UIManager.put("FileChooser.helpButtonText",
                    bundle.getString("helpButtonText"));
            UIManager.put("FileChooser.saveButtonToolTipText",
                    bundle.getString("saveButtonToolTipText"));
            UIManager.put("FileChooser.openButtonToolTipText",
                    bundle.getString("openButtonToolTipText"));
            UIManager.put("FileChooser.cancelButtonToolTipText",
                    bundle.getString("cancelButtonToolTipText"));
            UIManager.put("FileChooser.updateButtonToolTipText",
                    bundle.getString("updateButtonToolTipText"));
            UIManager.put("FileChooser.helpButtonToolTipText",
                    bundle.getString("helpButtonToolTipText"));
        }
    }

    @Override
    public String getActionMapKey(String groupId, int actionId) {
        return groupId + actionId;
    }

    @Override
    public Buzzer getBuzzer() {
        if (buzzer == null) {
            buzzer = new Buzzer() {
                @Override
                public void buzz(Component source) {
                    buzz(source,
                            ResourceBundle.getBundle(
                            UIServiceManager.class.getCanonicalName()).getString("NO_VALID"));
                }

                @Override
                public void buzz(Component source, String text) {
                    Toolkit.getDefaultToolkit().beep();
                    UIServiceManager.this.showMessageDialog(null, text,
                            MESSAGE_ACTION_WARNING);
                }
            };
        }
        return buzzer;
    }

    private Icon getDialogIcon(int messageType) {
        int size = (int) (32 * coefficient);
        switch (messageType) {
            case MESSAGE_ACTION_INFORMATION:
            case MESSAGE_ACTION_ERROR:
            case MESSAGE_ACTION_WARNING:
            case MESSAGE_ACTION_QUESTION:
                break;
            default:
                return null;
        }
        URL url = idActionService.getIcon(UIService.class.getCanonicalName(),
                messageType);
        return getIcon(url, new Dimension(size, size));
    }

    @Override
    public Frame getFrameForComponent(Component component) {
        if (component == null) {
            return getRootFrame();
        }
        if (component instanceof Frame) {
            return (Frame) component;
        }
        return getFrameForComponent(component.getParent());
    }

    @Override
    public Icon getIcon(URL url) {
        return getIcon(url, new Dimension(
                (int) (defaultDimension.width * coefficient),
                (int) (defaultDimension.height * coefficient)));
    }

    @Override
    public Icon getIcon(URL url, Dimension dimension) {
        if (url != null) {
            if (url.getFile().toLowerCase().endsWith(".svg")) {
                try {
                    return new SVGIcon(url, dimension);
                } catch (Exception e) {
                }
            } else {
                return new ImageIcon(url);
            }
        }
        return null;
    }

    @Override
    public IdActionService getIdActionService() {
        return idActionService;
    }

    @Override
    public Image getImage(URL url) {
        BufferedImage image = null;
        if (url != null) {
            if (url.getFile().toLowerCase().endsWith(".svg")) {
                try {
                    BufferedImageTranscoder t = new BufferedImageTranscoder();
                    t.transcode(new TranscoderInput(url.toURI().toString()), 0,
                            0);
                    image = t.getBufferedImage();
                } catch (Exception e) {
                }
            } else {
                try {
                    image = ImageIO.read(url);
                } catch (Exception e) {
                }
            }
        }
        return image;
    }

    @Override
    public byte[] getPassword(String title) {
        R3NPasswordDialog vuiPasswordDialog = new R3NPasswordDialog(
                getRootFrame());
        if (title != null) {
            vuiPasswordDialog.setTitle(title);
        }
        if (vuiPasswordDialog.initDialog()) {
            return vuiPasswordDialog.getPassword();
        }
        return null;
    }

    @Override
    public Frame getRootFrame() {
        return frame;
    }

    public Window getWindowForComponent(Component component) {
        if (component == null) {
            return getRootFrame();
        }
        if (component instanceof Frame || component instanceof Dialog) {
            return (Window) component;
        }
        return getWindowForComponent(component.getParent());
    }

    @Override
    public boolean isInputValid(List<R3NInputComponent<?>> inputComponents) {
        for (R3NInputComponent<?> baseInputComponent : inputComponents) {
            int contentValid = baseInputComponent.contentValid();
            if (contentValid != R3NInputComponent.VALID) {
                if (baseInputComponent instanceof JComponent) {
                    Component component = (JComponent) baseInputComponent;
                    if (component.isEnabled()) {
                        if (component.isFocusable()) {
                            component.requestFocus();
                        }
                        getBuzzer().buzz(
                                component,
                                ResourceBundle.getBundle(
                                UIServiceManager.class.getCanonicalName()).getString("NO_VALID"));
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void modifyDimensions(Window window) {
        if (window instanceof JFrame) {
            if (((JFrame) window).getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                return;
            }
        }
        Dimension result = window.getSize();
        // Zadane rozmery
        Dimension dim = dimension.get(window.getClass().getCanonicalName());
        if (dim != null) {
            if (result.width < dim.width) {
                result.width = dim.width;
            }
            if (result.height < dim.height) {
                result.height = dim.height;
            }
        }
        // Rozmery z nadradeneho
        Window owner = window.getOwner();
        Boolean rec = this.recount.get(window.getClass().getCanonicalName());
        if (owner != null && rec != null && rec) {
            dim = owner.getSize();
            if (result.width < dim.width) {
                result.width = dim.width - 20;
            }
            if (result.height < dim.height) {
                result.height = dim.height - 20;
            }
        }
        // Koeficient velkosti textu
        if (!(UIManager.getLookAndFeel().getClass().getCanonicalName().endsWith("NimbusLookAndFeel"))) {
            result.setSize(coefficient * result.width, coefficient
                    * result.height);
        }
        // Porovnanie s maximom
        if (max.width < result.width) {
            result.width = max.width - 20;
        }
        if (max.height < result.height) {
            result.height = max.height - 20;
        }
        // Porovnanie s obrazovkou
        Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
        if (scr.width < result.width) {
            result.width = scr.width;
        }
        if (scr.height < result.height) {
            result.height = scr.height;
        }
        window.setSize(result);
    }

    @Override
    public void modifyFocus(Component component) {
        KeyStroke backward = idActionService.getKeyStroke(
                UIService.class.getCanonicalName(), FOCUS_BACKWARD);
        KeyStroke forward = idActionService.getKeyStroke(
                UIService.class.getCanonicalName(), FOCUS_FORWARD);
        if (backward != null && forward != null) {
            Set<AWTKeyStroke> keys = new HashSet<>();
            keys.add(backward);
            component.setFocusTraversalKeys(
                    KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, keys);
            keys = new HashSet<>();
            keys.add(forward);
            component.setFocusTraversalKeys(
                    KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, keys);
        }
    }

    @Override
    public void modifyFontSize(float coefficient) {
        try {
            UIDefaults uidefs = UIManager.getLookAndFeelDefaults();
            Enumeration<Object> enum1 = uidefs.keys();
            while (enum1.hasMoreElements()) {
                Object item = enum1.nextElement();
                Object value = uidefs.get(item);
                if (value instanceof Font) {
                    Font font = (Font) value;
                    font = font.deriveFont(font.getSize() * coefficient);
                    UIManager.put(item, font);
                }
            }
            this.coefficient = coefficient;
        } catch (Exception e) {
        }
    }

    @Override
    public File openFile(int filter, String title, File defaultDir,
            R3NFileFilter[] filters, String fileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileSelectionMode(filter);
        if (title != null) {
            fileChooser.setDialogTitle(title);
        } else {
            fileChooser.setDialogTitle(ResourceBundle.getBundle(
                    UIServiceManager.class.getCanonicalName()).getString("OPEN"));
        }
        if (defaultDir != null && defaultDir.exists()) {
            fileChooser.setCurrentDirectory(defaultDir);
        } else {
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        }
        if (filters != null && filters.length > 0) {
            fileChooser.setAcceptAllFileFilterUsed(false);
            for (R3NFileFilter fl : filters) {
                fileChooser.addChoosableFileFilter(fl);
            }
        }
        if (fileName != null) {
            fileChooser.setSelectedFile(new File(fileName));
        }
        if (fileChooser.showOpenDialog(getRootFrame()) == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    @Override
    public void positionCenterScreen(Window window) {
        Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension wSize = window.getSize();
        int maxWidth = sSize.width;
        int maxHeight = sSize.height;
        // fit on window
        if (wSize.height > maxHeight) {
            wSize.height = maxHeight;
        }
        if (wSize.width > maxWidth) {
            wSize.width = maxWidth;
        }
        window.setSize(wSize);
        if (sSize.width != wSize.width && sSize.height != wSize.height) {
            int x = (sSize.width - wSize.width) / 2;
            int y = (sSize.height - wSize.height) / 2;
            window.setLocation(x, y);
        } else {
            if (window instanceof JFrame) {
                JFrame f = (JFrame) window;
                f.setExtendedState(JFrame.MAXIMIZED_BOTH);
            } else {
                window.setLocation(0, 0);
            }
        }
        window.toFront();
    }

    @Override
    public void positionCenterWindow(Window parent, Window window) {
        if (parent == null) {
            positionCenterScreen(window);
            return;
        } else {
            if (!parent.isVisible()) {
                positionCenterScreen(window);
                return;
            }
        }
        Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension wSize = window.getSize();
        if (wSize.height > sSize.height) {
            wSize.height = sSize.height;
        }
        if (wSize.width > sSize.width) {
            wSize.width = sSize.width;
        }
        window.setSize(wSize);
        Rectangle pBounds = parent.getBounds();
        int x = pBounds.x + ((pBounds.width - wSize.width) / 2);
        if (x < 0) {
            x = 0;
        }
        int y = pBounds.y + ((pBounds.height - wSize.height) / 2);
        if (y < 0) {
            y = 0;
        }
        if (x + wSize.width > sSize.width) {
            x = sSize.width - wSize.width;
        }
        if (y + wSize.height > sSize.height) {
            y = sSize.height - wSize.height;
        }
        window.setLocation(x, y);
        window.toFront();
    }

    @Override
    public File saveFile(int filter, String title, File defaultDir,
            R3NFileFilter[] filters, String fileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setFileSelectionMode(filter);
        if (title != null) {
            fileChooser.setDialogTitle(title);
        } else {
            fileChooser.setDialogTitle(ResourceBundle.getBundle(
                    UIServiceManager.class.getCanonicalName()).getString("SAVE"));
        }
        if (defaultDir != null && defaultDir.exists()) {
            fileChooser.setCurrentDirectory(defaultDir);
        } else {
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        }
        if (filters != null && filters.length > 0) {
            fileChooser.setAcceptAllFileFilterUsed(false);
            for (R3NFileFilter fl : filters) {
                fileChooser.addChoosableFileFilter(fl);
            }
        }
        if (fileName != null) {
            fileChooser.setSelectedFile(new File(fileName));
        }
        if (fileChooser.showSaveDialog(getRootFrame()) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            FileFilter fileFilter = fileChooser.getFileFilter();
            if (fileFilter instanceof R3NFileFilter) {
                String ext = ((R3NFileFilter) fileFilter).getExtension();
                if (!file.getName().toLowerCase().endsWith(ext.toLowerCase())) {
                    file = new File(file.getPath() + ext);
                }
            }
            return file;
        }
        return null;
    }

    @Override
    public void setBuzzer(Buzzer buzzer) {
        this.buzzer = buzzer;
    }

    @Override
    public void setDimension(String key, Dimension dimension) {
        if (dimension == null) {
            this.dimension.remove(key);
        } else {
            this.dimension.put(key, dimension);
        }
    }

    @Override
    public void setKeyStroke(String groupId, int actionId, InputMap map,
            Object actionKey) {
        if (actionId == FOCUS_FORWARD || actionId == FOCUS_BACKWARD) {
            return;
        }
        KeyStroke keyStroke = idActionService.getKeyStroke(groupId, actionId);
        if (keyStroke != null) {
            map.put(keyStroke, actionKey);
        }
    }

    @Override
    public void setKeyStroke(String groupId, int actionId, int condition,
            JComponent component, Action action) {
        if (actionId == FOCUS_FORWARD || actionId == FOCUS_BACKWARD) {
            return;
        }
        KeyStroke keyStroke = idActionService.getKeyStroke(groupId, actionId);
        String actionMapKey = getActionMapKey(groupId, actionId);
        if (keyStroke != null) {
            component.getInputMap(condition).put(keyStroke, actionMapKey);
            component.getActionMap().put(actionMapKey, action);
        } else {
            component.getActionMap().remove(actionMapKey);
        }
    }

    @Override
    public void setKeyStroke(String groupId, int actionId, int condition,
            JComponent component, IdActionExecutor idActionExecutor) {
        setKeyStroke(groupId, actionId, condition, component, new IdAction(
                groupId, actionId, idActionExecutor));
    }

    @Override
    public final void setMaxDimension(Dimension max) {
        if (max == null) {
            max = Toolkit.getDefaultToolkit().getScreenSize();
        }
        this.max = max;
    }

    @Override
    public void setRecount(String key, Boolean recount) {
        if (recount == null) {
            this.recount.remove(key);
        } else {
            this.recount.put(key, recount);
        }
    }

    @Override
    public final void setRootFrame(Frame frame) {
        if (frame == null) {
            frame = new Frame();
            frame.setSize(max);
            positionCenterScreen(frame);
        }
        this.frame = frame;
    }

    public void showCenterScreen(Window window) {
        positionCenterScreen(window);
        window.setVisible(true);
    }

    public void showCenterWindow(Window parent, Window window) {
        positionCenterWindow(parent, window);
        window.setVisible(true);
    }

    @Override
    public void showMessageDialog(String title, Object message, int messageType) {
        R3NOkDialog dialog = new R3NOkDialog(getRootFrame()) {
            private static final long serialVersionUID = 6772942023292876492L;
        };
        dialog.setModal(true);
        JPanel form = new JPanel(new GridBagLayout());
        if (title != null) {
            dialog.setTitle(title);
        } else {
            dialog.setTitle(idActionService.getName(
                    UIService.class.getCanonicalName(), messageType));
        }
        JLabel icon = new JLabel();
        icon.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        icon.setVerticalAlignment(JLabel.TOP);
        icon.setIcon(getDialogIcon(messageType));
        form.add(icon, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                5, 5, 5, 0), 0, 0));
        MessagePanel messagePanel = new MessagePanel();
        messagePanel.setMessage(message);
        form.add(messagePanel, new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                5, 0, 5, 5), 0, 0));
        dialog.add(form, BorderLayout.CENTER);
        dialog.pack();
        dialog.setVisible(true);
    }

    @Override
    public int showYesNoCancelDialog(String title, Object message,
            int messageType) {
        R3NYesNoCancelDialog dialog = new R3NYesNoCancelDialog(getRootFrame()) {
            private static final long serialVersionUID = 6509675348749804838L;
        };
        dialog.setModal(true);
        JPanel form = new JPanel(new GridBagLayout());
        if (title != null) {
            dialog.setTitle(title);
        } else {
            dialog.setTitle(idActionService.getName(
                    UIService.class.getCanonicalName(), messageType));
        }
        JLabel icon = new JLabel();
        icon.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        icon.setVerticalAlignment(JLabel.TOP);
        icon.setIcon(getDialogIcon(messageType));
        form.add(icon, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                5, 5, 5, 0), 0, 0));
        MessagePanel messagePanel = new MessagePanel();
        messagePanel.setMessage(message);
        form.add(messagePanel, new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                5, 0, 5, 5), 0, 0));
        dialog.add(form, BorderLayout.CENTER);
        dialog.pack();
        dialog.setVisible(true);
        return dialog.getLastAction();
    }

    @Override
    public int showYesNoDialog(String title, Object message, int messageType) {
        R3NYesNoDialog dialog = new R3NYesNoDialog(getRootFrame()) {
            private static final long serialVersionUID = 1107964695867871206L;
        };
        dialog.setModal(true);
        JPanel form = new JPanel(new GridBagLayout());
        if (title != null) {
            dialog.setTitle(title);
        } else {
            dialog.setTitle(idActionService.getName(
                    UIService.class.getCanonicalName(), messageType));
        }
        JLabel icon = new JLabel();
        icon.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        icon.setVerticalAlignment(JLabel.TOP);
        icon.setIcon(getDialogIcon(messageType));
        form.add(icon, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                5, 5, 5, 0), 0, 0));
        MessagePanel messagePanel = new MessagePanel();
        messagePanel.setMessage(message);
        form.add(messagePanel, new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                5, 0, 5, 5), 0, 0));
        dialog.add(form, BorderLayout.CENTER);
        dialog.pack();
        dialog.setVisible(true);
        return dialog.getLastAction();
    }

    @Override
    public void statusAutoProgress(long id) {
        if (statusDialogs.containsKey(id)) {
            statusDialogs.get(id).autoProgress();
        }
    }

    @Override
    public void statusFinishProgress(long id) {
        if (statusDialogs.containsKey(id)) {
            statusDialogs.get(id).finishProgress();
        }
    }

    @Override
    public void statusHide(long id) {
        if (statusDialogs.containsKey(id)) {
            statusDialogs.get(id).statusHide();
        }
        statusDialogs.remove(id);
    }

    @Override
    public void statusIncrementProgress(long id) {
        if (statusDialogs.containsKey(id)) {
            statusDialogs.get(id).incrementProgress();
        }
    }

    @Override
    public void statusSetText(long id, String text) {
        if (statusDialogs.containsKey(id) && text != null) {
            statusDialogs.get(id).setText(text);
        }
    }

    @Override
    public void statusSetTitle(long id, String title) {
        if (statusDialogs.containsKey(id) && title != null) {
            statusDialogs.get(id).setTitle(title);
        }
    }

    @Override
    public long statusShow(String title) {
        R3NStatusDialog vuiStatusDialog = new R3NStatusDialog(getRootFrame());
        if (title != null) {
            vuiStatusDialog.setTitle(title);
        }
        statusId++;
        statusDialogs.put(statusId, vuiStatusDialog);
        vuiStatusDialog.statusShow();
        return statusId;
    }

    @Override
    public void statusStartProgress(long id) {
        if (statusDialogs.containsKey(id)) {
            statusDialogs.get(id).startProgress();
        }
    }
}
