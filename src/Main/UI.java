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

    Panel panel;
    public boolean exit = false;
    public boolean escInq = false;

    // --- BUTTON LISTS FOR MOUSEHANDLER ---
    private ArrayList<ButtonManager> backEndButtons = new ArrayList<>();
    private ArrayList<ButtonManager> popupButtons = new ArrayList<>();

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
    
    // --- STATE VARIABLES ---
    public boolean isRepeat = false;
    public boolean isShuffle = false;
    public int currentSongIndex = 0;
    
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
        
        backEndButtons.add(addFolderButton);
        backEndButtons.add(libraryListClicker);
        backEndButtons.add(playPauseButton);
        backEndButtons.add(progressBarSeeker);
        backEndButtons.add(skipFwdButton);
        backEndButtons.add(skipBackButton);
        backEndButtons.add(repeatButton);
        backEndButtons.add(shuffleButton);

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

        // --- NEW: Trigger the ESC hover UI ---
        if (escInq) {
            drawExit(g2);
        }

        if (exit) {
            PopupView.draw(g2, this, w, h);
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
}