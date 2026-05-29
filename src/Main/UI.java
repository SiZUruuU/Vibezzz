package Main;

import ControlPanel.AudioEngine;
import ControlPanel.ButtonManager;
import ControlPanel.Buttons.*;
import ControlPanel.MusicHandler; 
import java.awt.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.Timer;

/**
 * The central State Manager and UI Orchestrator.
 * This class holds every variable, button instance, and graphical asset used by the app.
 * By centralizing the state here, the various "View" classes (LibraryView, PlayerView, etc.) 
 * can remain entirely stateless—they simply read variables from this class and draw them on screen.
 */
public class UI {

    public Panel panel;
    public boolean exit = false;     // Toggles the Exit Confirmation popup
    public boolean escInq = false;  // Toggles the "Press ESC to Exit" hover badge

    // --- BUTTON HITBOX ROUTING ---
    // The MouseHandler loops through these lists to check for clicks.
    public ArrayList<ButtonManager> backEndButtons = new ArrayList<>();
    private ArrayList<ButtonManager> popupButtons = new ArrayList<>();

    // --- SMART BUTTON INSTANCES ---
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
    
    // --- GLOBAL STATE VARIABLES ---
    public boolean isRepeat = false;
    public boolean isShuffle = false;
    public int currentSongIndex = 0;
    public boolean settingsPressed = false;
    
    public boolean isDraggingVolume = false;
    public boolean isDraggingProgress = false;
    public double dragProgress = 0.0;
    
    // Library Scrolling State
    public int scrollOffset = 0;       
    public int maxScrollOffset = 0;   
    public int libraryViewportH = 0;
    
    // Playlist Scrolling State
    public int playlistScrollOffset = 0;
    public int maxPlaylistScrollOffset = 0;
    public int playlistViewportH = 0;
    
    // Search Bar State
    public String searchText = "";
    public boolean searchBarFocused = false;
    public Rectangle searchBarBounds = new Rectangle();
    public int marqueeTick = 0; // Used to calculate the ping-pong animation for long text
    
    // Playlist Navigation State
    public boolean insidePlaylistView = false; 
    public boolean isAddingToPlaylist = false;
    public String selectedPlaylistName = ""; 
    
    // --- BACKEND HANDLERS ---
    public AudioEngine audioEngine = new AudioEngine();
    public MusicHandler musicHandler = new MusicHandler();

    // --- SHARED GRAPHICAL ASSETS ---
    public Image iconLibrary, iconAlbum, iconArtist, iconSong;
    public Image iconPlay, iconPause, iconFastFwd, iconMute;
    public Image imgProgressBar, imgProgressKnob;
    public Image iconRepeat, iconRewind, iconSearch, iconSettings, iconShuffle, iconSkipBack, iconSkipFwd;
    public Image iconVolDown, iconVolUp;

    /**
     * Constructs the UI Manager, loads all assets, and initializes the background animation timers.
     * @param panel The root Swing panel required for repainting the screen.
     */
    public UI(Panel panel) {
        this.panel = panel;
        
        // Base background color
        this.panel.setBackground(Color.decode("#1E1F22")); 
        
        loadAssets();
        initializeButtons();

        // Register a callback to automatically skip to the next track when the current one finishes
        audioEngine.setTrackEndCallback(() -> {
            if (skipFwdButton != null) {
                skipFwdButton.execute(0, 0); // Simulate a click on the Skip Forward button
            }
        });

        // 50ms Swing Timer to drive the Marquee text animation and keep the UI feeling "alive"
        Timer timer = new Timer(50, e -> {
            marqueeTick++;
            panel.repaint();
        });
        timer.start();
    }

    /**
     * Instantiates all interactive buttons and adds them to the master routing lists.
     */
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

        // Add standard elements to the main interaction layer
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

        // Add popup elements to an isolated interaction layer to block background clicks
        exitYesButton = new ExitPopupButton(panel, this, true);
        exitNoButton = new ExitPopupButton(panel, this, false);
        popupButtons.add(exitYesButton);
        popupButtons.add(exitNoButton);
        
