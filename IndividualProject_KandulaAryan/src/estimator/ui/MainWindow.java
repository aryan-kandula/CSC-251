package estimator.ui;

import estimator.csv.CsvManager;
import estimator.model.*;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Main application window — sidebar navigation + card layout for 4 steps.
 *
 * Concrete Pad & Chain-Link Fence Estimator
 * Author: Aryan Kandula  |  CSC-251 Module 5
 */
public class MainWindow extends JFrame {

    private final Map<String, Double> prices;
    private final Map<String, Double> laborRates;

    private final ProjectInfoPanel projectInfoPanel;
    private final ConcretePadPanel concretePadPanel;
    private final FencePanel       fencePanel;
    private final ReportPanel      reportPanel;

    private final JPanel     cardPanel;
    private final CardLayout cardLayout;

    private static final String STEP_INFO   = "step_info";
    private static final String STEP_PAD    = "step_pad";
    private static final String STEP_FENCE  = "step_fence";
    private static final String STEP_REPORT = "step_report";

    private final JButton[] navBtns = new JButton[4];
    private int currentStep = 0;

    // Bottom-bar references
    private JButton btnBack, btnNext, btnGenerate;
    private JLabel  lblStep;

    public MainWindow() {
        super("Concrete Pad & Chain-Link Fence Estimator  |  Aryan Kandula  |  CSC-251");
        prices     = CsvManager.loadMaterialPrices();
        laborRates = CsvManager.loadLaborRates();

        projectInfoPanel = new ProjectInfoPanel();
        concretePadPanel = new ConcretePadPanel(prices, laborRates);
        fencePanel       = new FencePanel(prices, laborRates);
        reportPanel      = new ReportPanel();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1120, 760);
        setMinimumSize(new Dimension(900, 620));
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout());

        add(buildSidebar(),    BorderLayout.WEST);
        add(buildBottomBar(),  BorderLayout.SOUTH);

        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBackground(AppTheme.BG_PANEL);
        cardPanel.setOpaque(true);
        cardPanel.add(projectInfoPanel, STEP_INFO);
        cardPanel.add(concretePadPanel, STEP_PAD);
        cardPanel.add(fencePanel,       STEP_FENCE);
        cardPanel.add(reportPanel,      STEP_REPORT);
        add(cardPanel, BorderLayout.CENTER);

        showStep(0);
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(AppTheme.BG_SIDEBAR);
        sidebar.setOpaque(true);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppTheme.BORDER));

        // ── Logo area ─────────────────────────────────────────────────────────
        JPanel logoArea = new JPanel();
        logoArea.setLayout(new BoxLayout(logoArea, BoxLayout.Y_AXIS));
        logoArea.setBackground(AppTheme.BG_SIDEBAR);
        logoArea.setOpaque(true);
        logoArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER),
            BorderFactory.createEmptyBorder(22, 18, 18, 18)));

        JLabel appName = new JLabel("<html><b>Concrete &amp; Fence</b><br>Estimator</html>");
        appName.setForeground(AppTheme.ACCENT);
        appName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel author = AppTheme.hintLabel("Aryan Kandula  |  CSC-251");
        author.setAlignmentX(Component.LEFT_ALIGNMENT);
        author.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        logoArea.add(appName);
        logoArea.add(author);

        // ── Step nav ──────────────────────────────────────────────────────────
        // Labels use plain text with a step number prefix — no emoji
        String[] stepLabels = {
            "1.  Project Info",
            "2.  Concrete Pad",
            "3.  Fence (Optional)",
            "4.  Report"
        };
        String[] stepDesc = {
            "Client & project details",
            "Dimensions, labor & costs",
            "Chain-link fence module",
            "Estimate summary & export"
        };

        JPanel navPanel = new JPanel();
        navPanel.setBackground(AppTheme.BG_SIDEBAR);
        navPanel.setOpaque(true);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        for (int i = 0; i < stepLabels.length; i++) {
            final int step = i;

            JPanel btnWrapper = new JPanel(new BorderLayout());
            btnWrapper.setBackground(AppTheme.BG_SIDEBAR);
            btnWrapper.setOpaque(true);
            btnWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            btnWrapper.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

            JPanel btnInner = new JPanel(new BorderLayout(0, 2));
            btnInner.setBackground(AppTheme.BG_SIDEBAR);
            btnInner.setOpaque(true);
            btnInner.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            btnInner.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel lblMain = new JLabel(stepLabels[i]);
            lblMain.setFont(AppTheme.FONT_BTN);
            lblMain.setForeground(AppTheme.TEXT_SECONDARY);

            JLabel lblSub = new JLabel(stepDesc[i]);
            lblSub.setFont(AppTheme.FONT_SMALL);
            lblSub.setForeground(AppTheme.TEXT_MUTED);

            btnInner.add(lblMain, BorderLayout.NORTH);
            btnInner.add(lblSub,  BorderLayout.SOUTH);
            btnWrapper.add(btnInner, BorderLayout.CENTER);

            // We create a real JButton behind it for accessibility, but use
            // the panel as the visual element to avoid L&F button painting bugs
            JButton hiddenBtn = new JButton();
            hiddenBtn.setOpaque(false);
            hiddenBtn.setContentAreaFilled(false);
            hiddenBtn.setBorderPainted(false);
            hiddenBtn.setFocusPainted(false);
            hiddenBtn.addActionListener(e -> showStep(step));

            // Make the whole row clickable
            btnInner.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) { showStep(step); }
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (currentStep != step) {
                        btnInner.setBackground(new Color(22, 28, 42));
                        btnInner.setOpaque(true);
                    }
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    if (currentStep != step) {
                        btnInner.setBackground(AppTheme.BG_SIDEBAR);
                    }
                }
            });

            // Store refs for highlight updates
            btnWrapper.putClientProperty("inner",   btnInner);
            btnWrapper.putClientProperty("lblMain", lblMain);
            btnWrapper.putClientProperty("lblSub",  lblSub);

            navBtns[i] = hiddenBtn;
            navBtns[i].putClientProperty("wrapper", btnWrapper);

            navPanel.add(btnWrapper);
        }

        // ── Settings button ───────────────────────────────────────────────────
        JButton btnPrices = AppTheme.secondaryBtn("[=]  Edit Prices & Rates");
        btnPrices.setFont(AppTheme.FONT_SMALL);
        btnPrices.setForeground(AppTheme.TEXT_SECONDARY);
        btnPrices.setBackground(AppTheme.BG_SIDEBAR);
        btnPrices.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btnPrices.setToolTipText("Edit material unit prices and labor rates");
        btnPrices.addActionListener(e -> new PricesEditorDialog(this).setVisible(true));

        JPanel bottomNav = new JPanel(new BorderLayout());
        bottomNav.setBackground(AppTheme.BG_SIDEBAR);
        bottomNav.setOpaque(true);
        bottomNav.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER),
            BorderFactory.createEmptyBorder(10, 8, 14, 8)));
        bottomNav.add(btnPrices, BorderLayout.CENTER);

        sidebar.add(logoArea,  BorderLayout.NORTH);
        sidebar.add(navPanel,  BorderLayout.CENTER);
        sidebar.add(bottomNav, BorderLayout.SOUTH);
        return sidebar;
    }

    // ── Bottom Action Bar ─────────────────────────────────────────────────────
    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(AppTheme.BG_SIDEBAR);
        bar.setOpaque(true);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)));

        // Left: step info
        JPanel leftInfo = new JPanel(new BorderLayout(0, 2));
        leftInfo.setBackground(AppTheme.BG_SIDEBAR);
        leftInfo.setOpaque(true);
        lblStep = new JLabel("Step 1 of 4  —  Project Information");
        lblStep.setForeground(AppTheme.TEXT_SECONDARY);
        lblStep.setFont(AppTheme.FONT_LABEL);
        leftInfo.add(lblStep, BorderLayout.CENTER);

        // Right: navigation buttons — plain text, no emoji
        btnBack     = AppTheme.secondaryBtn("<  Back");
        btnNext     = AppTheme.primaryBtn("Next  >");
        btnGenerate = AppTheme.primaryBtn("Generate Estimate");

        btnBack.setToolTipText("Go to the previous step");
        btnNext.setToolTipText("Proceed to the next step");
        btnGenerate.setToolTipText("Calculate and display the full estimate report");

        btnBack.addActionListener(e -> {
            if (currentStep > 0) showStep(currentStep - 1);
        });
        btnNext.addActionListener(e -> {
            if (validateCurrentStep()) showStep(currentStep + 1);
        });
        btnGenerate.addActionListener(e -> generateEstimate());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setBackground(AppTheme.BG_SIDEBAR);
        right.setOpaque(true);
        right.add(btnBack);
        right.add(btnNext);
        right.add(btnGenerate);

        bar.add(leftInfo, BorderLayout.WEST);
        bar.add(right,    BorderLayout.EAST);
        return bar;
    }

    // ── Navigation ────────────────────────────────────────────────────────────
    private void showStep(int step) {
        currentStep = step;
        String[] keys  = {STEP_INFO, STEP_PAD, STEP_FENCE, STEP_REPORT};
        String[] titles = {
            "Step 1 of 4  —  Project Information",
            "Step 2 of 4  —  Concrete Pad Configuration",
            "Step 3 of 4  —  Chain-Link Fence (Optional)",
            "Step 4 of 4  —  Estimate Report"
        };
        cardLayout.show(cardPanel, keys[step]);

        // Highlight active sidebar step
        for (int i = 0; i < navBtns.length; i++) {
            JPanel wrapper = (JPanel) navBtns[i].getClientProperty("wrapper");
            if (wrapper == null) continue;
            JPanel inner   = (JPanel) wrapper.getClientProperty("inner");
            JLabel lblMain = (JLabel) wrapper.getClientProperty("lblMain");
            JLabel lblSub  = (JLabel) wrapper.getClientProperty("lblSub");
            boolean active = (i == step);
            if (inner != null) {
                inner.setBackground(active ? AppTheme.ACCENT_DIM : AppTheme.BG_SIDEBAR);
                inner.setOpaque(true);
            }
            if (lblMain != null) lblMain.setForeground(active ? AppTheme.ACCENT    : AppTheme.TEXT_SECONDARY);
            if (lblSub  != null) lblSub .setForeground(active ? AppTheme.ACCENT_DIM.brighter() : AppTheme.TEXT_MUTED);
        }

        // Update bottom bar
        if (lblStep     != null) lblStep.setText(titles[step]);
        if (btnBack     != null) btnBack    .setVisible(step > 0 && step < 3);
        if (btnNext     != null) btnNext    .setVisible(step < 2);
        if (btnGenerate != null) btnGenerate.setVisible(step == 2);

        // On step 3 (report), no nav buttons needed
    }

    private boolean validateCurrentStep() {
        return switch (currentStep) {
            case 0 -> projectInfoPanel.validateInputs();
            case 1 -> concretePadPanel.validateInputs();
            case 2 -> fencePanel.validateInputs();
            default -> true;
        };
    }

    // ── Generate ──────────────────────────────────────────────────────────────
    private void generateEstimate() {
        if (!projectInfoPanel.validateInputs()) { showStep(0); return; }
        if (!concretePadPanel.validateInputs()) { showStep(1); return; }
        if (!fencePanel.validateInputs())       { showStep(2); return; }

        ProjectInfo    info  = projectInfoPanel.buildProjectInfo();
        ConcretePad    pad   = concretePadPanel.buildConcretePad();
        ChainLinkFence fence = fencePanel.buildChainLinkFence();

        Estimate est = new Estimate(info, pad, fence);
        reportPanel.setEstimate(est);
        showStep(3);
    }
}
