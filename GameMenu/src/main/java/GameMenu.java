import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class GameMenu extends Window{
    public static Color bkColor = new Color(200, 255, 200); // light green
    public static int x = 100, y = 30;
    public static Game theGame = null;

    public GameMenu() {
        super("GameMenu", 1000, 800);
    }

    public void paintComponent(Graphics g) {
        if (theGame != null) {
            theGame.paintComponent(g);
            return;
        }
        g.setColor(bkColor);
        g.fillRect(0, 0, 5000, 5000);
        g.setColor(Color.BLACK);
        y = 30;
        g.drawString("Games - Press escape to return to this menu", x, y); y += 20;
        g.drawString("(D)estructo - Press D to play Destructo", x, y); y += 20;
        g.drawString("(S)okoban - Press S to play Sokoban", x, y); y += 20;
        g.drawString("(T)etris - Press T to play Tetris", x, y); y += 20;
        g.drawString("(B)reakout - Press B to play Breakout", x, y); y += 20;
        g.drawString("S(k)unk - Press K to play Skunk", x, y); y += 20;
        g.drawString("(M)aze - Press M to play Maze", x, y); y += 20;
        g.drawString("(P)aint - Press P to play SymPaint", x, y); y += 20;
    }

    public void keyPressed(KeyEvent ke) {
        char ch = ke.getKeyChar();
        if (ch == 27) {stopGame();}
        if (theGame != null) {theGame.keyPressed(ke); return;}
        if (ch == 'D' || ch == 'd') {theGame = new Destructo();}
        if (ch == 'S' || ch == 's') {theGame = new Sokoban();}
        if (ch == 'T' || ch == 't') {theGame = new Tetris();}
        if (ch == 'B' || ch == 'b') {theGame = new Breakout();}
        if (ch == 'K' || ch == 'k') {theGame = new Skunk();}
        if (ch == 'M' || ch == 'm') {theGame = new Maze();}
        if (ch == 'P' || ch == 'p') {theGame = new SymPaint();}

        if (theGame != null) {theGame.panel = PANEL;}
        repaint();
    }

    public static void stopGame() {
        if (theGame == null) {return;}
        theGame.panel = null;
        theGame = null;
    }

    @Override
    public void mouseClicked(MouseEvent me){if (theGame != null) {theGame.mouseClicked(me);}}

    @Override
    public void mousePressed(MouseEvent me) {if (theGame != null) {theGame.mousePressed(me);}}
    @Override
    public void mouseReleased(MouseEvent me) {if (theGame != null) {theGame.mouseReleased(me);}}

    @Override
    public void mouseEntered(MouseEvent me) {if (theGame != null) {theGame.mouseEntered(me);}}
    @Override
    public void mouseExited(MouseEvent me) {if (theGame != null) {theGame.mouseExited(me);}}
    @Override
    public void mouseDragged(MouseEvent me) {if (theGame != null) {theGame.mouseDragged(me);}}
    @Override
    public void mouseMoved(MouseEvent me) {if (theGame != null) {theGame.mouseMoved(me);}}

    @Override
    public void keyTyped(KeyEvent ke) {if (theGame != null) {theGame.keyTyped(ke);}}
    @Override
    public void keyReleased(KeyEvent ke) {if (theGame != null) {theGame.keyReleased(ke);}}


    public static void main(String[] args) {
        Window.PANEL = new GameMenu();
        Window.launch();
    }
}
