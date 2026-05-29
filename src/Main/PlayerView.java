package Main;

import java.awt.*;
import javax.swing.ImageIcon;

/**
 * Handles the rendering of the bottom-right section of the application.
 * This View class draws the active "Now Playing" dashboard, including the album art, 
 * scrolling track metadata, interactive progress bar, and playback control buttons.
 */
public class PlayerView {

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
        int rightW = totalWidth - leftW; // Player takes up 40% of the available width

        int searchH = 36;
        int searchY = pad + 10;
        int contentY = searchY + searchH + 25;
        int contentH = h - contentY - pad;

        int rightX = pad + leftW + gap;
        int playlistsH = (int) (contentH * 0.35); // Playlists get top 35% of the right column
        int playerY = contentY + playlistsH + gap;
        int playerH = contentH - playlistsH - gap; // Player gets the remaining 65%

        // 1. CONTAINER BACKGROUND
        g2.setColor(Color.decode("#2B2D31"));
        g2.fillRoundRect(rightX, playerY, rightW, playerH, 30, 30);

        // 2. FETCH DYNAMIC METADATA
        String trackTitle = "No Track Loaded";
        String artistName = "Unknown Artist";
        if (!ui.musicHandler.getActiveList().isEmpty()) {
            trackTitle = ui.musicHandler.getActiveList().get(ui.currentSongIndex).getTitle();
            artistName = ui.musicHandler.getActiveList().get(ui.currentSongIndex).getArtist();
        }

        // 3. ALBUM ART
        int artSize = (int) (rightW * 0.76); // Art takes up 76% of the container's width
        int artX = rightX + (rightW - artSize) / 2; // Perfectly center the square
        int artY = playerY + 25;

        if (!ui.musicHandler.getActiveList().isEmpty()) {
            String imgPath = ui.musicHandler.getActiveList().get(ui.currentSongIndex).getImagePath();
            
            if (!imgPath.equals("NO_IMAGE")) {
                // Draw the extracted embedded metadata image
                Image albumCover = new ImageIcon(imgPath).getImage();
                g2.drawImage(albumCover, artX, artY, artSize, artSize, null);
            } else {
                // Fallback: Draw a dark placeholder box if the song has no album art
                g2.setColor(Color.decode("#1E1F22"));
                g2.fillRoundRect(artX, artY, artSize, artSize, 20, 20);
                
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Inter", Font.BOLD, 14));
                FontMetrics fmNoCover = g2.getFontMetrics();
                // Mathematically center the "No Cover" text inside the placeholder box
                g2.drawString("No Cover", artX + (artSize - fmNoCover.stringWidth("No Cover")) / 2, artY + (artSize / 2));
            }
        }

        // 4. TEXT (TITLE & ARTIST)
        int titleY = playerY + (int)(playerH * 0.68);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Inter", Font.BOLD, 16));
        FontMetrics fmTitle = g2.getFontMetrics();
        
        int titleW = fmTitle.stringWidth(trackTitle);
        int songIconSize = 16;
        int songIconGap = 8;
        int maxTitleW = rightW - 60; // Max allowed width before the marquee scrolling kicks in
        
        int rowX;
        
        // Dynamic Anchoring: 
        // If the title is too long, snap it to the left so the marquee scroll looks natural.
        // If the title is short, calculate the exact width to perfectly center it under the album art.
        if (titleW > maxTitleW) {
            rowX = rightX + 30; 
        } else {
            int totalRowW = songIconSize + songIconGap + titleW;
            rowX = rightX + (rightW - totalRowW) / 2; 
        }

        // Draw the little music note icon next to the title
        if (ui.iconSong != null) g2.drawImage(ui.iconSong, rowX, titleY - 14, songIconSize, songIconSize, null);
        
        // Pass to the custom UI method to draw the string (handling the marquee scroll if needed)
        ui.drawMarqueeText(g2, trackTitle, rowX + songIconSize + songIconGap, titleY, maxTitleW);

