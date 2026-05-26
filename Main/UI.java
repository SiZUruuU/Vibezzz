package Main;

import java.awt.*;
public class UI {

    Panel panel;

    public UI(Panel panel) {
        this.panel = panel;
    }

    public void draw(Graphics2D g2) {

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = panel.getWidth();
        int h = panel.getHeight();

        g2.setColor(Color.decode("#051261"));
        g2.fillRect(0, 0, w, h);

        int pad = 25;
        int gap = 15;
        
        int totalUsableWidth = w - (pad * 2) - gap;
        int leftW = (int) (totalUsableWidth * 0.55); 
        int rightW = totalUsableWidth - leftW;

        int searchBarY = pad + 10;
        int searchBarH = 32;
        int contentY = searchBarY + searchBarH + 20;
        int contentH = h - contentY - pad;

        g2.setColor(Color.decode("#0044b3"));
        int searchBarW = (int) (leftW * 1.0); 
        g2.fillRoundRect(pad, searchBarY, searchBarW, searchBarH, 15, 15);
  
        g2.setColor(Color.decode("#010626"));
        g2.fillRoundRect(pad, contentY, leftW, contentH, 25, 25);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Impact", Font.PLAIN, 16));
        g2.drawString("LIBRARY", pad + 55, contentY + 32);
    
        int rightX = pad + leftW + gap;
        int subH = (contentH - gap) / 2; 
     
        g2.setColor(Color.decode("#010626"));
        g2.fillRoundRect(rightX, contentY, rightW, subH, 25, 25);
              
        g2.setColor(Color.WHITE);
        g2.drawString("PLAYLISTS", rightX + 50, contentY + 32);
        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Impact", Font.BOLD, 14));
        g2.drawString("None", rightX + (rightW / 2) - 18, contentY + (subH / 2) + 5);

        int playerY = contentY + subH + gap;
        g2.setColor(Color.decode("#010626"));
        g2.fillRoundRect(rightX, playerY, rightW, subH, 25, 25);
       
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Impact", Font.PLAIN, 16));
        g2.drawString("PLAYER", rightX + 20, playerY + 32);
        
    }
    public void imageDraw(Graphics2D g2) {
    }
}
