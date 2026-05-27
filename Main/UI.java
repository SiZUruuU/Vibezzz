package Main;

import java.awt.*;
import java.util.ArrayList;

import ControlPanel.ButtonManager;
import ControlPanel.ExitButton;

public class UI {

    Panel panel;
    Graphics2D g2;
    public boolean exit = false;
    int exitX = 265, exitY = 355, exitW = 40, exitH = 20;

    //Arraylist for backend button coordinates
    private ArrayList<ButtonManager> backEndButtons = new ArrayList<>();

    // Added hitboxes for the buttons
    public Rectangle yesButtonBounds = new Rectangle(0, 0, 0, 0);
    public Rectangle noButtonBounds = new Rectangle(0, 0, 0, 0);

    public UI(Panel panel){
        this.panel = panel;

        backEndButtons.add(new ExitButton(exitX, exitY, exitW, exitH, panel, this));
    }

    public ArrayList<ButtonManager> getBackendButtons() {
        return backEndButtons;
    }

    public void draw(Graphics2D g2){
        panel.setBackground(Color.decode("#1E1F22"));
        drawPage(g2);
        if(exit){ drawExitInquiry(g2);}

    }

    public void imageDraw(Graphics2D g2){

    }

    public void drawExitInquiry(Graphics2D g2){
        
        g2.setFont(new Font("Inter", Font.PLAIN, 16));
        FontMetrics fm = g2.getFontMetrics();
        
        String message = "Are you sure you want to exit Vibezz?";
        int textWidth = fm.stringWidth(message);

        
        int paddingX = 30;
        int popupWidth = textWidth + (paddingX * 2);
        int popupHeight = 110; 

        // Center popup
        int panelW = panel.getWidth();
        int panelH = panel.getHeight();
        int popupX = (panelW - popupWidth) / 2;
        int popupY = (panelH - popupHeight) / 2;

        //BG
        g2.setColor(Color.decode("#313338"));
        g2.fillRoundRect(popupX, popupY, popupWidth, popupHeight, 30, 30);
        
        
        g2.setColor(Color.decode("#F2F3F5"));
        int textX = popupX + paddingX;
        int textY = popupY + 40;
        g2.drawString(message, textX, textY);

        // button
        int btnWidth = 60;
        int btnHeight = 30;
        int btnSpacing = 30; // Space between Yes and No
        int totalBtnWidth = (btnWidth * 2) + btnSpacing;
        
        // Center the button
        int btnStartX = popupX + (popupWidth - totalBtnWidth) / 2;
        int btnY = popupY + 60;

        // YES
        int yesX = btnStartX;
        g2.setColor(Color.decode("#5865F2"));
        g2.fillRoundRect(yesX, btnY, btnWidth, btnHeight, 15, 15);
        
        // Update the YES button hitbox to match its exact visual coordinates
        yesButtonBounds.setBounds(yesX, btnY, btnWidth, btnHeight);
        
        g2.setColor(Color.decode("#F2F3F5"));
        int yesTextWidth = fm.stringWidth("Yes");
        // Center text
        g2.drawString("Yes", yesX + (btnWidth - yesTextWidth) / 2, btnY + 20); 

        // NO
        int noX = btnStartX + btnWidth + btnSpacing;
        g2.setColor(Color.decode("#5865F2"));
        g2.fillRoundRect(noX, btnY, btnWidth, btnHeight, 15, 15);

        // Update the NO button hitbox to match its exact visual coordinates
        noButtonBounds.setBounds(noX, btnY, btnWidth, btnHeight);

        g2.setColor(Color.decode("#F2F3F5"));
        int noTextWidth = fm.stringWidth("No");
        // Center text
        g2.drawString("No", noX + (btnWidth - noTextWidth) / 2, btnY + 20);
    }

    public void drawPage(Graphics2D g2){
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = panel.getWidth();
        int h = panel.getHeight();
        //Background
        g2.setColor(Color.decode("#1E1F22"));
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


        //Search Bar
        g2.setColor(Color.decode("#313338"));
        int searchBarW = (int) (leftW * 1.0); 
        g2.fillRoundRect(pad, searchBarY, searchBarW, searchBarH, 15, 15);

        //Library
        g2.setColor(Color.decode("#2B2D31"));
        g2.fillRoundRect(pad, contentY, leftW, contentH, 25, 25);

        g2.setColor(Color.decode("#F2F3F5"));
        g2.setFont(new Font("Inter", Font.PLAIN, 16));
        g2.drawString("LIBRARY", pad + 55, contentY + 32);

        int rightX = pad + leftW + gap;
        int subH = (contentH - gap) / 2; 

        //Playlists
        g2.setColor(Color.decode("#2B2D31"));
        g2.fillRoundRect(rightX, contentY, rightW, subH, 25, 25);

        g2.setColor(Color.decode("#F2F3F5"));
        g2.drawString("PLAYLISTS", rightX + 50, contentY + 32);
        
        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Inter", Font.PLAIN, 14));
        g2.drawString("None", rightX + (rightW / 2) - 18, contentY + (subH / 2) + 5);

        int playerY = contentY + subH + gap;
        //Player
        g2.setColor(Color.decode("#2B2D31"));
        g2.fillRoundRect(rightX, playerY, rightW, subH, 25, 25);

        g2.setColor(Color.decode("#F2F3F5"));
        g2.setFont(new Font("Inter", Font.PLAIN, 16));
        g2.drawString("PLAYER", rightX + 20, playerY + 32);
    }
}