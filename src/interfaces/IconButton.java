package interfaces;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class IconButton extends JPanel {

    public static final int W = 90;
    public static final int H = 56;

    private boolean hovered = false;

    public IconButton(String iconPath, String caption, Runnable action) {

        setLayout(null);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText(caption);

        JLabel iconLabel = new JLabel(ImageUtil.smoothIcon(iconPath, 28, 28), JLabel.CENTER);
        iconLabel.setBounds(0, 5, W, 30);

        JLabel textLabel = new JLabel(caption, JLabel.CENTER);
        textLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        textLabel.setForeground(Color.WHITE);
        textLabel.setBounds(0, 37, W, 15);

        add(iconLabel);
        add(textLabel);

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {

                hovered = true;
                repaint();

            }

            @Override
            public void mouseExited(MouseEvent e) {

                hovered = false;
                repaint();

            }

            @Override
            public void mouseClicked(MouseEvent e) {

                action.run();

            }

        });

    }

    public IconButton place(int x, int y) {

        setBounds(x, y, W, H);
        return this;

    }

    @Override
    protected void paintComponent(Graphics g) {

        if (hovered) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 255, 50));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.dispose();

        }

        super.paintComponent(g);

    }

}
