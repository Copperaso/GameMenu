import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Text extends Window{

  public static Ed ed = new Ed();
  public static Key.Style.List styles = new Key.Style.List();
  public static ArrayList<String> strings = new ArrayList<>();

  public Text() {
    super("text", 1000, 800);
    Key.focus = ed;
    strings.add("Hello, "); styles.add(new Key.Style(Key.Style.tr));
    strings.add("World!"); styles.add(new Key.Style(Key.Style.h));
  }

  public void paintComponent(Graphics g) {
    G.whiteBackground(g);
    ed.show(g);
    styles.layout(g, strings, 100, 200);
    styles.show(g, strings);
  }

  public void keyPressed(KeyEvent ke) {
    Key.focus.keyPressed(ke);
    repaint();
  }

  //--------------------------Ed----------------------
  public static class Ed implements Key.Press{
    public String pre = "", post = "";
    G.VS vs =new G.VS(100, 100, 100, 0);
    public int ascent = 0;
    public int xCur = 0, iCur = 0; // iCur == pre.length, class invariant

    public void left() {
      if (iCur > 0) {
        post = delPreLast() + post;
      }
    }

    public void bksp() {
      // backspace
      if (iCur > 0) {
        delPreLast();
      }
    }

    public void right() {
      if (post.length() > 0) {
        pre = pre + delPostFirst();
        iCur++;}
    }

    public void del() {
      // delete one char right side of the cursor
      if (post.length() > 0) {
        delPostFirst();
      }
    }

    public char delPreLast() {
      // unsafe only call when iCur > 0
      iCur--;
      char res = pre.charAt(iCur);
      pre = pre.substring(0, iCur);
      return res;
    }

    public char delPostFirst() {
      // unsafe only call when post.length > 0
      char res = post.charAt(0);
      post = post.substring(1);
      return res;
    }


    @Override
    public void keyPressed(KeyEvent ke) {
      int vk = ke.getKeyCode();
      if (vk == KeyEvent.VK_LEFT) {
        left();
        return;
      }
      if (vk == KeyEvent.VK_RIGHT) {
        right();
        return;
      }
      if (vk == KeyEvent.VK_BACK_SPACE) {
        bksp();
        return;
      }
      if (vk == KeyEvent.VK_DELETE) {
        del();
        return;
      }
      charEvent(ke);
    }

    public void charEvent(KeyEvent ke) {
      char c = ke.getKeyChar();
      if (c != KeyEvent.CHAR_UNDEFINED) {
        pre += c;
        iCur++;
      }
    }

    public void show(Graphics g) {
      validAscent(g);
      g.setColor(Color.BLUE);
      g.drawString(pre, vs.loc.x, vs.loc.y + ascent);
      xCur = g.getFontMetrics().stringWidth(pre) + vs.loc.x;
      g.setColor(Color.ORANGE);
      g.drawString(post, xCur, vs.loc.y + ascent);
      drawCursor(g);
    }

    public void drawCursor(Graphics g) {
      if (Key.focus != this) {
        return;
      }
      g.setColor(Color.CYAN);
      g.drawLine(xCur, vs.loc.y, xCur, vs.loc.y + vs.size.y);
    }

    public void validAscent(Graphics g) {
      if (ascent == 0) {
        FontMetrics fn = g.getFontMetrics();
        vs.size.y = fn.getHeight();
        ascent = fn.getAscent();
      }
    }

  }

  //--------------------------Key----------------------
  public static class Key {
    public static Press noOne = new Press() {
      @Override
      public void keyPressed(KeyEvent ke) {
      }
    };

    public static Press focus = noOne;

    public interface Press {
      void keyPressed(KeyEvent ke);
    }

    //------------------TF-------------------------------
    public static class TF implements Key.Press {

      @Override
      public void keyPressed(KeyEvent ke) {
        char c = ke.getKeyChar();
        if (c == KeyEvent.CHAR_UNDEFINED) {
          return;
        }
        if (c == '\n') {
          System.out.println();
        } else {
          System.out.print(c);
        }
      }
    }

    //------------------Style--------------------------
    public static class Style {
      public Font font;
      public Color color;
      public G.V v = new G.V(0, 0);
      public static Font tr = new Font("TimesRoman", Font.PLAIN, 25);
      public static Font h = new Font("Helvetica", Font.PLAIN, 8);

      public Style(Font font) {
        this.font = font;
        this.color = new Color(G.rnd(256), G.rnd(256), G.rnd(256));
      }

      public void show(Graphics g, String str) {
        g.setColor(color);
        g.setFont(font);
        g.drawString(str, v.x, v.y);
      }

      public static class List extends ArrayList<Style> {
        public void show(Graphics g, ArrayList<String> strList) {
          for (int i = 0; i < this.size(); i++) {
            this.get(i).show(g, strList.get(i));
          }
        }

        public void layout(Graphics g, ArrayList<String> strList, int x, int y) {
          int ascent = 0;
          for (int i = 0; i < this.size(); i++) {
            Style s = this.get(i);
            g.setFont(s.font);
            s.v.x = x;
            int a = g.getFontMetrics().getAscent();
            if (a > ascent) {
              ascent = a;
            }
            x += g.getFontMetrics().stringWidth(strList.get(i));
          }
          for (int i = 0; i < this.size(); i++) {
            Style s = this.get(i);
            s.v.y = ascent + y;
          }
        }

      }

    }

  }
}
