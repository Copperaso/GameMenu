import javax.swing.*;
import java.awt.Graphics;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class G {
    public static Random RANDOM = new Random();
    public static int rnd(int k) {return RANDOM.nextInt(k);}
    public static G.V LEFT = new G.V(-1, 0), RIGHT = new G.V(1, 0), UP = new G.V(0, -1), DOWN = new G.V(0, 1);
    public static void whiteBackground(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, 5000, 5000);
    }

    //----------------V-----------------
    public static class V {
        public int x = 0, y = 0;

        public V(int x, int y) {this.x = x; this.y = y;}
        public V() {this.x = 0; this.y = 0;}
        public V(V v) {this.x = v.x; this.y = v.y;}

        public void add(V v) {x += v.x; y += v.y;}
        public void set(V v) {x = v.x; y = v.y;}
        public void set(int x, int y){this.x =x; this.y=y;}
    }
    //---------------VS-------------------
    public static class VS {
        public V loc, size;

        public VS(int x, int y, int w, int h) {
            this.loc = new V(x, y);
            this.size = new V(w, h);
        }

        public void fill(Graphics g, Color c) {
            g.setColor(c);
            g.fillRect(this.loc.x, this.loc.y, this.size.x, this.size.y);
        }
        public void draw(Graphics g, Color c) {
            g.setColor(c);
            g.drawRect(this.loc.x, this.loc.y, this.size.x, this.size.y);
        }

        public boolean hit(int x, int y) {
            return (x > this.loc.x && y > this.loc.y && (x < this.loc.x + this.size.x) && (y < this.loc.y + this.size.y));
        }

    }

    //----------------button-----------------

    public static abstract class Button {
        public abstract void act();

        public static final V margin = new V(5, 3);
        public boolean enable = true, boarder = true;
        public String text = "";
        public VS vs = new VS(0, 0, 0, 0);
        public int dyText = 0;
        public LookAndFeel lnf = new LookAndFeel();

        public Button(Button.List list, String text) {
            this.text = text;
            if (list != null) {list.add(this);}
        }

        public void show(Graphics g) {
            if (vs.size.x == 0) {setSize(g);}
            vs.fill(g, lnf.back);
            if (boarder) {vs.draw(g, enable ? lnf.boarder : lnf.disable);}
            g.setColor(enable ? lnf.text : lnf.disable);
            g.drawString(text, vs.loc.x + lnf.margin.x, vs.loc.y + dyText);
        }

        public void setSize(Graphics g) {
            FontMetrics fn = g.getFontMetrics();
            vs.size.set(2 * lnf.margin.x + fn.stringWidth(text), 2 * lnf.margin.y + fn.getHeight());
            dyText = lnf.margin.y + fn.getAscent();
        }

        public void set(int x, int y) {
            vs.loc.set(x, y);
        }

        public boolean hit(int x, int y) {
            return vs.hit(x, y);
        }

        public void click() {
            if (enable) {
                act();
            }
        }

        //-------------LookAndFeel-----------------
        public static class LookAndFeel {
            public static Color text = Color.BLACK, back = Color.WHITE, boarder = Color.BLACK, disable = Color.LIGHT_GRAY;
            public static final V margin = new V(5, 3);

        }


        //-----------List------------------
        public static class List extends ArrayList<Button> {
            public Button hit(int x, int y) {
                for (Button b: this) {
                    if (b.hit(x, y)) {
                        return b;
                    }
                }
                return null;
            }

            public boolean click(int x, int y) {
                Button b = hit(x, y);
                if (b == null) {
                    return false;
                }
                b.click();
                return true;
            }

            public void showAll(Graphics g) {
                for (Button b : this) {
                    b.show(g);
                }
            }
        }
    }
}
