package ControlPanel;

import Main.Panel;
import Main.UI;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Handles all keyboard input for the application.
 * This class acts as a global listener, intercepting keystrokes to manage 
 * the custom search bar logic (typing and backspacing) as well as global 
 * shortcuts (like pressing ESC to trigger the exit popup).
 */
public class KeyInputHandler implements KeyListener {

    private UI ui;
    private Panel panel;

    /**
     * Constructs the KeyInputHandler.
     * @param panel The main application panel used to refresh the screen.
     * @param ui    The main UI state manager.
     */
    public KeyInputHandler(Panel panel, UI ui) {
        this.ui = ui;
        this.panel = panel;
    }

    /**
     * Triggered when a key is pressed and released, producing a valid character.
     * Used primarily to capture raw text input for the search bar.
     * @param e The KeyEvent containing the typed character.
     */
    @Override
    public void keyTyped(KeyEvent e) {
        
        // 1. Guard clause: Only capture text if the user actually clicked the search bar
        if (!ui.searchBarFocused) return;
        
        char c = e.getKeyChar();
        
        // 2. Input Validation: Only allow letters, numbers, and spaces.
        // This prevents users from accidentally typing invisible control characters or weird symbols.
        if (Character.isLetterOrDigit(c) || Character.isWhitespace(c)) {            
            ui.searchText += c;
            panel.repaint(); // Update the screen to show the newly typed character
        }
    }

    /**
     * Triggered the exact moment a physical key is pressed down.
     * Used for capturing system commands (ESC, Backspace) before they produce a character.
     * @param e The KeyEvent containing the hardware key code.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        
        int code = e.getKeyCode();
        
        // --- GLOBAL SHORTCUTS ---
        // Pressing Escape toggles the Exit confirmation popup from anywhere in the app
        if (code == KeyEvent.VK_ESCAPE) {
            ui.exitInquiry(); 
        }

        // --- SEARCH BAR SHORTCUTS ---
        // Guard clause: Stop here if the search bar is not active
        if (!ui.searchBarFocused) return;
        
        // Handle the Backspace key to delete characters
        if (code == KeyEvent.VK_BACK_SPACE && !ui.searchText.isEmpty()) {
            
            // Delete the last character by taking a substring of everything except the very last index
            ui.searchText = ui.searchText.substring(0, ui.searchText.length() - 1);
            panel.repaint(); // Update the screen to reflect the deleted character
        }
    }

    /**
     * Triggered when a key is physically released. 
     * Required by the KeyListener interface, but unused in this application.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        // Intentionally left blank
    }
}