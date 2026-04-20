package estimator.ui;

import estimator.csv.CsvManager;
import estimator.model.*;
import estimator.util.ReportBuilder;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.print.*;
import java.io.*;

/**
 * Step 4 — Estimate Report display panel.
 * Shows the full formatted report, with buttons to copy, save (.txt), export (.pdf), and print.
 *
 * Author: Aryan Kandula  |  CSC-251 Module 5
 */
public class ReportPanel extends JPanel {

    private final JTextArea taReport;
    private Estimate currentEstimate;

    public ReportPanel() {
        setBackground(AppTheme.BG_PANEL);
        setOpaque(true);
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(20, 28, 16, 28));

        // Header
        JLabel title = new JLabel("Estimate Report");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel hint = AppTheme.hintLabel("Your completed estimate — review, copy, save as .txt, export as PDF, or print.");
        hint.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.BG_PANEL);
        header.setOpaque(true);
        header.add(title, BorderLayout.NORTH);
        header.add(hint,  BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Report text area
        taReport = new JTextArea();
        taReport.setFont(AppTheme.FONT_MONO);
        taReport.setBackground(AppTheme.BG_DARK);
        taReport.setForeground(AppTheme.TEXT_PRIMARY);
        taReport.setCaretColor(AppTheme.ACCENT);
        taReport.setEditable(false);
        taReport.setOpaque(true);
        taReport.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        taReport.setText("No estimate generated yet.\n\nComplete Steps 1 through 3, then click  Generate Estimate  in the bottom bar.");

        JScrollPane scroll = new JScrollPane(taReport);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER, 1));
        scroll.setBackground(AppTheme.BG_DARK);
        scroll.getViewport().setBackground(AppTheme.BG_DARK);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // Buttons
        JButton btnCopy      = AppTheme.secondaryBtn("Copy to Clipboard");
        JButton btnSave      = AppTheme.secondaryBtn("Save as .txt");
        JButton btnExportPdf = AppTheme.primaryBtn("Export PDF");
        JButton btnPrint     = AppTheme.secondaryBtn("Print");

        btnCopy     .setToolTipText("Copy the entire report text to your clipboard");
        btnSave     .setToolTipText("Save the report as a plain-text .txt file");
        btnExportPdf.setToolTipText("Export the report as a PDF file (black text, white background)");
        btnPrint    .setToolTipText("Send the report to your printer");

        btnCopy.addActionListener(e -> doCopy());
        btnSave.addActionListener(e -> doSaveTxt());
        btnExportPdf.addActionListener(e -> doExportPdf());
        btnPrint.addActionListener(e -> doPrint());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        btnRow.setBackground(AppTheme.BG_PANEL);
        btnRow.setOpaque(true);
        btnRow.add(btnPrint);
        btnRow.add(btnCopy);
        btnRow.add(btnSave);
        btnRow.add(btnExportPdf);
        add(btnRow, BorderLayout.SOUTH);
    }

    public void setEstimate(Estimate est) {
        this.currentEstimate = est;
        String report = ReportBuilder.build(est);
        taReport.setText(report);
        taReport.setCaretPosition(0);
        CsvManager.appendEstimateLog(est);
        CsvManager.saveEstimateReport(est, report);
    }

    // ── Copy ──────────────────────────────────────────────────────────────────
    private void doCopy() {
        if (!checkEstimate()) return;
        StringSelection sel = new StringSelection(taReport.getText());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, sel);
        showInfo("Report copied to clipboard successfully!");
    }

    // ── Save .txt ─────────────────────────────────────────────────────────────
    private void doSaveTxt() {
        if (!checkEstimate()) return;
        JFileChooser fc = buildFileChooser(
            "estimate_" + currentEstimate.getEstimateId() + ".txt",
            "Save Estimate Report (.txt)");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = ensureExtension(fc.getSelectedFile(), ".txt");
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.print(taReport.getText());
                showInfo("Report saved to:\n" + f.getAbsolutePath());
            } catch (IOException ex) {
                showError("Error saving file: " + ex.getMessage());
            }
        }
    }

    // ── Export PDF ────────────────────────────────────────────────────────────
    private void doExportPdf() {
        if (!checkEstimate()) return;
        JFileChooser fc = buildFileChooser(
            "estimate_" + currentEstimate.getEstimateId() + ".pdf",
            "Export Estimate Report as PDF");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Files (*.pdf)", "pdf"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File f = ensureExtension(fc.getSelectedFile(), ".pdf");
        try {
            exportPdf(f, taReport.getText());
            showInfo("PDF exported to:\n" + f.getAbsolutePath());
        } catch (Exception ex) {
            showError("PDF export error: " + ex.getMessage());
        }
    }

    /**
     * Renders the report text as a paginated PDF using Java2D PrinterJob
     * captured into a PDF stream via a custom PagePainter.
     * Uses a plain Java approach (no third-party lib): writes a minimal
     * PDF with embedded text rendered at printer resolution, then captures
     * via javax.print to a file output stream.
     *
     * Because javax.print PDF output needs a service, we use a simpler reliable
     * approach: render each page onto a BufferedImage at 150 DPI and write a
     * multi-page PDF manually (PDF 1.4, uncompressed, ASCII-safe).
     */
    private void exportPdf(File dest, String text) throws Exception {
        // ── Page geometry (letter, 72 pts/inch) ──────────────────────────────
        final int PAGE_W_PT  = 612;   // 8.5 in
        final int PAGE_H_PT  = 792;   // 11 in
        final int MARGIN_PT  = 54;    // 0.75 in
        final int DPI        = 150;
        final double SCALE   = DPI / 72.0;
        final int PAGE_W_PX  = (int)(PAGE_W_PT  * SCALE);
        final int PAGE_H_PX  = (int)(PAGE_H_PT  * SCALE);
        final int MARGIN_PX  = (int)(MARGIN_PT  * SCALE);

        // ── Font for PDF body ─────────────────────────────────────────────────
        Font pdfFont      = new Font("Courier New", Font.PLAIN, 11);
        Font pdfFontBold  = new Font("Courier New", Font.BOLD,  13);
        FontMetrics fm;

        // Measure line height using a scratch image
        java.awt.image.BufferedImage scratch =
            new java.awt.image.BufferedImage(10, 10, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D sg = scratch.createGraphics();
        sg.setFont(pdfFont);
        fm = sg.getFontMetrics();
        int lineH = fm.getHeight();
        sg.dispose();

        int contentW = PAGE_W_PX - 2 * MARGIN_PX;
        int contentH = PAGE_H_PX - 2 * MARGIN_PX;
        int linesPerPage = contentH / lineH;

        // ── Split text into lines, wrap if needed ─────────────────────────────
        java.util.List<String> allLines = new java.util.ArrayList<>();
        java.util.List<String> fontFlags = new java.util.ArrayList<>(); // "bold" or "plain"

        // Rough char-width estimate for wrapping (monospace)
        int charW = fm.charWidth('M');
        int maxChars = Math.max(10, contentW / Math.max(1, charW));

        for (String raw : text.split("\n", -1)) {
            // Detect section headers (ALL CAPS lines)
            boolean bold = raw.trim().length() > 4 &&
                           raw.equals(raw.toUpperCase()) &&
                           raw.matches(".*[A-Z]{3,}.*");
            if (raw.length() <= maxChars) {
                allLines.add(raw);
                fontFlags.add(bold ? "bold" : "plain");
            } else {
                // Hard-wrap
                int pos = 0;
                while (pos < raw.length()) {
                    int end = Math.min(pos + maxChars, raw.length());
                    allLines.add(raw.substring(pos, end));
                    fontFlags.add("plain");
                    pos = end;
                }
            }
        }

        // ── Paginate ──────────────────────────────────────────────────────────
        java.util.List<java.awt.image.BufferedImage> pages = new java.util.ArrayList<>();
        int totalLines = allLines.size();
        int pageIdx = 0;

        while (pageIdx * linesPerPage < totalLines) {
            java.awt.image.BufferedImage img =
                new java.awt.image.BufferedImage(PAGE_W_PX, PAGE_H_PX,
                    java.awt.image.BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();

            // White page background
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, PAGE_W_PX, PAGE_H_PX);

            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                               RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(Color.BLACK);  // All text BLACK

            int startLine = pageIdx * linesPerPage;
            int endLine   = Math.min(startLine + linesPerPage, totalLines);

            for (int li = startLine; li < endLine; li++) {
                String line = allLines.get(li);
                boolean isBold = "bold".equals(fontFlags.get(li));
                g.setFont(isBold ? pdfFontBold : pdfFont);
                fm = g.getFontMetrics();
                int y = MARGIN_PX + (li - startLine) * lineH + fm.getAscent();
                g.drawString(line, MARGIN_PX, y);
            }

            // Thin footer line + page number
            int footerY = PAGE_H_PX - MARGIN_PX / 2;
            g.setColor(new Color(180, 180, 180));
            g.drawLine(MARGIN_PX, footerY - 14, PAGE_W_PX - MARGIN_PX, footerY - 14);
            g.setFont(new Font("Courier New", Font.PLAIN, 9));
            g.setColor(Color.DARK_GRAY);
            String pageNum = "Page " + (pageIdx + 1);
            int pnW = g.getFontMetrics().stringWidth(pageNum);
            g.drawString(pageNum, (PAGE_W_PX - pnW) / 2, footerY);

            g.dispose();
            pages.add(img);
            pageIdx++;
        }

        // ── Write minimal PDF (PDF 1.4) ───────────────────────────────────────
        writePdf(dest, pages, PAGE_W_PT, PAGE_H_PT, DPI);
    }

    /**
     * Write a minimal valid PDF 1.4 file, one JPEG-encoded raster image per page.
     *
     * Object layout (per page i, 0-indexed):
     *   obj 1        = Catalog
     *   obj 2        = Pages
     *   obj 3+3*i    = Page dict
     *   obj 4+3*i    = Content stream ("q … cm /Im Do Q")
     *   obj 5+3*i    = Image XObject (DCTDecode / JPEG)
     *
     * Total objects in xref = 1 (free obj 0) + 2 (catalog + pages) + 3*n (per page)
     *                       = 3 + 3*n
     */
    private void writePdf(File dest, java.util.List<java.awt.image.BufferedImage> pages,
                          int pageWpt, int pageHpt, int dpi) throws IOException {

        // Encode every page as JPEG
        java.util.List<byte[]> jpegs = new java.util.ArrayList<>();
        for (java.awt.image.BufferedImage img : pages) {
            java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
            javax.imageio.ImageIO.write(img, "jpeg", bos);
            jpegs.add(bos.toByteArray());
        }

        int n = jpegs.size();
        // offsets[k] = byte offset of object (k+1) — i.e. offsets[0] = offset of obj 1
        java.util.List<Integer> offsets = new java.util.ArrayList<>();

        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        java.io.PrintStream out = new java.io.PrintStream(buf, false, "ISO-8859-1");

        // ── PDF header ────────────────────────────────────────────────────────
        out.print("%PDF-1.4\n%\u00e2\u00e3\u00cf\u00d3\n");

        // ── obj 1: Catalog ────────────────────────────────────────────────────
        offsets.add(buf.size());
        out.print("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        // ── obj 2: Pages ──────────────────────────────────────────────────────
        offsets.add(buf.size());
        StringBuilder kids = new StringBuilder("[");
        for (int i = 0; i < n; i++) {
            if (i > 0) kids.append(' ');
            kids.append(3 + 3 * i).append(" 0 R");
        }
        kids.append(']');
        out.print("2 0 obj\n<< /Type /Pages /Kids " + kids
                + " /Count " + n + " >>\nendobj\n");

        // ── Per-page objects ──────────────────────────────────────────────────
        for (int i = 0; i < n; i++) {
            byte[] jpeg = jpegs.get(i);
            int imgW = pages.get(i).getWidth();
            int imgH = pages.get(i).getHeight();
            int pageNum    = 3 + 3 * i;
            int contentNum = 4 + 3 * i;
            int imageNum   = 5 + 3 * i;

            // Content stream body: scale image to fill page then paint it
            String cs = "q " + pageWpt + " 0 0 " + pageHpt
                      + " 0 0 cm /Im" + i + " Do Q\n";

            // Page dict
            offsets.add(buf.size());
            out.print(pageNum + " 0 obj\n"
                + "<< /Type /Page /Parent 2 0 R"
                + " /MediaBox [0 0 " + pageWpt + " " + pageHpt + "]"
                + " /Resources << /XObject << /Im" + i + " " + imageNum + " 0 R >> >>"
                + " /Contents " + contentNum + " 0 R >>\nendobj\n");

            // Content stream
            offsets.add(buf.size());
            out.print(contentNum + " 0 obj\n"
                + "<< /Length " + cs.length() + " >>\nstream\n"
                + cs + "endstream\nendobj\n");

            // Image XObject (binary JPEG data)
            offsets.add(buf.size());
            out.print(imageNum + " 0 obj\n"
                + "<< /Type /XObject /Subtype /Image"
                + " /Width " + imgW + " /Height " + imgH
                + " /ColorSpace /DeviceRGB /BitsPerComponent 8"
                + " /Filter /DCTDecode /Length " + jpeg.length
                + " >>\nstream\n");
            out.flush();
            buf.write(jpeg);          // raw binary — must flush PrintStream first
            out.print("\nendstream\nendobj\n");
        }

        // ── Cross-reference table ─────────────────────────────────────────────
        out.flush();
        int xrefOffset = buf.size();
        int totalObjs  = 3 + 3 * n;  // obj 0 (free) + obj1 + obj2 + 3*n page objects
        out.print("xref\n0 " + totalObjs + "\n");
        out.print("0000000000 65535 f \n");           // obj 0 — always free
        for (int off : offsets) {
            out.print(String.format("%010d 00000 n \n", off));
        }

        // ── Trailer ───────────────────────────────────────────────────────────
        out.print("trailer\n<< /Size " + totalObjs + " /Root 1 0 R >>\n");
        out.print("startxref\n" + xrefOffset + "\n%%EOF\n");
        out.flush();

        try (FileOutputStream fos = new FileOutputStream(dest)) {
            buf.writeTo(fos);
        }
    }

    // ── Print ─────────────────────────────────────────────────────────────────
    private void doPrint() {
        if (!checkEstimate()) return;
        // Print with explicit black text on white — do NOT use taReport.print()
        // which inherits the dark theme colors
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Estimate Report - " + currentEstimate.getEstimateId());
        job.setPrintable(new ReportPrintable(taReport.getText()));
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException ex) {
                showError("Print error: " + ex.getMessage());
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private boolean checkEstimate() {
        if (currentEstimate == null) {
            showInfo("No estimate has been generated yet. Complete Steps 1–3 first.");
            return false;
        }
        return true;
    }

    private JFileChooser buildFileChooser(String defaultName, String title) {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(defaultName));
        fc.setDialogTitle(title);
        AppTheme.themeFileChooser(fc);
        return fc;
    }

    private File ensureExtension(File f, String ext) {
        if (!f.getName().toLowerCase().endsWith(ext))
            return new File(f.getParentFile(), f.getName() + ext);
        return f;
    }

    private void showInfo(String msg) {
        AppTheme.showInfo(this, msg);
    }
    private void showError(String msg) {
        AppTheme.showError(this, msg);
    }

    // ── Printable impl — black text on white, paginated ───────────────────────
    private static class ReportPrintable implements Printable {
        private final String[] lines;
        private static final Font PRINT_FONT = new Font("Courier New", Font.PLAIN, 10);
        private int linesPerPage = -1;

        ReportPrintable(String text) {
            this.lines = text.split("\n", -1);
        }

        @Override
        public int print(Graphics graphics, PageFormat pf, int pageIndex) {
            Graphics2D g = (Graphics2D) graphics;
            g.setFont(PRINT_FONT);
            FontMetrics fm = g.getFontMetrics();
            int lineH = fm.getHeight();

            int x = (int) pf.getImageableX();
            int y = (int) pf.getImageableY();
            int h = (int) pf.getImageableHeight();

            if (linesPerPage < 0) linesPerPage = Math.max(1, h / lineH);

            int start = pageIndex * linesPerPage;
            if (start >= lines.length) return NO_SUCH_PAGE;

            g.setColor(Color.BLACK);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                               RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int end = Math.min(start + linesPerPage, lines.length);
            for (int i = start; i < end; i++) {
                g.drawString(lines[i], x, y + (i - start) * lineH + fm.getAscent());
            }
            return PAGE_EXISTS;
        }
    }
}

