package ControlPanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import Main.Panel;
import Main.UI;

public class MouseHandler implements MouseListener{

    Panel panel;
    UI ui;

    public MouseHandler(Panel panel, UI ui){
        this.panel = panel;
        this.ui = ui;
        
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        int x = e.getX();
        int y = e.getY();

        if(ui.exit){
            if(x >= 265 && x <= 305 && y >= 355 && y <= 375){
                System.exit(0);
            }
            else if(x >= 345 && x <= 385 && y >= 355 && y <= 375){
                ui.exit = false;
                panel.repaint();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
    
}
