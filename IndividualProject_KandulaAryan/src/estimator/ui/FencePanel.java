package estimator.ui;

import estimator.model.*;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Step 3 — Chain-Link Fence Configuration panel (optional module).
 */
public class FencePanel extends JPanel {

    private final JCheckBox chkIncludeFence;
    private final JPanel    fenceContent;

    // Preset & layout
    private final JComboBox<String> cbPreset;
    private final JTextField tfPerimeter, tfHeight;

    // Specs
    private final JComboBox<String> cbFenceHeight;
    private final JTextField        tfCustomHeight;
    private final JSpinner          spPostSpacing;
    private final JComboBox<String> cbGauge;
    private final JComboBox<String> cbPostType;

    // Gates
    private final JSpinner spSingleGates, spDoubleGates, spSlidingGates;

    // Top treatment
    private final JComboBox<String> cbTopTreatment;

    // Overage & labor
    private final JSpinner   spOverage;
    private final JTextField tfFenceLaborHours;
    private final JComboBox<String> cbFenceLaborRate;
    private final JTextField tfCustomFenceLaborRate;

    // Discount
    private final JTextField tfDiscPct, tfDiscFixed;

    private final Map<String, Double> prices;
    private final Map<String, Double> laborRates;
    private final String[] laborRateKeys;

