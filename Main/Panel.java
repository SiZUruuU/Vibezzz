package Main;
import ControlPanel.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class Panel extends JPanel {
    
    private final int screenWidth = 650, screenHeight = 700;
    private UI ui = new UI(this);
    private MouseHandler mouse = new MouseHandler(this, ui);
    private KeyInputHandler keyH = new KeyInputHandler(this, ui);

    public Panel(){

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setLayout(null);
        this.setDoubleBuffered(true);

        this.addKeyListener(keyH);
        this.addMouseListener(mouse);
        this.addMouseMotionListener(mouse);
        
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    public void exitInquiry(){

        if(!ui.exit){ui.exit = true;}
        else{ui.exit = false;}  
        repaint();
    }

    // public void escInquiry(){
    //     if(ui.escInq){ui.escInq = false;}
    //     else{ui.escInq = true;}
    //     repaint();
    // }

    public void setHeaderHover(boolean hovering){
        if(ui.escInq != hovering){
            ui.escInq = hovering;
            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.decode("#000000"));
        ui.draw(g2);

    }
}
