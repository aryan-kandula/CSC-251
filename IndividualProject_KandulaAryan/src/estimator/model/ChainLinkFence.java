package estimator.model;

public class ChainLinkFence {
    // Layout
    private double perimeterFt;
    private double heightFt;
    private double postSpacingFt;

    // Specs
    private String gaugeKey;        // "11.5", "11", "9"
    private String postType;        // "galvanized", "aluminum", "schedule40"

    // Gates
    private int singleGates;
    private int doubleGates;
    private int slidingGates;

    // Top treatment
    private String topTreatment;    // "none","barbed1","barbed2","barbed3","razor","privacy"

    // Overage
    private double overagePercent;

    // Labor
    private double fenceLaborHours;
    private double fenceLaborRate;

    // Discount
    private double discountPercent;
    private double discountFixed;

    // Prices
    private double fabricPricePerLF;
    private double postPrice;
    private double postConcreteBagPrice;
    private double topRailPricePerLF;
    private double singleGatePrice;
    private double doubleGatePrice;
    private double slidingGatePrice;
    private double topTreatmentPricePerLF;
    private double hardwarePercent;

    public ChainLinkFence(double perimeterFt, double heightFt, double postSpacingFt,
                          String gaugeKey, String postType,
                          int singleGates, int doubleGates, int slidingGates,
                          String topTreatment, double overagePercent,
                          double fenceLaborHours, double fenceLaborRate,
                          double discountPercent, double discountFixed,
                          double fabricPricePerLF, double postPrice,
                          double postConcreteBagPrice, double topRailPricePerLF,
                          double singleGatePrice, double doubleGatePrice, double slidingGatePrice,
                          double topTreatmentPricePerLF, double hardwarePercent) {
        this.perimeterFt            = perimeterFt;
        this.heightFt               = heightFt;
        this.postSpacingFt          = postSpacingFt;
        this.gaugeKey               = gaugeKey;
        this.postType               = postType;
        this.singleGates            = singleGates;
        this.doubleGates            = doubleGates;
        this.slidingGates           = slidingGates;
        this.topTreatment           = topTreatment;
        this.overagePercent         = overagePercent;
        this.fenceLaborHours        = fenceLaborHours;
        this.fenceLaborRate         = fenceLaborRate;
        this.discountPercent        = discountPercent;
        this.discountFixed          = discountFixed;
        this.fabricPricePerLF       = fabricPricePerLF;
        this.postPrice              = postPrice;
        this.postConcreteBagPrice   = postConcreteBagPrice;
        this.topRailPricePerLF      = topRailPricePerLF;
        this.singleGatePrice        = singleGatePrice;
        this.doubleGatePrice        = doubleGatePrice;
        this.slidingGatePrice       = slidingGatePrice;
        this.topTreatmentPricePerLF = topTreatmentPricePerLF;
        this.hardwarePercent        = hardwarePercent;
    }

    // ── Gate opening deductions ───────────────────────────────────────────────
    private double getGateOpeningFt() {
        return (singleGates * 3.5) + (doubleGates * 14.0) + (slidingGates * 20.0);
    }

    // ── Fabric ───────────────────────────────────────────────────────────────
    public double getFabricLinearFt() {
        return Math.max(0, perimeterFt - getGateOpeningFt());
    }

    public double getAdjustedFabricLF() {
        return getFabricLinearFt() * (1.0 + overagePercent / 100.0);
    }

    public double getFabricCost() {
        return getAdjustedFabricLF() * fabricPricePerLF;
    }

    // ── Posts ────────────────────────────────────────────────────────────────
    public int getLinePostCount() {
        return (int) Math.max(0, (getFabricLinearFt() / postSpacingFt) - 1);
    }

    public int getTerminalPostCount() {
        // 4 corners + 2 per gate opening
        return 4 + (singleGates * 2) + (doubleGates * 2) + (slidingGates * 2);
    }

    public int getTotalPostCount() {
        return getLinePostCount() + getTerminalPostCount();
    }

    public double getPostCost() {
        return getTotalPostCount() * postPrice;
    }

    public double getPostConcreteCost() {
        return getTotalPostCount() * postConcreteBagPrice;
    }

    // ── Top Rail ─────────────────────────────────────────────────────────────
    public double getTopRailCost() {
        return getFabricLinearFt() * topRailPricePerLF;
    }

    // ── Gates ────────────────────────────────────────────────────────────────
    public double getGateCost() {
        return (singleGates  * singleGatePrice)
             + (doubleGates  * doubleGatePrice)
             + (slidingGates * slidingGatePrice);
    }

    // ── Top Treatment ────────────────────────────────────────────────────────
    public double getTopTreatmentCost() {
        if (topTreatment.equals("none")) return 0;
        return getFabricLinearFt() * topTreatmentPricePerLF;
    }

    // ── Hardware ─────────────────────────────────────────────────────────────
    public double getHardwareCost() {
        double baseMaterial = getFabricCost() + getPostCost() + getTopRailCost();
        return baseMaterial * hardwarePercent;
    }

    // ── Labor ────────────────────────────────────────────────────────────────
    public double getFenceLaborCost() {
        return fenceLaborHours * fenceLaborRate;
    }

    // ── Subtotals ────────────────────────────────────────────────────────────
    public double getSubtotalBeforeDiscount() {
        return getFabricCost()
             + getPostCost()
             + getPostConcreteCost()
             + getTopRailCost()
             + getGateCost()
             + getTopTreatmentCost()
             + getHardwareCost()
             + getFenceLaborCost();
    }

    public double getDiscountAmount() {
        double pctDiscount = getSubtotalBeforeDiscount() * (discountPercent / 100.0);
        return pctDiscount + discountFixed;
    }

    public double getFenceGrandTotal() {
        return Math.max(0, getSubtotalBeforeDiscount() - getDiscountAmount());
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public double getPerimeterFt()      { return perimeterFt; }
    public double getHeightFt()         { return heightFt; }
    public double getPostSpacingFt()    { return postSpacingFt; }
    public String getGaugeKey()         { return gaugeKey; }
    public String getPostType()         { return postType; }
    public int    getSingleGates()      { return singleGates; }
    public int    getDoubleGates()      { return doubleGates; }
    public int    getSlidingGates()     { return slidingGates; }
    public String getTopTreatment()     { return topTreatment; }
    public double getOveragePercent()   { return overagePercent; }
    public double getFenceLaborHours()  { return fenceLaborHours; }
    public double getFenceLaborRate()   { return fenceLaborRate; }
    public double getDiscountPercent()  { return discountPercent; }
    public double getDiscountFixed()    { return discountFixed; }
}
