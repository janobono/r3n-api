package sk.r3n.ui.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.Icon;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.transcoder.TranscoderInput;

public class SVGIcon extends UserAgentAdapter implements Icon {

    protected BufferedImage bufferedImage;
    protected Dimension size;

    public SVGIcon(URL url, Dimension size) throws Exception {
        super();
        this.size = size;
        BufferedImageTranscoder t = new BufferedImageTranscoder();
        t.transcode(new TranscoderInput(url.toURI().toString()), size.width,
                size.height);
        bufferedImage = t.getBufferedImage();
        size.setSize(bufferedImage.getWidth(), bufferedImage.getHeight());
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    @Override
    public int getIconHeight() {
        return size.height;
    }

    @Override
    public int getIconWidth() {
        return size.width;
    }

    @Override
    public Dimension2D getViewportSize() {
        return size;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.drawImage(bufferedImage, x, y, null);
    }
}
