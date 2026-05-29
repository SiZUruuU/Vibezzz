package Main;

import ControlPanel.AudioEngine;
import ControlPanel.ButtonManager;
import ControlPanel.Buttons.*;
import ControlPanel.MusicHandler; 
import java.awt.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.Timer;

public class UI {

    public Panel panel;
    public boolean exit = false;     
    public boolean escInq = false;  

    public ArrayList<ButtonManager> backEndButtons = new ArrayList<>();
    private ArrayList<ButtonManager> popupButtons = new ArrayList<>();

    public ButtonManager playPauseButton;
    public ButtonManager skipFwdButton;
    public ButtonManager skipBackButton;
    public ButtonManager repeatButton;
    public ButtonManager shuffleButton;
    public ButtonManager progressBarSeeker;
    public ButtonManager exitYesButton;
    public ButtonManager exitNoButton;
    public ButtonManager addFolderButton;
    public ButtonManager libraryListClicker;
    public ButtonManager settings;
    public ButtonManager addPlaylistButton;
    public ButtonManager addSongButton;
    public ButtonManager backButton;
    public VolumeSlider volumeSlider;
    
    public boolean isRepeat = false;
    public boolean isShuffle = false;
    public int currentSongIndex = 0;
    public boolean settingsPressed = false;
    
    public boolean isDraggingVolume = false;
    public boolean isDraggingProgress = false;
    public double dragProgress = 0.0;
    
    public int scrollOffset = 0;       
    public int maxScrollOffset = 0;   
    public int libraryViewportH = 0;
    
    public int playlistScrollOffset = 0;
    public int maxPlaylistScrollOffset = 0;
    public int playlistViewportH = 0;
    
    public String searchText = "";
    public boolean searchBarFocused = false;
    public Rectangle searchBarBounds = new Rectangle();
    public int marqueeTick = 0; 
    
    public boolean insidePlaylistView = false; 
    public boolean isAddingToPlaylist = false;
    public String selectedPlaylistName = ""; 
    
    public AudioEngine audioEngine = new AudioEngine();
    public MusicHandler musicHandler = new MusicHandler();

    public Image iconLibrary, iconAlbum, iconArtist, iconSong;
    public Image iconPlay, iconPause, iconFastFwd, iconMute;
    public Image imgProgressBar, imgProgressKnob;
    public Image iconRepeat, iconRewind, iconSearch, iconSettings, iconShuffle, iconSkipBack, iconSkipFwd;
    public Image iconVolDown, iconVolUp;

    public UI(Panel panel) {
        this.panel = panel;
        
        this.panel.setBackground(Color.decode("#1E1F22")); 
        
        loadAssets();
        initializeButtons();

        audioEngine.setTrackEndCallback(() -> {
            if (skipFwdButton != null) {
                skipFwdButton.execute(0, 0); 
            }
        });

        Timer timer = new Timer(50, e -> {
            marqueeTick++;
            panel.repaint();
        });
        timer.start();
    }

    private void initializeButtons() {
        playPauseButton = new PlayPauseButton(panel, this);
        progressBarSeeker = new ProgressBarSeeker(panel, this);
        skipFwdButton = new SkipButton(panel, this, true);
        skipBackButton = new SkipButton(panel, this, false);
        repeatButton = new ToggleButton(panel, this, "repeat");
        shuffleButton = new ToggleButton(panel, this, "shuffle");
        addFolderButton = new AddFolderButton(panel, this);
        libraryListClicker = new LibraryListClicker(panel, this);
        settings = new SettingsButton(panel, this); 
        volumeSlider = new VolumeSlider(panel, this); 
        addPlaylistButton = new AddPlaylistButton(panel, this); 
        addSongButton = new AddSongButton(panel, this);
        backButton = new BackButton(panel, this); 

        backEndButtons.add(addFolderButton);
        backEndButtons.add(libraryListClicker);
        backEndButtons.add(playPauseButton);
        backEndButtons.add(progressBarSeeker);
        backEndButtons.add(skipFwdButton);
        backEndButtons.add(skipBackButton);
        backEndButtons.add(repeatButton);
        backEndButtons.add(shuffleButton);
        backEndButtons.add(settings);
        backEndButtons.add(volumeSlider);
        backEndButtons.add(addPlaylistButton);
        backEndButtons.add(addSongButton);
        backEndButtons.add(backButton);

        exitYesButton = new ExitPopupButton(panel, this, true);
        exitNoButton = new ExitPopupButton(panel, this, false);
        popupButtons.add(exitYesButton);
        popupButtons.add(exitNoButton);
        
        refreshPlaylistButtons();
    }

