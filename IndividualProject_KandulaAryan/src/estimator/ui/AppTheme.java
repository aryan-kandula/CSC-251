package estimator.ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.DefaultMetalTheme;
import java.awt.*;

/**
 * Central design system — colors, fonts, and component factories.
 * Designed specifically for Java Metal L&F, which fully respects UIManager keys.
 *
 * Author: Aryan Kandula  |  CSC-251 Module 5
 */
public class AppTheme {

    // ── Palette ──────────────────────────────────────────────────────────────
    public static final Color BG_DARK        = new Color(10, 12, 18);
    public static final Color BG_PANEL       = new Color(18, 22, 32);
    public static final Color BG_CARD        = new Color(26, 32, 46);
    public static final Color BG_INPUT       = new Color(10, 12, 18);   // near-black for all fields
    public static final Color BG_SIDEBAR     = new Color(8, 10, 16);
    public static final Color BG_BTN_DARK    = new Color(30, 40, 58);

    public static final Color ACCENT         = new Color(251, 188, 4);
    public static final Color ACCENT_HOVER   = new Color(255, 215, 80);
    public static final Color ACCENT_DIM     = new Color(70, 52, 2);
    public static final Color ACCENT_LIGHT   = new Color(255, 235, 150);

    public static final Color SUCCESS        = new Color(52, 211, 153);
    public static final Color DANGER         = new Color(248, 113, 113);

    public static final Color TEXT_WHITE     = new Color(241, 245, 249);
    public static final Color TEXT_PRIMARY   = new Color(241, 245, 249);
    public static final Color TEXT_SECONDARY = new Color(180, 192, 210);
    public static final Color TEXT_MUTED     = new Color(100, 116, 139);
    public static final Color BORDER         = new Color(40, 52, 72);
    public static final Color BORDER_FOCUS   = new Color(96, 130, 182);

    // ColorUIResource versions (required for Metal UIManager keys)
    private static final ColorUIResource CUR_BG_INPUT   = new ColorUIResource(10, 12, 18);
    private static final ColorUIResource CUR_TEXT       = new ColorUIResource(241, 245, 249);
    private static final ColorUIResource CUR_TEXT_DIM   = new ColorUIResource(180, 192, 210);
    private static final ColorUIResource CUR_TEXT_MUTED = new ColorUIResource(100, 116, 139);
    private static final ColorUIResource CUR_BG_PANEL   = new ColorUIResource(18, 22, 32);
    private static final ColorUIResource CUR_BG_CARD    = new ColorUIResource(26, 32, 46);
    private static final ColorUIResource CUR_BG_DARK    = new ColorUIResource(10, 12, 18);
    private static final ColorUIResource CUR_BG_BTN     = new ColorUIResource(30, 40, 58);
    private static final ColorUIResource CUR_BORDER     = new ColorUIResource(40, 52, 72);
    private static final ColorUIResource CUR_ACCENT     = new ColorUIResource(251, 188, 4);
    private static final ColorUIResource CUR_ACCENT_DIM = new ColorUIResource(70, 52, 2);

