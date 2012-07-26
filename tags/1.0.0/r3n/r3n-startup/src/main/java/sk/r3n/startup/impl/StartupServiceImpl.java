package sk.r3n.startup.impl;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import org.osgi.service.component.ComponentContext;
import sk.r3n.ui.StartupService;
import sk.r3n.ui.UIService;
import sk.r3n.ui.util.SVGIcon;

public class StartupServiceImpl implements StartupService {

    private class AutoIncrementThread implements Runnable {

        @Override
        public void run() {
            while (auto) {
                if (startupFrame != null) {
                    startupFrame.incrementProgress();
                }
                try {
                    Thread.sleep(25);
                } catch (Exception e) {
                    auto = false;
                }
            }

        }
    }
    private StartupFrame startupFrame;
    protected static UIService uiService;
    private boolean auto;

    public StartupServiceImpl() {
        super();
    }

    protected void activate(ComponentContext context) {
        uiService = (UIService) context.locateService("UIService");
    }

    @Override
    public void autoIncrementProgress() {
        if (auto) {
            return;
        }
        auto = true;
        new Thread(new AutoIncrementThread(), "StartupService").start();
    }

    protected void deactivate(ComponentContext context) {
        auto = false;
        hideService();
        uiService = null;
    }

    @Override
    public void finishProgress() {
        auto = false;
        if (startupFrame != null) {
            startupFrame.progressBar.setValue(100);
        }
    }

    @Override
    public void hideService() {
        auto = false;
        if (startupFrame != null) {
            startupFrame.setVisible(false);
            startupFrame.dispose();
        }
        startupFrame = null;
        if (uiService != null) {
            uiService.setRootFrame(null);
        }
    }

    @Override
    public void incrementProgress() {
        auto = false;
        if (startupFrame != null) {
            startupFrame.incrementProgress();
        }
    }

    @Override
    public boolean isServiceVisible() {
        if (startupFrame != null) {
            return startupFrame.isVisible();
        }
        return false;
    }

    @Override
    public void setAppIcon(URL url) {
        auto = false;
        BufferedImage image = null;
        if (url != null) {
            try {
                if (url.getFile().toLowerCase().endsWith(".svg")) {
                    SVGIcon svgIcon = (SVGIcon) uiService.getIcon(url);
                    image = svgIcon.getBufferedImage();
                } else {
                    image = ImageIO.read(url);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (startupFrame == null) {
            startupFrame = new StartupFrame();
        }
        startupFrame.setIconImage(image);
    }

    @Override
    public void setAppImage(URL url, boolean stretch) {
        auto = false;
        if (startupFrame == null) {
            startupFrame = new StartupFrame();
        }
        startupFrame.imagePanel.setImage(url);
        startupFrame.imagePanel.setStretch(stretch);
    }

    @Override
    public void setAppName(String appName) {
        auto = false;
        if (startupFrame == null) {
            startupFrame = new StartupFrame();
        }
        startupFrame.setTitle(appName);
    }

    @Override
    public void setInfoText(String infoText) {
        auto = false;
        if (startupFrame == null) {
            startupFrame = new StartupFrame();
        }
        startupFrame.statusLabel.setText(infoText);
    }

    @Override
    public void setInfoTextForegroun(Color infoTextColor) {
        if (startupFrame == null) {
            startupFrame = new StartupFrame();
        }
        startupFrame.statusLabel.setForeground(infoTextColor);
    }

    @Override
    public void showService(int width, int height) {
        auto = false;
        if (startupFrame == null) {
            startupFrame = new StartupFrame();
        }
        startupFrame.setSize(width, height);
        if (uiService != null) {
            uiService.modifyDimensions(startupFrame);
        }
        startupFrame.setVisible(true);
        startupFrame.toFront();
        if (uiService != null) {
            uiService.setRootFrame(startupFrame);
        }
    }

    @Override
    public void startProgress() {
        auto = false;
        if (startupFrame != null) {
            startupFrame.progressBar.setMaximum(100);
            startupFrame.progressBar.setValue(0);
            startupFrame.asc = true;
            if (!startupFrame.isVisible()) {
                startupFrame.setVisible(true);
            }
        }
    }
}
