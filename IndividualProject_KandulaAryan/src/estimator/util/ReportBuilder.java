package estimator.util;

import estimator.model.*;

public class ReportBuilder {

    private static final String LINE  = "=".repeat(65);
    private static final String DLINE = "-".repeat(65);

    public static String build(Estimate est) {
        StringBuilder sb = new StringBuilder();
        ProjectInfo pi  = est.getProjectInfo();
        ConcretePad pad = est.getConcretePad();

        sb.append(LINE).append("\n");
        sb.append("       CONCRETE PAD & CHAIN-LINK FENCE ESTIMATOR\n");
        sb.append("                   ESTIMATE REPORT\n");
        sb.append(LINE).append("\n\n");

        // ── Project Info ──────────────────────────────────────────────────
        sb.append("  PROJECT INFORMATION\n").append(DLINE).append("\n");
        sb.append(row("Estimate #",      String.valueOf(est.getEstimateId())));
        sb.append(row("Project Name",    pi.getProjectName()));
        sb.append(row("Client Name",     pi.getClientName()));
        sb.append(row("Location",        pi.getLocation()));
        sb.append(row("Estimate Date",   pi.getEstimateDate()));
        sb.append(row("Estimator",       pi.getEstimatorName()));
        if (!pi.getNotes().isEmpty())
            sb.append(row("Notes", pi.getNotes()));
        sb.append("\n");

        // ── Concrete Pad Summary ──────────────────────────────────────────
        sb.append("  CONCRETE PAD SUMMARY\n").append(DLINE).append("\n");
        sb.append(row("Pad Dimensions",
                String.format("%.0f ft x %.0f ft", pad.getLength(), pad.getWidth())));
        sb.append(row("Total Area",
                String.format("%.2f sq ft", pad.getAreaSqFt())));
        sb.append(row("Slab Thickness",
                String.format("%.1f inches (%.4f ft)", pad.getThicknessInches(), pad.getThicknessFt())));
        sb.append(row("Raw Volume",
                String.format("%.2f cubic yards", pad.getRawVolumeCY())));
        sb.append(row("Waste Factor",
                String.format("%.1f%%", pad.getWastePercent())));
        sb.append(row("Adjusted Volume (ordered)",
                String.format("%.2f cubic yards", pad.getAdjustedVolumeCY())));
        sb.append("\n");

        // ── Labor ────────────────────────────────────────────────────────
        sb.append("  LABOR (CONCRETE PAD)\n").append(DLINE).append("\n");
        sb.append(row("Employees",           String.valueOf(pad.getEmployees())));
        sb.append(row("Hours per Employee",  String.format("%.1f hrs", pad.getHoursPerEmployee())));
        sb.append(row("Total Labor Hours",   String.format("%.1f hrs", pad.getTotalLaborHours())));
        sb.append(row("Labor Rate",          String.format("$%.2f / hr", pad.getLaborRatePerHour())));
        sb.append(row("Labor Cost",          money(pad.getLaborCost())));
        sb.append("\n");

        // ── Cost Breakdown ────────────────────────────────────────────────
        sb.append("  COST BREAKDOWN — CONCRETE PAD\n").append(DLINE).append("\n");
        sb.append(row("Concrete Material",   money(pad.getConcreteMaterialCost())));
        sb.append(row("Labor",               money(pad.getLaborCost())));
        if (pad.isIncludeRebar())
            sb.append(row("Rebar",           money(pad.getRebarCost())));
        if (pad.isIncludeWireMesh())
            sb.append(row("Wire Mesh",       money(pad.getWireMeshCost())));
        if (pad.isIncludeEquipmentRental())
            sb.append(row("Equipment Rental (" + String.format("%.0f", pad.getEquipmentRentalDays()) + " days)",
                          money(pad.getEquipmentRentalCost())));
        sb.append(row("  Subtotal (before discount)", money(pad.getSubtotalBeforeDiscount())));
        if (pad.getDiscountPercent() > 0 || pad.getDiscountFixed() > 0) {
            sb.append(row("  Discount", "- " + money(pad.getDiscountAmount())
                    + (pad.getDiscountPercent() > 0 ? String.format(" (%.1f%%)", pad.getDiscountPercent()) : "")
                    + (pad.getDiscountFixed() > 0   ? String.format(" + $%.2f fixed", pad.getDiscountFixed()) : "")));
            sb.append(row("  Subtotal (after discount)", money(pad.getSubtotalAfterDiscount())));
        }
        if (pad.getContingencyPercent() > 0) {
            sb.append(row(String.format("  Contingency (%.1f%%)", pad.getContingencyPercent()),
                          money(pad.getContingencyAmount())));
        }
        sb.append(row("  PAD TOTAL", money(pad.getPadGrandTotal())));
        sb.append("\n");

        // ── Fencing ───────────────────────────────────────────────────────
        if (est.hasFencing()) {
            ChainLinkFence f = est.getChainLinkFence();
            sb.append("  CHAIN-LINK FENCE SUMMARY\n").append(DLINE).append("\n");
            sb.append(row("Perimeter",          String.format("%.0f ft", f.getPerimeterFt())));
            sb.append(row("Height",             String.format("%.0f ft", f.getHeightFt())));
            sb.append(row("Post Spacing",       String.format("%.0f ft on center", f.getPostSpacingFt())));
            sb.append(row("Mesh Gauge",         f.getGaugeKey() + " gauge"));
            sb.append(row("Post Type",          capitalise(f.getPostType())));
            sb.append(row("Fabric (net LF)",    String.format("%.2f LF", f.getFabricLinearFt())));
            sb.append(row("Fabric (w/ overage)",String.format("%.2f LF (%.1f%% overage)", f.getAdjustedFabricLF(), f.getOveragePercent())));
            sb.append(row("Line Posts",         String.valueOf(f.getLinePostCount())));
            sb.append(row("Terminal Posts",     String.valueOf(f.getTerminalPostCount())));
            sb.append(row("Total Posts",        String.valueOf(f.getTotalPostCount())));
            if (f.getSingleGates() > 0)
                sb.append(row("Single Walk Gates",  String.valueOf(f.getSingleGates())));
            if (f.getDoubleGates() > 0)
                sb.append(row("Double Drive Gates", String.valueOf(f.getDoubleGates())));
            if (f.getSlidingGates() > 0)
                sb.append(row("Sliding Gates",      String.valueOf(f.getSlidingGates())));
            sb.append(row("Top Treatment",       formatTopTreatment(f.getTopTreatment())));
            sb.append("\n");

            sb.append("  COST BREAKDOWN — CHAIN-LINK FENCE\n").append(DLINE).append("\n");
            sb.append(row("Fence Fabric",        money(f.getFabricCost())));
            sb.append(row("Posts",               money(f.getPostCost())));
            sb.append(row("Post Concrete",       money(f.getPostConcreteCost())));
            sb.append(row("Top Rail",            money(f.getTopRailCost())));
            if (f.getGateCost() > 0)
                sb.append(row("Gates",           money(f.getGateCost())));
            if (f.getTopTreatmentCost() > 0)
                sb.append(row("Top Treatment",   money(f.getTopTreatmentCost())));
            sb.append(row("Hardware",            money(f.getHardwareCost())));
            sb.append(row("Fence Labor",         money(f.getFenceLaborCost())));
            sb.append(row("  Subtotal (before discount)", money(f.getSubtotalBeforeDiscount())));
            if (f.getDiscountPercent() > 0 || f.getDiscountFixed() > 0) {
                sb.append(row("  Discount",      "- " + money(f.getDiscountAmount())));
            }
            sb.append(row("  FENCE TOTAL",       money(f.getFenceGrandTotal())));
            sb.append("\n");
        }

        // ── Grand Total ───────────────────────────────────────────────────
        sb.append(LINE).append("\n");
        sb.append(row("  PAD TOTAL",        money(pad.getPadGrandTotal())));
        if (est.hasFencing())
            sb.append(row("  FENCE TOTAL",  money(est.getChainLinkFence().getFenceGrandTotal())));
        sb.append(row("  *** PROJECT GRAND TOTAL ***", money(est.getProjectGrandTotal())));
        sb.append(LINE).append("\n\n");
        sb.append("  This estimate is valid for 30 days from the date shown above.\n");
        sb.append("  Prices subject to change. Thank you for your business!\n\n");

        return sb.toString();
    }

    private static String row(String label, String value) {
        return String.format("  %-38s %s%n", label + ":", value);
    }

    private static String money(double amount) {
        return String.format("$%,.2f", amount);
    }

    private static String capitalise(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String formatTopTreatment(String key) {
        return switch (key) {
            case "barbed1"  -> "Barbed Wire – 1 Strand";
            case "barbed2"  -> "Barbed Wire – 2 Strands";
            case "barbed3"  -> "Barbed Wire – 3 Strands";
            case "razor"    -> "Razor Wire Coil";
            case "privacy"  -> "Privacy Slats";
            default         -> "None";
        };
    }
}