    // ── Fonts ────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font FONT_LABEL  = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_INPUT  = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_MONO   = new Font("Consolas",  Font.PLAIN, 12);
    public static final Font FONT_BTN    = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);

    // ── Text field — always dark bg regardless of enabled/editable state ──────
    public static JTextField styledField(int cols) {
        // Override getBackground() so Metal cannot substitute its disabled-white
        JTextField f = new JTextField(cols) {
            @Override public Color getBackground() {
                return BG_INPUT;
            }
            @Override public Color getForeground() {
                return isEnabled() ? TEXT_PRIMARY : TEXT_MUTED;
            }
        };
        f.setBackground(BG_INPUT);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT);
        f.setFont(FONT_INPUT);
        f.setOpaque(true);
        f.setDisabledTextColor(TEXT_MUTED);
        f.setSelectedTextColor(TEXT_WHITE);
        f.setSelectionColor(ACCENT_DIM);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (f.isEditable() && f.isEnabled())
                    f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_FOCUS, 2),
                        BorderFactory.createEmptyBorder(5, 9, 5, 9)));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER, 1),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));
            }
        });
        return f;
    }

    // ── Combo box ─────────────────────────────────────────────────────────────
    public static <T> JComboBox<T> styledCombo(T[] items) {
        JComboBox<T> cb = new JComboBox<>(items) {
            @Override public Color getBackground() { return BG_INPUT; }
            @Override public Color getForeground() { return TEXT_PRIMARY; }
        };
        cb.setBackground(BG_INPUT);
        cb.setForeground(TEXT_PRIMARY);
        cb.setFont(FONT_INPUT);
        cb.setOpaque(true);
        cb.setBorder(BorderFactory.createLineBorder(BORDER, 1));

        // Dark dropdown list
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                setBackground(selected ? new Color(60, 48, 4) : BG_INPUT);
                setForeground(selected ? ACCENT_LIGHT : TEXT_PRIMARY);
                setFont(FONT_INPUT);
                setOpaque(true);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return this;
            }
        });

        // Recolor the internal arrow button after UI installs
        SwingUtilities.invokeLater(() -> darkifyChildren(cb, BG_INPUT));
        return cb;
    }

    // ── Spinner ───────────────────────────────────────────────────────────────
    public static JSpinner styledSpinner(SpinnerModel model) {
        JSpinner sp = new JSpinner(model) {
            @Override public Color getBackground() { return BG_INPUT; }
        };
        sp.setBackground(BG_INPUT);
        sp.setForeground(TEXT_PRIMARY);
        sp.setFont(FONT_INPUT);
        sp.setOpaque(true);
        sp.setBorder(BorderFactory.createLineBorder(BORDER, 1));

        JComponent editor = sp.getEditor();
        if (editor instanceof JSpinner.DefaultEditor de) {
            styleSpinnerField(de.getTextField());
        }

        // Recolor arrow buttons and re-apply field after UI fully builds
        SwingUtilities.invokeLater(() -> {
            darkifyChildren(sp, BG_INPUT);
            if (sp.getEditor() instanceof JSpinner.DefaultEditor de) {
                styleSpinnerField(de.getTextField());
            }
        });
        return sp;
    }

    private static void styleSpinnerField(JTextField tf) {
        tf.setBackground(BG_INPUT);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(ACCENT);
        tf.setFont(FONT_INPUT);
        tf.setOpaque(true);
        tf.setDisabledTextColor(TEXT_MUTED);
        tf.setSelectedTextColor(TEXT_WHITE);
        tf.setSelectionColor(ACCENT_DIM);
        tf.setHorizontalAlignment(JTextField.LEFT);
        tf.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 4));
    }

    /**
     * Recursively walk a component's children and paint any buttons/panels
     * (i.e. the arrow buttons inside spinners and combos) with a dark background.
     */
    private static void darkifyChildren(JComponent parent, Color bg) {
        for (Component c : parent.getComponents()) {
            if (c instanceof AbstractButton btn) {
                btn.setBackground(new Color(30, 40, 58));
                btn.setForeground(TEXT_SECONDARY);
                btn.setOpaque(true);
                btn.setContentAreaFilled(true);
                btn.setBorderPainted(false);
                btn.setFocusPainted(false);
                // Hover effect
                btn.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        btn.setBackground(new Color(50, 62, 85));
                    }
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        btn.setBackground(new Color(30, 40, 58));
                    }
                });
            } else if (c instanceof JPanel p) {
                p.setBackground(bg);
                p.setOpaque(true);
                darkifyChildren(p, bg);
            } else if (c instanceof JComponent jc) {
                darkifyChildren(jc, bg);
            }
        }
    }

    // ── Buttons ───────────────────────────────────────────────────────────────
    public static JButton primaryBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(ACCENT);
        b.setForeground(BG_DARK);
        b.setFont(FONT_BTN);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(ACCENT_HOVER); b.setForeground(BG_DARK); }
            public void mouseExited (java.awt.event.MouseEvent e) { b.setBackground(ACCENT);       b.setForeground(BG_DARK); }
        });
        return b;
    }

    public static JButton secondaryBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(BG_BTN_DARK);
        b.setForeground(TEXT_PRIMARY);
        b.setFont(FONT_BTN);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(true);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(9, 20, 9, 20)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(46, 58, 80));
                b.setForeground(TEXT_PRIMARY);
                b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_FOCUS, 1),
                    BorderFactory.createEmptyBorder(9, 20, 9, 20)));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(BG_BTN_DARK);
                b.setForeground(TEXT_PRIMARY);
                b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER, 1),
                    BorderFactory.createEmptyBorder(9, 20, 9, 20)));
            }
        });
        return b;
    }

    public static JButton dangerBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(127, 29, 29));
        b.setForeground(new Color(254, 202, 202));
        b.setFont(FONT_BTN);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        return b;
    }

    // ── Panels / Labels / Checkbox ────────────────────────────────────────────
    public static JPanel card(String title) {
        JPanel p = new JPanel();
        p.setBackground(BG_CARD);
        p.setOpaque(true);
        if (title != null && !title.isEmpty()) {
            p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(ACCENT_DIM, 1),
                    "  " + title + "  ",
                    TitledBorder.LEFT, TitledBorder.TOP,
                    FONT_HEADER, ACCENT),
                BorderFactory.createEmptyBorder(6, 12, 10, 12)));
        } else {
            p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        }
        return p;
    }

    public static JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text); l.setForeground(ACCENT);         l.setFont(FONT_HEADER); return l;
    }
    public static JLabel formLabel(String text) {
        JLabel l = new JLabel(text); l.setForeground(TEXT_SECONDARY); l.setFont(FONT_LABEL);  return l;
    }
    public static JLabel hintLabel(String text) {
        JLabel l = new JLabel(text); l.setForeground(TEXT_MUTED);     l.setFont(FONT_SMALL);  return l;
    }

    public static JCheckBox styledCheck(String text) {
        JCheckBox cb = new JCheckBox(text);
        cb.setBackground(BG_CARD);
        cb.setForeground(TEXT_PRIMARY);
        cb.setFont(FONT_LABEL);
        cb.setFocusPainted(false);
        cb.setOpaque(true);
        return cb;
    }

    /**
     * Show a themed JOptionPane dialog.
     * Replaces direct JOptionPane.showMessageDialog() calls so the dialog
     * background, text, and buttons always use the dark theme rather than
     * the system default which bleeds through on Metal L&F.
     */
    public static void showInfo(java.awt.Component parent, String message) {
        showThemedDialog(parent, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showWarning(java.awt.Component parent, String message, String title) {
        showThemedDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public static void showError(java.awt.Component parent, String message) {
        showThemedDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static void showThemedDialog(java.awt.Component parent,
                                          String message, String title, int type) {
        JOptionPane pane = new JOptionPane(message, type);
        pane.setBackground(BG_CARD);
        pane.setForeground(TEXT_PRIMARY);
        // Force dark on every child inside the pane
        JDialog dialog = pane.createDialog(parent, title);
        dialog.getContentPane().setBackground(BG_CARD);
        forceTheme(dialog.getContentPane());
        dialog.setBackground(BG_CARD);
        dialog.setVisible(true);
    }


    public static void applyGlobalDefaults() {

        // ── Text inputs ────────────────────────────────────────────────────────
        UIManager.put("TextField.background",               CUR_BG_INPUT);
        UIManager.put("TextField.foreground",               CUR_TEXT);
        UIManager.put("TextField.inactiveForeground",       CUR_TEXT_MUTED);
        UIManager.put("TextField.caretForeground",          CUR_ACCENT);
        UIManager.put("TextField.selectionBackground",      CUR_ACCENT_DIM);
        UIManager.put("TextField.selectionForeground",      CUR_TEXT);
        UIManager.put("TextField.disabledBackground",       CUR_BG_INPUT);
        UIManager.put("TextField.inactiveBackground",       CUR_BG_INPUT);
        UIManager.put("FormattedTextField.background",      CUR_BG_INPUT);
        UIManager.put("FormattedTextField.foreground",      CUR_TEXT);
        UIManager.put("FormattedTextField.inactiveBackground", CUR_BG_INPUT);
        UIManager.put("PasswordField.background",           CUR_BG_INPUT);
        UIManager.put("PasswordField.foreground",           CUR_TEXT);

        // ── TextArea ──────────────────────────────────────────────────────────
        UIManager.put("TextArea.background",                CUR_BG_INPUT);
        UIManager.put("TextArea.foreground",                CUR_TEXT);
        UIManager.put("TextArea.inactiveBackground",        CUR_BG_INPUT);
        UIManager.put("TextArea.caretForeground",           CUR_ACCENT);
        UIManager.put("TextArea.selectionBackground",       CUR_ACCENT_DIM);
        UIManager.put("TextArea.selectionForeground",       CUR_TEXT);

        // ── Spinner ────────────────────────────────────────────────────────────
        UIManager.put("Spinner.background",                 CUR_BG_INPUT);
        UIManager.put("Spinner.foreground",                 CUR_TEXT);
        UIManager.put("Spinner.arrowButtonBackground",      CUR_BG_BTN);
        UIManager.put("Spinner.arrowButtonForeground",      CUR_TEXT_DIM);
        UIManager.put("Spinner.arrowButtonInsets",          new Insets(0, 0, 0, 0));

        // ── ComboBox ──────────────────────────────────────────────────────────
        UIManager.put("ComboBox.background",                CUR_BG_INPUT);
        UIManager.put("ComboBox.foreground",                CUR_TEXT);
        UIManager.put("ComboBox.disabledBackground",        CUR_BG_INPUT);
        UIManager.put("ComboBox.disabledForeground",        CUR_TEXT_MUTED);
        UIManager.put("ComboBox.selectionBackground",       CUR_ACCENT_DIM);
        UIManager.put("ComboBox.selectionForeground",       CUR_TEXT);
        UIManager.put("ComboBox.buttonBackground",          CUR_BG_BTN);
        UIManager.put("ComboBox.buttonShadow",              CUR_BG_DARK);
        UIManager.put("ComboBox.buttonDarkShadow",          CUR_BG_DARK);
        UIManager.put("ComboBox.buttonHighlight",           CUR_BG_BTN);
        UIManager.put("ComboBox.listBackground",            CUR_BG_INPUT);
        UIManager.put("ComboBox.listForeground",            CUR_TEXT);

        // ── List (dropdown popup) ─────────────────────────────────────────────
        UIManager.put("List.background",                    CUR_BG_INPUT);
        UIManager.put("List.foreground",                    CUR_TEXT);
        UIManager.put("List.selectionBackground",           CUR_ACCENT_DIM);
        UIManager.put("List.selectionForeground",           CUR_TEXT);

        // ── Panels & general ──────────────────────────────────────────────────
        UIManager.put("Panel.background",                   CUR_BG_PANEL);
        UIManager.put("Panel.foreground",                   CUR_TEXT);
        UIManager.put("Label.foreground",                   CUR_TEXT);
        UIManager.put("Label.background",                   CUR_BG_PANEL);
        UIManager.put("ScrollPane.background",              CUR_BG_PANEL);
        UIManager.put("Viewport.background",                CUR_BG_PANEL);

        // ── Buttons (Metal keys) ──────────────────────────────────────────────
        UIManager.put("Button.background",                  CUR_BG_BTN);
        UIManager.put("Button.foreground",                  CUR_TEXT);
        UIManager.put("Button.select",                      CUR_ACCENT_DIM);
        UIManager.put("Button.focus",                       CUR_BG_DARK);
        UIManager.put("Button.highlight",                   CUR_BG_BTN);
        UIManager.put("Button.shadow",                      CUR_BG_DARK);
        UIManager.put("Button.darkShadow",                  CUR_BG_DARK);
        UIManager.put("Button.light",                       CUR_BG_BTN);
        UIManager.put("Button.disabledForeground",          CUR_TEXT_MUTED);
        UIManager.put("Button.disabledShadow",              CUR_BG_DARK);

        // ── CheckBox ─────────────────────────────────────────────────────────
        UIManager.put("CheckBox.background",                CUR_BG_CARD);
        UIManager.put("CheckBox.foreground",                CUR_TEXT);

        // ── TabbedPane ────────────────────────────────────────────────────────
        UIManager.put("TabbedPane.background",              CUR_BG_PANEL);
        UIManager.put("TabbedPane.foreground",              CUR_TEXT);
        UIManager.put("TabbedPane.selected",                CUR_BG_CARD);
        UIManager.put("TabbedPane.selectedForeground",      CUR_ACCENT);
        UIManager.put("TabbedPane.contentAreaColor",        CUR_BG_PANEL);
        UIManager.put("TabbedPane.tabAreaBackground",       CUR_BG_PANEL);
        UIManager.put("TabbedPane.unselectedBackground",    CUR_BG_PANEL);
        UIManager.put("TabbedPane.light",                   CUR_BORDER);
        UIManager.put("TabbedPane.highlight",               CUR_BORDER);
        UIManager.put("TabbedPane.shadow",                  CUR_BG_DARK);
        UIManager.put("TabbedPane.darkShadow",              CUR_BG_DARK);
        UIManager.put("TabbedPane.focus",                   CUR_ACCENT_DIM);

        // ── Table ─────────────────────────────────────────────────────────────
        UIManager.put("Table.background",                   CUR_BG_CARD);
        UIManager.put("Table.foreground",                   CUR_TEXT);
        UIManager.put("Table.gridColor",                    CUR_BORDER);
        UIManager.put("Table.selectionBackground",          CUR_ACCENT_DIM);
        UIManager.put("Table.selectionForeground",          CUR_TEXT);
        UIManager.put("TableHeader.background",             CUR_BG_DARK);
        UIManager.put("TableHeader.foreground",             CUR_ACCENT);
        UIManager.put("TableHeader.cellBorder",
            BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(40, 52, 72)));

        // ── ScrollBar ─────────────────────────────────────────────────────────
        UIManager.put("ScrollBar.background",               CUR_BG_DARK);
        UIManager.put("ScrollBar.foreground",               CUR_BG_DARK);
        UIManager.put("ScrollBar.thumb",                    new ColorUIResource(50, 62, 82));
        UIManager.put("ScrollBar.thumbHighlight",           new ColorUIResource(65, 80, 105));
        UIManager.put("ScrollBar.thumbShadow",              CUR_BG_DARK);
        UIManager.put("ScrollBar.thumbDarkShadow",          CUR_BG_DARK);
        UIManager.put("ScrollBar.track",                    CUR_BG_DARK);
        UIManager.put("ScrollBar.trackHighlight",           CUR_BG_DARK);

        // ── OptionPane (warning/info/error dialogs) ───────────────────────────
        UIManager.put("OptionPane.background",              CUR_BG_CARD);
        UIManager.put("OptionPane.foreground",              CUR_TEXT);
        UIManager.put("OptionPane.messageForeground",       CUR_TEXT);
        UIManager.put("OptionPane.messageBackground",       CUR_BG_CARD);
        UIManager.put("OptionPane.buttonBackground",        CUR_BG_BTN);
        UIManager.put("OptionPane.buttonForeground",        CUR_TEXT);
        UIManager.put("OptionPane.buttonPadding",           Integer.valueOf(8));
        UIManager.put("OptionPane.informationIcon",         null);
        UIManager.put("OptionPane.warningIcon",             null);
        UIManager.put("OptionPane.errorIcon",               null);

        // ── Dialog / Pane ─────────────────────────────────────────────────────
        UIManager.put("Dialog.background",                  CUR_BG_CARD);
        UIManager.put("Dialog.foreground",                  CUR_TEXT);
        UIManager.put("RootPane.background",                CUR_BG_PANEL);
        UIManager.put("PopupMenu.background",               CUR_BG_CARD);
        UIManager.put("PopupMenu.foreground",               CUR_TEXT);
        UIManager.put("MenuItem.background",                CUR_BG_CARD);
        UIManager.put("MenuItem.foreground",                CUR_TEXT);
        UIManager.put("MenuItem.selectionBackground",       CUR_ACCENT_DIM);
        UIManager.put("MenuItem.selectionForeground",       CUR_TEXT);

        // ── ToolTip ───────────────────────────────────────────────────────────
        UIManager.put("ToolTip.background",                 CUR_BG_CARD);
        UIManager.put("ToolTip.foreground",                 CUR_TEXT);
        UIManager.put("ToolTip.border",
            BorderFactory.createLineBorder(new Color(40, 52, 72)));

        // ── Metal-specific system color overrides ─────────────────────────────
        // These control the Metal chrome (borders, gradients, etc.)
        UIManager.put("controlHighlight",                   CUR_BG_BTN);
        UIManager.put("controlLtHighlight",                 CUR_BG_BTN);
        UIManager.put("controlShadow",                      CUR_BG_DARK);
        UIManager.put("controlDkShadow",                    CUR_BG_DARK);
        UIManager.put("control",                            CUR_BG_BTN);
        UIManager.put("controlText",                        CUR_TEXT);
        UIManager.put("controlDisabled",                    CUR_TEXT_MUTED);
        UIManager.put("textHighlight",                      CUR_ACCENT_DIM);
        UIManager.put("textHighlightText",                  CUR_TEXT);
        UIManager.put("textInactiveText",                   CUR_TEXT_MUTED);
        UIManager.put("text",                               CUR_BG_INPUT);
        UIManager.put("window",                             CUR_BG_PANEL);
        UIManager.put("windowBorder",                       CUR_BORDER);
        UIManager.put("windowText",                         CUR_TEXT);
        UIManager.put("desktop",                            CUR_BG_DARK);
        UIManager.put("activeCaption",                      CUR_BG_DARK);
        UIManager.put("activeCaptionText",                  CUR_ACCENT);
        UIManager.put("activeCaptionBorder",                CUR_BORDER);
        UIManager.put("inactiveCaption",                    CUR_BG_DARK);
        UIManager.put("inactiveCaptionText",                CUR_TEXT_MUTED);
        UIManager.put("inactiveCaptionBorder",              CUR_BORDER);
        UIManager.put("menu",                               CUR_BG_CARD);
        UIManager.put("menuText",                           CUR_TEXT);
        UIManager.put("info",                               CUR_BG_CARD);
        UIManager.put("infoText",                           CUR_TEXT);

        // ── FileChooser ───────────────────────────────────────────────────────
        UIManager.put("FileChooser.background",             CUR_BG_PANEL);
        UIManager.put("FileChooser.foreground",             CUR_TEXT);
        UIManager.put("FileChooser.listBackground",         CUR_BG_CARD);
        UIManager.put("FileChooser.listForeground",         CUR_TEXT);
        UIManager.put("FileChooser.readOnly",               Boolean.FALSE);
        // SplitPane inside FileChooser
        UIManager.put("SplitPane.background",               CUR_BG_PANEL);
        UIManager.put("SplitPaneDivider.background",        CUR_BG_PANEL);
    }

    /**
     * Recursively force dark colors onto every component inside a JFileChooser
     * or any other system-spawned container that ignores UIManager keys.
     * Call this immediately after constructing the JFileChooser, before showing it.
     */
    public static void themeFileChooser(JFileChooser fc) {
        fc.setBackground(BG_PANEL);
        fc.setForeground(TEXT_PRIMARY);
        fc.setOpaque(true);
        forceTheme(fc);
        // Force the filename text field and filter combo explicitly
        SwingUtilities.invokeLater(() -> forceTheme(fc));
    }

    private static void forceTheme(Component c) {
        if (c instanceof JTextField tf) {
            tf.setBackground(BG_INPUT);
            tf.setForeground(TEXT_PRIMARY);
            tf.setCaretColor(ACCENT);
            tf.setOpaque(true);
        } else if (c instanceof JComboBox<?> cb) {
            cb.setBackground(BG_INPUT);
            cb.setForeground(TEXT_PRIMARY);
            cb.setOpaque(true);
        } else if (c instanceof AbstractButton btn) {
            // Keep standard buttons readable but dark
            btn.setBackground(BG_BTN_DARK);
            btn.setForeground(TEXT_PRIMARY);
            btn.setOpaque(true);
            btn.setContentAreaFilled(true);
        } else if (c instanceof JLabel lbl) {
            lbl.setForeground(TEXT_PRIMARY);
        } else if (c instanceof JList<?> list) {
            list.setBackground(BG_CARD);
            list.setForeground(TEXT_PRIMARY);
            list.setSelectionBackground(ACCENT_DIM);
            list.setSelectionForeground(ACCENT_LIGHT);
        } else if (c instanceof JScrollPane sp) {
            sp.setBackground(BG_CARD);
            sp.getViewport().setBackground(BG_CARD);
        } else if (c instanceof JTable tbl) {
            tbl.setBackground(BG_CARD);
            tbl.setForeground(TEXT_PRIMARY);
            tbl.setGridColor(BORDER);
            tbl.setSelectionBackground(ACCENT_DIM);
            tbl.setSelectionForeground(ACCENT_LIGHT);
        } else if (c instanceof JToolBar tb) {
            tb.setBackground(BG_PANEL);
            tb.setOpaque(true);
        } else if (c instanceof JSplitPane sp) {
            sp.setBackground(BG_PANEL);
        }

        if (c instanceof JComponent jc) {
            jc.setOpaque(c instanceof JPanel || c instanceof JScrollPane
                         || c instanceof JToolBar || c instanceof JSplitPane
                         || c instanceof JList);
        }

        if (c instanceof Container container) {
            for (Component child : container.getComponents()) {
                forceTheme(child);
            }
        }
    }
}
