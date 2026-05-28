package ControlPanel;

import Main.Panel;
import Main.UI;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.SwingUtilities;

// Implement MouseMotionListener here
public class MouseHandler implements MouseListener, MouseMotionListener {

    Panel panel;
    UI ui;
    private Point initialClick;

    public MouseHandler(Panel panel, UI ui){
        this.panel = panel;
        this.ui = ui;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // 1. If Exit Popup is active, ONLY check popup buttons
        if (ui.exit) {
            for (ButtonManager button : ui.getPopupButtons()) {
                if (button.collisionCheck(x, y)) {
                    button.execute(x, y);
                    break;
                }
            }
            return; 
        }

        // 2. Otherwise, check all backend interactables
        for (ButtonManager button : ui.getBackendButtons()) {
            if (button.collisionCheck(x, y)) {
                button.execute(x, y);
                break;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        initialClick = e.getPoint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (initialClick == null) return;

        Window window = SwingUtilities.getWindowAncestor(panel);
        
        if (window != null) {

            int windowX = window.getLocation().x;
            int windowY = window.getLocation().y;

            int xMoved = e.getX() - initialClick.x;
            int yMoved = e.getY() - initialClick.y;


            window.setLocation(windowX + xMoved, windowY + yMoved);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        initialClick = null;
    }

    @Override
    public void mouseMoved(MouseEvent e) {

        int x = e.getX();
        int y = e.getY();

        if(y <= 50){panel.setHeaderHover(true);}
        else{panel.setHeaderHover(false);}
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
    
}