    public void refreshPlaylistButtons() {
        backEndButtons.removeIf(b -> b instanceof ControlPanel.Buttons.PlaylistClicker || b instanceof ControlPanel.SongClicker);

        for (String pName : musicHandler.getCreatedPlaylists()) {
            backEndButtons.add(new ControlPanel.Buttons.PlaylistClicker(panel, this, pName));
        }

        if (insidePlaylistView && selectedPlaylistName != null && !selectedPlaylistName.isEmpty()) {
            ArrayList<ControlPanel.Song> songs = musicHandler.getPlaylistSongs().get(selectedPlaylistName);
            if (songs != null) {
                for (ControlPanel.Song s : songs) {
                    backEndButtons.add(new ControlPanel.SongClicker(panel, this, s));
                }
            }
        }
    }

    public ArrayList<ButtonManager> getBackendButtons() { return backEndButtons; }
    public ArrayList<ButtonManager> getPopupButtons() { return popupButtons; }

    public void draw(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = panel.getWidth();
        int h = panel.getHeight();

        LibraryView.draw(g2, this, w, h);
        PlaylistView.draw(g2, this, w, h); 
        PlayerView.draw(g2, this, w, h);

        int gearSize = 20;
        int gearX = w - 45;
        int gearY = 25;
        
        if (iconSettings != null) {
            g2.drawImage(iconSettings, gearX, gearY, gearSize, gearSize, null);
        }
        if (settings != null) {
            settings.setBounds(gearX - 5, gearY - 5, gearSize + 10, gearSize + 10); 
        }

        if (settingsPressed) VolumeView.draw(g2, this, w, h);
        if (exit) PopupView.draw(g2, this, w, h);
        
        if (escInq && !exit) drawExitBadge(g2);
    }

    public void exitInquiry() {
        exit = !exit;
        panel.repaint();
    }

    private Image loadImage(String path) {
        try {
            java.net.URL url = getClass().getResource(path);
            if (url != null) {
                return new ImageIcon(url).getImage();
            } else {
                System.out.println("⚠️ Warning: Could not find image at " + path);
                return null;
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error loading image at " + path);
            return null;
        }
    }

    private void loadAssets() {
        iconLibrary     = loadImage("/assets/Library.png");
        iconAlbum       = loadImage("/assets/Album.png");
        iconArtist      = loadImage("/assets/Artist.png");
        iconSong        = loadImage("/assets/Song.png");
        iconPlay        = loadImage("/assets/Play.png");
        iconPause       = loadImage("/assets/Pause.png");
        iconFastFwd     = loadImage("/assets/Fast Fwd.png");
        iconMute        = loadImage("/assets/Mute.png");
        imgProgressBar  = loadImage("/assets/Line 1.png");
        imgProgressKnob = loadImage("/assets/Ellipse 1.png");
        iconRepeat      = loadImage("/assets/Repeat.png");
        iconRewind      = loadImage("/assets/Rewind.png");
        iconSearch      = loadImage("/assets/Search.png");
        iconSettings    = loadImage("/assets/Settings.png");
        iconShuffle     = loadImage("/assets/Shuffle.png");
        iconSkipBack    = loadImage("/assets/Skip Back.png");
        iconSkipFwd     = loadImage("/assets/Skip Fwd.png");
        iconVolDown     = loadImage("/assets/Volume Down.png");
        iconVolUp       = loadImage("/assets/Volume Up.png");
    }

    private void drawExitBadge(Graphics2D g2) {
        g2.setFont(new Font("Inter", Font.PLAIN, 16));
        FontMetrics fm = g2.getFontMetrics();
        String text = "Press ESC to Exit";

        int textWidth = fm.stringWidth(text);
        int padding = 20; 
        int boxWidth = textWidth + (padding * 2);
        int boxHeight = 30;

        int boxX = (panel.getWidth() - boxWidth) / 2;
        int boxY = 5;

        g2.setColor(Color.decode("#555555"));
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, boxHeight, boxHeight);
        g2.setColor(Color.WHITE);
        g2.drawString(text, boxX + padding, boxY + 21); 
    }

    public void drawMarqueeText(Graphics2D g2, String text, int x, int y, int maxW) {
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(text);

        if (textW <= maxW) {
            g2.drawString(text, x, y);
            return;
        }

        int overflow = textW - maxW; 
        int cycle = overflow + 100;  
        
        int offset = (marqueeTick / 2) % (cycle * 2); 

        int shift = 0;
        if (offset > 50 && offset <= 50 + overflow) {
            shift = offset - 50;
        } else if (offset > 50 + overflow && offset <= 100 + overflow) {
            shift = overflow;
        } else if (offset > 100 + overflow && offset <= 100 + overflow * 2) {
            shift = overflow - (offset - (100 + overflow));
        }

        Shape oldClip = g2.getClip(); 
        
        g2.clipRect(x, y - fm.getAscent(), maxW, fm.getHeight() + 10);
        g2.drawString(text, x - shift, y); 
        
        g2.setClip(oldClip); 
    }
}