        refreshPlaylistButtons();
    }

    /**
     * Dynamically rebuilds the hitboxes for the dynamic playlist menu.
     * This destroys old hitboxes to prevent memory leaks and ghost clicks, 
     * generating new ones based on whether the user is viewing the main menu or inside a specific list.
     */
    public void refreshPlaylistButtons() {
        // 1. Scrub out any existing dynamic buttons
        backEndButtons.removeIf(b -> b instanceof ControlPanel.Buttons.PlaylistClicker || b instanceof ControlPanel.SongClicker);

        // 2. Generate hitboxes for the Main Menu (Playlist Folders)
        for (String pName : musicHandler.getCreatedPlaylists()) {
            backEndButtons.add(new ControlPanel.Buttons.PlaylistClicker(panel, this, pName));
        }

        // 3. Generate hitboxes for specific songs IF the user is currently looking inside a playlist
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

    /**
     * The master draw pipeline. Called by the Panel's paintComponent method.
     * Delegates rendering tasks to the highly specialized View classes.
     * @param g2 The Graphics2D context.
     */
    public void draw(Graphics2D g2) {
        // Enable high-quality rendering for smooth circles and crisp text
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = panel.getWidth();
        int h = panel.getHeight();

        // --- DRAW LAYERS (Bottom to Top) ---
        LibraryView.draw(g2, this, w, h);
        PlaylistView.draw(g2, this, w, h); 
        PlayerView.draw(g2, this, w, h);

        if (settingsPressed) VolumeView.draw(g2, this, w, h);
        if (exit) PopupView.draw(g2, this, w, h);
        
        // Draw the contextual window-drag hint
        if (escInq && !exit) drawExitBadge(g2);
    }

    /**
     * Toggles the exit confirmation popup.
     */
    public void exitInquiry() {
        exit = !exit;
        panel.repaint();
    }

    /**
     * Loads all graphical assets into memory during application boot.
     */
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
        }
    }

    /**
     * Draws a subtle hover badge at the top of the screen reminding the user 
     * how to access the custom exit popup on an undecorated frame.
     */
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

    /**
     * Renders text that automatically scrolls side-to-side (ping-pong style) if it is 
     * too long to fit within its designated maximum width boundary.
     * @param g2   The Graphics2D context.
     * @param text The string to draw.
     * @param x    The starting X coordinate.
     * @param y    The baseline Y coordinate for the text.
     * @param maxW The maximum pixel width allowed before scrolling triggers.
     */
    public void drawMarqueeText(Graphics2D g2, String text, int x, int y, int maxW) {
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(text);

        // Guard clause: If the text fits perfectly, just draw it normally and stop.
        if (textW <= maxW) {
            g2.drawString(text, x, y);
            return;
        }

        // --- PING-PONG ANIMATION MATH ---
        int overflow = textW - maxW; // How many pixels are bleeding over the edge
        int cycle = overflow + 100;  // Add a 100-tick delay to pause at the edges
        
        // Use the global timer tick and modulo math to loop the animation infinitely
        int offset = (marqueeTick / 2) % (cycle * 2); 

        int shift = 0;
        if (offset > 50 && offset <= 50 + overflow) {
            // Scrolling Left
            shift = offset - 50;
        } else if (offset > 50 + overflow && offset <= 100 + overflow) {
            // Paused at the end of the text
            shift = overflow;
        } else if (offset > 100 + overflow && offset <= 100 + overflow * 2) {
            // Scrolling Right (Back to start)
            shift = overflow - (offset - (100 + overflow));
        }

        // --- SAFE ZONE CLIPPING ---
        Shape oldClip = g2.getClip(); 
        
        // Prevent the text from spilling out into other UI elements while scrolling
        g2.clipRect(x, y - fm.getAscent(), maxW, fm.getHeight() + 10);
        g2.drawString(text, x - shift, y); // Apply the calculated shift
        
        g2.setClip(oldClip); // Restore normal drawing bounds
    }
}