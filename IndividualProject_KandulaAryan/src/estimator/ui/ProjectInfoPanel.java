package estimator.ui;

import estimator.model.ProjectInfo;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Step 1 — Project Information form panel.
 *
 * Author: Aryan Kandula  |  CSC-251 Module 5
 */
public class ProjectInfoPanel extends JPanel {

    private final JTextField tfProject;
    private final JTextField tfClient;
    private final JTextField tfLocation;
    private final JTextField tfDate;
    private final JTextField tfEstimator;
    private final JTextArea  taNotes;

    public ProjectInfoPanel() {
        setBackground(AppTheme.BG_PANEL);
        setOpaque(true);
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // ── Header ────────────────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout(0, 6));
        headerPanel.setBackground(AppTheme.BG_PANEL);
        headerPanel.setOpaque(true);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JLabel title = new JLabel("Project Information");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel sub = AppTheme.hintLabel("Fill in the basic details for this estimate. Fields marked * are required.");
        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(sub,   BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // ── Form card ─────────────────────────────────────────────────────────
        JPanel card = AppTheme.card("Estimate Details");
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 12);
        gbc.anchor = GridBagConstraints.WEST;

        tfProject   = AppTheme.styledField(30);
        tfClient    = AppTheme.styledField(30);
        tfLocation  = AppTheme.styledField(30);

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        tfDate      = AppTheme.styledField(14);
        tfDate.setText(today);
        tfDate.setToolTipText("Format: MM/DD/YYYY");

        tfEstimator = AppTheme.styledField(30);
        tfEstimator.setText("Aryan Kandula");   // Default to project author

        taNotes = new JTextArea(4, 30);
        taNotes.setBackground(AppTheme.BG_INPUT);
        taNotes.setForeground(AppTheme.TEXT_PRIMARY);
        taNotes.setFont(AppTheme.FONT_INPUT);
        taNotes.setCaretColor(AppTheme.ACCENT);
        taNotes.setLineWrap(true);
        taNotes.setWrapStyleWord(true);
        taNotes.setOpaque(true);
        taNotes.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        JScrollPane notesScroll = new JScrollPane(taNotes);
        notesScroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER, 1));
        notesScroll.setBackground(AppTheme.BG_INPUT);
        notesScroll.getViewport().setBackground(AppTheme.BG_INPUT);

        // Tooltips
        tfProject  .setToolTipText("Enter the project or job site name");
        tfClient   .setToolTipText("Enter the client or company name");
        tfLocation .setToolTipText("Street address or site description");
        tfEstimator.setToolTipText("Person preparing this estimate");
        taNotes    .setToolTipText("Any special conditions, notes, or scope details");

        addRow(card, gbc, 0, "Project Name *",      tfProject,   null);
        addRow(card, gbc, 1, "Client Name *",        tfClient,    null);
        addRow(card, gbc, 2, "Location / Address *", tfLocation,  null);
        addRow(card, gbc, 3, "Estimate Date *",      tfDate,      AppTheme.hintLabel("MM/DD/YYYY"));
        addRow(card, gbc, 4, "Estimator Name *",     tfEstimator, null);
        addRow(card, gbc, 5, "Notes (optional)",     notesScroll, null);

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrap.setBackground(AppTheme.BG_PANEL);
        wrap.setOpaque(true);
        wrap.add(card);
        add(wrap, BorderLayout.CENTER);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc,
                        int row, String label, JComponent field, JComponent hint) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        JLabel lbl = AppTheme.formLabel(label);
        lbl.setPreferredSize(new Dimension(170, 26));
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
        if (hint != null) {
            gbc.gridx = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
            panel.add(hint, gbc);
        }
    }

    public boolean validateInputs() {
        if (tfProject.getText().trim().isEmpty())   { flash(tfProject,   "Project Name is required.");   return false; }
        if (tfClient.getText().trim().isEmpty())     { flash(tfClient,    "Client Name is required.");    return false; }
        if (tfLocation.getText().trim().isEmpty())   { flash(tfLocation,  "Location is required.");       return false; }
        if (tfDate.getText().trim().isEmpty())       { flash(tfDate,      "Estimate Date is required.");  return false; }
        if (tfEstimator.getText().trim().isEmpty())  { flash(tfEstimator, "Estimator Name is required."); return false; }
        return true;
    }

    private void flash(JTextField f, String msg) {
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.DANGER, 2),
            BorderFactory.createEmptyBorder(5, 9, 5, 9)));
        AppTheme.showWarning(this, msg, "Required Field");
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        f.requestFocus();
    }

    public ProjectInfo buildProjectInfo() {
        return new ProjectInfo(
            tfProject.getText().trim(),
            tfClient.getText().trim(),
            tfLocation.getText().trim(),
            tfDate.getText().trim().isEmpty() ? LocalDate.now().toString() : tfDate.getText().trim(),
            tfEstimator.getText().trim(),
            taNotes.getText().trim()
        );
    }
}
