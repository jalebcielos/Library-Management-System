package interfaces;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ImageUtil {

    public static ImageIcon scale(String path, int maxW, int maxH) {

        if (path == null) {

            return null;

        }

        try {

            BufferedImage original = ImageIO.read(new File(path));

            if (original == null) {

                return null;

            }

            double ratio = Math.min((double) maxW / original.getWidth(), (double) maxH / original.getHeight());
            int w = Math.max(1, (int) (original.getWidth() * ratio));
            int h = Math.max(1, (int) (original.getHeight() * ratio));

            BufferedImage current = original;
            int cw = original.getWidth();
            int ch = original.getHeight();

            while (cw / 2 >= w && ch / 2 >= h) {

                cw /= 2;
                ch /= 2;
                current = draw(current, cw, ch);

            }

            return new ImageIcon(draw(current, w, h));

        } catch (IOException e) {

            return null;

        }

    }

    public static Icon smoothIcon(String path, int maxW, int maxH) {

        if (path == null) {

            return null;

        }

        try {

            BufferedImage original = ImageIO.read(new File(path));

            if (original == null) {

                return null;

            }

            double ratio = Math.min((double) maxW / original.getWidth(), (double) maxH / original.getHeight());
            final int w = Math.max(1, (int) Math.round(original.getWidth() * ratio));
            final int h = Math.max(1, (int) Math.round(original.getHeight() * ratio));

            int hw = w * 2;
            int hh = h * 2;
            BufferedImage hi = original;

            while (hi.getWidth() / 2 >= hw && hi.getHeight() / 2 >= hh) {

                hi = draw(hi, hi.getWidth() / 2, hi.getHeight() / 2);

            }

            if (hi.getWidth() > hw && hi.getHeight() > hh) {

                hi = draw(hi, hw, hh);

            }

            final BufferedImage img = hi;

            return new Icon() {

                @Override
                public int getIconWidth() {

                    return w;

                }

                @Override
                public int getIconHeight() {

                    return h;

                }

                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {

                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.drawImage(img, x, y, w, h, null);
                    g2.dispose();

                }

            };

        } catch (IOException e) {

            return null;

        }

    }

    private static BufferedImage draw(BufferedImage src, int w, int h) {

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(src, 0, 0, w, h, null);
        g2.dispose();
        return out;

    }

}
