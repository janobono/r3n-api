package sk.r3n.startup.impl;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;
import sk.r3n.ui.panel.ImagePanel;
import sk.r3n.ui.util.R3NFrame;

public class StartupFrame extends R3NFrame {

    private static final long serialVersionUID = -5523880037039128267L;
    protected ImagePanel imagePanel;
    protected JProgressBar progressBar;
    protected JLabel statusLabel;
    protected boolean asc;

    public StartupFrame() {
        super();
        setUndecorated(true);

        JTextField textField = new JTextField();
        textField.setColumns(25);

        imagePanel = new ImagePanel();
        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setOpaque(true);
        statusLabel.setPreferredSize(textField.getPreferredSize());
        imagePanel.add(statusLabel, new GridBagConstraints(0, 0, 1, 1, 1.0,
                1.0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        add(imagePanel, BorderLayout.CENTER);
        progressBar = new JProgressBar();
        progressBar.setPreferredSize(textField.getPreferredSize());
        add(progressBar, BorderLayout.SOUTH);
        ((JComponent) getContentPane()).setBorder(BorderFactory.createRaisedBevelBorder());
    }

    @Override
    public void execute(String groupId, int actionId, Object source) {
    }

    public void finishProgress() {
        progressBar.setValue(100);
    }

    public void hideService() {
        setVisible(false);
    }

    public void incrementProgress() {
        if (asc) {
            if (progressBar.getValue() < 100) {
                progressBar.setValue(progressBar.getValue() + 1);
            } else {
                asc = false;
                progressBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                incrementProgress();
            }
        } else {
            if (progressBar.getValue() > 0) {
                progressBar.setValue(progressBar.getValue() - 1);
            } else {
                asc = true;
                progressBar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                incrementProgress();
            }
        }
    }

    public boolean isServiceVisible() {
        return isVisible();
    }

    @Override
    public void refreshUI() {
    }

    public void setAppIcon(String url) {
        BufferedImage image = null;
        if (url != null) {
            try {
                image = ImageIO.read(new URL(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setIconImage(image);
    }

    public void setAppImage(URL url, boolean stretch) {
        imagePanel.setImage(url);
        imagePanel.setStretch(stretch);
    }

    public void setAppName(String appName) {
        setTitle(appName);
    }

    public void setInfoText(String infoText) {
        statusLabel.setText(infoText);
    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
    }
}