        // Draw the Artist name centered directly below the Title
        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Inter", Font.PLAIN, 12));
        FontMetrics fmArtist = g2.getFontMetrics();
        g2.drawString(artistName, rightX + (rightW - fmArtist.stringWidth(artistName)) / 2, titleY + 18);

        // 5. PROGRESS BAR (SEEKER)
        int barW = artSize;
        int barX = artX;
        int barY = titleY + 36;
        
        // Map the invisible hitbox over the progress bar track
        if (ui.progressBarSeeker != null) ui.progressBarSeeker.setBounds(barX, barY - 10, barW, 24); 
        
        // Draw the background track of the bar
        if (ui.imgProgressBar != null) g2.drawImage(ui.imgProgressBar, barX, barY, barW, 4, null);
        
        // Determine the knob position:
        // If the user is currently holding the mouse down, freeze the position at their cursor (dragProgress).
        // Otherwise, ask the audio engine for the actual real-time byte calculation (getProgress).
        double progress = ui.isDraggingProgress ? ui.dragProgress : ui.audioEngine.getProgress();
        int knobX = barX + (int)(barW * progress) - 6; 
        
        if (ui.imgProgressKnob != null) g2.drawImage(ui.imgProgressKnob, knobX, barY - 4, 12, 12, null);

        // 6. AUDIO CONTROLS
        int ctrlY = barY + 16;
        Image[] controls = { ui.iconRepeat, ui.iconSkipBack, ui.iconPlay, ui.iconSkipFwd, ui.iconShuffle };
        int iconWidth = 22;
        int totalControlsW = rightW - 50; 
        
        // Calculate the exact mathematical spacing needed to evenly distribute all 5 icons
        int spacing = (totalControlsW - (controls.length * iconWidth)) / (controls.length - 1);

        // Loop through the icons, draw them, and assign their respective hitboxes
        for (int i = 0; i < controls.length; i++) {
            if (controls[i] != null) {
                
                int cx = rightX + 25 + i * (iconWidth + spacing);
                Image iconToDraw = controls[i];
                
                if (i == 0) { 
                    // Repeat Button
                    if (ui.repeatButton != null) ui.repeatButton.setBounds(cx, ctrlY, iconWidth, iconWidth);
                    if (ui.isRepeat) {
                        // Draw a purple accent dot if Repeat is active
                        g2.setColor(Color.decode("#BB86FC"));
                        g2.fillOval(cx + (iconWidth / 2) - 2, ctrlY + iconWidth + 4, 4, 4);
                    }
                } else if (i == 1) { 
                    // Skip Backward
                    if (ui.skipBackButton != null) ui.skipBackButton.setBounds(cx, ctrlY, iconWidth, iconWidth);
                } else if (i == 2) { 
                    // Play/Pause (Dynamically swap the icon based on the engine's active state)
                    iconToDraw = ui.audioEngine.isPlaying() ? ui.iconPause : ui.iconPlay;
                    if (ui.playPauseButton != null) ui.playPauseButton.setBounds(cx, ctrlY, iconWidth, iconWidth);
                } else if (i == 3) { 
                    // Skip Forward
                    if (ui.skipFwdButton != null) ui.skipFwdButton.setBounds(cx, ctrlY, iconWidth, iconWidth);
                } else if (i == 4) { 
                    // Shuffle Button
                    if (ui.shuffleButton != null) ui.shuffleButton.setBounds(cx, ctrlY, iconWidth, iconWidth);
                    if (ui.isShuffle) {
                        // Draw a purple accent dot if Shuffle is active
                        g2.setColor(Color.decode("#BB86FC")); 
                        g2.fillOval(cx + (iconWidth / 2) - 2, ctrlY + iconWidth + 4, 4, 4);
                    }
                }

                g2.drawImage(iconToDraw, cx, ctrlY, iconWidth, iconWidth, null);
            }
        }
    }
}