import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;

/**
 * Farm Management System
 * Author: Aryan Kandula
 *
 * Enhanced personal project — manages store inventory, animal sales,
 * veterinary services, and business reporting for a small family farm.
 *
 * -------------------------------------------------------
 *  Edit History
 * -------------------------------------------------------
 *  [02/16/2026 - 4:22 PM]  Aryan Kandula  - Project created, base class structure
 *  [02/19/2026 - 6:47 PM]  Aryan Kandula  - Store module, Animal Sales module
 *  [02/24/2026 - 5:15 PM]  Aryan Kandula  - Services & Payments, reports, seed data
 *  [02/27/2026 - 7:03 PM]  Aryan Kandula  - Farm-themed GUI, custom dialogs, FarmButton
 *  [02/28/2026 - 5:41 PM]  Aryan Kandula  - File I/O: loadFromCSV() and saveToCSV()
 *  [03/01/2026 - 9:00 AM]  Aryan Kandula  - Auto-save on exit, inline editing, status bar
 *  [03/15/2026 - 2:00 PM]  Aryan Kandula  - Full UI overhaul: fixed table headers, removed
 *                                            broken emoji, auto-save toggle, modern look,
 *                                            fixed row-index bugs with filter row maps
 * -------------------------------------------------------
 */

// ============================================================
//  DATA CLASSES
// ============================================================

class StoreItem {
    private String name;
    private double price;
    private int    quantity;

    public StoreItem(String name, double price, int quantity) {
        this.name = name; this.price = price; this.quantity = quantity;
    }

    public String getName()           { return name; }
    public double getPrice()          { return price; }
    public int    getQuantity()       { return quantity; }
    public void   setName(String n)   { name = n; }
    public void   setPrice(double p)  { price = p; }
    public void   setQuantity(int q)  { quantity = q; }

    public String toCSV() {
        return "STORE,item," + name.replace(",","") + "," + price + "," + quantity + ",,,";
    }
    @Override public String toString() {
        return String.format("%-28s $%-8.2f Qty: %d", name, price, quantity);
    }
}

class Animal {
    private String  type, breed, source;
    private double  salePrice;
    private boolean sold = false;

    public Animal(String type, String breed, double salePrice, String source) {
        this.type = type; this.breed = breed; this.salePrice = salePrice; this.source = source;
    }

    public String  getType()              { return type; }
    public String  getBreed()             { return breed; }
    public double  getSalePrice()         { return salePrice; }
    public boolean isSold()               { return sold; }
    public String  getSource()            { return source; }
    public void    setType(String t)      { type = t; }
    public void    setBreed(String b)     { breed = b; }
    public void    setSalePrice(double p) { salePrice = p; }
    public void    setSource(String s)    { source = s; }
    public void    markSold()             { sold = true; }
    public void    setSold(boolean s)     { sold = s; }

    public String toCSV() {
        return "ANIMAL,animal," + type.replace(",","") + "," + breed.replace(",","") + ","
             + salePrice + "," + source.replace(",","") + "," + sold + ",";
    }
    @Override public String toString() {
        return String.format("%-10s %-20s $%-8.2f %-18s %s",
            type, breed, salePrice, source, sold ? "[SOLD]" : "[Available]");
    }
}

class ServiceRecord {
    private String  customerName, animalType, serviceType, date;
    private double  fee;
    private boolean paid = false;

    public ServiceRecord(String customer, String animal, String service, double fee, String date) {
        this.customerName = customer; this.animalType = animal;
        this.serviceType  = service;  this.fee = fee; this.date = date;
    }

    public String  getCustomerName()         { return customerName; }
    public String  getAnimalType()           { return animalType; }
    public String  getServiceType()          { return serviceType; }
    public String  getDate()                 { return date; }
    public double  getFee()                  { return fee; }
    public boolean isPaid()                  { return paid; }
    public void    setCustomerName(String s) { customerName = s; }
    public void    setAnimalType(String s)   { animalType = s; }
    public void    setServiceType(String s)  { serviceType = s; }
    public void    setDate(String s)         { date = s; }
    public void    setFee(double f)          { fee = f; }
    public void    markPaid()                { paid = true; }
    public void    setPaid(boolean p)        { paid = p; }

    public String toCSV() {
        return "SERVICE,record," + customerName.replace(",","") + "," + animalType.replace(",","") + ","
             + serviceType.replace(",","") + "," + fee + "," + date + "," + paid;
    }
    @Override public String toString() {
        return String.format("%-18s %-10s %-18s $%-7.2f %-12s %s",
            customerName, animalType, serviceType, fee, date, paid ? "[PAID]" : "[UNPAID]");
    }
}

// ============================================================
//  THEME  —  centralized colors and fonts
// ============================================================

class FT {
    // Core palette
    static final Color BARN_RED    = new Color(158, 44,  44);
    static final Color BARN_DARK   = new Color(112, 26,  26);
    static final Color BARN_LIGHT  = new Color(192, 72,  72);
    static final Color GOLD        = new Color(208, 165, 48);
    static final Color GOLD_LIGHT  = new Color(238, 202, 86);
    static final Color GREEN       = new Color(60,  124, 42);
    static final Color GREEN_DARK  = new Color(38,  84,  22);
    static final Color GREEN_LIGHT = new Color(88,  155, 65);
    static final Color SKY         = new Color(128, 190, 226);
    static final Color CREAM       = new Color(255, 250, 228);
    static final Color CREAM_MID   = new Color(250, 240, 206);
    static final Color CREAM_DARK  = new Color(238, 226, 188);
    static final Color BROWN       = new Color(145, 92,  42);
    static final Color BROWN_DARK  = new Color(105, 64,  26);
    static final Color TEXT        = new Color(38,  22,   6);
    static final Color TEXT_MID    = new Color(98,  68,  32);
    static final Color STEEL       = new Color(52,  108, 162);
    static final Color STEEL_DARK  = new Color(32,  78,  128);
    static final Color PURPLE      = new Color(92,  65,  155);
    static final Color BG          = new Color(246, 242, 225);
    static final Color ROW_A       = new Color(255, 252, 235);
    static final Color ROW_B       = new Color(248, 244, 218);

    // Status
    static final Color STATUS_OK   = new Color(38,  145, 62);
    static final Color STATUS_WARN = new Color(192, 136, 18);
    static final Color STATUS_ERR  = new Color(172, 40,  40);

    // Dialog button palette
    static final Color[] PALETTE = {
        BARN_RED, GREEN, BROWN, PURPLE, STEEL,
        new Color(92, 24, 24), new Color(155, 90, 22)
    };

    // Fonts — Dialog is always available on all platforms
    static final Font TITLE   = new Font("Dialog", Font.BOLD,   20);
    static final Font HEADER  = new Font("Dialog", Font.BOLD,   13);
    static final Font BUTTON  = new Font("Dialog", Font.BOLD,   12);
    static final Font BODY    = new Font("Dialog", Font.PLAIN,  13);
    static final Font BODY_B  = new Font("Dialog", Font.BOLD,   13);
    static final Font SMALL   = new Font("Dialog", Font.PLAIN,  11);
    static final Font SMALL_B = new Font("Dialog", Font.BOLD,   11);
    static final Font MONO    = new Font("Monospaced", Font.PLAIN, 12);
    static final Font TBL_HDR = new Font("Dialog", Font.BOLD,   12);
    static final Font TBL_ROW = new Font("Dialog", Font.PLAIN,  12);
}

// ============================================================
//  FARM BUTTON  —  smooth animated gradient
// ============================================================

class FarmButton extends JButton {
    private final Color  base;
    private float        anim = 0f;
    private boolean      hov  = false;
    private javax.swing.Timer animTimer;

    public FarmButton(String text, Color base) {
        super(text);
        this.base = base;
        setFont(FT.BUTTON);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(230, 40));

