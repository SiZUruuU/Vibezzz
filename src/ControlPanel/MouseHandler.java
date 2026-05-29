package ControlPanel;

import Main.Panel;
import Main.UI;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.SwingUtilities;

// Implement MouseMotionListener here
public class MouseHandler implements MouseListener, MouseMotionListener, MouseWheelListener {

    Panel panel;
    UI ui;
    private Point initialClick;
    private boolean cursorOnTop = false;

    public MouseHandler(Panel panel, UI ui){
        this.panel = panel;
        this.ui = ui;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // 1. If Exit Popup is active, ONLY check popup buttons
        if (ui.exit) {
            for (ButtonManager button : ui.getPopupButtons()) {
                if (button.collisionCheck(x, y)) {
                    button.execute(x, y);
                    break;
                }
            }
            return; 
        }

        // 2. Otherwise, check all backend interactables
        for (ButtonManager button : ui.getBackendButtons()) {
            if (button.collisionCheck(x, y)) {
                button.execute(x, y);
                break;
            }
        }

        // Search Bar
        Point p = e.getPoint();

        if(ui.searchBarBounds.contains(p)) {
            ui.searchBarFocused = true;
        } else {
            ui.searchBarFocused = false;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        initialClick = e.getPoint();

        if (ui.settingsPressed && ui.volumeSlider != null && ui.volumeSlider.collisionCheck(e.getX(), e.getY())) {
            ui.isDraggingVolume = true;
            ui.volumeSlider.updateVolume(e.getX()); 
        }

        // FIX: Detect Progress Bar Grab
        if (ui.progressBarSeeker != null && ui.progressBarSeeker.collisionCheck(e.getX(), e.getY())) {
            ui.isDraggingProgress = true;
            ((ControlPanel.Buttons.ProgressBarSeeker) ui.progressBarSeeker).updateVisual(e.getX());
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (ui.isDraggingVolume && ui.volumeSlider != null) {
            ui.volumeSlider.updateVolume(e.getX());
            return; 
        }

        // FIX: Drag progress visually
        if (ui.isDraggingProgress && ui.progressBarSeeker != null) {
            ((ControlPanel.Buttons.ProgressBarSeeker) ui.progressBarSeeker).updateVisual(e.getX());
            return;
        }

        if (initialClick == null) return;
        Window window = SwingUtilities.getWindowAncestor(panel);
       
        if(cursorOnTop && window != null){
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
        ui.isDraggingVolume = false;

        // FIX: Actually seek the audio file when they let go of the mouse
        if (ui.isDraggingProgress) {
            ui.isDraggingProgress = false;
            ui.audioEngine.seek(ui.dragProgress);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

        int y = e.getY();

        if(y <= 50){panel.setHeaderHover(true); cursorOnTop = true;}
        else{panel.setHeaderHover(false); cursorOnTop = false;}
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (!ui.settingsPressed) {
            int scrollSpeed = 15; 
            
            // Calculate where the dividing line between the two boxes is
            int w = panel.getWidth();
            int pad = 25;
            int gap = 20;
            int totalWidth = w - (pad * 2) - gap;
            int leftW = (int) (totalWidth * 0.60);
            int dividerX = pad + leftW + (gap / 2);

            // If mouse is on the LEFT, scroll the Library
            if (e.getX() < dividerX) {
                ui.scrollOffset += e.getWheelRotation() * scrollSpeed;
                if (ui.scrollOffset < 0) ui.scrollOffset = 0;
                if (ui.scrollOffset > ui.maxScrollOffset) ui.scrollOffset = ui.maxScrollOffset;
            } 
            // If mouse is on the RIGHT, scroll the Playlist
            else {
                ui.playlistScrollOffset += e.getWheelRotation() * scrollSpeed;
                if (ui.playlistScrollOffset < 0) ui.playlistScrollOffset = 0;
                if (ui.playlistScrollOffset > ui.maxPlaylistScrollOffset) ui.playlistScrollOffset = ui.maxPlaylistScrollOffset;
            }

            panel.repaint();
        }
    }
}
