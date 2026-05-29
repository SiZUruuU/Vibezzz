package Main;

import ControlPanel.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 * The master canvas and event hub for the application.
 * Extending Java Swing's JPanel, this class acts as the root container. 
 * It initializes the core UI manager and input listeners, sets up the graphics 
 * environment (like double-buffering), and acts as the bridge between the 
 * operating system's paint cycle and your custom rendering logic.
 */
public class Panel extends JPanel {
    
    // --- Core Dimensions & State Managers ---
    private final int screenWidth = 650;
    private final int screenHeight = 700;
    
    private UI ui = new UI(this);
    private MouseHandler mouse = new MouseHandler(this, ui);
    private KeyInputHandler keyH = new KeyInputHandler(this, ui);

    /**
     * Constructs the main application panel, setting its size and registering all input hooks.
     */
    public Panel() {
        // 1. Set fixed dimensions for the application window
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        
        // 2. Disable Swing's default layout manager since we are using custom absolute/math positioning
        this.setLayout(null);
        
        // 3. Enable double buffering to eliminate screen flickering during rapid repaints
        this.setDoubleBuffered(true);

        // 4. Register global input listeners to route OS events into our custom Handlers
        this.addKeyListener(keyH);
        this.addMouseListener(mouse);
        this.addMouseMotionListener(mouse);
        this.addMouseWheelListener(mouse);
        
        // 5. CRITICAL: A JPanel must explicitly request focus to receive Keyboard events
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    /**
     * Toggles the visibility state of the exit confirmation popup.
     * Forces a screen repaint to instantly show or hide the overlay.
     */
    public void exitInquiry() {
        // Flips the boolean state (true becomes false, false becomes true)
        ui.exit = !ui.exit;  
        repaint();
    }

    /**
     * Updates the UI state when the user hovers over the custom top window drag-bar.
     * @param hovering true if the mouse is in the top 50 pixels, false otherwise.
     */
    public void setHeaderHover(boolean hovering) {
        // Optimization: Only request a repaint if the hover state actually changed
        if (ui.escInq != hovering) {
            ui.escInq = hovering;
            repaint();
        }
    }

    /**
     * The main rendering pipeline hooked directly into the Java Swing paint cycle.
     * Whenever repaint() is called anywhere in the app, the OS eventually triggers this method.
     * @param g The standard graphics context provided by Java.
     */
    @Override
    public void paintComponent(Graphics g) {
        // 1. MUST call super to properly clear the previous frame and prevent visual "ghosting"
        super.paintComponent(g);

        // 2. Cast to Graphics2D for modern drawing tools (Anti-aliasing, Alpha blending, etc.)
        Graphics2D g2 = (Graphics2D) g;
        
        // 3. Set the base background color (though your Views will likely paint over this)
        g2.setColor(Color.decode("#000000"));
        
        // 4. Delegate all actual rendering logic to the centralized UI manager
        ui.draw(g2);
    }
}