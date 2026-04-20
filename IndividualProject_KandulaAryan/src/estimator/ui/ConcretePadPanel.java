package estimator.ui;

import estimator.model.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.Map;

/**
 * Step 2 — Concrete Pad Configuration panel.
 */
public class ConcretePadPanel extends JPanel {

    // Preset
    private final JComboBox<String> cbPreset;
    private final JTextField tfLength, tfWidth;
    private final JLabel lblArea = AppTheme.hintLabel("Area: —");

    // Thickness
    private final JComboBox<String> cbThickness;
    private final JTextField tfCustomThickness;

    // Waste
    private final JSpinner spWaste;

    // Labor
    private final JSpinner        spEmployees;
    private final JTextField      tfHoursPerEmployee;
    private final JComboBox<String> cbLaborRate;
    private final JTextField      tfCustomLaborRate;

    // Add-ons
    private final JCheckBox chkRebar, chkMesh, chkEquipment;
    private final JTextField tfEquipmentDays;

    // Discount / contingency
    private final JTextField tfDiscPct, tfDiscFixed, tfContingency;

    private final Map<String, Double> prices;
    private final Map<String, Double> laborRates;
    private final String[] laborRateKeys;

    public ConcretePadPanel(Map<String, Double> prices, Map<String, Double> laborRates) {
        this.prices     = prices;
        this.laborRates = laborRates;
        this.laborRateKeys = laborRates.keySet().toArray(new String[0]);

        setBackground(AppTheme.BG_PANEL);
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel title = new JLabel("Concrete Pad Configuration");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        add(title, BorderLayout.NORTH);

        // ── Scrollable main content ───────────────────────────────────────────
        JPanel content = new JPanel();
        content.setBackground(AppTheme.BG_PANEL);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // ── Preset & Dimensions ───────────────────────────────────────────────
        PadPreset[] presets = PadPreset.getPresets();
        String[] presetNames = new String[presets.length];
        for (int i = 0; i < presets.length; i++) {
            PadPreset p = presets[i];
            presetNames[i] = p.getLength() == 0
                ? p.getName()
                : String.format("%s  (%.0f' × %.0f'  —  %s)", p.getName(), p.getLength(), p.getWidth(), p.getTypicalUse());
        }
        cbPreset = AppTheme.styledCombo(presetNames);
        tfLength = AppTheme.styledField(8);
        tfWidth  = AppTheme.styledField(8);

        JPanel dimCard = AppTheme.card("Pad Size");
        dimCard.setLayout(new GridBagLayout());
        GridBagConstraints g = gbc();

        addRow(dimCard, g, 0, "Preset:", cbPreset, null);
        g.gridx = 0; g.gridy = 1; dimCard.add(AppTheme.formLabel("Length (ft):"), g);
        g.gridx = 1; dimCard.add(tfLength, g);
        g.gridx = 2; dimCard.add(AppTheme.formLabel("  Width (ft):"), g);
        g.gridx = 3; dimCard.add(tfWidth, g);
        g.gridx = 4; dimCard.add(lblArea, g);

        // Preset selection auto-fills length/width
        cbPreset.addActionListener(e -> {
            int idx = cbPreset.getSelectedIndex();
            PadPreset p = presets[idx];
            if (p.getLength() > 0) {
                tfLength.setText(String.valueOf((int)p.getLength()));
                tfWidth.setText(String.valueOf((int)p.getWidth()));
                tfLength.setEditable(false); tfWidth.setEditable(false);
            } else {
                tfLength.setText(""); tfWidth.setText("");
                tfLength.setEditable(true); tfWidth.setEditable(true);
            }
            updateArea();
        });
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { updateArea(); }
            public void removeUpdate(DocumentEvent e)  { updateArea(); }
            public void changedUpdate(DocumentEvent e) { updateArea(); }
        };
        tfLength.getDocument().addDocumentListener(dl);
        tfWidth.getDocument().addDocumentListener(dl);

        // Pre-select first preset
        cbPreset.setSelectedIndex(0);
        content.add(dimCard); content.add(Box.createVerticalStrut(10));

        // ── Thickness ─────────────────────────────────────────────────────────
        cbThickness = AppTheme.styledCombo(new String[]{
            "4 inches — Standard residential / light commercial",
            "5 inches — Standard commercial",
            "6 inches — Heavy-duty / forklift traffic",
            "8 inches — Industrial / heavy equipment",
            "Custom..."
        });
        tfCustomThickness = AppTheme.styledField(6);
        tfCustomThickness.setEnabled(false);
        tfCustomThickness.setText("4");
        cbThickness.addActionListener(e ->
            tfCustomThickness.setEnabled(cbThickness.getSelectedIndex() == 4));

        spWaste = AppTheme.styledSpinner(new SpinnerNumberModel(8.0, 5.0, 15.0, 0.5));

