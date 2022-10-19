import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Maze extends Game{

    public static final int W = 90, H = 60;
    public static final int xM = 50, yM = 50, c = 10; // Margin and cell size
    public static int[] next = new int[W + 1]; // pointer to next
    public static int[] prev = new int[W + 1]; // pointer back to prev
    public static int y;
    public static Graphics gg;

    public Maze() {
        super("Maze", 1000, 800);
    }

    @Override
    public void paintComponent(Graphics g) {
        gg = g;
        gg.setColor(Color.white);
        gg.fillRect(0 ,0, 5000, 5000);
        gg.setColor(Color.black);
        G.RANDOM.setSeed(205); // random number generate
        hRow0();  // first h row
        mid();  // alternating between vRows and hRows
        vLast();  // last vertical row
        hLast(); // last h Row
    }

    public void hRow0() {
        y = yM;
        singletonCycle(0);
        for (int i = 0; i < W; i++) {
            singletonCycle(i + 1); // because previous is already made a cycle
            hLine(i);
        }
    }

    public void hRule(int i) {
        // is legal to connect i and i+1
        if (!sameCycle(i, i + 1) && pH()) {
            hLine(i);
        }
    }

    public void vRule(int i) {
        // next[i] == i means single element is itself, so must draw vertical line
        if (next[i] == i || pV()) {
            vLine(i);
        } else {
            // not drawing v line
            noVLine(i);
        }
    }

    // loop for all hRow and vRow to check if should draw line
    public void hRow() {
        for (int i = 0; i < W; i++) {
            hRule(i);
        }
    }

    public void vRow() {
        for (int i = 1; i < W; i++) {
            vRule(i);
        }
        // draw left edge and right edge.
        vLine(0);
        vLine(W);
    }

    public void noVLine(int i) {
        split(i);
    }

    public void mid() {
        // loop through H
        for (int i = 0; i < H - 1; i++) {
            vRow();
            y += c;
            hRow();
        }
    }

    public void vLast() {
        vLine(0);
        vLine(W);
        for (int i = 1; i < W; i++) {
            if (!sameCycle(i, 0)) {
                merge(i, 0);
                vLine(i);
            }
        }
    }

    public void hLast() {
       y += c;
       for (int i = 0; i < W; i++) {
           hLine(i);
       }
    }

    public int x(int i) {
        return xM + i * c;
    }

    public void hLine(int i) {
        gg.drawLine(x(i), y, x(i + 1), y);
        merge(i, i + 1);
    }


    public void vLine(int i) {
        gg.drawLine(x(i), y, x(i), y + c);
    }


    // merge two double inked list
    public void merge(int i, int j) {
        int ip = prev[i];
        int jp = prev[j];
        next[ip] = j;
        next[jp] = i;
        prev[i] = jp;
        prev[j] = ip;
    }

    public void split(int i) {
        int ip = prev[i], in = next[i];
        next[ip] = in;
        prev[in] = ip;
        next[i] = i; // make i point to itself
        prev[i] = i;
    }

    public void singletonCycle(int i) {
        next[i] = i;
        prev[i] = i;
    }

    public boolean sameCycle(int i, int j) {
        int n = next[i];
        while (n != i) {
            if (n == j) {
                return true;
            }
            n = next[n];
        }
        return false;
    }

    public static boolean pV() {
        // probability of making vertical connection
        return G.rnd(100) < 33;
    }

    public static boolean pH() {
        // probability of making horizontal connection
        return G.rnd(100) < 47;
    }











    @Override
    public void endGame() {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
