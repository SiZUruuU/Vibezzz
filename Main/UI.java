package Main;

import java.awt.*;
import javax.swing.*;

public class UI {

    Panel panel;
    Graphics2D g2;
    public boolean exit = false;

    public UI(Panel panel){
        this.panel = panel;
    }

    public void draw(Graphics2D g2){
        panel.setBackground(Color.decode("#37008f"));
        drawPage(g2);
        if(exit){ drawExitInquiry(g2);}

    }

    public void imageDraw(Graphics2D g2){

    }

    public void exitInquiry(){

        if(!exit){exit = true;}
        else{exit = false;}
        
        panel.repaint();
    }

    public void drawExitInquiry(Graphics2D g2){

        g2.setColor(Color.decode("#6f01ff"));
        g2.fillRoundRect(215, 310, 215, 80, 30, 30);

        g2.setColor(Color.BLACK);
        g2.drawString("Are you sure you want to exit Vibezz?", 225, 330);

        g2.setColor(Color.decode("#3b0285"));
        g2.fillRoundRect(265, 355, 40, 20, 10, 10);

        g2.setColor(Color.BLACK);
        g2.drawString("Yes", 275, 370);

        g2.setColor(Color.decode("#3b0285"));
        g2.fillRoundRect(345, 355, 40, 20, 10, 10);

        g2.setColor(Color.BLACK);
        g2.drawString("No", 355, 370);
    }

    public void drawPage(Graphics2D g2){
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
}
