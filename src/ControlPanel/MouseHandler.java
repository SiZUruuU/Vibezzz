package ControlPanel;

import Main.Panel;
import Main.UI;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

// Implement MouseMotionListener here
public class MouseHandler implements MouseListener, MouseMotionListener {

    Panel panel;
    UI ui;
    private Point initialClick;

    public MouseHandler(Panel panel, UI ui){
        this.panel = panel;
        this.ui = ui;
    }

@Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // 1. Check Exit Popup First
        if (ui.exit) {
            if (ui.yesButtonBounds.contains(x, y)) {
                System.exit(0);
            } else if (ui.noButtonBounds.contains(x, y)) {
                panel.exitInquiry();
            }
            return;
        }

        // 2. Check Play/Pause Button
        if (ui.playPauseBounds.contains(x, y)) {
            ArrayList<Song> playlist = ui.musicHandler.getPlaylist();
            
            if (playlist.isEmpty()) {
                // If playlist is empty, open the Folder Picker dialog!
                ui.musicHandler.loadDynamicPlaylist();
                
                // If the user successfully loaded songs, auto-play the first one
                if (!playlist.isEmpty()) {
                    ui.currentSongIndex = 0;
                    ui.audioEngine.playTrack(playlist.get(ui.currentSongIndex).getAudioPath());
                }
            } else {
                // Play / Pause / Resume Logic
                if (ui.audioEngine.isPlaying()) {
                    ui.audioEngine.pauseTrack();
                } else {
                    // Try to resume the track if it was paused
                    ui.audioEngine.resumeTrack();
                    
                    // If resumeTrack did nothing (meaning no song was loaded yet), play from start
                    if (!ui.audioEngine.isPlaying()) {
                        ui.audioEngine.playTrack(playlist.get(ui.currentSongIndex).getAudioPath());
                    }
                }
            }
            panel.repaint(); // Force UI to update the Play/Pause icon visually
            return;
        }

        // 3. Check normal backend buttons (like the top left Exit button)
        if (!ui.exit) {
            for (ButtonManager button : ui.getBackendButtons()) {
                if (button.collisionCheck(x, y)) {
                    button.execute();
                    break;
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        initialClick = e.getPoint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (initialClick == null) return;

        Window window = SwingUtilities.getWindowAncestor(panel);
        
        if (window != null) {

            int windowX = window.getLocation().x;
            int windowY = window.getLocation().y;

            int xMoved = e.getX() - initialClick.x;
            int yMoved = e.getY() - initialClick.y;


            window.setLocation(windowX + xMoved, windowY + yMoved);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        initialClick = null;
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
    
}