package sk.r3n.sw.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;
import sk.r3n.sw.component.ImagePanel;
import sk.r3n.ui.LongTermJobListener;
import sk.r3n.ui.UIActionKey;

public class StartupFrame extends R3NFrame implements LongTermJobListener {

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
        imagePanel.add(statusLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.SOUTH,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        add(imagePanel, BorderLayout.CENTER);
        progressBar = new JProgressBar();
        progressBar.setPreferredSize(textField.getPreferredSize());
        add(progressBar, BorderLayout.SOUTH);
        ((JComponent) getContentPane()).setBorder(BorderFactory.createRaisedBevelBorder());
    }

    @Override
    public void execute(UIActionKey actionKey, Object source) {
    }

    public void setAppIcon(URL url) {
        BufferedImage image = null;
        if (url != null) {
            try {
                image = ImageIO.read(url);
            } catch (IOException e) {
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

    public void setInfoTextForegroun(Color color) {
        statusLabel.setForeground(color);
    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
    }

    public void jobStarted() {
        asc = true;
        progressBar.setValue(0);
        progressBar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        setVisible(true);
    }

    public void jobStarted(String title) {
        setTitle(title);
        jobStarted();
    }

    @Override
    public void jobInProgress() {
        if (asc) {
            if (progressBar.getValue() < 100) {
                progressBar.setValue(progressBar.getValue() + 1);
            } else {
                asc = false;
                progressBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                jobInProgress();
            }
        } else {
            if (progressBar.getValue() > 0) {
                progressBar.setValue(progressBar.getValue() - 1);
            } else {
                asc = true;
                progressBar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                jobInProgress();
            }
        }
    }

    @Override
    public void jobInProgress(int value) {
        for (int i = 0; i < value; i++) {
            jobInProgress();
        }
    }

    @Override
    public void jobInProgress(String message) {
        setInfoText(message);
    }

    @Override
    public void jobInProgress(String message, int value) {
        jobInProgress(message);
        jobInProgress(value);
    }

    public void jobFinished() {
        progressBar.setValue(100);
        dispose();
    }

}
