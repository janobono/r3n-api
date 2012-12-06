package sk.r3n.swing.util;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import org.apache.batik.transcoder.TranscoderInput;

public class R3NSwingUtil {

    private static Frame frame;

    private static Dimension max;

    private static float coefficient = 1.0f;

    private static Dimension defaultDimension;

    private static Map<String, Dimension> dimensionMap = new HashMap<>();

    private static Map<String, Boolean> recountMap = new HashMap<>();

    public static Dimension getDefaultDimension() {
        if (defaultDimension == null) {
            defaultDimension = new Dimension(20, 20);
        }
        return defaultDimension;
    }

    public static Icon getIcon(URL url) {
        return getIcon(url, getDefaultDimension());
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

    public static Dimension getMaxDimension() {
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

    public static void setMaxDimension(Dimension max) {
        R3NSwingUtil.max = max;
    }

    public static Frame getRootFrame() {
        if (frame == null) {
            frame = new Frame();
            frame.setSize(getMaxDimension());
            positionCenterScreen(frame);
        }
        return frame;
    }

    public final void setRootFrame(Frame frame) {
        R3NSwingUtil.frame = frame;
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

    public static void modifyFontSize(float coefficient) {
        if (coefficient <= 0) {
            throw new IllegalArgumentException();
        }
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
            R3NSwingUtil.coefficient = coefficient;
        } catch (Exception e) {
        }
    }

    public static void modifyDimensions(Window window) {
        if (window instanceof JFrame) {
            if (((JFrame) window).getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                return;
            }
        }
        Dimension result = window.getSize();

        Dimension dim = dimensionMap.get(window.getClass().getCanonicalName());
        if (dim != null) {
            if (result.width < dim.width) {
                result.width = dim.width;
            }
            if (result.height < dim.height) {
                result.height = dim.height;
            }
        }

        Window owner = window.getOwner();
        Boolean rec = recountMap.get(window.getClass().getCanonicalName());
        if (owner != null && rec != null && rec) {
            dim = owner.getSize();
            if (result.width < dim.width) {
                result.width = dim.width - 20;
            }
            if (result.height < dim.height) {
                result.height = dim.height - 20;
            }
        }

        if (!(UIManager.getLookAndFeel().getClass().getCanonicalName().endsWith("NimbusLookAndFeel"))) {
            result.setSize(coefficient * result.width, coefficient * result.height);
        }

        Dimension maxDimension = getMaxDimension();
        if (maxDimension.width < result.width) {
            result.width = maxDimension.width - 20;
        }
        if (maxDimension.height < result.height) {
            result.height = maxDimension.height - 20;
        }

        Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
        if (scr.width < result.width) {
            result.width = scr.width;
        }
        if (scr.height < result.height) {
            result.height = scr.height;
        }
        window.setSize(result);
    }

    public static void setDimension(String key, Dimension dimension) {
        if (dimension == null) {
            dimensionMap.remove(key);
        } else {
            dimensionMap.put(key, dimension);
        }
    }

    public static void setRecount(String key, Boolean recount) {
        if (recount == null) {
            recountMap.remove(key);
        } else {
            recountMap.put(key, recount);
        }
    }

    public static void showCenterScreen(Window window) {
        positionCenterScreen(window);
        window.setVisible(true);
    }

    public static void showCenterWindow(Window parent, Window window) {
        positionCenterWindow(parent, window);
        window.setVisible(true);
    }

}
