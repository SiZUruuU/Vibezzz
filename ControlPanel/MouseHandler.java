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

        if (ui.exit) {
            // Check the Yes and No button bounds directly
            if (ui.yesButtonBounds.contains(x, y)) {
                System.exit(0);
            } else if (ui.noButtonBounds.contains(x, y)) {
                panel.exitInquiry(); // This toggles exit to false and closes the popup
            }
            return; // Stop checking other buttons if the popup is active
        }

        // If not exiting, check normal backend buttons
        for (ButtonManager button : ui.getBackendButtons()) {
            if (button.collisionCheck(x, y)) {
                button.execute();
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
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
    
}