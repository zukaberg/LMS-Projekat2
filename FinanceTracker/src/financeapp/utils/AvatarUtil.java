package financeapp.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class AvatarUtil {

    public static ImageIcon createAvatarIcon(String username, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        // Anti-aliasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Nacrtaj krug sa bojom
        g2.setColor(new Color(52, 152, 219));
        g2.fill(new Ellipse2D.Double(0, 0, size, size));

        // Izvuci inicijale
        String initials = getInitials(username);

        // Nacrtaj inicijale
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, size / 2));
        FontMetrics fm = g2.getFontMetrics();
        int x = (size - fm.stringWidth(initials)) / 2;
        int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(initials, x, y);

        g2.dispose();

        return new ImageIcon(image);
    }

    private static String getInitials(String username) {
        if (username == null || username.isEmpty()) {
            return "?";
        }

        String[] parts = username.split(" ");

        if (parts.length >= 2) {
            return (parts[0].charAt(0) + "" + parts[1].charAt(0)).toUpperCase();
        } else {
            return username.substring(0, Math.min(2, username.length())).toUpperCase();
        }
    }

    public static ImageIcon createAvatarFromImage(String imagePath, int size) {
        try {
            ImageIcon icon = new ImageIcon(imagePath);
            Image img = icon.getImage();

            BufferedImage circularImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = circularImage.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Clip u krug
            g2.setClip(new Ellipse2D.Double(0, 0, size, size));
            g2.drawImage(img, 0, 0, size, size, null);
            g2.dispose();

            return new ImageIcon(circularImage);

        } catch (Exception e) {
            return null;
        }
    }
}
