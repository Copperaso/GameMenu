import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Skunk extends Game{

    public static String AIName = "Archie";

    public static G.Button.List cmds = new G.Button.List();
    public static G.Button PASS = new G.Button(cmds, "PASS") {
        @Override
        public void act() {pass();}
    };

    public static G.Button ROLL = new G.Button(cmds, "ROLL") {
        @Override
        public void act() {roll();}
    };

    public static G.Button AGAIN = new G.Button(cmds, "PLAY AGAIN") {
        @Override
        public void act() {playAgain();}
    };

    public static int M = 0, E = 0, H = 0;
    public static boolean myTurn = true;
    public static int D1, D2;   // Two dices
    public static int xM = 50, yM = 50;

    public Skunk() {
        super("Skunk", 1000, 800);
        // converge(80000000); // can use a timer
        playAgain();
    }

    public static void playAgain() {
        M = 0;
        E = 0;
        H = 0;
        myTurn = G.rnd(2) == 0;
        PASS.set(100, 100);
        ROLL.set(150, 100);
        AGAIN.set(-100, -100);
    }

    public static void roll() {
        D1 = G.rnd(6) + 1;
        D2 = G.rnd(6) + 1;
        analyseDice();
    }

    public static void pass() {
        if (myTurn) {
            M += H;
        } else {
            E += H;
        }
        H = 0;
        ROLL.enable = true;
        myTurn = !myTurn;
        roll();
    }

    public static String skunkMsg;

    public static void showRoll(Graphics g) {
        g.setColor(Color.BLACK);
        String playerName = myTurn ? "Your" : AIName + "'s";
        g.drawString(playerName +" Roll: " + D1 + ", " + D2 + skunkMsg, xM, yM + 20);
    }

    public static String gameOverMsg() {
        String res = "";
        int total = H + (myTurn ? M : E);
        if (total >= 100) {
            res = myTurn ? "You win!" : AIName+"'s win!";
            gameOver();
        }
        return res;
    }

    public static void gameOver() {
        PASS.set(-100, -100);
        ROLL.set(-100, -100);

        AGAIN.set(100, 100);
    }

    public static void analyseDice() {
        PASS.enable = true;
        ROLL.enable = true;
        if (D1 == 1 && D2 == 1) {
            totalSkunk();
            skunkMsg = " Totally skunked";
            return;
        }
        if (D1 == 1 || D2 == 1) {
            skunked();
            skunkMsg = " Skunked";
            return;
        }
        skunkMsg = "";
        normalHand();
    }

    public static String scoreStr() {
        return "hand score: " + H + "     your score: " + M + " " + AIName + "'s score: " + E;
    }

    public static void showScore(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawString(scoreStr(), xM, yM + 40);
    }

    public void paintComponent(Graphics g) {
        G.whiteBackground(g);
        converge(1000000);
        if (showStrategy) {
            converge(100000);
            showAll(g);
        } else {
            showRoll(g);
            showScore(g);

            if (gameOverMsg() != "") {
                g.setColor(Color.BLACK);
                g.drawString(gameOverMsg(), xM, yM);
            }
            cmds.showAll(g);
        }
    }

    @Override
    public void endGame() {

    }

    public static void totalSkunk() {
        if (myTurn) {M = 0;} else {E = 0;}
        skunked();
    }

    public static void skunked() {
        H = 0;
        ROLL.enable = false;
    }

    public static void normalHand() {
        H += D1 + D2;
        setAIButtons();
    }

    public static boolean gottaRoll() {
        wOptimal(E, M, H);
        return ROLL.enable && !shouldPass;
    }

    public static void setAIButtons() {
        if (!myTurn) {
            if (gottaRoll()) {
                PASS.enable = false;
            } else {
                ROLL.enable = false;
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent me) {
        int x = me.getX(), y = me.getY();
        if (cmds.click(x, y)) {
            repaint();
        }
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

    // ------------------- markov ----------------------------
    public static double [][][]P = new double[100][100][100];

    public static double p(int m, int e, int h) {
        if (m + h >= 100) {
            return 1.0;
        }
        if (e >= 100) {
            return 0.0;
        }
        return P[m][e][h];
    }

    public static double wPass(int m, int e, int h) {
        return 1.0 - p(e, m + h, 0);
    }

    public static double wTS(int m, int e, int h) {
        return 1.0 - p(e, 0, 0);
    }

    public static double wS(int m, int e, int h) {
        return 1.0 - p(e, m, 0);
    }

    public static double wRoll(int m, int e, int h) {
        double res = wTS(m, e, h) / 36 + wS(m, e, h) / 3.6;
        for (int d1 = 2; d1 < 7; d1++) {
            for (int d2 = 2; d2 < 7; d2++) {
                res += p(m, e, h + d1 + d2) / 36;
            }
        }
        return res;
    }

    public static boolean shouldPass; // set by side effect

    public static double wOptimal(int m, int e, int h) {
        double wP = wPass(m, e, h), wR = wRoll(m, e, h);
        return (shouldPass = (wP > wR)) ? wP : wR;
    }

    public static void converge(int n) {
        for (int i = 0; i < n; i++) {
            int m = G.rnd(100), e = G.rnd(100), h = G.rnd(100);
            P[m][e][h] = wOptimal(m, e, h);
        }
    }

    //------------- strategy visualization -------------

    public static final int W = 7;
    public static boolean showStrategy = false;

    public static void showAll(Graphics g) {
        showStops(g);
        showGrids(g);
        showColorMap(g);
    }

    public static final int nC = 45;

    public static Color[] stopColor = new Color[nC];
    static {
        for (int i = 0; i < nC; i++) {
            stopColor[i] = new Color(G.rnd(250), G.rnd(250), G.rnd(250));
        }
    }

    public static void showColorMap(Graphics g) {
        int x = xM + 100 * W + 30;
        for (int i = 0; i < nC; i++) {
            g.setColor(stopColor[i]);
            g.fillRect(x, yM + 15 * i, 15, 30);
            g.setColor(Color.BLACK);
            g.drawString("" + i, x + 20, yM + 15 * i + 10);
        }
    }

    public static void showGrids(Graphics g) {
        g.setColor(Color.black);
        for (int k = 0; k <= 10; k++) {
            int d = 10 * W * k;
            g.drawLine(xM, yM + d, xM + 100 * W, yM + d);
            g.drawLine(xM + d, yM, xM + d, yM + 100 * W);
        }
    }

    public static void showStops(Graphics g) {
        for (int m = 0; m < 100; m++) {
            for (int e = 0; e < 100; e++) {
                int k = firstStop(m, e);
                g.setColor(stopColor[k]);
                g.fillRect(xM + W * m, yM + W * e, W, W);
            }
        }
    }

    public static int firstStop(int m, int e) {
        int h = 0;
        for (h = 0; h < 100 - m; h++) {
            wOptimal(m, e, h);
            if (shouldPass) {
                return h > nC ? 0 : h;
            }
        }
        return 0;
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
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}