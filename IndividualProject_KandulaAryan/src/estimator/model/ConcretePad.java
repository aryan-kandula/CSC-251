package estimator.model;

public class ConcretePad {
    // Dimensions
    private double length;
    private double width;
    private double thicknessInches;
    private double wastePercent;

    // Labor
    private int    employees;
    private double hoursPerEmployee;
    private double laborRatePerHour;

    // Optional add-ons
    private boolean includeRebar;
    private boolean includeWireMesh;
    private boolean includeEquipmentRental;
    private double  equipmentRentalDays;

    // Prices (loaded from CSV)
    private double concretePricePerCY;
    private double rebarPricePerLF;
    private double wireMeshPricePerSqFt;
    private double equipmentRentalPricePerDay;

    // Discount
    private double discountPercent;
    private double discountFixed;

    // Contingency
    private double contingencyPercent;

    public ConcretePad(double length, double width, double thicknessInches, double wastePercent,
                       int employees, double hoursPerEmployee, double laborRatePerHour,
                       boolean includeRebar, boolean includeWireMesh,
                       boolean includeEquipmentRental, double equipmentRentalDays,
                       double concretePricePerCY, double rebarPricePerLF,
                       double wireMeshPricePerSqFt, double equipmentRentalPricePerDay,
                       double discountPercent, double discountFixed, double contingencyPercent) {
        this.length                   = length;
        this.width                    = width;
        this.thicknessInches          = thicknessInches;
        this.wastePercent             = wastePercent;
        this.employees                = employees;
        this.hoursPerEmployee         = hoursPerEmployee;
        this.laborRatePerHour         = laborRatePerHour;
        this.includeRebar             = includeRebar;
        this.includeWireMesh          = includeWireMesh;
        this.includeEquipmentRental   = includeEquipmentRental;
        this.equipmentRentalDays      = equipmentRentalDays;
        this.concretePricePerCY       = concretePricePerCY;
        this.rebarPricePerLF          = rebarPricePerLF;
        this.wireMeshPricePerSqFt     = wireMeshPricePerSqFt;
        this.equipmentRentalPricePerDay = equipmentRentalPricePerDay;
        this.discountPercent          = discountPercent;
        this.discountFixed            = discountFixed;
        this.contingencyPercent       = contingencyPercent;
    }

    // ── Area & Volume ────────────────────────────────────────────────────────
    public double getAreaSqFt() {
        return length * width;
    }

    public double getThicknessFt() {
        return thicknessInches / 12.0;
    }

    public double getRawVolumeCY() {
        return (length * width * getThicknessFt()) / 27.0;
    }

    public double getAdjustedVolumeCY() {
        return getRawVolumeCY() * (1.0 + wastePercent / 100.0);
    }

    // ── Labor ────────────────────────────────────────────────────────────────
    public double getTotalLaborHours() {
        return employees * hoursPerEmployee;
    }

    public double getLaborCost() {
        return getTotalLaborHours() * laborRatePerHour;
    }

    // ── Materials ────────────────────────────────────────────────────────────
    public double getConcreteMaterialCost() {
        return getAdjustedVolumeCY() * concretePricePerCY;
    }

    public double getRebarCost() {
        if (!includeRebar) return 0;
        // Estimate: rebar grid at 18" spacing both ways ≈ perimeter + interior runs
        double perimeter = 2 * (length + width);
        double interiorRunsL = (int)(width / 1.5) * length;
        double interiorRunsW = (int)(length / 1.5) * width;
        double totalLF = perimeter + interiorRunsL + interiorRunsW;
        return totalLF * rebarPricePerLF;
    }

    public double getWireMeshCost() {
        if (!includeWireMesh) return 0;
        return getAreaSqFt() * wireMeshPricePerSqFt;
    }

    public double getEquipmentRentalCost() {
        if (!includeEquipmentRental) return 0;
        return equipmentRentalDays * equipmentRentalPricePerDay;
    }

    // ── Subtotals ────────────────────────────────────────────────────────────
    public double getSubtotalBeforeDiscount() {
        return getConcreteMaterialCost()
             + getLaborCost()
             + getRebarCost()
             + getWireMeshCost()
             + getEquipmentRentalCost();
    }

    public double getDiscountAmount() {
        double pctDiscount = getSubtotalBeforeDiscount() * (discountPercent / 100.0);
        return pctDiscount + discountFixed;
    }

    public double getSubtotalAfterDiscount() {
        return Math.max(0, getSubtotalBeforeDiscount() - getDiscountAmount());
    }

    public double getContingencyAmount() {
        return getSubtotalAfterDiscount() * (contingencyPercent / 100.0);
    }

    public double getPadGrandTotal() {
        return getSubtotalAfterDiscount() + getContingencyAmount();
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public double getLength()             { return length; }
    public double getWidth()              { return width; }
    public double getThicknessInches()    { return thicknessInches; }
    public double getWastePercent()       { return wastePercent; }
    public int    getEmployees()          { return employees; }
    public double getHoursPerEmployee()   { return hoursPerEmployee; }
    public double getLaborRatePerHour()   { return laborRatePerHour; }
    public boolean isIncludeRebar()       { return includeRebar; }
    public boolean isIncludeWireMesh()    { return includeWireMesh; }
    public boolean isIncludeEquipmentRental() { return includeEquipmentRental; }
    public double getEquipmentRentalDays(){ return equipmentRentalDays; }
    public double getDiscountPercent()    { return discountPercent; }
    public double getDiscountFixed()      { return discountFixed; }
    public double getContingencyPercent() { return contingencyPercent; }
}
