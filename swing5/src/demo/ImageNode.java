package demo;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ImageIcon;
import twaver.Follower;
import twaver.ResizableNode;
import twaver.TWaverConst;
import twaver.TWaverUtil;

public class ImageNode extends ResizableNode {

    private Follower shadowNode = new Follower();
    private int shadowGap = 1;

    public ImageNode(String imageURL) {
        super();
        init();
        this.setImage(imageURL);
    }

    public ImageNode(Object id, String imageURL) {
        super(id);
        init();
        this.setImage(imageURL);
    }

    public Follower getShadowNode() {
        return shadowNode;
    }

    private void init() {
        this.putBorderVisible(false);
        shadowNode.putBorderVisible(false);
        shadowNode.setHost(this);
        addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (TWaverUtil.getPropertyName(evt).equalsIgnoreCase(TWaverConst.PROPERTYNAME_IMAGE)) {
                    updateImage();
                }
                if (TWaverUtil.getPropertyName(evt).equalsIgnoreCase(TWaverConst.PROPERTYNAME_SIZE)) {
                    updateSize();
                }
            }
        });
    }

    private void updateImage() {
        String shadowImageURL = this.getImageURL() + ".shadow";
        ImageIcon shadowImage = createShadowImage(TWaverUtil.getImage(this.getImageURL()), 3);
        TWaverUtil.registerImageIcon(shadowImageURL, shadowImage);
        shadowNode.setImage(shadowImageURL);
        updateSize();
    }

    private void updateSize() {
        shadowNode.setSize(getSize().width, (int) (getSize().height * 1));
        shadowNode.setLocation(this.getLocation().x, this.getLocation().y + this.getHeight() + shadowGap);
    }

    private static int convertPixel(int x, int y, int w, int h, int pixel, int fadeSpeed) {
        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        double percent = (double) (y * 100) / h / 100d;
        for (int i = 0; i < fadeSpeed; i++) {
            percent = percent * percent;
        }
        alpha = (int) (alpha * percent);
        alpha = Math.min(alpha, 150);
        int gray = (red + green + blue) / 3;
        return ((alpha << 24) + (gray << 16) + (gray << 8) + gray);
    }

    public static ImageIcon createShadowImage(Image image, int fadeSpeed) {
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        int[] pixels = new int[w * h];
        PixelGrabber pg = new PixelGrabber(image, 0, 0, w, h, pixels, 0, w);
        try {
            pg.grabPixels();
        } catch (Exception e) {
            return null;
        }
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                pixels[j * w + i] = convertPixel(i, j, w, h, pixels[j * w + i], fadeSpeed);
            }
        }
        int[] newPixels = new int[w * h];
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                newPixels[j * w + i] = pixels[(h - j - 1) * w + i];
            }
        }

        MemoryImageSource source = new MemoryImageSource(w, h, newPixels, 0, w);
        return new ImageIcon(Toolkit.getDefaultToolkit().createImage(source));
    }
}
