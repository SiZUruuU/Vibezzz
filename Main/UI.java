package Main;

import java.awt.*;
import javax.swing.*;

public class UI {

    Panel panel;
    Graphics2D g2;

    public UI(Panel panel){
        this.panel = panel;
    }

    public void draw(Graphics2D g2){
        panel.setBackground(Color.decode("#00daaa"));
    }

    public void imageDraw(Graphics2D g2){

    }
}
