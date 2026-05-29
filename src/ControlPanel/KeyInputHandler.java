package ControlPanel;

import Main.Panel;
import Main.UI;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInputHandler implements KeyListener {

    UI ui;
    Panel panel;

    public KeyInputHandler(Panel panel, UI ui){
        this.ui = ui;
        this.panel = panel;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();

        if (Character.isLetterOrDigit(c) || Character.isWhitespace(c)) {            
                ui.searchText += c;
                panel.repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();

        if(code == KeyEvent.VK_ESCAPE){
            panel.exitInquiry();
        }

        if(code == KeyEvent.VK_BACK_SPACE && !ui.searchText.isEmpty()) {
            ui.searchText = ui.searchText.substring(0, ui.searchText.length() - 1);
            panel.repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
    
}
