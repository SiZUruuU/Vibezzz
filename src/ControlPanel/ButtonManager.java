package ControlPanel;

import Main.*;

//The parent class that links Front-end and Back-end
public abstract class ButtonManager {
    
    protected int x, y, width, height;
    protected Panel panel;
    protected UI ui;

    public ButtonManager(int x, int y, int width, int height, Panel panel, UI ui){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.ui = ui;
        this.panel = panel;
    }
  
    //Collision checker 
    public boolean collisionCheck(int mouseX, int mouseY){
        return mouseX >= this.x && 
            mouseX <= (this.x + this.width) && 
            mouseY >= this.y && 
            mouseY <= (this.y + this.height);
    }

    public abstract void execute();



}
