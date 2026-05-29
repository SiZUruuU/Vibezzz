package Main;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Handles the rendering of the settings/volume overlay.
 * This View class acts as a contextual dropdown menu that appears when the user 
 * clicks the Settings gear. It draws the volume slider UI and physically maps 
 * the interactive hitbox to the calculated layout boundaries.
 */
public class VolumeView {
    
    /**
     * The main render method called by the UI repaint loop when the settings state is active.
     * @param g2 The Graphics2D context used to draw shapes and text.
     * @param ui The main UI state manager.
     * @param w  The current width of the application window.
     * @param h  The current height of the application window.
     */
    public static void draw(Graphics2D g2, UI ui, int w, int h) {

        int pad = 25;
        
        // --- LAYOUT MATH ---
        // Setup dropdown layout bounds to sit elegantly in the top-right corner
        int boxW = 200;  
        int boxH = 46;   
        int boxX = w - pad - boxW; 
        int boxY = 55; // Sits neatly right below the 50px custom header bar
        
        // 1. DRAW CONTAINER BACKGROUND
        // Draws the main background of the dropdown box
        g2.setColor(Color.decode("#2B2D31")); 
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 12, 12); 
        
        // Draws a subtle lighter border outline around the box to help it pop against the background
        g2.setColor(Color.decode("#3F4147")); 
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 12, 12);
        
        // --- INNER COMPONENT MATH ---
        int iconSize = 16;
        int innerPadding = 15;
        
        int iconX = boxX + innerPadding;
        int iconY = boxY + (boxH - iconSize) / 2;
        
        int sliderX = iconX + iconSize + 10; 
        int sliderH = 6;
        int sliderY = boxY + (boxH - sliderH) / 2; 
        int sliderW = boxW - (sliderX - boxX) - innerPadding; 
        
        // 2. DRAW SPEAKER DECORATION
        if (ui.iconVolUp != null) {
            g2.drawImage(ui.iconVolUp, iconX, iconY, iconSize, iconSize, null);
        }
        
        // 3. DRAW BACKGROUND TRACK RAIL
        // This represents the "empty" portion of the volume bar
        g2.setColor(Color.decode("#4E5058")); 
        g2.fillRoundRect(sliderX, sliderY, sliderW, sliderH, sliderH, sliderH);
        
        // CRITICAL MAP: Inject the live calculated layout boundaries into your slider's hitbox
        // The Y coordinate is pulled up by 5 pixels, and the height expanded to 16, 
        // to make the invisible clickable area larger and more forgiving for the user.
        if (ui.volumeSlider != null) {
            ui.volumeSlider.setBounds(sliderX, sliderY - 5, sliderW, 16);
        }
        
        // 4. DRAW ACTIVE VOLUME FILL
        // Query the audio engine for the actual hardware volume decimal to calculate the fill width
        float currentVol = ui.audioEngine.getVolume(); 
        int fillW = (int) (sliderW * currentVol);
        
        g2.setColor(Color.decode("#BB86FC")); // Custom Purple accent color
        g2.fillRoundRect(sliderX, sliderY, fillW, sliderH, sliderH, sliderH);
        
        // 5. DRAW KNOB INDICATOR
        // Mathematically center the knob exactly at the edge of the active fill
        int knobSize = 12;
        int knobX = sliderX + fillW - (knobSize / 2);
        int knobY = sliderY + (sliderH / 2) - (knobSize / 2);
        
        g2.setColor(Color.white);
        g2.fillOval(knobX, knobY, knobSize, knobSize);
    }
}