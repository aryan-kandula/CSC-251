package estimator.ui;

import estimator.csv.CsvManager;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

/**
 * Modal dialog for viewing and editing material prices and labor rates.
 * Changes are saved back to CSV immediately on clicking Save.
 *
 * Author: Aryan Kandula  |  CSC-251 Module 5
 */
public class PricesEditorDialog extends JDialog {

    public PricesEditorDialog(JFrame parent) {
        super(parent, "Edit Prices & Labor Rates", true);
        getContentPane().setBackground(AppTheme.BG_PANEL);
        setBackground(AppTheme.BG_PANEL);
        setSize(720, 520);
        setMinimumSize(new Dimension(580, 380));
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(0, 0));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(AppTheme.BG_PANEL);
        tabs.setForeground(AppTheme.TEXT_PRIMARY);
        tabs.setFont(AppTheme.FONT_HEADER);
        tabs.setOpaque(true);

        // ── Material Prices Tab ───────────────────────────────────────────────
        Map<String, Double> prices = CsvManager.loadMaterialPrices();
        JPanel pricePanel = buildTablePanel(prices, "Material Key", "Unit Price ($)");
        JButton savePrices = AppTheme.primaryBtn("Save Material Prices");
        savePrices.addActionListener(e -> {
            if (commitEdits(pricePanel)) {
                CsvManager.saveMaterialPrices(prices);
                AppTheme.showInfo(this, "Material prices saved successfully!");
            }
        });

        JPanel priceWrap = new JPanel(new BorderLayout(0, 8));
        priceWrap.setBackground(AppTheme.BG_PANEL);
        priceWrap.setOpaque(true);
        priceWrap.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel priceHint = AppTheme.hintLabel("Click a value in the 'Unit Price' column to edit. Click Save when done.");
        priceHint.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        priceWrap.add(priceHint,  BorderLayout.NORTH);
        priceWrap.add(pricePanel, BorderLayout.CENTER);
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pBtn.setBackground(AppTheme.BG_PANEL);
        pBtn.setOpaque(true);
        pBtn.add(savePrices);
        priceWrap.add(pBtn, BorderLayout.SOUTH);

        // ── Labor Rates Tab ───────────────────────────────────────────────────
        Map<String, Double> laborRates = CsvManager.loadLaborRates();
        JPanel laborPanel = buildTablePanel(laborRates, "Trade / Role", "Hourly Rate ($/hr)");
        JButton saveLaborRates = AppTheme.primaryBtn("Save Labor Rates");
        saveLaborRates.addActionListener(e -> {
            if (commitEdits(laborPanel)) {
                CsvManager.saveLaborRates(laborRates);
                AppTheme.showInfo(this, "Labor rates saved successfully!");
            }
        });

        JPanel laborWrap = new JPanel(new BorderLayout(0, 8));
        laborWrap.setBackground(AppTheme.BG_PANEL);
        laborWrap.setOpaque(true);
        laborWrap.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel laborHint = AppTheme.hintLabel("Click a value in the 'Hourly Rate' column to edit. Click Save when done.");
        laborHint.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        laborWrap.add(laborHint,  BorderLayout.NORTH);
        laborWrap.add(laborPanel, BorderLayout.CENTER);
        JPanel lBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        lBtn.setBackground(AppTheme.BG_PANEL);
        lBtn.setOpaque(true);
        lBtn.add(saveLaborRates);
        laborWrap.add(lBtn, BorderLayout.SOUTH);

        tabs.addTab("Material Prices", priceWrap);
        tabs.addTab("Labor Rates",     laborWrap);
        add(tabs, BorderLayout.CENTER);

        JButton closeBtn = AppTheme.secondaryBtn("Close");
        closeBtn.addActionListener(e -> dispose());
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));
        footer.setBackground(AppTheme.BG_PANEL);
        footer.setOpaque(true);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER));
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);
    }

    @SuppressWarnings("unchecked")
    private JPanel buildTablePanel(Map<String, Double> data, String keyHeader, String valHeader) {
        String[] cols = {keyHeader, valHeader};
        Object[][] rows = new Object[data.size()][2];
        int i = 0;
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            rows[i][0] = entry.getKey();
            rows[i][1] = String.format("%.2f", entry.getValue());
            i++;
        }

        DefaultTableModel model = new DefaultTableModel(rows, cols) {
            @Override public boolean isCellEditable(int r, int c) { return c == 1; }
        };
        JTable table = new JTable(model);
        table.setBackground(AppTheme.BG_CARD);
        table.setForeground(AppTheme.TEXT_PRIMARY);
        table.setFont(AppTheme.FONT_INPUT);
        table.setGridColor(AppTheme.BORDER);
        table.setRowHeight(30);
        table.setOpaque(true);
        table.setShowVerticalLines(true);
        table.setSelectionBackground(AppTheme.ACCENT_DIM);
        table.setSelectionForeground(AppTheme.ACCENT_LIGHT);
        table.getTableHeader().setBackground(AppTheme.BG_DARK);
        table.getTableHeader().setForeground(AppTheme.ACCENT);
        table.getTableHeader().setFont(AppTheme.FONT_HEADER);
        table.getTableHeader().setOpaque(true);
        table.getTableHeader().setBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER));

        // Alternate row colors for readability
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, focus, row, col);
                setOpaque(true);
                if (sel) {
                    setBackground(AppTheme.ACCENT_DIM);
                    setForeground(AppTheme.ACCENT_LIGHT);
                } else {
                    setBackground(row % 2 == 0 ? AppTheme.BG_CARD : new Color(26, 34, 48));
                    setForeground(col == 0 ? AppTheme.TEXT_SECONDARY : AppTheme.TEXT_PRIMARY);
                }
                setFont(col == 0 ? AppTheme.FONT_LABEL : AppTheme.FONT_INPUT);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        model.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                String key = (String) model.getValueAt(row, 0);
                try {
                    double val = Double.parseDouble(model.getValueAt(row, 1).toString().trim());
                    if (val >= 0) data.put(key, val);
                } catch (NumberFormatException ignored) {}
            }
        });

        // Dark-theme the inline cell editor (default editor uses white JTextField)
        JTextField editorField = new JTextField();
        editorField.setBackground(AppTheme.BG_INPUT);
        editorField.setForeground(AppTheme.TEXT_PRIMARY);
        editorField.setCaretColor(AppTheme.ACCENT);
        editorField.setSelectionColor(AppTheme.ACCENT_DIM);
        editorField.setSelectedTextColor(AppTheme.TEXT_WHITE);
        editorField.setFont(AppTheme.FONT_INPUT);
        editorField.setOpaque(true);
        editorField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_FOCUS, 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        DefaultCellEditor darkEditor = new DefaultCellEditor(editorField);
        table.setDefaultEditor(Object.class, darkEditor);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        scroll.setBackground(AppTheme.BG_DARK);
        scroll.getViewport().setBackground(AppTheme.BG_CARD);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.BG_PANEL);
        panel.setOpaque(true);
        panel.putClientProperty("table", table);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private boolean commitEdits(JPanel panel) {
        JTable table = (JTable) panel.getClientProperty("table");
        if (table != null && table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
        return true;
    }
}
