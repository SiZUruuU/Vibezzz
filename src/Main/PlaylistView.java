package Main;

import java.awt.*;

/**
 * Handles the rendering of the top-right section of the application.
 * This View class manages a dual-state UI: it either renders the main menu 
 * of all custom playlists, or it renders the specific songs inside a selected playlist.
 * It dynamically enables/disables hitboxes based on the active state to prevent ghost clicks.
 */
public class PlaylistView {

    /**
     * The main render method called continuously by the UI repaint loop.
     * @param g2 The Graphics2D context used to draw shapes and text.
     * @param ui The main UI state manager.
     * @param w  The current width of the application window.
     * @param h  The current height of the application window.
     */
    public static void draw(Graphics2D g2, UI ui, int w, int h) {
        
        // --- RESPONSIVE LAYOUT MATH ---
        int pad = 25;
        int gap = 20;
        int totalWidth = w - (pad * 2) - gap;
        int leftW = (int) (totalWidth * 0.60);
        int rightW = totalWidth - leftW; // Playlists take up 40% of the available width

        int searchH = 36;
        int contentY = pad + 10 + searchH + 25;
        int contentH = h - contentY - pad;

        int rightX = pad + leftW + gap;
        int playlistsH = (int) (contentH * 0.35); // Playlists get the top 35% of the right column

        // --- BACKGROUND CONTAINER ---
        g2.setColor(Color.decode("#2B2D31"));
        g2.fillRoundRect(rightX, contentY, rightW, playlistsH, 30, 30);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Inter", Font.BOLD, 18));

        // =================================================================
        // STATE 1: VIEWING THE CONTENTS OF A SPECIFIC PLAYLIST
        // =================================================================
        if (ui.insidePlaylistView) {
            
            // 1. Ghost Click Prevention: Throw the "Create Playlist" hitbox off-screen
            if (ui.addPlaylistButton != null) ui.addPlaylistButton.setBounds(-100, -100, 0, 0);

            // 2. Draw Header (Playlist Name and Icon)
            g2.drawString(ui.selectedPlaylistName.toUpperCase(), rightX + 60, contentY + 35);
            if (ui.iconAlbum != null) g2.drawImage(ui.iconAlbum, rightX + 25, contentY + 16, 24, 24, null);

            // 3. Draw "< Back" Button
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("Inter", Font.BOLD, 12));
            g2.drawString("< Back", rightX + 25, contentY + playlistsH - 20);
            if (ui.backButton != null) ui.backButton.setBounds(rightX + 20, contentY + playlistsH - 35, 60, 20);

            // 4. Draw "+ Add Song" Button (Toggles text/color when active)
            g2.setColor(ui.isAddingToPlaylist ? Color.decode("#BB86FC") : Color.WHITE);
            String addText = ui.isAddingToPlaylist ? "Select from Library..." : "+ Add Song";
            int textW = g2.getFontMetrics().stringWidth(addText);
            
            g2.drawString(addText, rightX + rightW - textW - 25, contentY + playlistsH - 20);
            if (ui.addSongButton != null) ui.addSongButton.setBounds(rightX + rightW - textW - 30, contentY + playlistsH - 35, textW + 10, 20);

            // 5. SCROLLING MATH FOR SONGS
            int songCount = ui.musicHandler.getPlaylistSongs().get(ui.selectedPlaylistName) != null ? ui.musicHandler.getPlaylistSongs().get(ui.selectedPlaylistName).size() : 0;
            int rowHeight = 25;
            
            // Leave space at the bottom for the control buttons so they don't get drawn over
            ui.playlistViewportH = playlistsH - 95; 
            ui.maxPlaylistScrollOffset = Math.max(0, (songCount * rowHeight) - ui.playlistViewportH);

            // 6. CREATE SAFE ZONE (Clipping Box)
            Shape originalClip = g2.getClip();
            g2.clipRect(rightX, contentY + 50, rightW, ui.playlistViewportH);

            int maxTextW = rightW - 50;
            int songIdx = 0;
            
            // 7. Render Songs and Map Hitboxes
            for (ControlPanel.ButtonManager btn : ui.getBackendButtons()) {
                if (btn instanceof ControlPanel.SongClicker) {
                    
                    ControlPanel.Song s = ((ControlPanel.SongClicker) btn).getSong();
                    
                    // Apply the user's scroll wheel offset to the Y position
                    int yPos = contentY + 70 + (songIdx * rowHeight) - ui.playlistScrollOffset;

                    // Hitbox Culling: Only set physical hitboxes for songs currently visible inside the box
                    if (yPos > contentY + 30 && yPos < contentY + playlistsH - 40) {
                        btn.setBounds(rightX + 20, yPos - 15, maxTextW, 20);
                    } else {
                        btn.setBounds(-100, -100, 0, 0); // Disable hitbox if scrolled out of view
                    }

                    // Highlight logic: Check if this specific song is the one actively playing
                    if (!ui.musicHandler.getActiveList().isEmpty() && ui.currentSongIndex >= 0 && ui.currentSongIndex < ui.musicHandler.getActiveList().size()) {
                        if (s == ui.musicHandler.getActiveList().get(ui.currentSongIndex)) g2.setColor(Color.decode("#BB86FC"));
                        else g2.setColor(Color.WHITE);
                    } else g2.setColor(Color.WHITE);

                    // Combine Numbering and Title, then pass to the marquee text renderer
                    String numberedDisplay = String.format("%d.  %s", (songIdx + 1), s.getTitle());
                    ui.drawMarqueeText(g2, numberedDisplay, rightX + 25, yPos, maxTextW);
                    
                    songIdx++;
                }
            }
            g2.setClip(originalClip); // Remove Safe Zone
        } 
        