        JPanel thickCard = AppTheme.card("Slab Thickness & Waste");
        thickCard.setLayout(new GridBagLayout());
        GridBagConstraints g2 = gbc();
        addRow(thickCard, g2, 0, "Thickness:", cbThickness, null);
        addRow(thickCard, g2, 1, "Custom thickness (in):", tfCustomThickness, null);
        addRow(thickCard, g2, 2, "Waste % (5–15):", spWaste, AppTheme.hintLabel("Default: 8%"));
        content.add(thickCard); content.add(Box.createVerticalStrut(10));

        // ── Labor ─────────────────────────────────────────────────────────────
        spEmployees = AppTheme.styledSpinner(new SpinnerNumberModel(2, 1, 50, 1));
        tfHoursPerEmployee = AppTheme.styledField(6);
        tfHoursPerEmployee.setText("8");

        String[] rateLabels = buildRateLabels();
        cbLaborRate = AppTheme.styledCombo(rateLabels);
        tfCustomLaborRate = AppTheme.styledField(8);
        tfCustomLaborRate.setEnabled(false);
        tfCustomLaborRate.setText("0.00");
        cbLaborRate.addActionListener(e ->
            tfCustomLaborRate.setEnabled(cbLaborRate.getSelectedIndex() == rateLabels.length - 1));

        JPanel laborCard = AppTheme.card("Labor");
        laborCard.setLayout(new GridBagLayout());
        GridBagConstraints g3 = gbc();
        addRow(laborCard, g3, 0, "# of Employees:", spEmployees, null);
        addRow(laborCard, g3, 1, "Hours per Employee:", tfHoursPerEmployee, null);
        addRow(laborCard, g3, 2, "Labor Rate:", cbLaborRate, null);
        addRow(laborCard, g3, 3, "Custom Rate ($/hr):", tfCustomLaborRate, null);
        content.add(laborCard); content.add(Box.createVerticalStrut(10));

        // ── Add-ons ───────────────────────────────────────────────────────────
        chkRebar     = AppTheme.styledCheck("Include Rebar");
        chkMesh      = AppTheme.styledCheck("Include Wire Mesh");
        chkEquipment = AppTheme.styledCheck("Include Equipment Rental");
        tfEquipmentDays = AppTheme.styledField(6);
        tfEquipmentDays.setText("1");
        tfEquipmentDays.setEnabled(false);
        chkEquipment.addActionListener(e -> tfEquipmentDays.setEnabled(chkEquipment.isSelected()));

        JPanel addOnCard = AppTheme.card("Optional Add-Ons");
        addOnCard.setLayout(new GridBagLayout());
        GridBagConstraints g4 = gbc();
        g4.gridx = 0; g4.gridy = 0; g4.gridwidth = 2; addOnCard.add(chkRebar,     g4);
        g4.gridy = 1;                                   addOnCard.add(chkMesh,      g4);
        g4.gridy = 2; g4.gridwidth = 1;                addOnCard.add(chkEquipment, g4);
        g4.gridx = 1; g4.gridy = 2; g4.gridwidth = 1;
        JPanel rentalRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        rentalRow.setBackground(AppTheme.BG_CARD);
        rentalRow.add(AppTheme.formLabel("Days:"));
        rentalRow.add(tfEquipmentDays);
        addOnCard.add(rentalRow, g4);
        content.add(addOnCard); content.add(Box.createVerticalStrut(10));

        // ── Discount & Contingency ────────────────────────────────────────────
        tfDiscPct    = AppTheme.styledField(6); tfDiscPct.setText("0");
        tfDiscFixed  = AppTheme.styledField(8); tfDiscFixed.setText("0.00");
        tfContingency = AppTheme.styledField(6); tfContingency.setText("0");

