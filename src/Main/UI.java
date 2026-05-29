package Main;

import ControlPanel.AudioEngine;
import ControlPanel.ButtonManager;
import ControlPanel.Buttons.*;
import ControlPanel.MusicHandler; // Assumes you created the specific button classes here
import java.awt.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.Timer;

public class UI {

    public Panel panel;
    public boolean exit = false;
    public boolean escInq = false;

    // --- BUTTON LISTS FOR MOUSEHANDLER ---
    public ArrayList<ButtonManager> backEndButtons = new ArrayList<>();
    private ArrayList<ButtonManager> popupButtons = new ArrayList<>();

    public java.util.Map<String, ArrayList<ControlPanel.Song>> playlistSongs = new java.util.HashMap<>();

    // --- SMART BUTTON INSTANCES ---
    // These start with 0 bounds; PlayerView and PopupView will map them dynamically!
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

    // --- OBJECTS ---

    public VolumeSlider volumeSlider;
    
    // --- STATE VARIABLES ---
    public boolean isRepeat = false;
    public boolean isShuffle = false;
    public int currentSongIndex = 0;
    public boolean settingsPressed = false;
    public boolean isDraggingVolume = false;
    public int scrollOffset = 0;       
    public int maxScrollOffset = 0;   
    public int libraryViewportH = 0;
    public boolean insidePlaylistView = false; // Tracks if we are "inside" a playlist window
    
    // --- HANDLERS ---
    public AudioEngine audioEngine = new AudioEngine();
    public MusicHandler musicHandler = new MusicHandler();

    // --- ASSETS (Made public so Views can read them) ---
    public Image iconLibrary, iconAlbum, iconArtist, iconSong;
    public Image iconPlay, iconPause, iconFastFwd, iconMute;
    public Image imgProgressBar, imgProgressKnob;
    public Image iconRepeat, iconRewind, iconSearch, iconSettings, iconShuffle, iconSkipBack, iconSkipFwd;
    public Image iconVolDown, iconVolUp;

    public UI(Panel panel) {
        this.panel = panel;
        loadAssets();
        initializeButtons();

        // Repaint loop for the progress bar animation
        Timer timer = new Timer(50, e -> {
            if (audioEngine.isPlaying()) {
                panel.repaint();
            }
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
        volumeSlider = new VolumeSlider(panel, this); // Instantiate the slider
        addPlaylistButton = new AddPlaylistButton(panel, this); //Instantiates the Playlist butto
        addSongButton = new AddSongButton(panel, this);


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

        exitYesButton = new ExitPopupButton(panel, this, true);
        exitNoButton = new ExitPopupButton(panel, this, false);
        
        popupButtons.add(exitYesButton);
        popupButtons.add(exitNoButton);

        audioEngine.setTrackEndCallback(() -> {
            if (skipFwdButton != null) {
                skipFwdButton.execute(0, 0);
            }
        });
    }

    public ArrayList<String> createdPlaylists = new ArrayList<>();
    public String selectedPlaylistName = "";   //Playlist tracking

    public ArrayList<ButtonManager> getBackendButtons() {
        return backEndButtons;
    }

    public ArrayList<ButtonManager> getPopupButtons() {
        return popupButtons;
    }

    //  MAIN DRAW (Delegates everything to the Views)
    public void draw(Graphics2D g2) {
        panel.setBackground(Color.decode("#1E1F22"));

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = panel.getWidth();
        int h = panel.getHeight();

        // Let the Views handle the heavy lifting!
        LibraryView.draw(g2, this, w, h);
        PlayerView.draw(g2, this, w, h);

        if(settingsPressed) {
            VolumeView.draw(g2, this, w, h);
        }

        // --- NEW: Trigger the ESC hover UI ---
        if (escInq) {
            drawExit(g2);
        }

        if (exit) {
            PopupView.draw(g2, this, w, h);
        }
    }

    // Add this method to UI.java
    public void refreshPlaylistButtons() {
        // Remove old ones
        backEndButtons.removeIf(b -> b instanceof ControlPanel.Buttons.PlaylistClicker);

        // Add new ones with names
        for (String name : createdPlaylists) {
            backEndButtons.add(new ControlPanel.Buttons.PlaylistClicker(panel, this, name));
        }
    }

    public void exitInquiry() {
        exit = !exit;
        panel.repaint();
    }

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
        public void drawExit(Graphics2D g2) {
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

    public static String getClampedText(Graphics2D g2, String text, int maxWidth) {
        if (text == null || text.isEmpty()) return "";

        FontMetrics fm = g2.getFontMetrics();
    
        // If the text naturally fits inside the allowed boundary, return it as-is
        if (fm.stringWidth(text) <= maxWidth) {
        return text;
        }
    
        String ellipsis = "...";
        int ellipsisWidth = fm.stringWidth(ellipsis);
    
        // Safety check: if the column is narrower than the ellipsis itself, return empty
        if (maxWidth <= ellipsisWidth) return "";
    
        // Build the string up character by character until it hits the limit
        StringBuilder truncated = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            // Test if adding the next character + the ellipsis exceeds our boundary
            if (fm.stringWidth(truncated.toString() + c + ellipsis) > maxWidth) {
            break;
            }
            truncated.append(c);
        }
    
        return truncated.toString() + ellipsis;
    }
}