package sk.r3n.swing.util;

import java.awt.image.BufferedImage;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

public class BufferedImageTranscoder extends ImageTranscoder {

    protected BufferedImage bufferedImage;

    public BufferedImageTranscoder() {
        super();
    }

    @Override
    public BufferedImage createImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setDimensions(int w, int h) {
        hints.put(KEY_WIDTH, new Float(w));
        hints.put(KEY_HEIGHT, new Float(h));
    }

    public void transcode(TranscoderInput in, int w, int h) throws TranscoderException {
        if (w > 0 && h > 0) {
            setDimensions(w, h);
        }
        transcode(in, null);
    }

    @Override
    public void writeImage(BufferedImage img, TranscoderOutput output)
            throws TranscoderException {
        bufferedImage = img;
    }

}