        JPanel discCard = AppTheme.card("Discount & Contingency");
        discCard.setLayout(new GridBagLayout());
        GridBagConstraints g5 = gbc();
        addRow(discCard, g5, 0, "Discount % (0–100):",    tfDiscPct,     null);
        addRow(discCard, g5, 1, "Fixed $ Discount:",       tfDiscFixed,   AppTheme.hintLabel("e.g. 500.00"));
        addRow(discCard, g5, 2, "Contingency % (0–50):",  tfContingency, AppTheme.hintLabel("Added to subtotal"));
        content.add(discCard);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setBackground(AppTheme.BG_PANEL);
        scroll.getViewport().setBackground(AppTheme.BG_PANEL);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        add(scroll, BorderLayout.CENTER);
    }

    private void updateArea() {
        try {
            double l = Double.parseDouble(tfLength.getText().trim());
            double w = Double.parseDouble(tfWidth.getText().trim());
            lblArea.setText(String.format("  Area: %.0f sq ft", l * w));
            lblArea.setForeground(AppTheme.SUCCESS);
        } catch (NumberFormatException ex) {
            lblArea.setText("  Area: —");
            lblArea.setForeground(AppTheme.TEXT_MUTED);
        }
    }

    private String[] buildRateLabels() {
        String[] labels = new String[laborRateKeys.length + 1];
        for (int i = 0; i < laborRateKeys.length; i++) {
            labels[i] = String.format("%s  —  $%.2f/hr", laborRateKeys[i], laborRates.get(laborRateKeys[i]));
        }
        labels[laborRateKeys.length] = "Custom rate...";
        return labels;
    }

    public boolean validateInputs() {
        try {
            double l = Double.parseDouble(tfLength.getText().trim());
            double w = Double.parseDouble(tfWidth.getText().trim());
            if (l <= 0 || w <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            AppTheme.showWarning(this, "Please enter valid Length and Width (must be > 0).", "Invalid Dimensions");
            return false;
        }
        if (cbThickness.getSelectedIndex() == 4) {
            try {
                double t = Double.parseDouble(tfCustomThickness.getText().trim());
                if (t <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                AppTheme.showWarning(this, "Custom thickness must be > 0.", "Invalid Thickness");
                return false;
            }
        }
        try { Double.parseDouble(tfHoursPerEmployee.getText().trim()); }
        catch (NumberFormatException e) {
            AppTheme.showWarning(this, "Hours per employee must be a number.", "Invalid Labor");
            return false;
        }
        if (cbLaborRate.getSelectedIndex() == laborRateKeys.length) {
            try {
                double r = Double.parseDouble(tfCustomLaborRate.getText().trim());
                if (r <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                AppTheme.showWarning(this, "Custom labor rate must be > 0.", "Invalid Rate");
                return false;
            }
        }
        if (chkEquipment.isSelected()) {
            try {
                double d = Double.parseDouble(tfEquipmentDays.getText().trim());
                if (d <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                AppTheme.showWarning(this, "Equipment rental days must be > 0.", "Invalid Days");
                return false;
            }
        }
        return true;
    }

    public ConcretePad buildConcretePad() {
        double length  = Double.parseDouble(tfLength.getText().trim());
        double width   = Double.parseDouble(tfWidth.getText().trim());
        double[] thicknessMap = {4, 5, 6, 8};
        double thickness = cbThickness.getSelectedIndex() < 4
            ? thicknessMap[cbThickness.getSelectedIndex()]
            : Double.parseDouble(tfCustomThickness.getText().trim());
        double waste       = ((Number) spWaste.getValue()).doubleValue();
        int    employees   = ((Number) spEmployees.getValue()).intValue();
        double hoursPerEmp = Double.parseDouble(tfHoursPerEmployee.getText().trim());

        double laborRate;
        if (cbLaborRate.getSelectedIndex() < laborRateKeys.length) {
            laborRate = laborRates.get(laborRateKeys[cbLaborRate.getSelectedIndex()]);
        } else {
            laborRate = Double.parseDouble(tfCustomLaborRate.getText().trim());
        }

        boolean rebar    = chkRebar.isSelected();
        boolean mesh     = chkMesh.isSelected();
        boolean rental   = chkEquipment.isSelected();
        double  rentalDays = rental ? Double.parseDouble(tfEquipmentDays.getText().trim()) : 0;

        double discPct   = parseOrZero(tfDiscPct);
        double discFixed = parseOrZero(tfDiscFixed);
        double conting   = parseOrZero(tfContingency);

        return new ConcretePad(
            length, width, thickness, waste,
            employees, hoursPerEmp, laborRate,
            rebar, mesh, rental, rentalDays,
            prices.getOrDefault("concrete_per_cy", 165.0),
            prices.getOrDefault("rebar_per_lf",    0.85),
            prices.getOrDefault("wire_mesh_per_sqft", 0.45),
            prices.getOrDefault("equipment_rental_day", 450.0),
            discPct, discFixed, conting);
    }

    private double parseOrZero(JTextField f) {
        try { return Math.max(0, Double.parseDouble(f.getText().trim())); }
        catch (NumberFormatException e) { return 0; }
    }

    private GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(5, 4, 5, 10);
        g.anchor  = GridBagConstraints.WEST;
        g.fill    = GridBagConstraints.HORIZONTAL;
        return g;
    }

    private void addRow(JPanel p, GridBagConstraints g, int row,
                        String label, JComponent field, JComponent hint) {
        g.gridx = 0; g.gridy = row; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        JLabel lbl = AppTheme.formLabel(label);
        lbl.setPreferredSize(new Dimension(190, 24));
        p.add(lbl, g);
        g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        p.add(field, g);
        if (hint != null) {
            g.gridx = 2; g.weightx = 0; g.fill = GridBagConstraints.NONE;
            p.add(hint, g);
        }
    }
}