        animTimer = new javax.swing.Timer(14, e -> {
            float target = hov ? 1f : 0f;
            anim += (target - anim) * 0.2f;
            if (Math.abs(anim - target) < 0.012f) { anim = target; animTimer.stop(); }
            repaint();
        });
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { hov = true;  animTimer.start(); }
            public void mouseExited (MouseEvent e) { hov = false; animTimer.start(); }
        });
    }

    private Color blend(Color a, Color b, float f) {
        f = Math.max(0f, Math.min(1f, f));
        return new Color(
            (int)(a.getRed()   + (b.getRed()   - a.getRed())   * f),
            (int)(a.getGreen() + (b.getGreen() - a.getGreen()) * f),
            (int)(a.getBlue()  + (b.getBlue()  - a.getBlue())  * f));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight(), r = 9;

        // Drop shadow
        g2.setColor(new Color(0, 0, 0, (int)(22 * (1f - anim * 0.35f))));
        g2.fillRoundRect(2, 3, w - 4, h - 2, r, r);

        // Gradient body
        Color top = blend(base, blend(base, Color.WHITE, 0.22f), anim);
        Color bot = blend(base.darker(), base, anim);
        g2.setPaint(new GradientPaint(0, 0, top, 0, h, bot));
        g2.fillRoundRect(0, 0, w - 1, h - 1, r, r);

        // Gloss
        g2.setColor(new Color(255, 255, 255, (int)(42 + 22 * anim)));
        g2.fillRoundRect(1, 1, w - 3, h / 2 - 1, r - 1, r - 1);

        // Border
        g2.setColor(new Color(0, 0, 0, 36));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, 0, w - 2, h - 2, r, r);

        g2.dispose();
        super.paintComponent(g);
    }
}

// ============================================================
//  FARM BACKGROUND  —  animated farm scene for splash
// ============================================================

class FarmBackground extends JPanel {
    private float         cloudX = 0f;
    private javax.swing.Timer cloudTimer;

    public FarmBackground() {
        cloudTimer = new javax.swing.Timer(36, e -> {
            cloudX = (cloudX + 0.32f) % 640f;
            repaint();
        });
        cloudTimer.start();
    }

    public void stopAnimation() { if (cloudTimer != null) cloudTimer.stop(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,    RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth(), h = getHeight();

        // Sky
        g2.setPaint(new GradientPaint(0, 0, new Color(165, 210, 255),
                                      0, (int)(h * 0.54f), new Color(208, 232, 255)));
        g2.fillRect(0, 0, w, h);

        // Sun glow
        g2.setColor(new Color(255, 232, 88, 95));
        g2.fillOval(w - 102, 4, 74, 74);
        g2.setColor(new Color(255, 218, 38, 155));
        g2.fillOval(w - 92, 13, 54, 54);

        // Animated clouds
        int cw = w + 140;
        drawCloud(g2, (int)(28  + cloudX) % cw - 70, 15);
        drawCloud(g2, (int)(162 + cloudX * 0.62f) % cw - 70, 9);
        drawCloud(g2, (int)(w / 2 - 18 + cloudX * 0.44f) % cw - 70, 22);

        // Grass
        g2.setPaint(new GradientPaint(0, (int)(h * 0.51f), FT.GREEN,
                                      0, h, FT.GREEN_DARK));
        g2.fillRect(0, (int)(h * 0.51f), w, h);

        drawBarn(g2, w / 2 - 62, (int)(h * 0.18f));
        drawFence(g2, w, (int)(h * 0.55f));
        drawFlowers(g2, w, h);
        g2.dispose();
    }

    private void drawCloud(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(255, 255, 255, 200));
        g2.fillOval(x,      y + 17, 52, 33);
        g2.fillOval(x + 17, y,      53, 47);
        g2.fillOval(x + 39, y + 12, 40, 30);
    }

    private void drawBarn(Graphics2D g2, int x, int y) {
        g2.setPaint(new GradientPaint(x, y, FT.BARN_LIGHT, x + 124, y + 80, FT.BARN_DARK));
        g2.fillRect(x, y + 40, 124, 76);

        int[] rx = {x - 12, x + 62, x + 136};
        int[] ry = {y + 44,  y + 4,  y + 44};
        g2.setColor(FT.BROWN_DARK);
        g2.fillPolygon(rx, ry, 3);
        g2.setColor(new Color(202, 158, 92));
        g2.setStroke(new BasicStroke(2f));
        g2.drawPolygon(rx, ry, 3);

        g2.setColor(FT.BROWN);
        g2.fillRoundRect(x + 44, y + 80, 36, 36, 4, 4);
        g2.setColor(FT.GOLD);
        g2.fillOval(x + 58, y + 96, 6, 6);

        for (int wx : new int[]{x + 14, x + 86}) {
            g2.setColor(FT.SKY);
            g2.fillRect(wx, y + 54, 22, 17);
            g2.setColor(FT.BROWN_DARK);
            g2.setStroke(new BasicStroke(1.8f));
            g2.drawRect(wx, y + 54, 22, 17);
            g2.drawLine(wx + 11, y + 54, wx + 11, y + 71);
            g2.drawLine(wx,      y + 62, wx + 22, y + 62);
        }
    }

    private void drawFence(Graphics2D g2, int w, int y) {
        g2.setColor(new Color(236, 226, 195));
        g2.setStroke(new BasicStroke(2.8f));
        g2.drawLine(0, y,      w, y);
        g2.drawLine(0, y + 17, w, y + 17);
        g2.setStroke(new BasicStroke(1f));
        for (int x = 8; x < w; x += 38) {
            g2.setColor(new Color(243, 232, 205));
            g2.fillRoundRect(x, y - 9, 7, 33, 3, 3);
            g2.setColor(new Color(198, 182, 150));
            g2.drawRoundRect(x, y - 9, 7, 33, 3, 3);
        }
    }

    private void drawFlowers(Graphics2D g2, int w, int h) {
        Color[] petals = {
            new Color(218, 55, 55), Color.YELLOW,
            new Color(255, 155, 195), new Color(255, 135, 38),
            new Color(165, 95, 205)
        };
        int[] xs = {18, 56, w-42, w-80, w/2-58, w/2+38, 12, w-25, w/2};
        int[] ys = {h-27, h-22, h-25, h-20, h-27, h-22, h-21, h-27, h-23};
        for (int i = 0; i < xs.length; i++) {
            g2.setColor(new Color(38, 98, 22));
            g2.setStroke(new BasicStroke(1.8f));
            g2.drawLine(xs[i], ys[i], xs[i], ys[i] + 20);
            g2.setColor(petals[i % petals.length]);
            for (int p = 0; p < 5; p++) {
                double a = p * (2 * Math.PI / 5);
                g2.fillOval(xs[i] + (int)(7 * Math.cos(a)) - 4,
                            ys[i] + (int)(7 * Math.sin(a)) - 4, 9, 9);
            }
            g2.setColor(FT.GOLD_LIGHT);
            g2.fillOval(xs[i] - 4, ys[i] - 4, 8, 8);
        }
    }
}

// ============================================================
//  CUSTOM TABLE HEADER RENDERER
//  Paints its own background so LAF cannot override it
// ============================================================

class FarmHeaderRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(
            JTable t, Object val, boolean sel, boolean foc, int row, int col) {

        final String text = val == null ? "" : val.toString();
        JLabel lbl = new JLabel(text, SwingConstants.LEFT) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Dark gradient background
                g2.setPaint(new GradientPaint(0, 0, new Color(98, 34, 34),
                                              0, getHeight(), new Color(70, 19, 19)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Gold accent line at bottom
                g2.setColor(new Color(196, 155, 40));
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                // Subtle right divider
                g2.setColor(new Color(255, 255, 255, 20));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(getWidth() - 1, 3, getWidth() - 1, getHeight() - 4);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(FT.TBL_HDR);
        lbl.setForeground(Color.WHITE);
        lbl.setOpaque(false);
        lbl.setBorder(new EmptyBorder(0, 10, 0, 8));
        lbl.setPreferredSize(new Dimension(lbl.getPreferredSize().width, 34));
        return lbl;
    }
}

// ============================================================
//  FARM DIALOG  —  themed modal dialogs
// ============================================================

class FarmDialog {

    static void message(String title, String body) {
        JDialog d = base(title, 530, 375);
        JPanel  p = panel(title);

        JTextArea ta = new JTextArea(body);
        ta.setFont(FT.MONO);
        ta.setEditable(false);
        ta.setLineWrap(false);
        ta.setBackground(new Color(255, 253, 236));
        ta.setForeground(FT.TEXT);
        ta.setBorder(new EmptyBorder(10, 14, 6, 14));

        JScrollPane sp = new JScrollPane(ta);
        sp.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(8, 14, 4, 14),
            new LineBorder(FT.BROWN, 1, true)));
        sp.getViewport().setBackground(new Color(255, 253, 236));
        p.add(sp, BorderLayout.CENTER);

        JPanel row = row();
        FarmButton ok = btn("  OK  ", FT.GREEN, 108);
        ok.addActionListener(e -> d.dispose());
        row.add(ok);
        p.add(row, BorderLayout.SOUTH);
        d.add(p);
        d.setVisible(true);
    }

    static int option(String title, String prompt, String[] options) {
        final int[] res = {-1};
        JDialog d = base(title, 490, 120 + options.length * 50);
        JPanel  p = panel(title);

        JLabel lbl = new JLabel(prompt, SwingConstants.CENTER);
        lbl.setFont(FT.BODY);
        lbl.setForeground(FT.TEXT);
        lbl.setBorder(new EmptyBorder(10, 0, 4, 0));
        p.add(lbl, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 1, 0, 7));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(4, 26, 14, 26));
        for (int i = 0; i < options.length; i++) {
            final int idx = i;
            FarmButton b = new FarmButton(options[i], FT.PALETTE[i % FT.PALETTE.length]);
            b.addActionListener(e -> { res[0] = idx; d.dispose(); });
            grid.add(b);
        }
        p.add(grid, BorderLayout.CENTER);
        d.add(p);
        d.pack();
        d.setLocationRelativeTo(null);
        d.setVisible(true);
        return res[0];
    }

    static String input(String title, String prompt) { return input(title, prompt, ""); }

    static String input(String title, String prompt, String prefill) {
        final String[] res = {null};
        JDialog d = base(title, 460, 232);
        JPanel  p = panel(title);

        JLabel lbl = new JLabel("<html><div style='text-align:center'>"
            + prompt.replace("\n", "<br>") + "</div></html>", SwingConstants.CENTER);
        lbl.setFont(FT.BODY);
        lbl.setForeground(FT.TEXT);
        lbl.setBorder(new EmptyBorder(12, 16, 4, 16));
        p.add(lbl, BorderLayout.NORTH);

        JTextField field = styledField(prefill);
        JPanel mid = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
        mid.setOpaque(false);
        mid.add(field);
        p.add(mid, BorderLayout.CENTER);

        JPanel row = row();
        FarmButton ok  = btn("Confirm", FT.GREEN,    122);
        FarmButton no  = btn("Cancel",  FT.BARN_RED, 122);
        ok.addActionListener   (e -> { res[0] = field.getText(); d.dispose(); });
        no.addActionListener   (e -> d.dispose());
        field.addActionListener(e -> { res[0] = field.getText(); d.dispose(); });
        row.add(ok); row.add(no);
        p.add(row, BorderLayout.SOUTH);
        d.add(p);
        SwingUtilities.invokeLater(field::requestFocusInWindow);
        d.setVisible(true);
        return res[0];
    }

    static boolean confirm(String title, String prompt) {
        final boolean[] res = {false};
        JDialog d = base(title, 440, 216);
        JPanel  p = panel(title);

        JLabel lbl = new JLabel("<html><div style='text-align:center'>"
            + prompt.replace("\n", "<br>") + "</div></html>", SwingConstants.CENTER);
        lbl.setFont(FT.BODY);
        lbl.setForeground(FT.TEXT);
        lbl.setBorder(new EmptyBorder(14, 18, 8, 18));
        p.add(lbl, BorderLayout.CENTER);

        JPanel row = row();
        FarmButton yes = btn("Yes, Confirm", FT.GREEN,    148);
        FarmButton no  = btn("Cancel",       FT.BARN_RED, 118);
        yes.addActionListener(e -> { res[0] = true; d.dispose(); });
        no.addActionListener (e -> d.dispose());
        row.add(yes); row.add(no);
        p.add(row, BorderLayout.SOUTH);
        d.add(p);
        d.setVisible(true);
        return res[0];
    }

    // ── Private helpers ──────────────────────────────────────

    private static JDialog base(String title, int w, int h) {
        JDialog d = new JDialog();
        d.setTitle(title);
        d.setSize(w, h);
        d.setModal(true);
        d.setResizable(false);
        d.setLocationRelativeTo(null);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        return d;
    }

    static JPanel panel(String title) {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, FT.CREAM, 0, getHeight(), FT.CREAM_DARK));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        p.setBorder(new CompoundBorder(new LineBorder(FT.BROWN, 2), new EmptyBorder(0, 0, 6, 0)));

        // Header band — painted, so LAF cannot hide the text
        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 9)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, FT.BARN_LIGHT, getWidth(), 0, FT.BARN_DARK));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(FT.GOLD);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        hdr.setOpaque(false);
        JLabel tl = new JLabel("  " + title);
        tl.setFont(FT.HEADER);
        tl.setForeground(Color.WHITE);
        hdr.add(tl);
        p.add(hdr, BorderLayout.NORTH);
        return p;
    }

    private static JPanel row() {
        JPanel r = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        r.setOpaque(false);
        return r;
    }

    static FarmButton btn(String text, Color c, int w) {
        FarmButton b = new FarmButton(text, c);
        b.setPreferredSize(new Dimension(w, 38));
        return b;
    }

    static JTextField styledField(String prefill) {
        JTextField f = new JTextField(prefill, 24);
        f.setFont(FT.BODY);
        f.setBackground(FT.CREAM);
        f.setForeground(FT.TEXT);
        f.setCaretColor(FT.BARN_RED);
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(FT.BROWN, 2, true),
            new EmptyBorder(4, 8, 4, 8)));
        f.selectAll();
        return f;
    }
}

// ============================================================
//  STATUS BAR
// ============================================================

class StatusBar extends JPanel {
    private final JLabel      msgLabel;
    private final JLabel      saveLabel;
    private       boolean     autoSaveOn = true;
    private javax.swing.Timer clearTimer;

    public StatusBar() {
        setLayout(new BorderLayout(10, 0));
        setBorder(new EmptyBorder(3, 14, 3, 14));
        setPreferredSize(new Dimension(0, 30));
        setOpaque(false);

        msgLabel  = mkLabel("Ready", new Color(255, 240, 185));
        saveLabel = mkLabel("Auto-Save: ON", new Color(138, 228, 138));

        JButton toggle = new JButton("Toggle");
        toggle.setFont(FT.SMALL_B);
        toggle.setForeground(new Color(255, 222, 128));
        toggle.setBackground(new Color(128, 34, 34));
        toggle.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(198, 118, 48), 1, true),
            new EmptyBorder(1, 8, 1, 8)));
        toggle.setFocusPainted(false);
        toggle.setContentAreaFilled(true);
        toggle.setOpaque(true);
        toggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggle.addActionListener(e -> {
            autoSaveOn = !autoSaveOn;
            refreshSaveLabel();
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);
        right.add(saveLabel);
        right.add(toggle);

        add(msgLabel, BorderLayout.WEST);
        add(right,    BorderLayout.EAST);

        clearTimer = new javax.swing.Timer(4500, e -> { msgLabel.setText("Ready"); clearTimer.stop(); });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(0, 0, FT.BARN_DARK, getWidth(), 0, new Color(82, 20, 20)));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(FT.GOLD);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(0, 0, getWidth(), 0);
        g2.dispose();
    }

    private JLabel mkLabel(String text, Color fg) {
        JLabel l = new JLabel(text);
        l.setFont(FT.SMALL_B);
        l.setForeground(fg);
        return l;
    }

    private void refreshSaveLabel() {
        if (autoSaveOn) {
            saveLabel.setText("Auto-Save: ON");
            saveLabel.setForeground(new Color(138, 228, 138));
        } else {
            saveLabel.setText("Auto-Save: OFF");
            saveLabel.setForeground(new Color(255, 138, 115));
        }
    }

    public boolean isAutoSaveOn() { return autoSaveOn; }

    public void setMessage(String msg) { msgLabel.setText(msg); clearTimer.restart(); }

    public void notifySaved(boolean ok) {
        saveLabel.setText(ok ? "Saved  [OK]" : "Save Failed!");
        saveLabel.setForeground(ok ? new Color(138, 228, 138) : new Color(255, 115, 95));
        new javax.swing.Timer(2600, e -> { refreshSaveLabel(); ((javax.swing.Timer)e.getSource()).stop(); }).start();
    }

    public void notifyDirty() {
        if (!autoSaveOn) {
            saveLabel.setText("Unsaved Changes");
            saveLabel.setForeground(new Color(255, 202, 85));
        }
    }
}

// ============================================================
//  MAIN APPLICATION
// ============================================================

