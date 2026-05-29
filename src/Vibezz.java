import Main.Panel;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JFrame;

public class Vibezz {
    public static void main(String [] args){

        JFrame jframe = new JFrame();
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setResizable(false);
        jframe.setTitle("Vibezz Music Player");
        jframe.setUndecorated(true);

        Panel panel = new Panel();
        jframe.add(panel);
        
        jframe.pack();

        jframe.setShape(new RoundRectangle2D.Double(0, 0, jframe.getWidth(), jframe.getHeight(), 100, 100));
        jframe.setLocationRelativeTo(null);
        jframe.setVisible(true);
    }
}
