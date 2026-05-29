package Main;

import ControlPanel.Song;
import java.awt.*;
import java.util.ArrayList;

/**
 * Handles the rendering of the left side of the application.
 * This View class is responsible for drawing the global library, the search bar, 
 * and the responsive, scrollable list of all loaded songs. It reads state from the 
 * UI manager and data from the MusicHandler, but contains no business logic of its own.
 */
public class LibraryView {

    /**
     * The main render method called continuously by the UI repaint loop.
     * @param g2 The Graphics2D context used to draw shapes and text.
     * @param ui The main UI state manager.
     * @param w  The current width of the application window.
     * @param h  The current height of the application window.
     */
    public static void draw(Graphics2D g2, UI ui, int w, int h) {
        
        // --- RESPONSIVE LAYOUT MATH ---
        // Dynamically calculate the sizes based on the current window dimensions
        int pad = 25;
        int gap = 20;
        int totalWidth = w - (pad * 2) - gap;
        int leftW = (int) (totalWidth * 0.60); // Library takes up 60% of available space

        int searchH = 36;
        int searchY = pad + 10;
        int contentY = searchY + searchH + 25;
        int contentH = h - contentY - pad;

        // 1. TOP HEADER / SEARCH BAR
        int searchW = (int) (w * 0.45);
        int searchX = (w - searchW) / 2;
        
        // Dynamically map the invisible search bar hitbox exactly over the drawn visual
        ui.searchBarBounds.setBounds(searchX, searchY, searchW, searchH);
        
        // Draw Search Bar Background
        g2.setColor(Color.decode("#313338"));
        g2.fillRoundRect(searchX, searchY, searchW, searchH, searchH, searchH);
        g2.setFont(new Font("Inter", Font.PLAIN, 13));

        // Calculate available text area to prevent text from overlapping the icons
        int textStartX = searchX + 40;
        int textMaxX   = searchX + searchW - 36;
        int maxTextW   = textMaxX - textStartX;

        FontMetrics fm = g2.getFontMetrics();

        // Keep the screen refreshing rapidly if the user is typing so the caret blinks smoothly
        if (ui.searchBarFocused) {
            ui.panel.repaint();
        }
        
        // --- SEARCH BAR TEXT & CARET LOGIC ---
        if (ui.searchText.isEmpty()) {

            // State A: Empty and NOT focused (Show Placeholder)
            if (!ui.searchBarFocused) {
                g2.setColor(new Color(160, 160, 160));
                g2.drawString("Search songs...", textStartX, searchY + 24);
            }
            // State B: Empty but FOCUSED (Show Blinking Caret)
            else {
                long time = System.currentTimeMillis();
                // Blinks on and off every 500 milliseconds
                if ((time / 500) % 2 == 0) {
                    int caretX = textStartX;
                    int caretY = searchY + 8;
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(caretX, caretY, 2, 20, 2, 2);
                }
            }

        } else {
            
            // State C: Contains Text
            String text = ui.searchText;
            int start = 0;

            // If the text exceeds the box width, shift the starting index so the latest letters stay visible
            while (start < text.length() && fm.stringWidth(text.substring(start)) > maxTextW) { start++; }
            String visibleText = text.substring(start);

            g2.setColor(Color.WHITE);
            g2.drawString(visibleText, textStartX, searchY + 24);

            // Draw Caret at the end of the typed string if currently focused
            if (ui.searchBarFocused) {
                long time = System.currentTimeMillis();
                if ((time / 500) % 2 == 0) {
                    int caretX = textStartX + fm.stringWidth(visibleText) + 2;
                    int caretY = searchY + 8;
                    g2.fillRoundRect(caretX, caretY, 2, 20, 2, 2);
                }
            }
        }

        // Draw Search Bar Icons
        if (ui.iconArtist != null) g2.drawImage(ui.iconArtist, searchX + 12, searchY + (searchH - 20) / 2, 20, 20, null);
        if (ui.iconSearch != null) g2.drawImage(ui.iconSearch, searchX + searchW - 32, searchY + (searchH - 20) / 2, 20, 20, null);

        // 2. LIBRARY CONTAINER
        g2.setColor(Color.decode("#2B2D31"));
        g2.fillRoundRect(pad, contentY, leftW, contentH, 30, 30);

        // Map the "+ Add Folder" hitbox exactly over the drawn text coordinates
        if (ui.addFolderButton != null) ui.addFolderButton.setBounds(pad + 145, contentY + 22, 80, 18);

        // Draw Library Headers
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Inter", Font.BOLD, 18));
        g2.drawString("LIBRARY", pad + 60, contentY + 35);
        
        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Inter", Font.PLAIN, 12));
        g2.drawString("+ Add Folder", pad + 150, contentY + 34);

        if (ui.iconLibrary != null) g2.drawImage(ui.iconLibrary, pad + 25, contentY + 16, 24, 24, null);

        // 3. DRAW THE SONG LIST
        ArrayList<Song> playlist = ui.musicHandler.searchSongs(ui.searchText);
        int listStartY = contentY + 75;
        int rowHeight = 30;
        int totalContentHeight = playlist.size() * rowHeight;
        
        // Calculate scrolling bounds
        ui.libraryViewportH = contentH - 95;
        ui.maxScrollOffset = Math.max(0, totalContentHeight - ui.libraryViewportH);

        // Save the current screen drawing state before we apply our clipping box
        Shape originalClip = g2.getClip();
        
        // Map the massive hitbox for the LibraryListClicker over the entire list area.
        // It accounts for scrollOffset so the math matches the visual shift.
        if (ui.libraryListClicker != null) {
            ui.libraryListClicker.setBounds(
                pad + 25,
                listStartY - 20 - ui.scrollOffset,
                 leftW - 50,
                 totalContentHeight
            );
        }
        
        // Create a "Safe Zone" clipping mask. Anything drawn outside this box is completely invisible.
        g2.setClip(pad + 10, listStartY - 20, leftW - 20, ui.libraryViewportH + 15);

        g2.setFont(new Font("Inter", Font.PLAIN, 13));
        int maxTextWidth = leftW - 60;

        // Resolve the currently-playing song once, outside the loop, for performance
        Song nowPlaying = (!ui.musicHandler.getActiveList().isEmpty() && ui.currentSongIndex >= 0
                && ui.currentSongIndex < ui.musicHandler.getActiveList().size())
                ? ui.musicHandler.getActiveList().get(ui.currentSongIndex)
                : null;

        for (int i = 0; i < playlist.size(); i++) {
            
            // Shift the text up or down based on the user's scroll wheel
            int currentRenderY = listStartY + (i * rowHeight) - ui.scrollOffset;
            
            Song s = playlist.get(i);
            String rawDisplay = String.format("%d.  %s - %s   [%s]", (i + 1), s.getTitle(), s.getArtist(), s.getDuration());

            // Highlight the currently playing song in your custom Purple accent color
            if (s == nowPlaying) g2.setColor(Color.decode("#BB86FC"));
            else g2.setColor(Color.WHITE);

            // Render smoothly with Ping-Pong Marquee if the text is too long for the box
            ui.drawMarqueeText(g2, rawDisplay, pad + 25, currentRenderY, maxTextWidth);
        }
        
        // Remove the clipping mask so the rest of the application can draw normally
        g2.setClip(originalClip);
    }
}