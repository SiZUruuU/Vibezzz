package ControlPanel;

import Main.Panel;
import Main.UI;

public class ExitButton extends ButtonManager{

    public ExitButton(int x, int y, int width, int height, Panel panel, UI ui) {
        super(x, y, width, height, panel, ui);

    }

    @Override
    public void execute(){System.exit(0);}
    
}
