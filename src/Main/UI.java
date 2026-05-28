package Main;

import ControlPanel.AudioEngine;
import ControlPanel.ButtonManager;
import ControlPanel.ExitButton;     
import ControlPanel.MusicHandler;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class UI {

    Panel panel;
    public boolean exit = false;
    public boolean escInq = false;

   
    int exitX = 265, exitY = 355, exitW = 40, exitH = 20;

    //Arraylist for backend button coordinates
    private ArrayList<ButtonManager> backEndButtons = new ArrayList<>();
    public Rectangle yesButtonBounds = new Rectangle(0, 0, 0, 0);
    public Rectangle noButtonBounds = new Rectangle(0, 0, 0, 0);

    public Rectangle playPauseBounds = new Rectangle(0, 0, 0, 0);
    public Rectangle skipFwdBounds = new Rectangle(0, 0, 0, 0);
    public Rectangle skipBackBounds = new Rectangle(0, 0, 0, 0);
    
    public AudioEngine audioEngine = new AudioEngine();
    public MusicHandler musicHandler = new MusicHandler();
    public int currentSongIndex = 0;

    //  ASSETS 
    private Image iconLibrary, iconAlbum, iconArtist, iconSong;
    private Image iconPlay, iconPause, iconFastFwd, iconMute;
    private Image imgProgressBar, imgProgressKnob;
    private Image iconRepeat, iconRewind, iconSearch, iconSettings, iconShuffle, iconSkipBack, iconSkipFwd;
    private Image iconVolDown, iconVolUp;

    public UI(Panel panel) {
        this.panel = panel;
        
        backEndButtons.add(new ExitButton(exitX, exitY, exitW, exitH, panel, this));
        loadAssets();
    }

    
    public ArrayList<ButtonManager> getBackendButtons() {
        return backEndButtons;
    }

    //  LOAD ASSETS 
    private void loadAssets() {
        try {
            iconLibrary     = new ImageIcon(getClass().getResource("/assets/Library.png")).getImage();
            iconAlbum       = new ImageIcon(getClass().getResource("/assets/Album.png")).getImage();
            iconArtist      = new ImageIcon(getClass().getResource("/assets/Artist.png")).getImage();
            iconSong        = new ImageIcon(getClass().getResource("/assets/Song.png")).getImage();
            iconPlay        = new ImageIcon(getClass().getResource("/assets/Play.png")).getImage();
            iconPause       = new ImageIcon(getClass().getResource("/assets/Pause.png")).getImage();
            iconFastFwd     = new ImageIcon(getClass().getResource("/assets/Fast Fwd.png")).getImage();
            iconMute        = new ImageIcon(getClass().getResource("/assets/Mute.png")).getImage();
            imgProgressBar  = new ImageIcon(getClass().getResource("/assets/Line 1.png")).getImage();
            imgProgressKnob = new ImageIcon(getClass().getResource("/assets/Ellipse 1.png")).getImage();
            iconRepeat      = new ImageIcon(getClass().getResource("/assets/Repeat.png")).getImage();
            iconRewind      = new ImageIcon(getClass().getResource("/assets/Rewind.png")).getImage();
            iconSearch      = new ImageIcon(getClass().getResource("/assets/Search.png")).getImage();
            iconSettings    = new ImageIcon(getClass().getResource("/assets/Settings.png")).getImage();
            iconShuffle     = new ImageIcon(getClass().getResource("/assets/Shuffle.png")).getImage();
            iconSkipBack    = new ImageIcon(getClass().getResource("/assets/Skip Back.png")).getImage();
            iconSkipFwd     = new ImageIcon(getClass().getResource("/assets/Skip Fwd.png")).getImage();
            iconVolDown     = new ImageIcon(getClass().getResource("/assets/Volume Down.png")).getImage();
            iconVolUp       = new ImageIcon(getClass().getResource("/assets/Volume Up.png")).getImage();
            
        } catch (Exception e) {
            System.out.println("Failed to load images. Make sure your '/assets/' folder exists and file names are exact.");
            e.printStackTrace();
        }
    }

    //  SHARED LAYOUT 
    private int pad() { return 25; }
    private int searchY() { return pad() + 10; }
    private int searchH() { return 36; }
    private int contentY() { return searchY() + searchH() + 25; }

    //  MAIN DRAW 
    public void draw(Graphics2D g2) {
        panel.setBackground(Color.decode("#1E1F22"));

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawPage(g2);
        drawImages(g2);

        if (exit) {drawExitInquiry(g2);}
        if (escInq) {drawExit(g2);}
    }

    //  PAGE CONTAINERS & TEXT 
    public void drawPage(Graphics2D g2) {
        int w = panel.getWidth();
        int h = panel.getHeight();

        int pad = this.pad();
        int gap = 20;

        int totalWidth = w - (pad * 2) - gap;
        int leftW = (int) (totalWidth * 0.60); 
        int rightW = totalWidth - leftW;

        int contentY = contentY();
        int contentH = h - contentY - pad;

        int rightX = pad + leftW + gap;
        int playlistsH = (int) (contentH * 0.35); 
        int playerY = contentY + playlistsH + gap;
        int playerH = contentH - playlistsH - gap;

        // 1. TOP HEADER / SEARCH BAR CONTAINER
        int searchW = (int) (w * 0.45);
        int searchX = (w - searchW) / 2;
        g2.setColor(Color.decode("#313338"));
        g2.fillRoundRect(searchX, searchY(), searchW, searchH(), searchH(), searchH()); 

        // 2. LIBRARY CONTAINER
        g2.setColor(Color.decode("#2B2D31"));
        g2.fillRoundRect(pad, contentY, leftW, contentH, 30, 30);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Inter", Font.BOLD, 18));
        g2.drawString("LIBRARY", pad + 60, contentY + 35);

        // 3. PLAYLISTS CONTAINER
        g2.setColor(Color.decode("#2B2D31"));
        g2.fillRoundRect(rightX, contentY, rightW, playlistsH, 30, 30);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Inter", Font.BOLD, 18));
        g2.drawString("PLAYLISTS", rightX + 60, contentY + 35);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Inter", Font.BOLD, 16));
        FontMetrics fmNone = g2.getFontMetrics();
        int noneW = fmNone.stringWidth("None");
        g2.drawString("None", rightX + (rightW - noneW) / 2, contentY + (playlistsH / 2) + 10);

        // 4. PLAYER CONTAINER
        g2.setColor(Color.decode("#2B2D31"));
        g2.fillRoundRect(rightX, playerY, rightW, playerH, 30, 30);

        // Fetch dynamic song data
        String trackTitle = "No Track Loaded";
        String artistName = "Unknown Artist";
        
        if (!musicHandler.getPlaylist().isEmpty()) {
            trackTitle = musicHandler.getPlaylist().get(currentSongIndex).getTitle();
            artistName = musicHandler.getPlaylist().get(currentSongIndex).getArtist();
        }

        g2.setFont(new Font("Inter", Font.BOLD, 16));
        FontMetrics fmTitle = g2.getFontMetrics();
        int titleW = fmTitle.stringWidth(trackTitle);
        
        int songIconSize = 18;
        int songIconGap = 8;
        int totalRowW = songIconSize + songIconGap + titleW;
        int rowX = rightX + (rightW - totalRowW) / 2;
        int titleY = playerY + (int)(playerH * 0.68);
        
        g2.drawString(trackTitle, rowX + songIconSize + songIconGap, titleY);

        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Inter", Font.PLAIN, 12));
        FontMetrics fmArtist = g2.getFontMetrics();
        int artistW = fmArtist.stringWidth(artistName);
        g2.drawString(artistName, rightX + (rightW - artistW) / 2, titleY + 18);
    }

    //  IMAGES & ICONS PLACEMENT 
    public void drawImages(Graphics2D g2) {
        int w = panel.getWidth();
        int h = panel.getHeight();

        int pad = this.pad();
        int gap = 20;

        int totalWidth = w - (pad * 2) - gap;
        int leftW = (int) (totalWidth * 0.60);
        int rightW = totalWidth - leftW;

        int contentY = contentY();
        int contentH = h - contentY - pad;

        int rightX = pad + leftW + gap;
        int playlistsH = (int) (contentH * 0.35);
        int playerY = contentY + playlistsH + gap;
        int playerH = contentH - playlistsH - gap;

        int searchW = (int) (w * 0.45);
        int searchX = (w - searchW) / 2;
        
        if (iconArtist != null) 
            g2.drawImage(iconArtist, searchX + 12, searchY() + (searchH() - 20) / 2, 20, 20, null);
        if (iconSearch != null) 
            g2.drawImage(iconSearch, searchX + searchW - 32, searchY() + (searchH() - 20) / 2, 20, 20, null);
        if (iconSettings != null) 
            g2.drawImage(iconSettings, w - pad - 28, searchY() + (searchH() - 24) / 2, 24, 24, null);

        if (iconLibrary != null)
            g2.drawImage(iconLibrary, pad + 25, contentY + 16, 24, 24, null);

        if (iconAlbum != null)
            g2.drawImage(iconAlbum, rightX + 25, contentY + 16, 24, 24, null);

        int artSize = (int) (rightW * 0.76);
        int artX = rightX + (rightW - artSize) / 2;
        int titleY = playerY + (int)(playerH * 0.68);

        g2.setFont(new Font("Inter", Font.BOLD, 16));
        FontMetrics fmTitle = g2.getFontMetrics();
        int titleW = fmTitle.stringWidth("Someday I'll Get it");
        int songIconSize = 16;
        int songIconGap = 8;
        int totalRowW = songIconSize + songIconGap + titleW;
        int rowX = rightX + (rightW - totalRowW) / 2;
        
        if (iconSong != null) {
            g2.drawImage(iconSong, rowX, titleY - 14, songIconSize, songIconSize, null);
        }

        int barW = artSize;
        int barX = artX;
        int barY = titleY + 36;
        if (imgProgressBar != null) {
            g2.drawImage(imgProgressBar, barX, barY, barW, 4, null);
        }
        if (imgProgressKnob != null) {
            int knobX = barX + (int)(barW * 0.30) - 6; 
            g2.drawImage(imgProgressKnob, knobX, barY - 4, 12, 12, null);
        }

        int ctrlY = barY + 16;
        Image[] controls = { iconRepeat, iconSkipBack, iconPlay, iconSkipFwd, iconShuffle };
        int iconWidth = 22;
        int totalControlsW = rightW - 50; 
        int spacing = (totalControlsW - (controls.length * iconWidth)) / (controls.length - 1);

        for (int i = 0; i < controls.length; i++) {
            if (controls[i] != null) {
                int cx = rightX + 25 + i * (iconWidth + spacing);
                
                // Determine which icon to draw and set their invisible hitboxes
                Image iconToDraw = controls[i];
                
                if (i == 2) { // The Play button is at index 2
                    iconToDraw = audioEngine.isPlaying() ? iconPause : iconPlay;
                    playPauseBounds.setBounds(cx, ctrlY, iconWidth, iconWidth);
                } else if (i == 1) { // Skip Back is at index 1
                    skipBackBounds.setBounds(cx, ctrlY, iconWidth, iconWidth);
                } else if (i == 3) { // Skip Forward is at index 3
                    skipFwdBounds.setBounds(cx, ctrlY, iconWidth, iconWidth);
                }

                g2.drawImage(iconToDraw, cx, ctrlY, iconWidth, iconWidth, null);
            }
        }   
    }

    //  EXIT POPUP 
    public void drawExitInquiry(Graphics2D g2) {
        g2.setFont(new Font("Inter", Font.PLAIN, 16));
        FontMetrics fm = g2.getFontMetrics();

        String msg = "Are you sure you want to exit Vibezz?";
        int textW = fm.stringWidth(msg);

        int pad = 30;
        int popupW = textW + (pad * 2);
        int popupH = 110;

        int w = panel.getWidth();
        int h = panel.getHeight();

        int x = (w - popupW) / 2;
        int y = (h - popupH) / 2;

        g2.setColor(Color.decode("#313338"));
        g2.fillRoundRect(x, y, popupW, popupH, 30, 30);

        g2.setColor(Color.WHITE);
        g2.drawString(msg, x + pad, y + 40);

        int btnW = 60;
        int btnH = 30;
        int space = 30;

        int startX = x + (popupW - (btnW * 2 + space)) / 2;
        int btnY = y + 60;

        // YES BUTTON
        g2.setColor(Color.decode("#5865F2"));
        g2.fillRoundRect(startX, btnY, btnW, btnH, 15, 15);
        
        
        yesButtonBounds.setBounds(startX, btnY, btnW, btnH);

        g2.setColor(Color.WHITE);
        int yesTextWidth = fm.stringWidth("Yes");
        g2.drawString("Yes", startX + (btnW - yesTextWidth) / 2, btnY + 20);

        // NO BUTTON
        int noX = startX + btnW + space;
        g2.setColor(Color.decode("#5865F2"));
        g2.fillRoundRect(noX, btnY, btnW, btnH, 15, 15);
        
    
        noButtonBounds.setBounds(noX, btnY, btnW, btnH);

        g2.setColor(Color.WHITE);
        int noTextWidth = fm.stringWidth("No");
        g2.drawString("No", noX + (btnW - noTextWidth) / 2, btnY + 20);
    }

    //  TOGGLE 
    public void exitInquiry() {

        exit = !exit;
        panel.repaint();
    }

    public void drawExit(Graphics2D g2){

        g2.setColor(Color.decode("#555555"));
        g2.fillRoundRect(270, 5, 120, 30, 30, 30);

        g2.setColor(Color.white);
        g2.setFont(new Font("Inter", Font.PLAIN, 12));
        String text = "Press ESC to exit";

        g2.drawString(text, 283, 23);
    }
}