package estimator.model;

public class FencePreset {
    private String name;
    private double enclosureLength;
    private double enclosureWidth;
    private double perimeter;
    private double heightFt;
    private String typicalUse;

    public FencePreset(String name, double enclosureLength, double enclosureWidth,
                       double perimeter, double heightFt, String typicalUse) {
        this.name            = name;
        this.enclosureLength = enclosureLength;
        this.enclosureWidth  = enclosureWidth;
        this.perimeter       = perimeter;
        this.heightFt        = heightFt;
        this.typicalUse      = typicalUse;
    }

    public String getName()            { return name; }
    public double getEnclosureLength() { return enclosureLength; }
    public double getEnclosureWidth()  { return enclosureWidth; }
    public double getPerimeter()       { return perimeter; }
    public double getHeightFt()        { return heightFt; }
    public String getTypicalUse()      { return typicalUse; }

    public static FencePreset[] getPresets() {
        return new FencePreset[] {
            new FencePreset("Small Yard Enclosure",       20,  40,  120,  4,  "Residential / pet"),
            new FencePreset("Garage / Shop Perimeter",    20,  40,  120,  6,  "Workshop security"),
            new FencePreset("Dumpster / Waste Enclosure", 12,  20,  64,   6,  "Commercial waste"),
            new FencePreset("Equipment Yard",             100, 100, 400,  8,  "Equipment / secure yard"),
            new FencePreset("Small Warehouse Perimeter",  50,  100, 300,  6,  "Small facility"),
            new FencePreset("Parking Lot Perimeter",      100, 200, 600,  4,  "Commercial parking"),
            new FencePreset("Mid Warehouse Perimeter",    100, 150, 500,  8,  "Commercial facility"),
            new FencePreset("Large Facility Perimeter",   150, 200, 700,  8,  "Large warehouse"),
            new FencePreset("Security Compound",          200, 300, 1000, 10, "High-security / industrial"),
            new FencePreset("Custom (User Input)",        0,   0,   0,    0,  "Any configuration")
        };
    }
}
