package Main;

import java.awt.*;

public class PlaylistView {

    public static void draw(Graphics2D g2, UI ui, int w, int h) {
        
        int pad = 25;
        int gap = 20;
        int totalWidth = w - (pad * 2) - gap;
        int leftW = (int) (totalWidth * 0.60);
        int rightW = totalWidth - leftW; 

        int searchH = 36;
        int contentY = pad + 10 + searchH + 25;
        int contentH = h - contentY - pad;

        int rightX = pad + leftW + gap;
        int playlistsH = (int) (contentH * 0.35); 

        g2.setColor(Color.decode("#2B2D31"));
        g2.fillRoundRect(rightX, contentY, rightW, playlistsH, 30, 30);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Inter", Font.BOLD, 18));

        if (ui.insidePlaylistView) {
            
            if (ui.addPlaylistButton != null) ui.addPlaylistButton.setBounds(-100, -100, 0, 0);

            g2.drawString(ui.selectedPlaylistName.toUpperCase(), rightX + 60, contentY + 35);
            if (ui.iconAlbum != null) g2.drawImage(ui.iconAlbum, rightX + 25, contentY + 16, 24, 24, null);

            g2.setColor(Color.GRAY);
            g2.setFont(new Font("Inter", Font.BOLD, 12));
            g2.drawString("< Back", rightX + 25, contentY + playlistsH - 20);
            if (ui.backButton != null) ui.backButton.setBounds(rightX + 20, contentY + playlistsH - 35, 60, 20);

            g2.setColor(ui.isAddingToPlaylist ? Color.decode("#BB86FC") : Color.WHITE);
            String addText = ui.isAddingToPlaylist ? "Select from Library..." : "+ Add Song";
            int textW = g2.getFontMetrics().stringWidth(addText);
            
            g2.drawString(addText, rightX + rightW - textW - 25, contentY + playlistsH - 20);
            if (ui.addSongButton != null) ui.addSongButton.setBounds(rightX + rightW - textW - 30, contentY + playlistsH - 35, textW + 10, 20);

            int songCount = ui.musicHandler.getPlaylistSongs().get(ui.selectedPlaylistName) != null ? ui.musicHandler.getPlaylistSongs().get(ui.selectedPlaylistName).size() : 0;
            int rowHeight = 25;
            
            ui.playlistViewportH = playlistsH - 95; 
            ui.maxPlaylistScrollOffset = Math.max(0, (songCount * rowHeight) - ui.playlistViewportH);

            Shape originalClip = g2.getClip();
            g2.clipRect(rightX, contentY + 50, rightW, ui.playlistViewportH);

            int maxTextW = rightW - 50;
            int songIdx = 0;
            
            for (ControlPanel.ButtonManager btn : ui.getBackendButtons()) {
                if (btn instanceof ControlPanel.SongClicker) {
                    
                    ControlPanel.Song s = ((ControlPanel.SongClicker) btn).getSong();
                    
                    int yPos = contentY + 70 + (songIdx * rowHeight) - ui.playlistScrollOffset;

                    if (yPos > contentY + 30 && yPos < contentY + playlistsH - 40) {
                        btn.setBounds(rightX + 20, yPos - 15, maxTextW, 20);
                    } else {
                        btn.setBounds(-100, -100, 0, 0); 
                    }

                    if (!ui.musicHandler.getActiveList().isEmpty() && ui.currentSongIndex >= 0 && ui.currentSongIndex < ui.musicHandler.getActiveList().size()) {
                        if (s == ui.musicHandler.getActiveList().get(ui.currentSongIndex)) g2.setColor(Color.decode("#BB86FC"));
                        else g2.setColor(Color.WHITE);
                    } else g2.setColor(Color.WHITE);

                    String numberedDisplay = String.format("%d.  %s", (songIdx + 1), s.getTitle());
                    ui.drawMarqueeText(g2, numberedDisplay, rightX + 25, yPos, maxTextW);
                    
                    songIdx++;
                }
            }
            g2.setClip(originalClip); 
        } 
        
        else {
            
            if (ui.addSongButton != null) ui.addSongButton.setBounds(-100, -100, 0, 0);
            if (ui.backButton != null) ui.backButton.setBounds(-100, -100, 0, 0);

            g2.drawString("PLAYLISTS", rightX + 60, contentY + 35);
            if (ui.iconAlbum != null) g2.drawImage(ui.iconAlbum, rightX + 25, contentY + 16, 24, 24, null);

            g2.setColor(Color.GRAY);
            g2.setFont(new Font("Inter", Font.PLAIN, 11));
            g2.drawString("Create", rightX + rightW - 60, contentY + 26);
            g2.drawString("Playlist", rightX + rightW - 60, contentY + 39);
            if (ui.addPlaylistButton != null) ui.addPlaylistButton.setBounds(rightX + rightW - 70, contentY + 15, 60, 28);

            int pCount = ui.musicHandler.getCreatedPlaylists().size();
            int rowHeight = 25;
            ui.playlistViewportH = playlistsH - 55;
            ui.maxPlaylistScrollOffset = Math.max(0, (pCount * rowHeight) - ui.playlistViewportH);

            Shape originalClip = g2.getClip();
            g2.clipRect(rightX, contentY + 50, rightW, ui.playlistViewportH);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Inter", Font.PLAIN, 14));
            int pIdx = 0;
            
            for (ControlPanel.ButtonManager btn : ui.getBackendButtons()) {
                if (btn instanceof ControlPanel.Buttons.PlaylistClicker) {
                    
                    int yPos = contentY + 70 + (pIdx * rowHeight) - ui.playlistScrollOffset;
                    String pName = ui.musicHandler.getCreatedPlaylists().get(pIdx);

                    if (yPos > contentY + 30 && yPos < contentY + playlistsH) {
                        btn.setBounds(rightX + 20, yPos - 15, rightW - 40, 20);
                    } else {
                        btn.setBounds(-100, -100, 0, 0);
                    }

                    ui.drawMarqueeText(g2, (pIdx + 1) + ".  " + pName, rightX + 25, yPos, rightW - 50);
                    pIdx++;
                }
            }
            g2.setClip(originalClip); 
        }
    }
}