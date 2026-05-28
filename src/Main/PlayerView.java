package Main;

import java.awt.*;
import javax.swing.ImageIcon;

public class PlayerView {

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
        int playerY = contentY + playlistsH + gap;
        int playerH = contentH - playlistsH - gap;

        // CONTAINER BACKGROUND
        g2.setColor(Color.decode("#2B2D31"));
        g2.fillRoundRect(rightX, playerY, rightW, playerH, 30, 30);

        // FETCH DYNAMIC DATA
        String trackTitle = "No Track Loaded";
        String artistName = "Unknown Artist";
        if (!ui.musicHandler.getPlaylist().isEmpty()) {
            trackTitle = ui.musicHandler.getPlaylist().get(ui.currentSongIndex).getTitle();
            artistName = ui.musicHandler.getPlaylist().get(ui.currentSongIndex).getArtist();
        }

        // ALBUM ART
        int artSize = (int) (rightW * 0.76);
        int artX = rightX + (rightW - artSize) / 2;
        int artY = playerY + 25;

        if (!ui.musicHandler.getPlaylist().isEmpty()) {
            String imgPath = ui.musicHandler.getPlaylist().get(ui.currentSongIndex).getImagePath();
            if (!imgPath.equals("NO_IMAGE")) {
                Image albumCover = new ImageIcon(imgPath).getImage();
                g2.drawImage(albumCover, artX, artY, artSize, artSize, null);
            } else {
                g2.setColor(Color.decode("#1E1F22"));
                g2.fillRoundRect(artX, artY, artSize, artSize, 20, 20);
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Inter", Font.BOLD, 14));
                FontMetrics fmNoCover = g2.getFontMetrics();
                g2.drawString("No Cover", artX + (artSize - fmNoCover.stringWidth("No Cover")) / 2, artY + (artSize / 2));
            }
        }

        // TEXT (TITLE & ARTIST)
        int titleY = playerY + (int)(playerH * 0.68);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Inter", Font.BOLD, 16));
        FontMetrics fmTitle = g2.getFontMetrics();
        int titleW = fmTitle.stringWidth(trackTitle);
        int songIconSize = 16;
        int songIconGap = 8;
        int totalRowW = songIconSize + songIconGap + titleW;
        int rowX = rightX + (rightW - totalRowW) / 2;

        if (ui.iconSong != null) g2.drawImage(ui.iconSong, rowX, titleY - 14, songIconSize, songIconSize, null);
        g2.drawString(trackTitle, rowX + songIconSize + songIconGap, titleY);

        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Inter", Font.PLAIN, 12));
        FontMetrics fmArtist = g2.getFontMetrics();
        g2.drawString(artistName, rightX + (rightW - fmArtist.stringWidth(artistName)) / 2, titleY + 18);

        // PROGRESS BAR
        int barW = artSize;
        int barX = artX;
        int barY = titleY + 36;
        
        // Tell the Seeker object where it is on the screen so it can be clicked
        if (ui.progressBarSeeker != null) ui.progressBarSeeker.setBounds(barX, barY - 10, barW, 24); 

        if (ui.imgProgressBar != null) g2.drawImage(ui.imgProgressBar, barX, barY, barW, 4, null);
        double progress = ui.audioEngine.getProgress();
        int knobX = barX + (int)(barW * progress) - 6; 
        if (ui.imgProgressKnob != null) g2.drawImage(ui.imgProgressKnob, knobX, barY - 4, 12, 12, null);

        // AUDIO CONTROLS
        int ctrlY = barY + 16;
        Image[] controls = { ui.iconRepeat, ui.iconSkipBack, ui.iconPlay, ui.iconSkipFwd, ui.iconShuffle };
        int iconWidth = 22;
        int totalControlsW = rightW - 50; 
        int spacing = (totalControlsW - (controls.length * iconWidth)) / (controls.length - 1);

        for (int i = 0; i < controls.length; i++) {
            if (controls[i] != null) {
                int cx = rightX + 25 + i * (iconWidth + spacing);
                Image iconToDraw = controls[i];
                
                // Set bounds for each smart button dynamically
                if (i == 0) { 
                    if(ui.repeatButton != null) ui.repeatButton.setBounds(cx, ctrlY, iconWidth, iconWidth);
                    if (ui.isRepeat) {
                        g2.setColor(Color.decode("#1DB954"));
                        g2.fillOval(cx + (iconWidth / 2) - 2, ctrlY + iconWidth + 4, 4, 4);
                    }
                } else if (i == 1) { 
                    if(ui.skipBackButton != null) ui.skipBackButton.setBounds(cx, ctrlY, iconWidth, iconWidth);
                } else if (i == 2) { 
                    iconToDraw = ui.audioEngine.isPlaying() ? ui.iconPause : ui.iconPlay;
                    if(ui.playPauseButton != null) ui.playPauseButton.setBounds(cx, ctrlY, iconWidth, iconWidth);
                } else if (i == 3) { 
                    if(ui.skipFwdButton != null) ui.skipFwdButton.setBounds(cx, ctrlY, iconWidth, iconWidth);
                } else if (i == 4) { 
                    if(ui.shuffleButton != null) ui.shuffleButton.setBounds(cx, ctrlY, iconWidth, iconWidth);
                    if (ui.isShuffle) {
                        g2.setColor(Color.decode("#1DB954")); 
                        g2.fillOval(cx + (iconWidth / 2) - 2, ctrlY + iconWidth + 4, 4, 4);
                    }
                }

                g2.drawImage(iconToDraw, cx, ctrlY, iconWidth, iconWidth, null);
            }
        }
    }
}