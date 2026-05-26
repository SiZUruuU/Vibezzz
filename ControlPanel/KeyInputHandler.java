package ControlPanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import Main.UI;
import Main.Panel;

public class KeyInputHandler implements KeyListener {

    UI ui;
    Panel panel;

    public KeyInputHandler(Panel panel, UI ui){
        this.ui = ui;
        this.panel = panel;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();

        if(code == KeyEvent.VK_ESCAPE){
            ui.exitInquiry();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
    
}
