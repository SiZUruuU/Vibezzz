package Main;

import ControlPanel.Song;
import java.awt.*;
import java.util.ArrayList;

public class LibraryView {

    public static void draw(Graphics2D g2, UI ui, int w, int h) {
        
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

        if (ui.iconArtist != null) g2.drawImage(ui.iconArtist, searchX + 12, searchY + (searchH - 20) / 2, 20, 20, null);
        if (ui.iconSearch != null) g2.drawImage(ui.iconSearch, searchX + searchW - 32, searchY + (searchH - 20) / 2, 20, 20, null);

        // 2. LIBRARY CONTAINER
        g2.setColor(Color.decode("#2B2D31"));
        g2.fillRoundRect(pad, contentY, leftW, contentH, 30, 30);

        // Map the Add Folder Hitbox to the top left text area
        if (ui.addFolderButton != null) ui.addFolderButton.setBounds(pad + 150, contentY + 23, 70, 15);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Inter", Font.BOLD, 18));
        g2.drawString("LIBRARY", pad + 60, contentY + 35);
        
        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Inter", Font.PLAIN, 12));
        g2.drawString("+ Add Folder", pad + 150, contentY + 34);

        if (ui.iconLibrary != null) g2.drawImage(ui.iconLibrary, pad + 25, contentY + 16, 24, 24, null);

        // --- NEW: DRAW THE SONG LIST ---
        ArrayList<Song> playlist = ui.musicHandler.getPlaylist();
        int listStartY = contentY + 75;
        int rowHeight = 30;
        int totalContentHeight = playlist.size() * rowHeight;
        ui.libraryViewportH = contentH - 95;
        ui.maxScrollOffset = Math.max(0, totalContentHeight - ui.libraryViewportH);

        Shape originalClip = g2.getClip();
        
        // Map the massive hitbox for the clicker over the entire list area
        if (ui.libraryListClicker != null) {
            ui.libraryListClicker.setBounds(
                pad + 25, 
                listStartY - 20 - ui.scrollOffset,
                 leftW - 50, 
                 totalContentHeight
            );
        }
        g2.setClip(pad + 10, listStartY - 20, leftW - 20, ui.libraryViewportH + 15);

        g2.setFont(new Font("Inter", Font.PLAIN, 13));
        int maxTextWidth = leftW - 60;

        for (int i = 0; i < playlist.size(); i++) {
            // Stop drawing if we reach the bottom of the container (No scrolling yet)
           int currentRenderY = listStartY + (i * rowHeight) - ui.scrollOffset;
            
            Song s = playlist.get(i);
            String rawDisplay = String.format("%d.  %s - %s   [%s]", (i + 1), s.getTitle(), s.getArtist(), s.getDuration());
            String safeDisplay = UI.getClampedText(g2, rawDisplay, maxTextWidth);

            // Highlight the currently playing song in Spotify Green
            if (ui.currentSongIndex == i) g2.setColor(Color.decode("#1DB954"));
            else g2.setColor(Color.WHITE);

            g2.drawString(safeDisplay, pad + 25, currentRenderY);
        }
        g2.setClip(originalClip);

        // 3. PLAYLISTS CONTAINER (Unchanged)
        g2.setColor(Color.decode("#2B2D31"));
        g2.fillRoundRect(rightX, contentY, rightW, playlistsH, 30, 30);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Inter", Font.BOLD, 18));
        g2.drawString("PLAYLISTS", rightX + 60, contentY + 35);
        if (ui.iconAlbum != null) g2.drawImage(ui.iconAlbum, rightX + 25, contentY + 16, 24, 24, null);
        g2.setFont(new Font("Inter", Font.BOLD, 16));
        g2.drawString("None", rightX + (rightW - g2.getFontMetrics().stringWidth("None")) / 2, contentY + (playlistsH / 2) + 10);
        
        // 4. SETTINGS 

        int setX = w - pad - 28;
        int setY = searchY + (searchH - 24) / 2;
        int setW = 24;
        int setH = 24;

        // 2. Inject these physical coordinates into your smart button's hitbox
        if (ui.iconSettings != null) {
            // Pro-Tip: We subtract 4 from X/Y and add 8 to W/H to give it a slightly 
            // larger invisible click box, making it much easier for a user to hit!
            ui.settings.setBounds(setX - 4, setY - 4, setW + 8, setH + 8);
        }

        // 3. Draw the graphic using those exact same variables
        if (ui.iconSettings != null) {
            g2.drawImage(ui.iconSettings, setX, setY, setW, setH, null);
            }
    }
}