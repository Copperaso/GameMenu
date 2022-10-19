import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;

public class Sokoban extends Window{
    public Board board = new Board();
    public static Point LEFT = new Point(-1, 0),
                        RIGHT = new Point(1, 0),
                        UP = new Point(0, -1),
                        DOWN = new Point(0, 1);

    public Sokoban() {
        super("Sokoban", 1000, 800);
        board.loadStringArray(level1);
    }

    public void paintComponent(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, 5000, 5000);
        // board.b[0][0] = 'E';
        board.show(g);
        if (board.done()) {
            g.setColor(Color.black);
            g.drawString("Nice job!", 20, 20);
        }
    }

    public void keyPressed(KeyEvent ke) {
        int vk = ke.getKeyCode(); // virtual key
//        System.out.println("vk: "+vk);
        if (vk == KeyEvent.VK_LEFT) {board.go(LEFT);}
        if (vk == KeyEvent.VK_RIGHT) {board.go(RIGHT);}
        if (vk == KeyEvent.VK_UP) {board.go(UP);}
        if (vk == KeyEvent.VK_DOWN) {board.go(DOWN);}
        repaint();
    }

    public static void main (String[] args) {
        Window.PANEL = new Sokoban();
        Window.launch();
    }

    public static class Board {
        public static final int N = 25;
        public static final int xM = 50, yM = 50, W = 40;
        public static boolean onGoal = false; //track if player is on goal square
        public static Point destination = new Point(0, 0);
        public static String boardState = " WPCGgE"; //' '-blank W-wall P-person C-container G-goal g-boxInContainer E-error
        public static Color[] colors = {
            Color.white, Color.darkGray, Color.green, Color.orange, Color.cyan, Color.blue, Color.red
        };
        public char[][] b = new char[N][N];
        public Point person = new Point(0, 0);

        public Board() {clear();}

        public char ch(Point p) {return b[p.x][p.y];}
        public void set(Point p, char c) {b[p.x][p.y] = c;}

        public void movePerson() { // move to empty or goal
            boolean res = (ch(destination) == 'G');
            set(person, onGoal?'G':' '); // set value on square person is leaving
            set(destination, 'P');
            person.setLocation(destination);
            onGoal = res;
        }

        public void go(Point p) {
            destination.setLocation(person.x + p.x, person.y + p.y);
            if (ch(destination) == 'W' || ch(destination) == 'E') {return;} // do not walk into the wall
            if (ch(destination) == ' ' || ch(destination) == 'G') {movePerson(); return;}
            if (ch(destination) == 'C' || ch(destination) == 'g') { // moving container
                destination.setLocation(destination.x + p.x, destination.y + p.y); // changing destination to box destination
                if (ch(destination) != ' ' && ch(destination) != 'G') {return;}
                set(destination, (ch(destination) == 'G') ? 'g': 'C'); // put box in final spot
                destination.setLocation(destination.x - p.x, destination.y - p.y); // back up tp person's location
                set(destination, (ch(destination) == 'g') ? 'G' : ' ');
                movePerson();
            }
        }
        public boolean done() {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (b[i][j] == 'C') {return false;}
                }
            }
            return true;
        }

        public void clear() {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    b[i][j] = ' ';
                }
            }
        }

        public void show(Graphics g) {
            for (int r = 0; r < N; r++) {
                for (int c = 0; c < N; c++) {
                    int ndx = boardState.indexOf(b[c][r]); //index is used for Color array
                    g.setColor(colors[ndx]);
                    g.fillRect(xM + c*W, yM + r*W, W, W);
                }
            }
        }

        public void loadStringArray(String[] a) {
            person.setLocation(0, 0);
            for (int r = 0; r < a.length; r++) {
                String s = a[r];
                for (int c = 0; c < s.length(); c++) {
                    char ch = s.charAt(c);
                    b[c][r] = (boardState.indexOf(ch) > -1) ? ch : 'E'; // detect illegal char
                    if (ch == 'P' && person.x == 0) { person.x = c; person.y = r;}
                }
            }
        }
    }

    public static String[] level1 = {
        "  WWWWW",
        "WWW   W",
        "WGPC  W",
        "WWW CGW",
        "WGWWC W",
        "W W G WW",
        "WC gCCGW",
        "W   G  W",
        "WWWWWWWW"
    };
}
