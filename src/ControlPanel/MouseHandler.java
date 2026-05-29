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

/**
 * The master input router for all mouse interactions in the application.
 * By using a single global listener, this class efficiently tracks clicks, drags, 
 * and scrolling, routing the events to the correct UI components based on spatial math.
 * It also handles custom OS-level window dragging for the undecorated frame.
 */
public class MouseHandler implements MouseListener, MouseMotionListener, MouseWheelListener {

    private Panel panel;
    private UI ui;
    
    // State variables for calculating drag distances
    private Point initialClick;
    private boolean cursorOnTop = false;

    /**
     * Constructs the MouseHandler.
     * @param panel The main application panel used to refresh the screen.
     * @param ui    The main UI state manager.
     */
    public MouseHandler(Panel panel, UI ui) {
        this.panel = panel;
        this.ui = ui;
    }

    /**
     * Triggered when the mouse is pressed and released without moving.
     * Evaluates clicks based on "Z-Index priority" (Top layers block bottom layers).
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // Priority 1: The Exit Popup overlay. 
        // If active, it traps all clicks and prevents interaction with the app beneath it.
        if (ui.exit) {
            for (ButtonManager button : ui.getPopupButtons()) {
                if (button.collisionCheck(x, y)) {
                    button.execute(x, y);
                    break;
                }
            }
            return; 
        }

        // Priority 2: Standard backend buttons and lists
        for (ButtonManager button : ui.getBackendButtons()) {
            if (button.collisionCheck(x, y)) {
                button.execute(x, y);
                break;
            }
        }

        // Priority 3: The Search Bar Focus Hook
        Point p = e.getPoint();
        if (ui.searchBarBounds.contains(p)) {
            ui.searchBarFocused = true;
        } else {
            ui.searchBarFocused = false;
        }
    }

    /**
     * Triggered the instant the mouse button goes down.
     * Used to establish the starting coordinates for drag operations.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // Cache the exact pixel the user clicked for window-dragging math later
        initialClick = e.getPoint();

        // Check if the user is grabbing the Volume Knob
        if (ui.settingsPressed && ui.volumeSlider != null && ui.volumeSlider.collisionCheck(e.getX(), e.getY())) {
            ui.isDraggingVolume = true;
            ui.volumeSlider.updateVolume(e.getX()); 
        }

        // Check if the user is grabbing the Playback Progress Knob
        if (ui.progressBarSeeker != null && ui.progressBarSeeker.collisionCheck(e.getX(), e.getY())) {
            ui.isDraggingProgress = true;
            ((ControlPanel.Buttons.ProgressBarSeeker) ui.progressBarSeeker).updateVisual(e.getX());
        }
    }

    /**
     * Triggered continuously while the mouse is moving with the button held down.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        
        // 1. Update Volume slider visually and logically
        if (ui.isDraggingVolume && ui.volumeSlider != null) {
            ui.volumeSlider.updateVolume(e.getX());
            return; 
        }

        // 2. Update Progress slider visually (Does NOT seek audio yet to prevent glitching)
        if (ui.isDraggingProgress && ui.progressBarSeeker != null) {
            ((ControlPanel.Buttons.ProgressBarSeeker) ui.progressBarSeeker).updateVisual(e.getX());
            return;
        }

        // 3. Custom OS Window Dragging (For undecorated frames)
        if (initialClick == null) return;
        Window window = SwingUtilities.getWindowAncestor(panel);
       
        // If the user clicked the top 50 pixels (header area), drag the entire app window
        if (cursorOnTop && window != null) {
            int windowX = window.getLocation().x;
            int windowY = window.getLocation().y;
            
            // Calculate how far the mouse has moved since the initial click
            int xMoved = e.getX() - initialClick.x;
            int yMoved = e.getY() - initialClick.y;
            
            // Push the physical OS window to the new coordinates
            window.setLocation(windowX + xMoved, windowY + yMoved);
        }
    }

    /**
     * Triggered when the mouse button is physically released.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        // Reset dragging states
        initialClick = null; 
        ui.isDraggingVolume = false;

        // If the user was scrubbing the progress bar, commit the seek command to the audio engine now
        if (ui.isDraggingProgress) {
            ui.isDraggingProgress = false;
            ui.audioEngine.seek(ui.dragProgress);
        }
    }

    /**
     * Triggered whenever the mouse moves across the panel without clicking.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        int y = e.getY();
        
        // Detect if the mouse is hovering over the top 50 pixels (the custom title bar zone)
        if (y <= 50) {
            panel.setHeaderHover(true); 
            cursorOnTop = true;
        } else {
            panel.setHeaderHover(false); 
            cursorOnTop = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {} // Unused but required by MouseListener

    @Override
    public void mouseExited(MouseEvent e) {} // Unused but required by MouseListener

    /**
     * Triggered when the user scrolls the mouse wheel.
     * Intelligently routes the scroll event to either the Library or the Playlist view 
     * based on which half of the screen the mouse is hovering over.
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        
        // Disable scrolling if the settings/volume overlay is active
        if (!ui.settingsPressed) {
            int scrollSpeed = 15; 
            
            // Dynamically calculate where the dividing line between the two UI boxes is located
            int w = panel.getWidth();
            int pad = 25;
            int gap = 20;
            int totalWidth = w - (pad * 2) - gap;
            int leftW = (int) (totalWidth * 0.60); // Library takes up 60% of the screen
            int dividerX = pad + leftW + (gap / 2);

            // Spatial check: If mouse X is left of the divider, scroll the Library
            if (e.getX() < dividerX) {
                ui.scrollOffset += e.getWheelRotation() * scrollSpeed;
                
                // Clamp the scroll bounds to prevent scrolling off into the void
                if (ui.scrollOffset < 0) ui.scrollOffset = 0;
                if (ui.scrollOffset > ui.maxScrollOffset) ui.scrollOffset = ui.maxScrollOffset;
            } 
            // Spatial check: If mouse X is right of the divider, scroll the Playlist
            else {
                ui.playlistScrollOffset += e.getWheelRotation() * scrollSpeed;
                
                // Clamp the scroll bounds
                if (ui.playlistScrollOffset < 0) ui.playlistScrollOffset = 0;
                if (ui.playlistScrollOffset > ui.maxPlaylistScrollOffset) ui.playlistScrollOffset = ui.maxPlaylistScrollOffset;
            }

            // Request a screen update to render the lists at their new shifted Y-coordinates
            panel.repaint();
        }
    }
}