    public FencePanel(Map<String, Double> prices, Map<String, Double> laborRates) {
        this.prices     = prices;
        this.laborRates = laborRates;
        this.laborRateKeys = laborRates.keySet().toArray(new String[0]);

        setBackground(AppTheme.BG_PANEL);
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        // Header
        JLabel title = new JLabel("Chain-Link Fence (Optional)");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        add(title, BorderLayout.NORTH);

        // Toggle
        chkIncludeFence = AppTheme.styledCheck("Include chain-link fence in this estimate");
        chkIncludeFence.setFont(AppTheme.FONT_HEADER);
        chkIncludeFence.setForeground(AppTheme.ACCENT);

        // ── Fence content (shown/hidden by toggle) ────────────────────────────
        fenceContent = new JPanel();
        fenceContent.setBackground(AppTheme.BG_PANEL);
        fenceContent.setLayout(new BoxLayout(fenceContent, BoxLayout.Y_AXIS));

        // Presets
        FencePreset[] fPresets = FencePreset.getPresets();
        String[] fPresetNames = new String[fPresets.length];
        for (int i = 0; i < fPresets.length; i++) {
            FencePreset fp = fPresets[i];
            fPresetNames[i] = fp.getPerimeter() == 0
                ? fp.getName()
                : String.format("%s  (Perim: %.0f ft, H: %.0f ft — %s)",
                    fp.getName(), fp.getPerimeter(), fp.getHeightFt(), fp.getTypicalUse());
        }
        cbPreset  = AppTheme.styledCombo(fPresetNames);
        tfPerimeter = AppTheme.styledField(8);
        tfHeight    = AppTheme.styledField(6);

        cbPreset.addActionListener(e -> {
            int idx = cbPreset.getSelectedIndex();
            FencePreset fp = fPresets[idx];
            if (fp.getPerimeter() > 0) {
                tfPerimeter.setText(String.valueOf((int)fp.getPerimeter()));
                tfHeight.setText(String.valueOf((int)fp.getHeightFt()));
                tfPerimeter.setEditable(false);
                tfHeight.setEditable(false);
            } else {
                tfPerimeter.setText(""); tfHeight.setText("");
                tfPerimeter.setEditable(true); tfHeight.setEditable(true);
            }
        });
        cbPreset.setSelectedIndex(4); // Small Warehouse default

        JPanel layoutCard = AppTheme.card("Layout");
        layoutCard.setLayout(new GridBagLayout());
        GridBagConstraints g = gbc();
        addRow(layoutCard, g, 0, "Preset:", cbPreset, null);
        addRow(layoutCard, g, 1, "Perimeter (ft):", tfPerimeter, null);
        addRow(layoutCard, g, 2, "Height (ft):", tfHeight, AppTheme.hintLabel("Overrides preset height"));
        fenceContent.add(layoutCard); fenceContent.add(Box.createVerticalStrut(10));

        // Specs
        cbFenceHeight = AppTheme.styledCombo(new String[]{"4 ft","5 ft","6 ft","8 ft","10 ft","Custom..."});
        cbFenceHeight.setSelectedIndex(2);
        tfCustomHeight = AppTheme.styledField(6); tfCustomHeight.setText("6"); tfCustomHeight.setEnabled(false);
        cbFenceHeight.addActionListener(e -> tfCustomHeight.setEnabled(cbFenceHeight.getSelectedIndex() == 5));

        spPostSpacing = AppTheme.styledSpinner(new SpinnerNumberModel(10.0, 2.0, 20.0, 0.5));
        cbGauge       = AppTheme.styledCombo(new String[]{"9-gauge (heavy)","11-gauge (standard)","11.5-gauge (light)"});
        cbGauge.setSelectedIndex(1);
        cbPostType    = AppTheme.styledCombo(new String[]{"Galvanized Steel (standard)","Aluminum (lightweight)","Schedule 40 (heavy duty)"});

        JPanel specsCard = AppTheme.card("Fence Specifications");
        specsCard.setLayout(new GridBagLayout());
        GridBagConstraints g2 = gbc();
        addRow(specsCard, g2, 0, "Fence Height:", cbFenceHeight, null);
        addRow(specsCard, g2, 1, "Custom Height (ft):", tfCustomHeight, null);
        addRow(specsCard, g2, 2, "Post Spacing (ft):", spPostSpacing, AppTheme.hintLabel("Default: 10 ft on center"));
        addRow(specsCard, g2, 3, "Mesh Gauge:", cbGauge, null);
        addRow(specsCard, g2, 4, "Post Type:", cbPostType, null);
        fenceContent.add(specsCard); fenceContent.add(Box.createVerticalStrut(10));

        // Gates
        spSingleGates  = AppTheme.styledSpinner(new SpinnerNumberModel(0, 0, 20, 1));
        spDoubleGates  = AppTheme.styledSpinner(new SpinnerNumberModel(0, 0, 20, 1));
        spSlidingGates = AppTheme.styledSpinner(new SpinnerNumberModel(0, 0, 10, 1));

        JPanel gatesCard = AppTheme.card("Gates");
        gatesCard.setLayout(new GridBagLayout());
        GridBagConstraints g3 = gbc();
        addRow(gatesCard, g3, 0, "Single Walk Gates (3–4 ft):", spSingleGates, AppTheme.hintLabel("$185 ea."));
        addRow(gatesCard, g3, 1, "Double Drive Gates (12–16 ft):", spDoubleGates, AppTheme.hintLabel("$650 ea."));
        addRow(gatesCard, g3, 2, "Sliding Gates (12–40 ft):", spSlidingGates, AppTheme.hintLabel("$1,250 ea."));
        fenceContent.add(gatesCard); fenceContent.add(Box.createVerticalStrut(10));

        // Top treatment
        cbTopTreatment = AppTheme.styledCombo(new String[]{
            "None",
            "Barbed Wire – 1 Strand",
            "Barbed Wire – 2 Strands",
            "Barbed Wire – 3 Strands",
            "Razor Wire Coil",
            "Privacy Slats"
        });

        JPanel topCard = AppTheme.card("Top Treatment");
        topCard.setLayout(new GridBagLayout());
        GridBagConstraints g4 = gbc();
        addRow(topCard, g4, 0, "Top Treatment:", cbTopTreatment, null);
        fenceContent.add(topCard); fenceContent.add(Box.createVerticalStrut(10));

        // Overage & Labor
        spOverage = AppTheme.styledSpinner(new SpinnerNumberModel(5.0, 0.0, 20.0, 0.5));
        tfFenceLaborHours = AppTheme.styledField(6); tfFenceLaborHours.setText("40");

        String[] rateLabels = buildRateLabels();
        cbFenceLaborRate = AppTheme.styledCombo(rateLabels);
        // Pre-select fence_installer if available
        for (int i = 0; i < laborRateKeys.length; i++) {
            if (laborRateKeys[i].equals("fence_installer")) { cbFenceLaborRate.setSelectedIndex(i); break; }
        }
        tfCustomFenceLaborRate = AppTheme.styledField(8);
        tfCustomFenceLaborRate.setText("0.00");
        tfCustomFenceLaborRate.setEnabled(false);
        cbFenceLaborRate.addActionListener(e ->
            tfCustomFenceLaborRate.setEnabled(cbFenceLaborRate.getSelectedIndex() == rateLabels.length - 1));

        tfDiscPct   = AppTheme.styledField(6); tfDiscPct.setText("0");
        tfDiscFixed = AppTheme.styledField(8); tfDiscFixed.setText("0.00");

        JPanel laborCard = AppTheme.card("Fence Labor & Discount");
        laborCard.setLayout(new GridBagLayout());
        GridBagConstraints g5 = gbc();
        addRow(laborCard, g5, 0, "Material Overage % (0–20):", spOverage, AppTheme.hintLabel("Default: 5%"));
        addRow(laborCard, g5, 1, "Total Fence Labor Hours:", tfFenceLaborHours, null);
        addRow(laborCard, g5, 2, "Labor Rate:", cbFenceLaborRate, null);
        addRow(laborCard, g5, 3, "Custom Rate ($/hr):", tfCustomFenceLaborRate, null);
        addRow(laborCard, g5, 4, "Discount %:", tfDiscPct, null);
        addRow(laborCard, g5, 5, "Fixed $ Discount:", tfDiscFixed, null);
        fenceContent.add(laborCard);

        // ── Toggle logic ──────────────────────────────────────────────────────
        fenceContent.setVisible(false);
        chkIncludeFence.addActionListener(e -> fenceContent.setVisible(chkIncludeFence.isSelected()));

        JScrollPane scroll = new JScrollPane(fenceContent);
        scroll.setBorder(null);
        scroll.setBackground(AppTheme.BG_PANEL);
        scroll.getViewport().setBackground(AppTheme.BG_PANEL);
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setBackground(AppTheme.BG_PANEL);
        center.add(chkIncludeFence, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    public boolean isFenceIncluded() { return chkIncludeFence.isSelected(); }

    public boolean validateInputs() {
        if (!isFenceIncluded()) return true;
        try {
            double p = Double.parseDouble(tfPerimeter.getText().trim());
            if (p <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            AppTheme.showWarning(this, "Please enter a valid perimeter (must be > 0).", "Invalid Perimeter");
            return false;
        }
        try { Double.parseDouble(tfFenceLaborHours.getText().trim()); }
        catch (NumberFormatException e) {
            AppTheme.showWarning(this, "Please enter valid fence labor hours.", "Invalid Labor Hours");
            return false;
        }
        return true;
    }

    public ChainLinkFence buildChainLinkFence() {
        if (!isFenceIncluded()) return null;

        double perimeter = Double.parseDouble(tfPerimeter.getText().trim());
        double[] heights = {4, 5, 6, 8, 10};
        double heightFt = cbFenceHeight.getSelectedIndex() < 5
            ? heights[cbFenceHeight.getSelectedIndex()]
            : Double.parseDouble(tfCustomHeight.getText().trim());

        double postSpacing = ((Number) spPostSpacing.getValue()).doubleValue();
        String[] gaugeKeys = {"9", "11", "11.5"};
        String gaugeKey = gaugeKeys[cbGauge.getSelectedIndex()];
        String[] postTypes = {"galvanized", "aluminum", "schedule40"};
        String postType = postTypes[cbPostType.getSelectedIndex()];
        double postPrice = switch (cbPostType.getSelectedIndex()) {
            case 1 -> prices.getOrDefault("post_aluminum",   28.0);
            case 2 -> prices.getOrDefault("post_schedule40", 35.0);
            default -> prices.getOrDefault("post_galvanized",22.5);
        };

        int singleGates  = ((Number) spSingleGates.getValue()).intValue();
        int doubleGates  = ((Number) spDoubleGates.getValue()).intValue();
        int slidingGates = ((Number) spSlidingGates.getValue()).intValue();

        String[] topKeys   = {"none","barbed1","barbed2","barbed3","razor","privacy"};
        String   topTreat  = topKeys[cbTopTreatment.getSelectedIndex()];
        String[] topPriceKeys = {"","barbed_wire_1strand","barbed_wire_2strand","barbed_wire_3strand","razor_wire","privacy_slats"};
        double topPrice = cbTopTreatment.getSelectedIndex() > 0
            ? prices.getOrDefault(topPriceKeys[cbTopTreatment.getSelectedIndex()], 0.0) : 0;

        double overage  = ((Number) spOverage.getValue()).doubleValue();
        double fenceHrs = Double.parseDouble(tfFenceLaborHours.getText().trim());

        double fenceLaborRate;
        if (cbFenceLaborRate.getSelectedIndex() < laborRateKeys.length) {
            fenceLaborRate = laborRates.get(laborRateKeys[cbFenceLaborRate.getSelectedIndex()]);
        } else {
            fenceLaborRate = Double.parseDouble(tfCustomFenceLaborRate.getText().trim());
        }

        double discPct   = parseOrZero(tfDiscPct);
        double discFixed = parseOrZero(tfDiscFixed);

        // Fabric price: pick by height then adjust for gauge
        double fabricBase = switch ((int) heightFt) {
            case 4 -> prices.getOrDefault("fence_fabric_4ft", 4.5);
            case 5 -> prices.getOrDefault("fence_fabric_5ft", 5.25);
            case 8 -> prices.getOrDefault("fence_fabric_8ft", 9.5);
            case 10 -> prices.getOrDefault("fence_fabric_10ft", 13.0);
            default -> prices.getOrDefault("fence_fabric_6ft", 6.75);
        };
        double fabricPrice = switch (gaugeKey) {
            case "9"    -> fabricBase * 1.15;
            case "11.5" -> fabricBase * 0.95;
            default     -> fabricBase;
        };

        return new ChainLinkFence(
            perimeter, heightFt, postSpacing,
            gaugeKey, postType,
            singleGates, doubleGates, slidingGates,
            topTreat, overage,
            fenceHrs, fenceLaborRate,
            discPct, discFixed,
            fabricPrice, postPrice,
            prices.getOrDefault("post_concrete_bag", 8.5),
            prices.getOrDefault("top_rail", 2.75),
            prices.getOrDefault("gate_single",  185.0),
            prices.getOrDefault("gate_double",  650.0),
            prices.getOrDefault("gate_sliding", 1250.0),
            topPrice,
            prices.getOrDefault("hardware_percentage", 0.08));
    }

    private String[] buildRateLabels() {
        String[] labels = new String[laborRateKeys.length + 1];
        for (int i = 0; i < laborRateKeys.length; i++)
            labels[i] = String.format("%s  —  $%.2f/hr", laborRateKeys[i], laborRates.get(laborRateKeys[i]));
        labels[laborRateKeys.length] = "Custom rate...";
        return labels;
    }

    private double parseOrZero(JTextField f) {
        try { return Math.max(0, Double.parseDouble(f.getText().trim())); }
        catch (NumberFormatException e) { return 0; }
    }

    private GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 4, 5, 10);
        g.anchor = GridBagConstraints.WEST;
        g.fill   = GridBagConstraints.HORIZONTAL;
        return g;
    }

    private void addRow(JPanel p, GridBagConstraints g, int row,
                        String label, JComponent field, JComponent hint) {
        g.gridx = 0; g.gridy = row; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        JLabel lbl = AppTheme.formLabel(label);
        lbl.setPreferredSize(new Dimension(210, 24));
        p.add(lbl, g);
        g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        p.add(field, g);
        if (hint != null) {
            g.gridx = 2; g.weightx = 0; g.fill = GridBagConstraints.NONE;
            p.add(hint, g);
        }
    }
}
