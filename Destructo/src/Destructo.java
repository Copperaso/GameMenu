import java.awt.Color;
import java.awt.*;
import java.util.Random;

import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class Destructo extends Window implements ActionListener{

    public static final int nR = 15, nC = 13;
    public int brickRemaining;
    public int[][] grid = new int[nC][nR];
    public static Color[] color = {
        Color.lightGray, // index = 0 will be the background
        Color.cyan,
        Color.green,
        Color.yellow,
        Color.red,
        Color.pink
    };
    public int w = 50, h = 30, xM = 100, yM = 100; // "M" is margin
    public static Random RANDOM = new Random();
    public static int rnd(int k) {return RANDOM.nextInt(k);}
    public static Timer timer;


    public Destructo() {
        super("Destructo", 1000, 800);
        rndColors(3);
        timer = new Timer(30, this); // 30: // this Destructo is the listener
        timer.start();
    }

    public void paintComponent(Graphics g) { // called by operating system
        g.setColor(color[0]); g.fillRect(0, 0, 5000, 5000);
        showGrid(g);
        bubbleSort();
        // slideCol();
        if (slideCol()) {xM += w / 2;}
        g.setColor(Color.BLACK);
        g.drawString("Remaining: " + brickRemaining, 50, 25);
    }

    public void rndColors(int k) {
        brickRemaining = nC * nR;
        for (int c = 0; c < nC; c++) {
            for (int r = 0; r < nR; r++) {
                grid[c][r] = 1 + rnd(k);  // use "+1" to skip the background color
            }
        }
    }

    public void showGrid(Graphics g) {
        for (int c = 0; c < nC; c++) {
            for (int r = 0; r < nR; r++) {
                g.setColor(color[grid[c][r]]);
                g.fillRect(x(c), y(r), w, h);
            }
        }
    }

    public int x(int c) {return xM + c * w;}
    public int y(int r) {return yM + r * h;}

    public int c(int x) {return (x - xM) / w;}
    public int r(int y) {return (y - yM) / h;}

    public void mouseClicked(MouseEvent me) {
        int x = me.getX(), y = me.getY();
        if (x < xM || y < yM) {return;}
        int r = r(y), c = c(x);
        if (r < nR && c < nC) {
            rcAction(r, c);
        }
    }

    public void rcAction(int r, int c) {
        // grid[c][r] = 0; // set to background color
        if (infectable(c, r)) {
            infect(c, r, grid[c][r]); 
            // bubbleSort(); 
            repaint();}
        // repaint(); // give a signal to the OS to repaint
    }

    public void infect(int c, int r, int v) { // "v" the value (the color) of the cell
        if (grid[c][r] != v) {return;}
        grid[c][r] = 0; // kill the cell before infecting neighbors
        brickRemaining--;
        // then infect the neighbors by using recursion
        if (r > 0) {infect(c, r - 1, v);}
        if (c > 0) {infect(c - 1, r, v);}
        if (r < nR - 1) {infect(c, r + 1, v);}
        if (c < nC - 1) {infect(c + 1, r, v);}
    }

    public Boolean infectable(int c, int r) {
        int v = grid[c][r];
        if (v == 0) {return false;} // cannot infect the background
        if (r > 0) {if(grid[c][r-1] == v) {return true;}}
        if (c > 0) {if(grid[c-1][r] == v) {return true;}}
        if (r < nR - 1) {if(grid[c][r+1] == v) {return true;}}
        if (c < nC - 1) {if(grid[c+1][r] == v) {return true;}}
        return false;
    }

    public Boolean bubble(int c) {
        Boolean res = false; // res = result
        for (int r = nR - 1; r > 0; r--) {
            if (grid[c][r] == 0 && grid[c][r - 1] != 0) {
                res = true;
                grid[c][r] = grid[c][r-1];
                grid[c][r-1] = 0;
            }
        }
        return res;
    }

    public void bubbleSort() {
        for (int c = 0; c < nC; c++) {
            // while(bubble(c)){}
            if (bubble(c)) {break;}
        }
    }

    public Boolean colIsEmpty(int c) {
        for (int r = 0; r < nR; r++) {if(grid[c][r] != 0){return false;}}
        return true;
    }
    public void swapCol(int c) { // c is not empty, c-1 is empty
        for (int r = 0; r < nR; r++) {
            grid[c-1][r] = grid[c][r];
            grid[c][r] = 0;
        }
    }

    public Boolean slideCol() {
        Boolean res = false;
        for (int c = 1; c < nC; c++) {
            if(colIsEmpty(c-1) && !colIsEmpty(c)) {swapCol(c); res = true;}
        }
        return res;
    }

    public static void main(String[] args) {
        Window.PANEL = new Destructo();
        Window.launch();
    }

    @Override
    public void actionPerformed(ActionEvent e) {repaint();}
}