public class FarmManagementSystem_Enhanced_AryanKandula {

    static final ArrayList<StoreItem>     inventory = new ArrayList<>();
    static final ArrayList<Animal>        animals   = new ArrayList<>();
    static final ArrayList<ServiceRecord> services  = new ArrayList<>();
    static double  revenue        = 0.0;
    static boolean unsavedChanges = false;

    static final String CSV_FILE = "farm_data.csv";

    static JFrame    mainFrame;
    static StatusBar statusBar;

    // Filter state
    static boolean showAllAnimals  = true;
    static boolean showAllServices = true;

    // Row maps (visible table row -> actual list index when filtered)
    static final ArrayList<Integer> animalRowMap  = new ArrayList<>();
    static final ArrayList<Integer> serviceRowMap = new ArrayList<>();

    // ── Entry point ───────────────────────────────────────────

    public static void main(String[] args) {
        // Cross-platform LAF ensures our custom painters are always used
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}

        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        if (!loadFromCSV()) seedData();

        SwingUtilities.invokeLater(() -> {
            showWelcome();
            buildMainWindow();
        });
    }

    // ── File I/O ──────────────────────────────────────────────

    static boolean loadFromCSV() {
        File file = new File(CSV_FILE);
        if (!file.exists()) return false;
        inventory.clear(); animals.clear(); services.clear(); revenue = 0.0;
        try (Scanner sc = new Scanner(file)) {
            if (!sc.hasNextLine()) return false;
            sc.nextLine(); // skip header row
            while (sc.hasNextLine()) {
                String   line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] p    = line.split(",", -1);

                if (p[0].equals("STORE") && p.length >= 5) {
                    inventory.add(new StoreItem(p[2],
                        Double.parseDouble(p[3]), Integer.parseInt(p[4].trim())));

                } else if (p[0].equals("ANIMAL") && p.length >= 7) {
                    Animal a = new Animal(p[2], p[3], Double.parseDouble(p[4]), p[5]);
                    a.setSold(Boolean.parseBoolean(p[6].trim()));
                    if (a.isSold()) revenue += a.getSalePrice();
                    animals.add(a);

                } else if (p[0].equals("SERVICE") && p.length >= 8) {
                    ServiceRecord sr = new ServiceRecord(
                        p[2], p[3], p[4], Double.parseDouble(p[5]), p[6]);
                    if (Boolean.parseBoolean(p[7].trim())) { sr.markPaid(); revenue += sr.getFee(); }
                    services.add(sr);
                }
            }
            return true;
        } catch (Exception ex) {
            inventory.clear(); animals.clear(); services.clear(); revenue = 0.0;
            return false;
        }
    }

    static boolean saveToCSVSilent() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE))) {
            pw.println("TYPE,CATEGORY,FIELD1,FIELD2,FIELD3,FIELD4,FIELD5,FIELD6");
            for (StoreItem     i  : inventory) pw.println(i.toCSV());
            for (Animal        a  : animals)   pw.println(a.toCSV());
            for (ServiceRecord sr : services)  pw.println(sr.toCSV());
            unsavedChanges = false;
            return true;
        } catch (IOException ex) { return false; }
    }

    static void saveWithFeedback() {
        boolean ok = saveToCSVSilent();
        if (statusBar != null) {
            statusBar.notifySaved(ok);
            statusBar.setMessage(ok
                ? "Data saved to " + CSV_FILE + " successfully."
                : "ERROR: Could not write to " + CSV_FILE + " — check permissions.");
        }
    }

    static void markDirty() {
        unsavedChanges = true;
        if (statusBar != null) statusBar.notifyDirty();
    }

    // ── Welcome splash ────────────────────────────────────────

    static void showWelcome() {
        JDialog splash = new JDialog();
        splash.setUndecorated(true);
        splash.setSize(492, 326);
        splash.setLocationRelativeTo(null);
        splash.setModal(true);

        FarmBackground bg = new FarmBackground();
        bg.setLayout(new BorderLayout());

        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 122));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.setColor(new Color(196, 152, 48, 155));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 21, 21);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(24, 40, 24, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        // Title — plain text, no emoji
        JLabel titleLbl = new JLabel("FARM MANAGEMENT SYSTEM", SwingConstants.CENTER);
        titleLbl.setFont(new Font("Dialog", Font.BOLD, 20));
        titleLbl.setForeground(Color.WHITE);
        card.add(titleLbl, gbc);

        // Gold separator
        JSeparator sep = new JSeparator() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(196, 152, 48, 175));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(0, 0, getWidth(), 0);
                g2.dispose();
            }
        };
        sep.setPreferredSize(new Dimension(295, 3));
        card.add(sep, gbc);

        JLabel authLbl = new JLabel("by Aryan Kandula", SwingConstants.CENTER);
        authLbl.setFont(new Font("Dialog", Font.ITALIC | Font.BOLD, 14));
        authLbl.setForeground(new Color(255, 230, 135));
        card.add(authLbl, gbc);

        JLabel catsLbl = new JLabel("Inventory  *  Animals  *  Services  *  Reports",
                                     SwingConstants.CENTER);
        catsLbl.setFont(new Font("Dialog", Font.PLAIN, 11));
        catsLbl.setForeground(new Color(192, 218, 255));
        card.add(catsLbl, gbc);

        gbc.insets = new Insets(16, 0, 4, 0);
        FarmButton enter = new FarmButton("Enter Farm", FT.BARN_RED);
        enter.setPreferredSize(new Dimension(185, 44));
        enter.addActionListener(e -> { bg.stopAnimation(); splash.dispose(); });
        card.add(enter, gbc);

        bg.add(card, BorderLayout.CENTER);
        splash.add(bg);
        splash.setVisible(true);
    }

    // ── Main window ───────────────────────────────────────────

    static JTable storeTable,   animalTable,   serviceTable;
    static DefaultTableModel storeModel, animalModel, serviceModel;
    static JTextArea reportArea;

    static void buildMainWindow() {
        mainFrame = new JFrame("Farm Management System  -  Aryan Kandula");
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.setSize(880, 655);
        mainFrame.setMinimumSize(new Dimension(740, 520));
        mainFrame.setLocationRelativeTo(null);

        mainFrame.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { handleExit(); }
        });

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(FT.BG);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildTabs(),   BorderLayout.CENTER);

        statusBar = new StatusBar();
        root.add(statusBar, BorderLayout.SOUTH);

        mainFrame.setContentPane(root);
        mainFrame.setVisible(true);

        statusBar.setMessage("Loaded  " + inventory.size() + " items  |  "
            + animals.size() + " animals  |  " + services.size() + " services");
    }

    // ── Exit handler  (called by button AND window-close) ──────

    static void handleExit() {
        boolean autoSave = statusBar != null && statusBar.isAutoSaveOn();
        if (unsavedChanges && autoSave) {
            boolean ok = saveToCSVSilent();
            if (!ok) {
                boolean exit = FarmDialog.confirm("Exit — Save Failed",
                    "Auto-save could not write to " + CSV_FILE + ".\nExit anyway?");
                if (!exit) return;
            }
        } else if (unsavedChanges) {
            String[] opts = {"Save and Exit", "Exit Without Saving", "Cancel"};
            int ch = JOptionPane.showOptionDialog(mainFrame,
                "You have unsaved changes. What would you like to do?",
                "Unsaved Changes",
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                null, opts, opts[0]);
            if (ch == 2 || ch < 0) return;
            if (ch == 0) saveToCSVSilent();
        }
        System.exit(0);
    }

    // ── Header ─────────────────────────────────────────────────

    static JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, FT.BARN_LIGHT, getWidth(), getHeight(), FT.BARN_DARK));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(FT.GOLD);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        h.setPreferredSize(new Dimension(0, 56));
        h.setBorder(new EmptyBorder(0, 22, 0, 22));
        h.setOpaque(false);

        JLabel title = new JLabel("Farm Management System");
        title.setFont(FT.TITLE);
        title.setForeground(Color.WHITE);

        // Right-side panel: author label + exit button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        JLabel sub = new JLabel("Aryan Kandula");
        sub.setFont(new Font("Dialog", Font.ITALIC, 12));
        sub.setForeground(new Color(255, 226, 145));

        // Exit button — styled to sit neatly in the header bar
        JButton exitBtn = new JButton("Exit") {
            private boolean hov = false;
            {
                setFont(new Font("Dialog", Font.BOLD, 12));
                setForeground(Color.WHITE);
                setFocusPainted(false);
                setBorderPainted(false);
                setContentAreaFilled(false);
                setOpaque(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setPreferredSize(new Dimension(72, 30));
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                    public void mouseExited (MouseEvent e) { hov = false; repaint(); }
                });
                addActionListener(e -> handleExit());
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = hov ? new Color(200, 50, 50) : new Color(165, 38, 38);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.setColor(new Color(255, 255, 255, 55));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        rightPanel.add(sub);
        rightPanel.add(exitBtn);

        h.add(title,       BorderLayout.WEST);
        h.add(rightPanel,  BorderLayout.EAST);
        return h;
    }

    // ── Tabs ────────────────────────────────────────────────────

    static JTabbedPane buildTabs() {
        // Set properties BEFORE creating the tabbed pane
        UIManager.put("TabbedPane.selected",           FT.CREAM);
        UIManager.put("TabbedPane.background",         FT.CREAM_DARK);
        UIManager.put("TabbedPane.foreground",         FT.TEXT);
        UIManager.put("TabbedPane.selectedForeground", FT.BARN_RED);
        UIManager.put("TabbedPane.light",              FT.BROWN);
        UIManager.put("TabbedPane.highlight",          FT.CREAM);
        UIManager.put("TabbedPane.tabInsets",          new Insets(6, 16, 6, 16));
        UIManager.put("TabbedPane.font",               FT.BUTTON);

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(FT.BUTTON);
        tabs.setBackground(FT.BG);
        tabs.setOpaque(true);

        // Plain text tab labels — no emoji (avoids box-character rendering on some JVMs)
        tabs.addTab("  Store  ",     buildStorePanel());
        tabs.addTab("  Animals  ",   buildAnimalsPanel());
        tabs.addTab("  Services  ",  buildServicesPanel());
        tabs.addTab("  Reports  ",   buildReportsPanel());
        return tabs;
    }

    // ══════════════════════════════════════════
    //  MODULE 1 — Store & Inventory
    // ══════════════════════════════════════════

    static JPanel buildStorePanel() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(FT.BG);

        storeModel = new DefaultTableModel(new String[]{"Item Name","Price","Quantity","Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        storeTable = new JTable(storeModel);
        applyTableStyle(storeTable);
        storeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        storeTable.getColumnModel().getColumn(0).setPreferredWidth(272);
        storeTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        storeTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        storeTable.getColumnModel().getColumn(3).setPreferredWidth(118);

        storeTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean isSel, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, val, isSel, hasFocus, row, col);
                setFont(FT.TBL_ROW);
                setBorder(new EmptyBorder(0, 9, 0, 9));
                setHorizontalAlignment(col == 0 ? LEFT : CENTER);
                if (!isSel && row < inventory.size()) {
                    int qty = inventory.get(row).getQuantity();
                    setBackground(qty == 0 ? new Color(255, 222, 222)
                                : qty <= 3 ? new Color(255, 246, 202)
                                :            (row % 2 == 0 ? FT.ROW_A : FT.ROW_B));
                    setForeground(FT.TEXT);
                } else if (isSel) {
                    setBackground(new Color(178, 138, 40, 175));
                    setForeground(FT.TEXT);
                }
                return this;
            }
        });

        p.add(tableScroll(storeTable), BorderLayout.CENTER);
        p.add(buildSide(
            new String[]{"ACTIONS", "MANAGE"},
            new String[][]{{"Add Item", "Sell Item", "Restock Item"},
                           {"Edit Selected", "Remove Selected", "Refresh"}},
            new Color[][]{{FT.GREEN, FT.BARN_RED, FT.BROWN},
                          {FT.STEEL, new Color(128, 36, 36), new Color(82, 82, 82)}},
            new Runnable[]{
                () -> { addItem();     refreshStoreTable(); },
                () -> { sellItem();    refreshStoreTable(); },
                () -> { restockItem(); refreshStoreTable(); },
                () -> { editItem();    refreshStoreTable(); },
                () -> { deleteItem();  refreshStoreTable(); },
                ()  -> refreshStoreTable()
            }
        ), BorderLayout.EAST);

        refreshStoreTable();
        return p;
    }

    static void refreshStoreTable() {
        storeModel.setRowCount(0);
        for (StoreItem i : inventory) {
            storeModel.addRow(new Object[]{
                i.getName(),
                String.format("$%.2f", i.getPrice()),
                String.valueOf(i.getQuantity()),
                i.getQuantity() == 0 ? "Out of Stock" : i.getQuantity() <= 3 ? "Low Stock" : "In Stock"
            });
        }
        if (statusBar != null)
            statusBar.setMessage("Inventory: " + inventory.size() + " items tracked.");
    }

    static void addItem() {
        String name = FarmDialog.input("Add New Item", "Item name:");
        if (name == null || name.isBlank()) return;
        String ps = FarmDialog.input("Add New Item", "Price ($):");
        if (ps == null) return;
        String qs = FarmDialog.input("Add New Item", "Starting quantity:");
        if (qs == null) return;
        try {
            inventory.add(new StoreItem(name.trim(), Double.parseDouble(ps.trim()), Integer.parseInt(qs.trim())));
            markDirty();
            if (statusBar != null) statusBar.setMessage("Added: " + name.trim());
        } catch (NumberFormatException e) {
            FarmDialog.message("Input Error", "Price and quantity must be valid numbers.\nExample: price = 12.99   quantity = 10");
        }
    }

    static void sellItem() {
        if (inventory.isEmpty()) { FarmDialog.message("Store", "No items in inventory yet."); return; }
        String[] labels = inventory.stream()
            .map(i -> i.getName() + "   (Qty: " + i.getQuantity() + " | $" + String.format("%.2f", i.getPrice()) + ")")
            .toArray(String[]::new);
        int idx = FarmDialog.option("Sell Item", "Select item to sell:", labels);
        if (idx < 0) return;
        StoreItem item = inventory.get(idx);
        if (item.getQuantity() == 0) { FarmDialog.message("Out of Stock", "\"" + item.getName() + "\" has no stock remaining."); return; }
        String qs = FarmDialog.input("Sell Item", "Quantity to sell  (max " + item.getQuantity() + "):", "1");
        if (qs == null) return;
        try {
            int qty = Integer.parseInt(qs.trim());
            if (qty <= 0 || qty > item.getQuantity()) { FarmDialog.message("Error", "Quantity must be between 1 and " + item.getQuantity() + "."); return; }
            item.setQuantity(item.getQuantity() - qty);
            double sale = qty * item.getPrice();
            revenue += sale;
            markDirty();
            if (statusBar != null) statusBar.setMessage("Sold " + qty + "x " + item.getName() + " — $" + String.format("%.2f", sale));
        } catch (NumberFormatException e) { FarmDialog.message("Error", "Please enter a valid whole number."); }
    }

    static void restockItem() {
        if (inventory.isEmpty()) { FarmDialog.message("Store", "No items in inventory yet."); return; }
        String[] names = inventory.stream()
            .map(i -> i.getName() + "   (Current qty: " + i.getQuantity() + ")")
            .toArray(String[]::new);
        int idx = FarmDialog.option("Restock Item", "Select item to restock:", names);
        if (idx < 0) return;
        StoreItem item = inventory.get(idx);
        String qs = FarmDialog.input("Restock", "Units to add:", "10");
        if (qs == null) return;
        try {
            int add = Integer.parseInt(qs.trim());
            if (add <= 0) { FarmDialog.message("Error", "Please enter a positive number."); return; }
            item.setQuantity(item.getQuantity() + add);
            markDirty();
            if (statusBar != null) statusBar.setMessage("Restocked \"" + item.getName() + "\" - New total: " + item.getQuantity());
        } catch (NumberFormatException e) { FarmDialog.message("Error", "Please enter a valid whole number."); }
    }

    static void editItem() {
        int row = storeTable.getSelectedRow();
        if (row < 0 || row >= inventory.size()) { FarmDialog.message("Edit Item", "Please click a row in the table first, then click Edit."); return; }
        StoreItem item = inventory.get(row);
        String name = FarmDialog.input("Edit Item", "Item name:", item.getName());
        if (name == null) return;
        String ps = FarmDialog.input("Edit Item", "Price ($):", String.valueOf(item.getPrice()));
        if (ps == null) return;
        String qs = FarmDialog.input("Edit Item", "Quantity:", String.valueOf(item.getQuantity()));
        if (qs == null) return;
        try {
            if (!name.isBlank()) item.setName(name.trim());
            item.setPrice(Double.parseDouble(ps.trim()));
            item.setQuantity(Integer.parseInt(qs.trim()));
            markDirty();
            if (statusBar != null) statusBar.setMessage("Updated: " + item.getName());
        } catch (NumberFormatException e) { FarmDialog.message("Error", "Invalid number entered. Price and quantity must be numbers."); }
    }

    static void deleteItem() {
        int row = storeTable.getSelectedRow();
        if (row < 0 || row >= inventory.size()) { FarmDialog.message("Remove Item", "Please click a row in the table first."); return; }
        StoreItem item = inventory.get(row);
        if (FarmDialog.confirm("Remove Item", "Remove \"" + item.getName() + "\" from inventory?\nThis cannot be undone.")) {
            inventory.remove(row);
            markDirty();
            if (statusBar != null) statusBar.setMessage("Removed: " + item.getName());
        }
    }

    // ══════════════════════════════════════════
    //  MODULE 2 — Animal Sales
    // ══════════════════════════════════════════

    static JPanel buildAnimalsPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(FT.BG);

        animalModel = new DefaultTableModel(new String[]{"Type","Breed","Price","Source","Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        animalTable = new JTable(animalModel);
        applyTableStyle(animalTable);
        animalTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        animalTable.getColumnModel().getColumn(0).setPreferredWidth(92);
        animalTable.getColumnModel().getColumn(1).setPreferredWidth(162);
        animalTable.getColumnModel().getColumn(2).setPreferredWidth(88);
        animalTable.getColumnModel().getColumn(3).setPreferredWidth(158);
        animalTable.getColumnModel().getColumn(4).setPreferredWidth(92);

        animalTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean isSel, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, val, isSel, hasFocus, row, col);
                setFont(FT.TBL_ROW);
                setBorder(new EmptyBorder(0, 9, 0, 9));
                setHorizontalAlignment((col == 2 || col == 4) ? CENTER : LEFT);
                if (!isSel && row < animalRowMap.size()) {
                    boolean sold = animals.get(animalRowMap.get(row)).isSold();
                    setBackground(sold ? new Color(236, 236, 236) : (row % 2 == 0 ? new Color(238, 252, 230) : new Color(228, 245, 220)));
                    setForeground(sold ? new Color(142, 122, 102) : FT.TEXT);
                } else if (isSel) {
                    setBackground(new Color(178, 138, 40, 175));
                    setForeground(FT.TEXT);
                }
                return this;
            }
        });

        p.add(tableScroll(animalTable), BorderLayout.CENTER);
        p.add(buildSide(
            new String[]{"ACTIONS", "MANAGE", "FILTER"},
            new String[][]{{"Add Animal", "Sell Animal"},
                           {"Edit Selected", "Remove Selected"},
                           {"Available Only", "Show All"}},
            new Color[][]{{FT.GREEN, FT.BARN_RED},
                          {FT.STEEL, new Color(128, 36, 36)},
                          {FT.GREEN_DARK, new Color(82, 82, 82)}},
            new Runnable[]{
                () -> { addAnimal();               refreshAnimalTable(); },
                () -> { sellAnimal();              refreshAnimalTable(); },
                () -> { editAnimal();              refreshAnimalTable(); },
                () -> { removeAnimal();            refreshAnimalTable(); },
                () -> { showAllAnimals = false;    refreshAnimalTable(); },
                () -> { showAllAnimals = true;     refreshAnimalTable(); }
            }
        ), BorderLayout.EAST);

        refreshAnimalTable();
        return p;
    }

    static void refreshAnimalTable() {
        animalModel.setRowCount(0);
        animalRowMap.clear();
        long available = 0;
        for (int i = 0; i < animals.size(); i++) {
            Animal a = animals.get(i);
            if (showAllAnimals || !a.isSold()) {
                animalRowMap.add(i);
                animalModel.addRow(new Object[]{
                    a.getType(), a.getBreed(),
                    String.format("$%.2f", a.getSalePrice()),
                    a.getSource(),
                    a.isSold() ? "Sold" : "Available"
                });
            }
            if (!a.isSold()) available++;
        }
        if (statusBar != null)
            statusBar.setMessage("Animals: " + animals.size() + " total  |  " + available + " available");
    }

    static void addAnimal() {
        String[] types = {"Duck","Chicken","Hamster","Rabbit","Goat","Pig","Other"};
        int ti = FarmDialog.option("Animal Type", "Select animal type:", types);
        if (ti < 0) return;
        String breed = FarmDialog.input("Add Animal", "Breed / description:");
        if (breed == null || breed.isBlank()) return;
        String ps = FarmDialog.input("Add Animal", "Sale price ($):");
        if (ps == null) return;
        String[] srcOpts = {"Farm  (own animals)", "Local Breeder  (specialty resale)"};
        int si = FarmDialog.option("Animal Source", "Where is this animal from?", srcOpts);
        if (si < 0) return;
        String source = "Farm";
        if (si == 1) {
            String b = FarmDialog.input("Breeder Name", "Enter breeder name:");
            source = (b == null || b.isBlank()) ? "Local Breeder" : b.trim();
        }
        try {
            animals.add(new Animal(types[ti], breed.trim(), Double.parseDouble(ps.trim()), source));
            markDirty();
            if (statusBar != null) statusBar.setMessage("Added: " + types[ti] + " (" + breed.trim() + ")");
        } catch (NumberFormatException e) { FarmDialog.message("Error", "Please enter a valid price (e.g. 25.00)."); }
    }

    static void sellAnimal() {
        ArrayList<Animal>  avail = new ArrayList<>();
        ArrayList<Integer> idxs  = new ArrayList<>();
        for (int i = 0; i < animals.size(); i++) {
            if (!animals.get(i).isSold()) { avail.add(animals.get(i)); idxs.add(i); }
        }
        if (avail.isEmpty()) { FarmDialog.message("Animal Sales", "No animals are currently available for sale."); return; }
        String[] labels = avail.stream()
            .map(a -> a.getType() + " - " + a.getBreed() + "   ($" + String.format("%.2f", a.getSalePrice()) + ")")
            .toArray(String[]::new);
        int sel = FarmDialog.option("Sell Animal", "Select animal to sell:", labels);
        if (sel < 0) return;
        Animal a = avail.get(sel);
        if (FarmDialog.confirm("Confirm Sale",
                a.getType() + " - " + a.getBreed()
                + "\nPrice: $" + String.format("%.2f", a.getSalePrice())
                + "\n\nConfirm this sale?")) {
            a.markSold(); revenue += a.getSalePrice(); markDirty();
            if (statusBar != null) statusBar.setMessage(a.getType() + " sold for $" + String.format("%.2f", a.getSalePrice()));
        }
    }

    static void editAnimal() {
        int visRow = animalTable.getSelectedRow();
        if (visRow < 0 || visRow >= animalRowMap.size()) { FarmDialog.message("Edit Animal", "Please click an animal row in the table first."); return; }
        Animal a = animals.get(animalRowMap.get(visRow));
        if (a.isSold()) { FarmDialog.message("Edit Animal", "Cannot edit a sold animal."); return; }
        String breed = FarmDialog.input("Edit Animal", "Breed:", a.getBreed());
        if (breed == null) return;
        String ps = FarmDialog.input("Edit Animal", "Sale price ($):", String.valueOf(a.getSalePrice()));
        if (ps == null) return;
        String src = FarmDialog.input("Edit Animal", "Source:", a.getSource());
        if (src == null) return;
        try {
            if (!breed.isBlank()) a.setBreed(breed.trim());
            a.setSalePrice(Double.parseDouble(ps.trim()));
            if (!src.isBlank()) a.setSource(src.trim());
            markDirty();
            if (statusBar != null) statusBar.setMessage("Updated: " + a.getType() + " - " + a.getBreed());
        } catch (NumberFormatException e) { FarmDialog.message("Error", "Invalid price entered."); }
    }

    static void removeAnimal() {
        int visRow = animalTable.getSelectedRow();
        if (visRow < 0 || visRow >= animalRowMap.size()) { FarmDialog.message("Remove Animal", "Please click an animal row in the table first."); return; }
        int realIdx = animalRowMap.get(visRow);
        Animal a = animals.get(realIdx);
        if (FarmDialog.confirm("Remove Animal", "Remove " + a.getType() + " - " + a.getBreed() + "?\nThis cannot be undone.")) {
            animals.remove(realIdx); markDirty();
            if (statusBar != null) statusBar.setMessage("Removed: " + a.getType() + " - " + a.getBreed());
        }
    }

    // ══════════════════════════════════════════
    //  MODULE 3 — Services & Payments
    // ══════════════════════════════════════════

    static JPanel buildServicesPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(FT.BG);

        serviceModel = new DefaultTableModel(
                new String[]{"Customer","Animal","Service","Fee","Date","Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        serviceTable = new JTable(serviceModel);
        applyTableStyle(serviceTable);
        serviceTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        serviceTable.getColumnModel().getColumn(0).setPreferredWidth(132);
        serviceTable.getColumnModel().getColumn(1).setPreferredWidth(88);
        serviceTable.getColumnModel().getColumn(2).setPreferredWidth(152);
        serviceTable.getColumnModel().getColumn(3).setPreferredWidth(78);
        serviceTable.getColumnModel().getColumn(4).setPreferredWidth(98);
        serviceTable.getColumnModel().getColumn(5).setPreferredWidth(84);

        serviceTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean isSel, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, val, isSel, hasFocus, row, col);
                setFont(FT.TBL_ROW);
                setBorder(new EmptyBorder(0, 9, 0, 9));
                setHorizontalAlignment((col == 3 || col == 5) ? CENTER : LEFT);
                if (!isSel && row < serviceRowMap.size()) {
                    boolean paid = services.get(serviceRowMap.get(row)).isPaid();
                    setBackground(paid ? (row % 2 == 0 ? new Color(232, 252, 232) : new Color(222, 246, 222))
                                      : (row % 2 == 0 ? new Color(255, 250, 218) : new Color(252, 244, 208)));
                    setForeground(FT.TEXT);
                } else if (isSel) {
                    setBackground(new Color(178, 138, 40, 175));
                    setForeground(FT.TEXT);
                }
                return this;
            }
        });

        p.add(tableScroll(serviceTable), BorderLayout.CENTER);
        p.add(buildSide(
            new String[]{"ACTIONS", "MANAGE", "FILTER"},
            new String[][]{{"Schedule Service", "Mark as Paid"},
                           {"Edit Selected", "Remove Selected"},
                           {"Unpaid Only", "Show All"}},
            new Color[][]{{FT.GREEN, FT.STEEL},
                          {FT.BROWN, new Color(128, 36, 36)},
                          {FT.BARN_RED, new Color(82, 82, 82)}},
            new Runnable[]{
                () -> { scheduleService();          refreshServiceTable(); },
                () -> { markPaid();                 refreshServiceTable(); },
                () -> { editService();              refreshServiceTable(); },
                () -> { removeService();            refreshServiceTable(); },
                () -> { showAllServices = false;    refreshServiceTable(); },
                () -> { showAllServices = true;     refreshServiceTable(); }
            }
        ), BorderLayout.EAST);

        refreshServiceTable();
        return p;
    }

    static void refreshServiceTable() {
        serviceModel.setRowCount(0);
        serviceRowMap.clear();
        long unpaidCount = 0;
        for (int i = 0; i < services.size(); i++) {
            ServiceRecord sr = services.get(i);
            if (showAllServices || !sr.isPaid()) {
                serviceRowMap.add(i);
                serviceModel.addRow(new Object[]{
                    sr.getCustomerName(), sr.getAnimalType(), sr.getServiceType(),
                    String.format("$%.2f", sr.getFee()), sr.getDate(),
                    sr.isPaid() ? "Paid" : "Unpaid"
                });
            }
            if (!sr.isPaid()) unpaidCount++;
        }
        if (statusBar != null)
            statusBar.setMessage("Services: " + services.size() + " total  |  " + unpaidCount + " unpaid");
    }

    static void scheduleService() {
        String customer = FarmDialog.input("New Service", "Customer name:");
        if (customer == null || customer.isBlank()) return;
        String animalType = FarmDialog.input("New Service", "Animal type (e.g. Dog, Rabbit):");
        if (animalType == null || animalType.isBlank()) return;
        String[] svcTypes = {"Wellness Checkup","Vaccination","Wound Treatment",
                             "Grooming","Deworming","Microchipping","Other"};
        int si = FarmDialog.option("Service Type", "Select the service type:", svcTypes);
        if (si < 0) return;
        String fs = FarmDialog.input("Service Fee", "Fee ($):");
        if (fs == null) return;
        String date = FarmDialog.input("Appointment Date", "Date (MM/DD/YYYY):");
        if (date == null || date.isBlank()) return;
        try {
            services.add(new ServiceRecord(customer.trim(), animalType.trim(),
                svcTypes[si], Double.parseDouble(fs.trim()), date.trim()));
            markDirty();
            if (statusBar != null) statusBar.setMessage(
                "Scheduled: " + svcTypes[si] + " for " + customer.trim() + " on " + date.trim());
        } catch (NumberFormatException e) { FarmDialog.message("Error", "Please enter a valid fee amount (e.g. 45.00)."); }
    }

    static void markPaid() {
        ArrayList<ServiceRecord> unpaid = new ArrayList<>();
        ArrayList<Integer>       idxs   = new ArrayList<>();
        for (int i = 0; i < services.size(); i++) {
            if (!services.get(i).isPaid()) { unpaid.add(services.get(i)); idxs.add(i); }
        }
        if (unpaid.isEmpty()) { FarmDialog.message("Payments", "All services are fully paid up!"); return; }
        String[] labels = unpaid.stream()
            .map(sr -> sr.getCustomerName() + "  -  " + sr.getServiceType()
                    + "  ($" + String.format("%.2f", sr.getFee()) + ")")
            .toArray(String[]::new);
        int sel = FarmDialog.option("Mark as Paid", "Select record to mark paid:", labels);
        if (sel < 0) return;
        ServiceRecord sr = unpaid.get(sel);
        sr.markPaid(); revenue += sr.getFee(); markDirty();
        if (statusBar != null) statusBar.setMessage(
            "Payment of $" + String.format("%.2f", sr.getFee()) + " recorded for " + sr.getCustomerName());
    }

    static void editService() {
        int visRow = serviceTable.getSelectedRow();
        if (visRow < 0 || visRow >= serviceRowMap.size()) { FarmDialog.message("Edit Service", "Please click a service row in the table first."); return; }
        ServiceRecord sr = services.get(serviceRowMap.get(visRow));
        String cust = FarmDialog.input("Edit Service", "Customer name:", sr.getCustomerName());
        if (cust == null) return;
        String anim = FarmDialog.input("Edit Service", "Animal type:", sr.getAnimalType());
        if (anim == null) return;
        String fee  = FarmDialog.input("Edit Service", "Fee ($):", String.valueOf(sr.getFee()));
        if (fee == null) return;
        String date = FarmDialog.input("Edit Service", "Date (MM/DD/YYYY):", sr.getDate());
        if (date == null) return;
        try {
            if (!cust.isBlank()) sr.setCustomerName(cust.trim());
            if (!anim.isBlank()) sr.setAnimalType(anim.trim());
            sr.setFee(Double.parseDouble(fee.trim()));
            if (!date.isBlank()) sr.setDate(date.trim());
            markDirty();
            if (statusBar != null) statusBar.setMessage("Updated record for " + sr.getCustomerName());
        } catch (NumberFormatException e) { FarmDialog.message("Error", "Invalid fee entered."); }
    }

    static void removeService() {
        int visRow = serviceTable.getSelectedRow();
        if (visRow < 0 || visRow >= serviceRowMap.size()) { FarmDialog.message("Remove Service", "Please click a service row in the table first."); return; }
        int realIdx = serviceRowMap.get(visRow);
        ServiceRecord sr = services.get(realIdx);
        if (FarmDialog.confirm("Remove Service",
                "Remove service record for " + sr.getCustomerName() + "?\nThis cannot be undone.")) {
            if (sr.isPaid()) revenue -= sr.getFee();
            services.remove(realIdx); markDirty();
            if (statusBar != null) statusBar.setMessage("Removed record for " + sr.getCustomerName());
        }
    }

    // ══════════════════════════════════════════
    //  MODULE 4 — Reports
    // ══════════════════════════════════════════

    static JPanel buildReportsPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(FT.BG);
        p.setBorder(new EmptyBorder(14, 16, 14, 16));

        reportArea = new JTextArea();
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        reportArea.setEditable(false);
        reportArea.setBackground(new Color(255, 253, 236));
        reportArea.setForeground(FT.TEXT);
        reportArea.setBorder(new EmptyBorder(14, 18, 14, 18));

        JScrollPane sp = new JScrollPane(reportArea);
        sp.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(FT.BROWN, 2, true),
            BorderFactory.createEmptyBorder()));
        sp.getViewport().setBackground(new Color(255, 253, 236));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        btnRow.setOpaque(false);

        FarmButton refresh = new FarmButton("Refresh Report", FT.GREEN);
        FarmButton save    = new FarmButton("Save Data to CSV", FT.BARN_RED);
        refresh.setPreferredSize(new Dimension(198, 40));
        save.setPreferredSize(new Dimension(198, 40));

        refresh.addActionListener(e -> {
            reportArea.setText(buildReport());
            if (statusBar != null) statusBar.setMessage("Report refreshed.");
        });
        save.addActionListener(e -> saveWithFeedback());

        btnRow.add(refresh);
        btnRow.add(save);

        p.add(btnRow,      BorderLayout.NORTH);
        p.add(sp,          BorderLayout.CENTER);
        reportArea.setText(buildReport());
        return p;
    }

    static String buildReport() {
        long   soldCnt   = animals.stream().filter(Animal::isSold).count();
        long   paidCnt   = services.stream().filter(ServiceRecord::isPaid).count();
        double unpaidAmt = services.stream().filter(r -> !r.isPaid()).mapToDouble(ServiceRecord::getFee).sum();
        long   lowStock  = inventory.stream().filter(i -> i.getQuantity() > 0 && i.getQuantity() <= 3).count();
        long   outStock  = inventory.stream().filter(i -> i.getQuantity() == 0).count();
        return String.join("\n",
            "================================================",
            "     FARM MANAGEMENT SYSTEM  -  REPORT",
            "                Aryan Kandula",
            "================================================",
            "",
            "  STORE & INVENTORY",
            "  " + "-".repeat(44),
            "  Total items tracked   : " + inventory.size(),
            "  Low stock (1-3 units) : " + lowStock,
            "  Out of stock          : " + outStock,
            "",
            "  ANIMAL SALES",
            "  " + "-".repeat(44),
            "  Total animals listed  : " + animals.size(),
            "  Sold                  : " + soldCnt,
            "  Available             : " + (animals.size() - soldCnt),
            "",
            "  SERVICES & PAYMENTS",
            "  " + "-".repeat(44),
            "  Total scheduled       : " + services.size(),
            "  Paid                  : " + paidCnt,
            "  Unpaid                : " + (services.size() - paidCnt),
            "  Outstanding balance   : $" + String.format("%.2f", unpaidAmt),
            "",
            "  TOTAL REVENUE RECORDED",
            "  " + "-".repeat(44),
            "  $" + String.format("%.2f", revenue),
            "",
            "================================================");
    }

    // ══════════════════════════════════════════
    //  SHARED UI HELPERS
    // ══════════════════════════════════════════

    /**
     * Builds a side panel with labeled sections and buttons.
     * sections[]     — section heading strings
     * btnLabels[][]  — button texts per section
     * btnColors[][]  — button colors per section
     * actions[]      — flat ordered list of Runnables (one per button across all sections)
     */
    static JPanel buildSide(String[] sections, String[][] btnLabels,
                             Color[][] btnColors, Runnable[] actions) {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(FT.CREAM_MID);
        side.setBorder(new CompoundBorder(
            new MatteBorder(0, 2, 0, 0, FT.BROWN),
            new EmptyBorder(14, 10, 14, 10)));
        side.setPreferredSize(new Dimension(250, 0));

        int ai = 0;
        for (int s = 0; s < sections.length; s++) {
            if (s > 0) side.add(Box.createVerticalStrut(10));

            JLabel sec = new JLabel(sections[s]);
            sec.setFont(new Font("Dialog", Font.BOLD, 10));
            sec.setForeground(new Color(136, 92, 40));
            sec.setBorder(new EmptyBorder(4, 4, 2, 0));
            sec.setAlignmentX(Component.LEFT_ALIGNMENT);
            side.add(sec);

            JSeparator sp = new JSeparator() {
                @Override protected void paintComponent(Graphics g) {
                    g.setColor(new Color(192, 160, 112, 150));
                    g.fillRect(0, 0, getWidth(), 1);
                }
            };
            sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            sp.setAlignmentX(Component.LEFT_ALIGNMENT);
            side.add(sp);
            side.add(Box.createVerticalStrut(5));

            for (int b = 0; b < btnLabels[s].length; b++, ai++) {
                final Runnable action = actions[ai];
                FarmButton btn = new FarmButton(btnLabels[s][b], btnColors[s][b]);
                btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                btn.setAlignmentX(Component.LEFT_ALIGNMENT);
                btn.addActionListener(e -> action.run());
                side.add(btn);
                side.add(Box.createVerticalStrut(5));
            }
        }
        side.add(Box.createVerticalGlue());
        return side;
    }

    /** Applies consistent table styling including the custom header renderer. */
    static void applyTableStyle(JTable t) {
        t.setRowHeight(28);
        t.setFont(FT.TBL_ROW);
        t.setGridColor(new Color(212, 198, 172));
        t.setSelectionBackground(new Color(176, 136, 38, 170));
        t.setSelectionForeground(FT.TEXT);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.setFillsViewportHeight(true);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setRowMargin(0);

        JTableHeader hdr = t.getTableHeader();
        // Install custom renderer — this paints its own background so LAF cannot override it
        hdr.setDefaultRenderer(new FarmHeaderRenderer());
        hdr.setReorderingAllowed(false);
        hdr.setResizingAllowed(true);
        hdr.setPreferredSize(new Dimension(0, 34));
        // Also set these for LAFs that still check them
        hdr.setBackground(new Color(95, 28, 28));
        hdr.setForeground(Color.WHITE);
        hdr.setOpaque(true);
    }

    /** Returns a styled JScrollPane wrapping the table. */
    static JScrollPane tableScroll(JTable t) {
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(10, 10, 10, 0),
            new LineBorder(FT.BROWN, 1, true)));
        sp.getViewport().setBackground(FT.CREAM);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        return sp;
    }

    // ══════════════════════════════════════════
    //  SEED DATA  (fallback when CSV is missing)
    // ══════════════════════════════════════════

    static void seedData() {
        inventory.add(new StoreItem("Chicken Feed (50 lb)",      18.99, 20));
        inventory.add(new StoreItem("Rabbit Pellets (10 lb)",    12.49, 15));
        inventory.add(new StoreItem("Duck Starter Feed (25 lb)", 14.99, 10));
        inventory.add(new StoreItem("Animal Water Feeder",        9.99,  8));
        inventory.add(new StoreItem("Hay Bale (small)",           7.50, 25));
        inventory.add(new StoreItem("Pet Carrier (medium)",      34.99,  5));
        inventory.add(new StoreItem("Fencing Wire (50 ft)",      22.00, 12));
        inventory.add(new StoreItem("Milk",                      20.00, 13));
        inventory.add(new StoreItem("Milk Ice Cream",            10.00, 28));

        animals.add(new Animal("Duck",    "Pekin",            15.00, "Farm"));
        animals.add(new Animal("Duck",    "Mallard",          18.00, "Farm"));
        animals.add(new Animal("Chicken", "Rhode Island Red", 12.00, "Farm"));
        animals.add(new Animal("Chicken", "Silkie",           20.00, "Sunrise Breeders"));
        animals.add(new Animal("Hamster", "Syrian",           10.00, "Farm"));
        animals.add(new Animal("Rabbit",  "Holland Lop",      45.00, "Valley Breeders"));
        animals.add(new Animal("Rabbit",  "Mini Rex",         40.00, "Farm"));

        services.add(new ServiceRecord("John Smith",  "Dog",     "Wellness Checkup", 45.00, "02/10/2026"));
        services.add(new ServiceRecord("Maria Lopez", "Rabbit",  "Vaccination",      30.00, "02/15/2026"));
        services.add(new ServiceRecord("Tom Harris",  "Chicken", "Wound Treatment",  25.00, "02/18/2026"));
        services.get(0).markPaid();
        revenue += 45.00;
    }
}