package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;

public class ExitPopupButton extends ButtonManager {

    private boolean isConfirm; 
    
    public ExitPopupButton(Panel panel, UI ui, boolean isConfirm) {
        super(0, 0, 0, 0, panel, ui);
        this.isConfirm = isConfirm;
    }


    @Override
    public void execute(int mouseX, int mouseY) {
        
        
        if (isConfirm) {
            System.exit(0);
        } else {

            ui.exitInquiry();
        }
    }
}