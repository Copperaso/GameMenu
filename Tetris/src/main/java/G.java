import java.awt.Graphics;
import java.awt.*;
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
}
