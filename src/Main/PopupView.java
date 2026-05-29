package Main;

import java.awt.*;


public class PopupView {

   
    public static void draw(Graphics2D g2, UI ui, int w, int h) {
        
        g2.setFont(new Font("Inter", Font.PLAIN, 16));
        FontMetrics fm = g2.getFontMetrics();

        String msg = "Are you sure you want to exit Vibezz?";
        int textW = fm.stringWidth(msg);

        int pad = 30;
        int popupW = textW + (pad * 2);
        int popupH = 110;

        int x = (w - popupW) / 2;
        int y = (h - popupH) / 2;

        g2.setColor(Color.decode("#313338"));
        g2.fillRoundRect(x, y, popupW, popupH, 30, 30);

        g2.setColor(Color.WHITE);
        g2.drawString(msg, x + pad, y + 40);

        int btnW = 60;
        int btnH = 30;
        int space = 30; 
        
        int startX = x + (popupW - (btnW * 2 + space)) / 2;
        int btnY = y + 60;

        g2.setColor(Color.decode("#BB86FC"));
        g2.fillRoundRect(startX, btnY, btnW, btnH, 15, 15);
        
        g2.setColor(Color.WHITE);
        g2.drawString("Yes", startX + (btnW - fm.stringWidth("Yes")) / 2, btnY + 20);
        
        if (ui.exitYesButton != null) ui.exitYesButton.setBounds(startX, btnY, btnW, btnH);

      
        int noX = startX + btnW + space;
        
        g2.setColor(Color.decode("#BB86FC"));
        g2.fillRoundRect(noX, btnY, btnW, btnH, 15, 15);
        
        g2.setColor(Color.WHITE);
        g2.drawString("No", noX + (btnW - fm.stringWidth("No")) / 2, btnY + 20);
        
        if (ui.exitNoButton != null) ui.exitNoButton.setBounds(noX, btnY, btnW, btnH);
    }
}