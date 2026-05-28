package Main;

import java.awt.Color;
import java.awt.Graphics2D;

public class VolumeView {
    
    public static void draw(Graphics2D g2, UI ui, int w, int h){

        int pad = 25;
        
        // Setup dropdown layout bounds
        int boxW = 200;  
        int boxH = 46;   
        int boxX = w - pad - boxW; 
        int boxY = 55; // Sits neatly right below your 50px header bar
        
        // Draw the sleek box container background
        g2.setColor(Color.decode("#2B2D31")); 
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 12, 12); 
        
        g2.setColor(Color.decode("#3F4147")); 
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 12, 12);
        
        int iconSize = 16;
        int innerPadding = 15;
        
        int iconX = boxX + innerPadding;
        int iconY = boxY + (boxH - iconSize) / 2;
        
        int sliderX = iconX + iconSize + 10; 
        int sliderH = 6;
        int sliderY = boxY + (boxH - sliderH) / 2; 
        int sliderW = boxW - (sliderX - boxX) - innerPadding; 
        
        // Draw volume speaker decoration
        if (ui.iconVolUp != null) {
            g2.drawImage(ui.iconVolUp, iconX, iconY, iconSize, iconSize, null);
        }
        
        // Draw Track Background Rail
        g2.setColor(Color.decode("#4E5058")); 
        g2.fillRoundRect(sliderX, sliderY, sliderW, sliderH, sliderH, sliderH);
        
        // CRITICAL MAP: Inject the live calculated layout boundaries into your slider's hitbox
        if (ui.volumeSlider != null) {
            ui.volumeSlider.setBounds(sliderX, sliderY - 5, sliderW, 16);
        }
        
        // Draw Active Volume Fill
        float currentVol = ui.audioEngine.getVolume(); 
        int fillW = (int) (sliderW * currentVol);
        
        g2.setColor(Color.decode("#1DB954")); // Green accent color
        g2.fillRoundRect(sliderX, sliderY, fillW, sliderH, sliderH, sliderH);
        
        // Draw Knob Indicator Handle
        int knobSize = 12;
        int knobX = sliderX + fillW - (knobSize / 2);
        int knobY = sliderY + (sliderH / 2) - (knobSize / 2);
        
        g2.setColor(Color.white);
        g2.fillOval(knobX, knobY, knobSize, knobSize);
    }
}