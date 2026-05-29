package Main;

import java.awt.*;

/**
 * Handles the rendering of the exit confirmation overlay.
 * This View class acts as a modal, drawing a centered popup box over the rest 
 * of the application. It dynamically calculates its own dimensions based on the 
 * text content and maps the hitboxes for the Yes/No confirmation buttons.
 */
public class PopupView {

    /**
     * The main render method called by the UI repaint loop when the exit state is active.
     * @param g2 The Graphics2D context used to draw shapes and text.
     * @param ui The main UI state manager.
     * @param w  The current width of the application window.
     * @param h  The current height of the application window.
     */
    public static void draw(Graphics2D g2, UI ui, int w, int h) {
        
        g2.setFont(new Font("Inter", Font.PLAIN, 16));
        FontMetrics fm = g2.getFontMetrics();

        // --- DYNAMIC SIZING MATH ---
        String msg = "Are you sure you want to exit Vibezz?";
        int textW = fm.stringWidth(msg);

        // Calculate the popup's dimensions based on the string length plus some padding
        int pad = 30;
        int popupW = textW + (pad * 2);
        int popupH = 110;

        // Perfectly center the popup in the middle of the screen
        int x = (w - popupW) / 2;
        int y = (h - popupH) / 2;

        // 1. DRAW BACKGROUND
        g2.setColor(Color.decode("#313338"));
        g2.fillRoundRect(x, y, popupW, popupH, 30, 30);

        // 2. DRAW TEXT
        g2.setColor(Color.WHITE);
        g2.drawString(msg, x + pad, y + 40);

        // --- BUTTON LAYOUT MATH ---
        int btnW = 60;
        int btnH = 30;
        int space = 30; // The gap between the two buttons
        
        // Calculate the starting X coordinate so the group of two buttons is perfectly centered
        int startX = x + (popupW - (btnW * 2 + space)) / 2;
        int btnY = y + 60;

        // 3. DRAW "YES" BUTTON
        g2.setColor(Color.decode("#BB86FC")); // Custom Purple Accent
        g2.fillRoundRect(startX, btnY, btnW, btnH, 15, 15);
        
        g2.setColor(Color.WHITE);
        // Center the word "Yes" mathematically inside the button rectangle
        g2.drawString("Yes", startX + (btnW - fm.stringWidth("Yes")) / 2, btnY + 20);
        
        // Map the invisible hitbox to exactly match the drawn rectangle
        if (ui.exitYesButton != null) ui.exitYesButton.setBounds(startX, btnY, btnW, btnH);

        // 4. DRAW "NO" BUTTON
        int noX = startX + btnW + space;
        
        g2.setColor(Color.decode("#BB86FC"));
        g2.fillRoundRect(noX, btnY, btnW, btnH, 15, 15);
        
        g2.setColor(Color.WHITE);
        // Center the word "No" mathematically inside the button rectangle
        g2.drawString("No", noX + (btnW - fm.stringWidth("No")) / 2, btnY + 20);
        
        // Map the invisible hitbox to exactly match the drawn rectangle
        if (ui.exitNoButton != null) ui.exitNoButton.setBounds(noX, btnY, btnW, btnH);
    }
}