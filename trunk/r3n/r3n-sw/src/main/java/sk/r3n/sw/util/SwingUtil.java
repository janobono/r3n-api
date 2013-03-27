package sk.r3n.sw.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import org.apache.batik.transcoder.TranscoderInput;
import sk.r3n.sw.component.MessagePanel;
import sk.r3n.sw.dialog.OkDialog;
import sk.r3n.sw.dialog.YesNoCancelDialog;
import sk.r3n.sw.dialog.YesNoDialog;
import sk.r3n.ui.Answer;
import sk.r3n.ui.Filter;
import sk.r3n.ui.IconType;
import sk.r3n.ui.MessageType;
import sk.r3n.ui.R3NAction;
import sk.r3n.ui.R3NFileFilter;
import sk.r3n.ui.UIActionExecutor;
import sk.r3n.ui.UIActionKey;
import sk.r3n.util.ConfigUtil;

public class SwingUtil {

    private static final String OPEN = "OPEN";

    private static final String SAVE = "SAVE";

    private static Frame frame;

    private static SwingUtilConfig config;

    public static SwingUtilConfig getConfig() {
        if (config == null) {
            config = new SwingUtilConfig();
        }
        return config;
    }

    public static void setConfig(SwingUtilConfig config) {
        SwingUtil.config = config;
    }

    public static Icon getIcon(UIActionKey actionKey, IconType iconType) {
        Icon result = null;
        URL url = getConfig().getURL(actionKey, iconType);
        if (url != null) {
            result = getIcon(url);
        }
        return result;
    }

    public static Icon getIcon(URL url) {
        return getIcon(url, getConfig().getDefaultIconDimension());
    }

