package Main;

import java.awt.*;

public class LibraryView {

    public static void draw(Graphics2D g2, UI ui, int w, int h) {
        
        // Layout Math
        int pad = 25;
        int gap = 20;
        int totalWidth = w - (pad * 2) - gap;
        int leftW = (int) (totalWidth * 0.60);
        int rightW = totalWidth - leftW;

        int searchH = 36;
        int searchY = pad + 10;
        int contentY = searchY + searchH + 25;
        int contentH = h - contentY - pad;

        int rightX = pad + leftW + gap;
        int playlistsH = (int) (contentH * 0.35);

        // 1. TOP HEADER / SEARCH BAR
        int searchW = (int) (w * 0.45);
        int searchX = (w - searchW) / 2;
        g2.setColor(Color.decode("#313338"));
        g2.fillRoundRect(searchX, searchY, searchW, searchH, searchH, searchH);

        if (ui.iconArtist != null) 
            g2.drawImage(ui.iconArtist, searchX + 12, searchY + (searchH - 20) / 2, 20, 20, null);
        if (ui.iconSearch != null) 
            g2.drawImage(ui.iconSearch, searchX + searchW - 32, searchY + (searchH - 20) / 2, 20, 20, null);
        if (ui.iconSettings != null) 
            g2.drawImage(ui.iconSettings, w - pad - 28, searchY + (searchH - 24) / 2, 24, 24, null);

        // 2. LIBRARY CONTAINER
        g2.setColor(Color.decode("#2B2D31"));
        g2.fillRoundRect(pad, contentY, leftW, contentH, 30, 30);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Inter", Font.BOLD, 18));
        g2.drawString("LIBRARY", pad + 60, contentY + 35);
        
        if (ui.iconLibrary != null)
            g2.drawImage(ui.iconLibrary, pad + 25, contentY + 16, 24, 24, null);

        // 3. PLAYLISTS CONTAINER
        g2.setColor(Color.decode("#2B2D31"));
        g2.fillRoundRect(rightX, contentY, rightW, playlistsH, 30, 30);

        g2.setColor(Color.WHITE);
        g2.drawString("PLAYLISTS", rightX + 60, contentY + 35);
        
        if (ui.iconAlbum != null)
            g2.drawImage(ui.iconAlbum, rightX + 25, contentY + 16, 24, 24, null);

        g2.setFont(new Font("Inter", Font.BOLD, 16));
        FontMetrics fmNone = g2.getFontMetrics();
        int noneW = fmNone.stringWidth("None");
        g2.drawString("None", rightX + (rightW - noneW) / 2, contentY + (playlistsH / 2) + 10);
    }
}