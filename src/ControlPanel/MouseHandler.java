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

        ArrayList<Song> playlist = ui.musicHandler.getPlaylist();

        // 2. Check Play/Pause Button
        if (ui.playPauseBounds.contains(x, y)) {
            if (playlist.isEmpty()) {
                ui.musicHandler.loadDynamicPlaylist();
                if (!playlist.isEmpty()) {
                    ui.currentSongIndex = 0;
                    ui.audioEngine.playTrack(playlist.get(ui.currentSongIndex).getAudioPath());
                }
            } else {
                if (ui.audioEngine.isPlaying()) {
                    ui.audioEngine.pauseTrack();
                } else {
                    ui.audioEngine.resumeTrack();
                    if (!ui.audioEngine.isPlaying()) {
                        ui.audioEngine.playTrack(playlist.get(ui.currentSongIndex).getAudioPath());
                    }
                }
            }
            panel.repaint(); 
            return;
        }

        // 3. Check Skip Forward Button
        if (ui.skipFwdBounds.contains(x, y)) {
            if (!playlist.isEmpty()) {
                // Math to safely loop back to 0 if we hit the end of the playlist
                ui.currentSongIndex = (ui.currentSongIndex + 1) % playlist.size();
                ui.audioEngine.playTrack(playlist.get(ui.currentSongIndex).getAudioPath());
                panel.repaint();
            }
            return;
        }

        // 4. Check Skip Back Button
        if (ui.skipBackBounds.contains(x, y)) {
            if (!playlist.isEmpty()) {
                // Math to safely loop to the last song if we hit the beginning
                ui.currentSongIndex = (ui.currentSongIndex - 1 + playlist.size()) % playlist.size();
                ui.audioEngine.playTrack(playlist.get(ui.currentSongIndex).getAudioPath());
                panel.repaint();
            }
            return;
        }

        // 5. Check Repeat Button
        if (ui.repeatBounds.contains(x, y)) {
            ui.isRepeat = !ui.isRepeat; // Toggle on/off
            panel.repaint();
            return;
        }

        // 6. Check Shuffle Button
        if (ui.shuffleBounds.contains(x, y)) {
            ui.isShuffle = !ui.isShuffle; // Toggle on/off
            panel.repaint();
            return;
        }



        // 7. Check normal backend buttons
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