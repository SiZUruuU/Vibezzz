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
    public boolean isDraggingProgress = false;
    public double dragProgress = 0.0;
    public int marqueeTick = 0;

    // --- BUTTON LISTS FOR MOUSEHANDLER ---
    public ArrayList<ButtonManager> backEndButtons = new ArrayList<>();
    private ArrayList<ButtonManager> popupButtons = new ArrayList<>();
    // --- NEW: Active Queue & Persistence ---
    public ArrayList<ControlPanel.Song> activePlayingList = new ArrayList<>();
    public ArrayList<ControlPanel.Song> getActiveList() {
        return (activePlayingList != null && !activePlayingList.isEmpty()) ? activePlayingList : musicHandler.getPlaylist();
    }
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
    public String searchText = "";
    
    public int playlistScrollOffset = 0;
    public int maxPlaylistScrollOffset = 0;
    public int playlistViewportH = 0;
    
    public boolean searchBarFocused = false;
    public Rectangle searchBarBounds = new Rectangle();
    public boolean showCursor = true;
    public long lastCursorBlink = System.currentTimeMillis();
    public boolean insidePlaylistView = false; 
    public boolean isAddingToPlaylist = false; 
    public ButtonManager backButton; 

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
        audioEngine = new AudioEngine();
        musicHandler = new MusicHandler();
        
        loadPlaylists(); // Load playlists immediately after music handler boots

        // Bonus: Automatically trigger the Skip button when a track naturally finishes!
        audioEngine.setTrackEndCallback(() -> {
            if (skipFwdButton != null) skipFwdButton.execute(0, 0); 
        });


        // Repaint loop for the progress bar and marquee animations
        Timer timer = new Timer(50, e -> {
            marqueeTick++;
            panel.repaint(); // Always repaint to keep marquee moving
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
        backButton = new ControlPanel.Buttons.BackButton(panel, this);
        backEndButtons.add(backButton);


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
        PlaylistView.draw(g2, this, w, h);
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

        //Debugger
        for (ButtonManager btn : backEndButtons) {
                if (btn != null) btn.drawDebug(g2);
            }



    }

    public void refreshPlaylistButtons() {

        backEndButtons.removeIf(b -> b instanceof ControlPanel.Buttons.PlaylistClicker || b instanceof ControlPanel.SongClicker);

        // Rebuild the menu hitboxes
        for (String name : createdPlaylists) {
            backEndButtons.add(new ControlPanel.Buttons.PlaylistClicker(panel, this, name));
        }

        if (insidePlaylistView && selectedPlaylistName != null && !selectedPlaylistName.isEmpty()) {
            ArrayList<ControlPanel.Song> songs = playlistSongs.get(selectedPlaylistName);
            if (songs != null) {
                for (ControlPanel.Song s : songs) {
                    backEndButtons.add(new ControlPanel.SongClicker(panel, this, s));
                }
            }
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

    public void drawMarqueeText(Graphics2D g2, String text, int x, int y, int maxW) {
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(text);

        if (textW <= maxW) {
            g2.drawString(text, x, y);
            return;
        }

        int overflow = textW - maxW;
        int cycle = overflow + 100; // Adds a 50px delay at start and end
        int offset = (marqueeTick / 2) % (cycle * 2); 

        // Ping-pong math
        int shift = 0;
        if (offset > 50 && offset <= 50 + overflow) {
            shift = offset - 50;
        } else if (offset > 50 + overflow && offset <= 100 + overflow) {
            shift = overflow;
        } else if (offset > 100 + overflow && offset <= 100 + overflow * 2) {
            shift = overflow - (offset - (100 + overflow));
        }

        Shape oldClip = g2.getClip(); 
        
        // FIX: Use clipRect() to INTERSECT with the LibraryView's scroll limits
        // instead of setClip() which was completely overriding them!
        g2.clipRect(x, y - fm.getAscent(), maxW, fm.getHeight() + 10);
        
        g2.drawString(text, x - shift, y);
        
        // Restores the original limits after drawing this one string
        g2.setClip(oldClip); 
    }

    public void savePlaylists() {
        try (java.io.PrintWriter out = new java.io.PrintWriter("vibezz_playlists.txt")) {
            for (String pName : createdPlaylists) {
                out.println("[PLAYLIST] " + pName);
                ArrayList<ControlPanel.Song> songs = playlistSongs.get(pName);
                if (songs != null) {
                    for (ControlPanel.Song s : songs) {
                        out.println(s.getAudioPath()); // We only need to save the file path!
                    }
                }
            }
        } catch (Exception e) { }
    }

    public void loadPlaylists() {
        createdPlaylists.clear();
        playlistSongs.clear();
        try {
            java.io.File file = new java.io.File("vibezz_playlists.txt");
            if (!file.exists()) return;
            
            java.util.Scanner scanner = new java.util.Scanner(file);
            String currentPlaylist = null;
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                
                if (line.startsWith("[PLAYLIST] ")) {
                    currentPlaylist = line.substring(11);
                    createdPlaylists.add(currentPlaylist);
                    playlistSongs.put(currentPlaylist, new ArrayList<>());
                } else if (currentPlaylist != null) {
                    // Match the saved path to the actual Song objects loaded by MusicHandler
                    for (ControlPanel.Song s : musicHandler.getPlaylist()) {
                        if (s.getAudioPath().equals(line)) {
                            playlistSongs.get(currentPlaylist).add(s);
                            break;
                        }
                    }
                }
            }
            scanner.close();
            refreshPlaylistButtons();
        } catch (Exception e) {}
    }

}
