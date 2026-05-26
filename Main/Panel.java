package Main;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import ControlPanel.KeyInputHandler;
import ControlPanel.MouseHandler;

public class Panel extends JPanel {
    
    private final int screenWidth = 650, screenHeight = 700;
    private UI ui = new UI(this);
    private KeyInputHandler keyH = new KeyInputHandler(this, ui);
    private MouseHandler mouse = new MouseHandler(this, ui);

    public Panel(){

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setLayout(null);
        this.setDoubleBuffered(true);

        this.addKeyListener(keyH);
        this.addMouseListener(mouse);
        
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.decode("#000000"));
        ui.draw(g2);

    }
}
