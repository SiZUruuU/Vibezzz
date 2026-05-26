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
}
