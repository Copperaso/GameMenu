import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class SymPaint extends Game{
    public static final int W = 1000, H = 800;
    public static String name = "";
    public static boolean debug = true;

    public static PolyLine currentLine;
    public static PolyLine.List all = new PolyLine.List();

    public SymPaint() {
        super("SymPaint", W, H);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 5000, 5000);
        g.setColor(Color.black);
        all.show(g);
        if (debug) {
            g.setColor(Color.red);
            name = "r" + PolyLine.rVal + ((PolyLine.isM) ? "m" : "");
            name += (PolyLine.isG ? (PolyLine.hg == 0 ? "H" : "G") : "");
            g.drawString(name, 30, 50);
        }
        g.setColor(Color.orange);
        String gStr = SGroup.isG ? SGroup.current().name:"Not Group";
        g.drawString(gStr, 30, 30);
    }


    //----------------PolyLine------------------------
    public static class PolyLine extends ArrayList<Point> {

        public static Point c = new Point(W / 2, H / 2);
        public static Point a = new Point(), b = new Point(); // raw line segment
        public static Point ra = new Point(), rb = new Point(); // rotations of a and b
        public static int rVal = 1; // rotation count
        public static final double twoPi = Math.PI * 2;

        // vertical mirror
        public static Point ma = new Point(), mb = new Point(); // mirror of ra and rb
        public static boolean isM = true;
        public static int md = 0; // displacement of vertical mirror

        // glides
        public static Point ga = new Point(), gb = new Point(); // glides of ra and rb
        public static boolean isG = true;
        public static int hg = 0; // horizontal extra translation, perfect horizontal mirror is hg is 0
        public static int hTLo, hTHi; // how many copies horizontal
        public static Point hT = new Point(200, 0);

        // vertical translation
        public static int vTLo, vTHi; // how many copies vertical
        public static Point vT = new Point(0, 150);
        public static boolean isP3M1;   // special case
        public static Point ka = new Point(), kb = new Point(); // special case for P3M1 and P4G


        private void show(Graphics g) {
            setTransLimits();
            for (int i = 1; i < this.size(); i++) {
                a = get(i - 1); b = get(i);
                for (int iR = 0 ; iR < rVal; iR++) {
                    setR(iR); setM(); setG();

                    if (isM && isG) {setP4G();}
                    if (isP3M1) {setP3M1();}

                    for (int h = hTLo; h < hTHi; h++) {
                        for (int v = vTLo; v < vTHi; v++) {
                            int tx = h * hT.x + v * vT.x, ty = h * hT.y + v * vT.y;
                            g.drawLine(ra.x + tx, ra.y + ty, rb.x + tx, rb.y + ty);

                            if (isM) {g.drawLine(ma.x + tx, ma.y + ty, mb.x + tx, mb.y + ty);}
                            if (isG) {g.drawLine(ga.x + tx, ga.y + ty, gb.x + tx, gb.y + ty);}

                            if (isM && isG) {g.drawLine(ka.x + tx, ka.y + ty, kb.x + tx, kb.y + ty);}
                            if (isP3M1) {g.drawLine(ka.x + tx, ka.y + ty, kb.x + tx, kb.y + ty);}
                        }
                    }
                }
            }
        }

        public void setR(int iR) {
            double ith = iR * twoPi / rVal, cos = Math.cos(ith), sin = Math.sin(ith);
            // rotation of i theta about the center point c
            ra.x = (int)((a.x - c.x) * cos + (a.y - c.y) * sin + c.x);
            ra.y = (int)((a.x - c.x) * (-sin) + (a.y - c.y) * cos + c.y);
            rb.x = (int)((b.x - c.x) * cos + (b.y - c.y) * sin + c.x);
            rb.y = (int)((b.x - c.x) * (-sin) + (b.y - c.y) * cos + c.y);
        }

        public void setM() {
            ma.x = 2 * c.x + md - ra.x; ma.y = ra.y;
            mb.x = 2 * c.x + md - rb.x; mb.y = rb.y;
        }

        public void setG() {
            ga.x = ra.x + hg; ga.y = 2 * c.y + md - ra.y;
            gb.x = rb.x + hg; gb.y = 2 * c.y + md - rb.y;
        }

        public void setP4G() {
            ka.x = 2 * c.x + md - ra.x; ka.y = 2 * c.y + md - ra.y; // mirror in two directions
            kb.x = 2 * c.x + md - rb.x; kb.y = 2 * c.y + md - rb.y;
        }

        public void setP3M1() {
            ka.x = ra.x + hg; ka.y = 2 * c.y + md - ra.y;
            kb.x = rb.x + hg; kb.y = 2 * c.y + md - rb.y;
        }

        public void setTransLimits() {
            if (hT.x == 0 && hT.y == 0) {hTLo = 0; hTHi = 1;} else {hTLo = -6; hTHi = 6;}
            if (vT.x == 0 && vT.y == 0) {
                vTLo = 0; vTHi = 1;} else {
                vTLo = -10; vTHi = 10;}
        }

        public void add(int x, int y) {
            add(new Point(x, y));
        }

        //----------------List---------------------
        public static class List extends ArrayList<PolyLine> {

            public void show(Graphics g) {
                for (PolyLine pl : this) {pl.show(g);}
            }
        }
    }

    //--------SGroup----------------
    public static class SGroup {
        public static final double DZ = 0, DX = 200, DY = 150, R3 = Math.sqrt(3)/2;
        public static ArrayList<SGroup> groups = new ArrayList<>();
        public static int GID = 0;
        public static boolean isG;

        //translation pattern
        public static double[] tP = {0, 0, 0, 0};
        public static double[] tS = {0, 0, DX, 0};
        public static double[] tW = {0, DY, DX, 0};    // translation - wall pattern
        public static double[] tW4 = {0, DX, DX, 0};
        public static double[] tW36 = {DX / 2, DX * R3, DX, 0};
        public static double[] t3M1 = {1.5 * DX, R3 * DX, 0, 2 * R3 * DX};
        public static double[] tCM = {DX / 2, DY, DX, 0};
        public static double[] tCMM = {DX, DY, DX, -DY};
        // glide mirror patterns
        public static double[] g0 = {0, 0};
        public static double[] gA = {DX / 2, 0};
        public static double[] gGG = {DX / 2, DY / 2};
        public static double[] g4G = {0, DX / 2};

        // create all groups
        static {
            // point groups
            for (int r = 1; r < 21; r++) {
                new SGroup("R " + r, r, g0, tP);
                (new SGroup("D" + r, r, g0, tP)).m = true;
            }
            // strip groups
            new SGroup("P111", 1, g0, tS);
            new SGroup("P112", 2, g0, tS);
            (new SGroup("PM11", 1, g0, tS)).m = true;
            (new SGroup("PM12", 2, g0, tS)).m = true;
            (new SGroup("P1M1", 1, g0, tS)).g = true;
            (new SGroup("P1A1", 1, gA, tS)).g = true;
            (new SGroup("PMA2", 2, gA, tS)).g = true;

            // wallpaper
            (new SGroup("P1", 1, g0, tW)).txy[0] = DZ;
            (new SGroup("PM", 1, g0, tW)).m = true;
            (new SGroup("PG", 1, gA, tW)).g = true;
            (new SGroup("CM", 1, g0, tCM)).g = true;
            new SGroup("P2", 2, g0, tW);
            (new SGroup("PMM", 2, g0, tW)).m = true;
            (new SGroup("PGG", 2, gGG, tW)).g = true;
            (new SGroup("PMG", 2, gA, tW)).g = true;
            (new SGroup("CMM", 2, g0, tCMM)).m = true;
            new SGroup("P3", 3, g0, tW36);
            (new SGroup("P3M1", 3, g0, t3M1)).isP3M1 = true;
            (new SGroup("P31M", 3, g0, tW36)).g = true;
            new SGroup("P4", 4, g0, tW4);
            SGroup sg = new SGroup("P4G", 4, g4G, tW4); sg.m = true; sg.g = true;
            (new SGroup("P4M", 4, g0, tW4)).m = true;
            new SGroup("P6", 6, g0, tW36);
            (new SGroup("P6M", 6, g0, tW36)).m = true;


        }


        public String name;
        public int r;
        public boolean m = false, g = false, isP3M1 = false;

        public double[] gm; // glides and mirrors
        public double[] txy; // x and y translations

        public SGroup(String name, int r, double[] gm, double[] txy) {
            this.name = name;
            this.r = r;
            this.gm = gm;
            this.txy = txy;
            groups.add(this);
        }

        public void set() {
            PolyLine.isM = m; PolyLine.isG = g; PolyLine.isP3M1 = isP3M1;
            PolyLine.rVal = r;
            PolyLine.hg = (int)gm[0]; PolyLine.md = (int)gm[1];
            PolyLine.hT.x = (int)txy[2]; PolyLine.hT.y = (int)txy[3];
            PolyLine.vT.x = (int)txy[0]; PolyLine.vT.y = (int)txy[1];

        }

        public static SGroup current() {return groups.get(GID);}

        public static void left() {
            SGroup group = (GID == 0) ? groups.get(0) : groups.get(--GID);
            group.set();
        }

        public static void right() {
            int n = groups.size() - 1;
            SGroup group = (GID == n) ? groups.get(n) : groups.get(++GID);
            group.set();
        }
    }


    @Override
    public void endGame() {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent ke) {
        // rotation
        int vk = ke.getExtendedKeyCode();
        boolean isG = false;
        boolean isBK = false;

        if (vk == KeyEvent.VK_UP) {
            if (PolyLine.rVal < 20) {PolyLine.rVal++;}
        }
        if (vk == KeyEvent.VK_DOWN) {
            if (PolyLine.rVal > 1) {PolyLine.rVal--;}
        }
        if (vk == KeyEvent.VK_BACK_SPACE) {
            all.clear();
            currentLine = null;
            isBK = true;
        } else if (vk == KeyEvent.VK_LEFT) {
            SGroup.left();
            isG = true;
        } else if (vk == KeyEvent.VK_RIGHT) {
            SGroup.right();
            isG = true;
        }

        // mirror
        char ch = ke.getKeyChar();
        if (ch == 'M' || ch == 'm') {
            PolyLine.isM = !PolyLine.isM;
        }
        // horizontal mirror
        if (ch == 'H' || ch == 'h') {
            PolyLine.isG = !PolyLine.isG;
            PolyLine.hg = 0;
        }
        // glide
        if (ch == 'G' || ch == 'g') {
            PolyLine.isG = true;
            PolyLine.hg = PolyLine.hT.x / 2;
        }

        if (!isBK) {SGroup.isG = isG;}
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent me) {
        currentLine = new PolyLine();
        currentLine.add(me.getX(), me.getY());
        all.add(currentLine);
        repaint();
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
    public void mouseDragged(MouseEvent me) {
        currentLine.add(me.getX(), me.getY());
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
