package ibis.imaging4j.test;

import ibis.imaging4j.Image;
import ibis.imaging4j.Imaging4j;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ImageViewer extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JFrame frame;

    private java.awt.Image image;

    private String text;

    public ImageViewer(int width, int height) {
        text = "unset";
        image = null;

        frame = new JFrame("ConversionTest");
        frame.setMinimumSize(new Dimension(width, height));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and set up the content pane.
        this.setOpaque(true); // content panes must be opaque
        this.setMinimumSize(new Dimension(width, height));
        frame.setContentPane(this);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public void dispose() {
        frame.dispose();
    }

    public void setImage(Image image, String text) throws Exception {
        this.text = text;

        this.image = Imaging4j.convertToBufferedImage(image);
        repaint();
    }

    public void setImage(BufferedImage image, String text) throws Exception {
        this.text = text;
        this.image = image;

        repaint();
    }

    public void paint(Graphics g) {
        if (image != null) {
            g.drawImage(image, 0, 0, this);
        }
        // g.drawRect(0, 0, width, height);
        g.setColor(Color.PINK);
        g.drawString(text, 20, 20);
    }

}