        // =================================================================
        // STATE 2: MAIN PLAYLIST MENU
        // =================================================================
        else {
            
            // 1. Ghost Click Prevention: Throw the "Add Song" and "Back" hitboxes off-screen!
            if (ui.addSongButton != null) ui.addSongButton.setBounds(-100, -100, 0, 0);
            if (ui.backButton != null) ui.backButton.setBounds(-100, -100, 0, 0);

            // 2. Draw Header
            g2.drawString("PLAYLISTS", rightX + 60, contentY + 35);
            if (ui.iconAlbum != null) g2.drawImage(ui.iconAlbum, rightX + 25, contentY + 16, 24, 24, null);

            // 3. Draw "Create Playlist" Button
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("Inter", Font.PLAIN, 11));
            g2.drawString("Create", rightX + rightW - 60, contentY + 26);
            g2.drawString("Playlist", rightX + rightW - 60, contentY + 39);
            if (ui.addPlaylistButton != null) ui.addPlaylistButton.setBounds(rightX + rightW - 70, contentY + 15, 60, 28);

            // 4. SCROLLING MATH FOR PLAYLIST NAMES
            int pCount = ui.musicHandler.getCreatedPlaylists().size();
            int rowHeight = 25;
            ui.playlistViewportH = playlistsH - 55;
            ui.maxPlaylistScrollOffset = Math.max(0, (pCount * rowHeight) - ui.playlistViewportH);

            // 5. CREATE SAFE ZONE (Clipping Box)
            Shape originalClip = g2.getClip();
            g2.clipRect(rightX, contentY + 50, rightW, ui.playlistViewportH);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Inter", Font.PLAIN, 14));
            int pIdx = 0;
            
            // 6. Render Playlist Names and Map Hitboxes
            for (ControlPanel.ButtonManager btn : ui.getBackendButtons()) {
                if (btn instanceof ControlPanel.Buttons.PlaylistClicker) {
                    
                    // Apply scroll offset
                    int yPos = contentY + 70 + (pIdx * rowHeight) - ui.playlistScrollOffset;
                    String pName = ui.musicHandler.getCreatedPlaylists().get(pIdx);

                    // Hitbox Culling: Map Hitbox safely only if visible
                    if (yPos > contentY + 30 && yPos < contentY + playlistsH) {
                        btn.setBounds(rightX + 20, yPos - 15, rightW - 40, 20);
                    } else {
                        btn.setBounds(-100, -100, 0, 0);
                    }

                    ui.drawMarqueeText(g2, (pIdx + 1) + ".  " + pName, rightX + 25, yPos, rightW - 50);
                    pIdx++;
                }
            }
            g2.setClip(originalClip); // Remove Safe Zone
        }
    }
}