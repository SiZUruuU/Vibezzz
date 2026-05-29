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

        int searchH = 36;
        int searchY = pad + 10;
        int contentY = searchY + searchH + 25;
        int contentH = h - contentY - pad;

        int searchW = (int) (w * 0.45);
        int searchX = (w - searchW) / 2;
        
        ui.searchBarBounds.setBounds(searchX, searchY, searchW, searchH);
        
        g2.setColor(Color.decode("#313338"));
        g2.fillRoundRect(searchX, searchY, searchW, searchH, searchH, searchH);
        g2.setFont(new Font("Inter", Font.PLAIN, 13));

        int textStartX = searchX + 40;
        int textMaxX   = searchX + searchW - 36;
        int maxTextW   = textMaxX - textStartX;

        FontMetrics fm = g2.getFontMetrics();

        if (ui.searchBarFocused) {
            ui.panel.repaint();
        }
        
        if (ui.searchText.isEmpty()) {

            if (!ui.searchBarFocused) {
                g2.setColor(new Color(160, 160, 160));
                g2.drawString("Search songs...", textStartX, searchY + 24);
            }
            else {
                long time = System.currentTimeMillis();
                if ((time / 500) % 2 == 0) {
                    int caretX = textStartX;
                    int caretY = searchY + 8;
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(caretX, caretY, 2, 20, 2, 2);
                }
            }

        } else {
            
            String text = ui.searchText;
            int start = 0;

            while (start < text.length() && fm.stringWidth(text.substring(start)) > maxTextW) { start++; }
            String visibleText = text.substring(start);

            g2.setColor(Color.WHITE);
            g2.drawString(visibleText, textStartX, searchY + 24);

            if (ui.searchBarFocused) {
                long time = System.currentTimeMillis();
                if ((time / 500) % 2 == 0) {
                    int caretX = textStartX + fm.stringWidth(visibleText) + 2;
                    int caretY = searchY + 8;
                    g2.fillRoundRect(caretX, caretY, 2, 20, 2, 2);
                }
            }
        }

        if (ui.iconArtist != null) g2.drawImage(ui.iconArtist, searchX + 12, searchY + (searchH - 20) / 2, 20, 20, null);
        if (ui.iconSearch != null) g2.drawImage(ui.iconSearch, searchX + searchW - 32, searchY + (searchH - 20) / 2, 20, 20, null);

        g2.setColor(Color.decode("#2B2D31"));
        g2.fillRoundRect(pad, contentY, leftW, contentH, 30, 30);

        if (ui.addFolderButton != null) ui.addFolderButton.setBounds(pad + 145, contentY + 22, 80, 18);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Inter", Font.BOLD, 18));
        g2.drawString("LIBRARY", pad + 60, contentY + 35);
        
        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Inter", Font.PLAIN, 12));
        g2.drawString("+ Add Folder", pad + 150, contentY + 34);

        if (ui.iconLibrary != null) g2.drawImage(ui.iconLibrary, pad + 25, contentY + 16, 24, 24, null);

        ArrayList<Song> playlist = ui.musicHandler.searchSongs(ui.searchText);
        int listStartY = contentY + 75;
        int rowHeight = 30;
        int totalContentHeight = playlist.size() * rowHeight;
        
        ui.libraryViewportH = contentH - 95;
        ui.maxScrollOffset = Math.max(0, totalContentHeight - ui.libraryViewportH);

        Shape originalClip = g2.getClip();
        
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

        Song nowPlaying = (!ui.musicHandler.getActiveList().isEmpty() && ui.currentSongIndex >= 0
                && ui.currentSongIndex < ui.musicHandler.getActiveList().size())
                ? ui.musicHandler.getActiveList().get(ui.currentSongIndex)
                : null;

        for (int i = 0; i < playlist.size(); i++) {
            
            int currentRenderY = listStartY + (i * rowHeight) - ui.scrollOffset;
            
            Song s = playlist.get(i);
            String rawDisplay = String.format("%d.  %s - %s   [%s]", (i + 1), s.getTitle(), s.getArtist(), s.getDuration());

            if (s == nowPlaying) g2.setColor(Color.decode("#BB86FC"));
            else g2.setColor(Color.WHITE);

            ui.drawMarqueeText(g2, rawDisplay, pad + 25, currentRenderY, maxTextWidth);
        }
        
        g2.setClip(originalClip);
    }
}