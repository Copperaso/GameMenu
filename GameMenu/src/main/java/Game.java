import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;

public abstract class Game implements MouseListener, MouseMotionListener, KeyListener {

    public String title;
    public JPanel panel;

    public Game(String title, int x, int y) {
        this.title = title;
    }

    public abstract void paintComponent(Graphics g);

    public abstract void endGame(); // if use timer, shut off timer.

    public void repaint() {
        if (panel != null) {
            panel.repaint();
        }
    }
}
