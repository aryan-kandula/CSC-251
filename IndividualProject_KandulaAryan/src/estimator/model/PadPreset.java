package estimator.model;

public class PadPreset {
    private String name;
    private double length;
    private double width;
    private String typicalUse;

    public PadPreset(String name, double length, double width, String typicalUse) {
        this.name       = name;
        this.length     = length;
        this.width      = width;
        this.typicalUse = typicalUse;
    }

    public String getName()      { return name; }
    public double getLength()    { return length; }
    public double getWidth()     { return width; }
    public String getTypicalUse(){ return typicalUse; }

    public static PadPreset[] getPresets() {
        return new PadPreset[] {
            new PadPreset("Small Residential Pad",      20,  20,  "AC unit / shed base"),
            new PadPreset("Garage / Workshop Pad",      20,  40,  "2-car garage"),
            new PadPreset("Equipment Pad",              10,  10,  "Generator / HVAC unit"),
            new PadPreset("Dumpster Pad",               12,  20,  "Commercial waste enclosure"),
            new PadPreset("RV / Boat Storage Slab",     14,  40,  "RV or boat storage"),
            new PadPreset("Basketball / Sport Court",   50,  84,  "Sport court"),
            new PadPreset("Small Warehouse Slab",       50,  100, "Small warehouse / storage"),
            new PadPreset("Mid Warehouse Slab",         100, 150, "Mid-size warehouse"),
            new PadPreset("Large Warehouse Slab",       150, 200, "Large warehouse / distribution"),
            new PadPreset("Commercial Loading Apron",   60,  80,  "Truck / loading dock apron"),
            new PadPreset("Parking Lot Section",        100, 200, "Commercial parking"),
            new PadPreset("Custom (User Input)",        0,   0,   "Any custom size")
        };
    }
}
