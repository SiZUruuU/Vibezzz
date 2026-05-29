package ControlPanel;

import Main.Panel;
import Main.UI;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * The foundational blueprint for all interactive UI elements in the application.
 * This abstract class manages the spatial coordinates (hitboxes) and collision detection 
 * for mouse interactions. Every clickable button in the app extends this class 
 * and provides its own unique logic inside the abstract execute() method.
 */
public abstract class ButtonManager {
    
    protected int x, y, width, height;
    protected Panel panel;
    protected UI ui;

    /**
     * Constructs the base ButtonManager.
     * @param x      The initial X coordinate of the button's top-left corner.
     * @param y      The initial Y coordinate of the button's top-left corner.
     * @param width  The width of the button's clickable area.
     * @param height The height of the button's clickable area.
     * @param panel  The main application panel used to refresh the screen.
     * @param ui     The main UI state manager.
     */
    public ButtonManager(int x, int y, int width, int height, Panel panel, UI ui) {
        this.x = x; 
        this.y = y; 
        this.width = width; 
        this.height = height;
        this.ui = ui; 
        this.panel = panel;
    }
  
    /**
     * Dynamically updates the position and size of the button's hitbox.
     * This is heavily used by the View classes to attach hitboxes to scrollable lists 
     * or UI elements that change position based on window size or scroll state.
     * * @param x      The new X coordinate.
     * @param y      The new Y coordinate.
     * @param width  The new width.
     * @param height The new height.
     */
    public void setBounds(int x, int y, int width, int height) {
        this.x = x; 
        this.y = y; 
        this.width = width; 
        this.height = height;
    }

    /**
     * Determines if a specific set of coordinates falls within this button's hitbox.
     * Uses standard AABB (Axis-Aligned Bounding Box) collision math to check boundaries.
     * * @param mouseX The X coordinate of the user's mouse.
     * @param mouseY The Y coordinate of the user's mouse.
     * @return true if the mouse is inside the bounds, false otherwise.
     */
    public boolean collisionCheck(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseX <= (this.x + this.width) && 
               mouseY >= this.y && mouseY <= (this.y + this.height);
    }

    /**
     * The core action of the button. 
     * Because this is an abstract method, every subclass MUST define exactly 
     * what happens when this specific button is successfully clicked.
     * * @param mouseX The X coordinate of the user's mouse click.
     * @param mouseY The Y coordinate of the user's mouse click.
     */
    public abstract void execute(int mouseX, int mouseY);

    /**
     * A developer tool to visualize invisible hitboxes.
     * By calling this method in the draw phase, you can see a magenta outline 
     * representing exactly where the physical click area is currently located.
     * * @param g2 The Graphics2D context used to draw the outline.
     */
    public void drawDebug(Graphics2D g2) {
        // Only draw the box if the button has actual physical dimensions
        if (this.width > 0 && this.height > 0) {
            g2.setColor(Color.MAGENTA); 
            g2.drawRect(this.x, this.y, this.width, this.height);
        }
    }
}