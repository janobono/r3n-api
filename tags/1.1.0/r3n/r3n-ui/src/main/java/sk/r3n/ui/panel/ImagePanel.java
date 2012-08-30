package sk.r3n.ui.panel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import org.apache.batik.transcoder.TranscoderInput;
import sk.r3n.ui.util.BufferedImageTranscoder;

public class ImagePanel extends JPanel {

    private URL url;
    private boolean stretch;
    private Dimension imageSize;

    public ImagePanel() {
        this(null, false);
    }

    public ImagePanel(URL url, boolean stretch) {
        super(new GridBagLayout());
        this.url = url;
        this.stretch = stretch;
    }

    public void setImage(URL url) {
        this.url = url;
    }

    public void add(JComponent component, Object constraints) {
        component.setOpaque(false);
        if (component instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) component;
            JViewport viewport = scrollPane.getViewport();
            viewport.setOpaque(false);
            Component c = viewport.getView();
            if (c instanceof JComponent) {
                ((JComponent) c).setOpaque(false);
            }
        }
        super.add(component, constraints);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (url != null) {
            BufferedImage image = createImage();
            if (image != null) {
                if (!stretch) {
                    g.drawImage(image, 0, 0, this);
                } else {
                    Dimension size = getSize();
                    g.drawImage(image, 0, 0, size.width, size.height, this);
                }
            }
        }
    }

    public void setStretch(boolean stretch) {
        this.stretch = stretch;
    }

    public void setImageSize(Dimension imageSize) {
        this.imageSize = imageSize;
    }

    private BufferedImage createImage() {
        if (url.getFile().toLowerCase().endsWith(".svg")) {
            try {
                BufferedImageTranscoder t = new BufferedImageTranscoder();
                if (imageSize == null) {
                    imageSize = getSize();
                }
                t.transcode(new TranscoderInput(url.toURI().toString()),
                        imageSize.width, imageSize.height);
                BufferedImage bufferedImage = t.getBufferedImage();
                imageSize.setSize(bufferedImage.getWidth(),
                        bufferedImage.getHeight());
                return bufferedImage;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                BufferedImage bufferedImage = ImageIO.read(url);
                return bufferedImage;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void sizeToImage() {
        if (url != null) {
            BufferedImage image = createImage();
            Dimension dimension = new Dimension(image.getWidth(),
                    image.getHeight());
            setMinimumSize(dimension);
            setMaximumSize(dimension);
            setPreferredSize(dimension);
        }
    }
}