    public static Icon getIcon(URL url, Dimension dimension) {
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

    public static Image getImage(URL url) {
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

    public static Frame getRootFrame() {
        if (frame == null) {
            frame = new Frame();
            frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
            positionCenterScreen(frame);
        }
        return frame;
    }

    public static void setRootFrame(Frame frame) {
        SwingUtil.frame = frame;
    }

    public static Frame getFrameForComponent(Component component) {
        if (component == null) {
            return getRootFrame();
        }
        if (component instanceof Frame) {
            return (Frame) component;
        }
        return getFrameForComponent(component.getParent());
    }

    public static Window getWindowForComponent(Component component) {
        if (component == null) {
            return getRootFrame();
        }
        if (component instanceof Frame || component instanceof Dialog) {
            return (Window) component;
        }
        return getWindowForComponent(component.getParent());
    }

    public static void positionCenterScreen(Window window) {
        Dimension maxDimension = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension wSize = window.getSize();
        int maxWidth = maxDimension.width;
        int maxHeight = maxDimension.height;
        // fit on window
        if (wSize.height > maxHeight) {
            wSize.height = maxHeight;
        }
        if (wSize.width > maxWidth) {
            wSize.width = maxWidth;
        }
        window.setSize(wSize);
        if (maxDimension.width != wSize.width && maxDimension.height != wSize.height) {
            int x = (maxDimension.width - wSize.width) / 2;
            int y = (maxDimension.height - wSize.height) / 2;
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

    public static void positionCenterWindow(Window parent, Window window) {
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

    public static void modifyDimensions(Window window) {
        if (window instanceof JFrame) {
            if (((JFrame) window).getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                return;
            }
        }
        Dimension result = window.getSize();
        Dimension maxDimension = Toolkit.getDefaultToolkit().getScreenSize();
        if (maxDimension.width < result.width) {
            result.width = maxDimension.width;
        }
        if (maxDimension.height < result.height) {
            result.height = maxDimension.height;
        }
        window.setSize(result);
    }

    public static void showCenterScreen(Window window) {
        positionCenterScreen(window);
        window.setVisible(true);
    }

    public static void showCenterWindow(Window parent, Window window) {
        positionCenterWindow(parent, window);
        window.setVisible(true);
    }

    public static void setKeyStroke(JComponent component, int condition, KeyStroke keyStroke, UIAction action) {
        String key = ConfigUtil.createKey(action.actionKey.group(), action.actionKey.code());
        if (keyStroke == null) {
            keyStroke = getConfig().getKeyStroke(action.actionKey);
        }
        if (keyStroke != null) {
            component.getInputMap(condition).put(keyStroke, key);
            component.getActionMap().put(key, action);
        }
    }

    public static void setKeyStroke(JComponent component, int condition, UIActionKey actionKey,
            UIActionExecutor actionExecutor) {
        setKeyStroke(component, condition, null, new UIAction(actionKey, actionExecutor));
    }

    public static void setKeyStroke(JComponent component, int condition, KeyStroke keyStroke, UIActionKey actionKey,
            UIActionExecutor actionExecutor) {
        setKeyStroke(component, condition, keyStroke, new UIAction(actionKey, actionExecutor));
    }

    public static void removeKeyStroke(JComponent component, UIActionKey actionKey) {
        String actionMapKey = ConfigUtil.createKey(actionKey.group(), actionKey.code());
        component.getActionMap().remove(actionMapKey);
    }

    public static File openFile(Filter filter, String title, File defaultDir, R3NFileFilter[] filters, String fileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileSelectionMode(filter.code());
        if (title != null) {
            fileChooser.setDialogTitle(title);
        } else {
            fileChooser.setDialogTitle(ResourceBundle.getBundle(SwingUtil.class.getCanonicalName()).getString(OPEN));
        }
        if (defaultDir != null && defaultDir.exists()) {
            fileChooser.setCurrentDirectory(defaultDir);
        } else {
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        }
        if (filters != null && filters.length > 0) {
            fileChooser.setAcceptAllFileFilterUsed(false);
            for (R3NFileFilter fl : filters) {
                fileChooser.addChoosableFileFilter(new SwingUtilFileFilter(fl));
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

    public static File saveFile(Filter filter, String title, File defaultDir, R3NFileFilter[] filters, String fileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setFileSelectionMode(filter.code());
        if (title != null) {
            fileChooser.setDialogTitle(title);
        } else {
            fileChooser.setDialogTitle(ResourceBundle.getBundle(SwingUtil.class.getCanonicalName()).getString(SAVE));
        }
        if (defaultDir != null && defaultDir.exists()) {
            fileChooser.setCurrentDirectory(defaultDir);
        } else {
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        }
        if (filters != null && filters.length > 0) {
            fileChooser.setAcceptAllFileFilterUsed(false);
            for (R3NFileFilter fl : filters) {
                fileChooser.addChoosableFileFilter(new SwingUtilFileFilter(fl));
            }
        }
        if (fileName != null) {
            fileChooser.setSelectedFile(new File(fileName));
        }
        if (fileChooser.showSaveDialog(getRootFrame()) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            FileFilter fileFilter = fileChooser.getFileFilter();
            if (fileFilter instanceof SwingUtilFileFilter) {
                String ext = ((SwingUtilFileFilter) fileFilter).getFileFilter().getExtension();
                if (ext != null && !file.getName().toLowerCase().endsWith(ext.toLowerCase())) {
                    file = new File(file.getPath() + ext);
                }
            }
            return file;
        }
        return null;
    }

    public static void showMessageDialog(String title, Object message, MessageType messageType) {
        OkDialog dialog = new OkDialog(getRootFrame()) {
            @Override
            public boolean isInputValid() {
                return true;
            }
        };
        dialog.setModal(true);
        JPanel form = new JPanel(new GridBagLayout());
        if (title != null) {
            dialog.setTitle(title);
        } else {
            dialog.setTitle(messageType.value());
        }
        JLabel icon = new JLabel();
        icon.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        icon.setVerticalAlignment(JLabel.TOP);
        icon.setIcon(getMessageIcon(messageType));
        form.add(icon, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 0), 0, 0));
        MessagePanel messagePanel = new MessagePanel();
        messagePanel.setMessage(message);
        form.add(messagePanel, new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 5, 5), 0, 0));
        dialog.add(form, BorderLayout.CENTER);
        dialog.pack();
        dialog.setVisible(true);
    }

    public static Answer showYesNoCancelDialog(String title, Object message, MessageType messageType) {
        YesNoCancelDialog dialog = new YesNoCancelDialog(getRootFrame()) {
            @Override
            public boolean isInputValid() {
                return true;
            }
        };
        dialog.setModal(true);
        JPanel form = new JPanel(new GridBagLayout());
        if (title != null) {
            dialog.setTitle(title);
        } else {
            dialog.setTitle(messageType.value());
        }
        JLabel icon = new JLabel();
        icon.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        icon.setVerticalAlignment(JLabel.TOP);
        icon.setIcon(getMessageIcon(messageType));
        form.add(icon, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 0), 0, 0));
        MessagePanel messagePanel = new MessagePanel();
        messagePanel.setMessage(message);
        form.add(messagePanel, new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 5, 5), 0, 0));
        dialog.add(form, BorderLayout.CENTER);
        dialog.pack();
        dialog.setVisible(true);
        Answer result;
        switch ((R3NAction) dialog.getLastActionKey()) {
            case YES:
                result = Answer.YES;
                break;
            case NO:
                result = Answer.NO;
                break;
            default:
                result = Answer.CANCEL;
                break;
        }
        return result;
    }

    public static Answer showYesNoDialog(String title, Object message, MessageType messageType) {
        YesNoDialog dialog = new YesNoDialog(getRootFrame()) {
            @Override
            public boolean isInputValid() {
                return true;
            }
        };
        dialog.setModal(true);
        JPanel form = new JPanel(new GridBagLayout());
        if (title != null) {
            dialog.setTitle(title);
        } else {
            dialog.setTitle(messageType.value());
        }
        JLabel icon = new JLabel();
        icon.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        icon.setVerticalAlignment(JLabel.TOP);
        icon.setIcon(getMessageIcon(messageType));
        form.add(icon, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 0), 0, 0));
        MessagePanel messagePanel = new MessagePanel();
        messagePanel.setMessage(message);
        form.add(messagePanel, new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 5, 5), 0, 0));
        dialog.add(form, BorderLayout.CENTER);
        dialog.pack();
        dialog.setVisible(true);
        Answer result;
        switch ((R3NAction) dialog.getLastActionKey()) {
            case YES:
                result = Answer.YES;
                break;
            default:
                result = Answer.NO;
                break;
        }
        return result;
    }

    private static Icon getMessageIcon(MessageType messageType) {
        Icon icon = null;
        URL url = getConfig().getMessageIcon(messageType);
        if (url != null) {
            icon = getIcon(url);
        }
        return icon;
    }
}
