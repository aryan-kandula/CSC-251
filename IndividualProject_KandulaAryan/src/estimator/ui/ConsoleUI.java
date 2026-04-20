package estimator.ui;

import estimator.csv.CsvManager;
import estimator.model.*;
import estimator.util.InputValidator;
import estimator.util.ReportBuilder;

import java.time.LocalDate;
import java.util.Map;

public class ConsoleUI {

    private Map<String, Double> prices;
    private Map<String, Double> laborRates;

    public ConsoleUI() {
        prices     = CsvManager.loadMaterialPrices();
        laborRates = CsvManager.loadLaborRates();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Entry Point
    // ═══════════════════════════════════════════════════════════════════════════
    public void start() {
        printBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = InputValidator.readMenuChoice("  Your choice: ", 4);
            switch (choice) {
                case 1 -> runNewEstimate();
                case 2 -> viewEditPrices();
                case 3 -> viewEditLaborRates();
                case 4 -> {
                    System.out.println("\n  Thank you for using the Concrete & Fence Estimator. Goodbye!\n");
                    running = false;
                }
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Banners & Menus
    // ═══════════════════════════════════════════════════════════════════════════
    private void printBanner() {
        System.out.println();
        System.out.println("  ╔═══════════════════════════════════════════════════════════╗");
        System.out.println("  ║     CONCRETE PAD & CHAIN-LINK FENCE ESTIMATOR  v1.0      ║");
        System.out.println("  ║              Professional Estimate Generator              ║");
        System.out.println("  ╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private void printMainMenu() {
        System.out.println("  ┌─────────────────────────────────────────────────────────┐");
        System.out.println("  │                       MAIN MENU                         │");
        System.out.println("  ├─────────────────────────────────────────────────────────┤");
        System.out.println("  │  1. Create New Estimate                                 │");
        System.out.println("  │  2. View / Edit Material Prices                         │");
        System.out.println("  │  3. View / Edit Labor Rates                             │");
        System.out.println("  │  4. Exit                                                │");
        System.out.println("  └─────────────────────────────────────────────────────────┘");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  New Estimate Wizard
    // ═══════════════════════════════════════════════════════════════════════════
    private void runNewEstimate() {
        section("STEP 1 — PROJECT INFORMATION");
        ProjectInfo info = collectProjectInfo();

        section("STEP 2 — CONCRETE PAD CONFIGURATION");
        ConcretePad pad = collectPadConfig();

        ChainLinkFence fence = null;
        if (InputValidator.readYesNo("\n  Would you like to add a Chain-Link Fence to this estimate?")) {
            section("STEP 3 — CHAIN-LINK FENCE CONFIGURATION");
            fence = collectFenceConfig();
        }

        Estimate est = new Estimate(info, pad, fence);
        String report = ReportBuilder.build(est);

        System.out.println("\n" + report);

        // Save
        CsvManager.appendEstimateLog(est);
        String file = CsvManager.saveEstimateReport(est, report);
        if (file != null)
            System.out.println("  ✔ Estimate saved to: " + file);
        System.out.println();
    }

    // ── Project Info ─────────────────────────────────────────────────────────
    private ProjectInfo collectProjectInfo() {
        String project   = InputValidator.readNonBlank("  Project Name  : ");
        String client    = InputValidator.readNonBlank("  Client Name   : ");
        String location  = InputValidator.readNonBlank("  Location/Addr : ");
        String date      = InputValidator.readString("  Estimate Date  (Enter = today): ");
        if (date.isEmpty()) date = LocalDate.now().toString();
        String estimator = InputValidator.readNonBlank("  Estimator Name: ");
        String notes     = InputValidator.readString("  Notes (optional): ");
        return new ProjectInfo(project, client, location, date, estimator, notes);
    }

    // ── Concrete Pad ─────────────────────────────────────────────────────────
    private ConcretePad collectPadConfig() {
        // --- Pad size preset ---
        PadPreset[] presets = PadPreset.getPresets();
        System.out.println("\n  SELECT PAD SIZE PRESET:");
        for (int i = 0; i < presets.length; i++) {
            PadPreset p = presets[i];
            if (p.getLength() == 0) {
                System.out.printf("  %2d. %-30s  (custom)%n", i + 1, p.getName());
            } else {
                System.out.printf("  %2d. %-30s  %.0f' x %.0f'  —  %s%n",
                        i + 1, p.getName(), p.getLength(), p.getWidth(), p.getTypicalUse());
            }
        }
        int padChoice = InputValidator.readMenuChoice("  Choice: ", presets.length);
        PadPreset selected = presets[padChoice - 1];

        double length, width;
        if (selected.getLength() == 0) {
            length = InputValidator.readStrictPositiveDouble("  Enter pad LENGTH (ft): ");
            width  = InputValidator.readStrictPositiveDouble("  Enter pad WIDTH  (ft): ");
        } else {
            System.out.printf("  ✔ Selected: %s (%.0f' x %.0f' — %s)%n",
                    selected.getName(), selected.getLength(), selected.getWidth(), selected.getTypicalUse());
            System.out.print("  Override dimensions? (y/n): ");
            boolean override = InputValidator.readYesNo("");
            if (override) {
                length = InputValidator.readStrictPositiveDouble("  Length (ft): ");
                width  = InputValidator.readStrictPositiveDouble("  Width  (ft): ");
            } else {
                length = selected.getLength();
                width  = selected.getWidth();
            }
        }

        // --- Thickness ---
        System.out.println("\n  SLAB THICKNESS:");
        System.out.println("  1. 4 inches — Standard residential / light commercial");
        System.out.println("  2. 5 inches — Standard commercial");
        System.out.println("  3. 6 inches — Heavy-duty / forklift traffic");
        System.out.println("  4. 8 inches — Industrial / heavy equipment");
        System.out.println("  5. Custom");
        int tChoice = InputValidator.readMenuChoice("  Choice: ", 5);
        double thickness = switch (tChoice) {
            case 1 -> 4.0;
            case 2 -> 5.0;
            case 3 -> 6.0;
            case 4 -> 8.0;
            default -> InputValidator.readStrictPositiveDouble("  Enter custom thickness (inches): ");
        };

        // --- Waste ---
        double waste = InputValidator.readPercentage(
                "  Concrete waste % [default 8, range 5-15]: ", 5, 15);

        // --- Labor ---
        System.out.println("\n  LABOR SETUP:");
        showLaborRates();
        int    employees         = InputValidator.readPositiveInt("  Number of employees: ");
        double hoursPerEmployee  = InputValidator.readStrictPositiveDouble("  Hours per employee: ");
        double laborRate         = selectLaborRate();

        // --- Add-ons ---
        System.out.println("\n  ADD-ONS:");
        boolean rebar   = InputValidator.readYesNo("  Include Rebar?");
        boolean mesh    = InputValidator.readYesNo("  Include Wire Mesh?");
        boolean rental  = InputValidator.readYesNo("  Include Equipment Rental?");
        double  rentalDays = 0;
        if (rental) rentalDays = InputValidator.readStrictPositiveDouble("  Rental days: ");

        // --- Discount ---
        System.out.println("\n  DISCOUNT (Concrete Pad):");
        double discPct   = InputValidator.readPercentage("  Discount % (0 = none): ", 0, 100);
        double discFixed = InputValidator.readPositiveDouble("  Fixed $ discount (0 = none): $");

        // --- Contingency ---
        double contingency = InputValidator.readPercentage(
                "  Project contingency % (0 = none, typical 5-10): ", 0, 50);

        return new ConcretePad(
                length, width, thickness, waste,
                employees, hoursPerEmployee, laborRate,
                rebar, mesh, rental, rentalDays,
                prices.getOrDefault("concrete_per_cy", 165.0),
                prices.getOrDefault("rebar_per_lf",    0.85),
                prices.getOrDefault("wire_mesh_per_sqft", 0.45),
                prices.getOrDefault("equipment_rental_day", 450.0),
                discPct, discFixed, contingency);
    }

    // ── Fence Config ─────────────────────────────────────────────────────────
    private ChainLinkFence collectFenceConfig() {
        // --- Preset ---
        FencePreset[] presets = FencePreset.getPresets();
        System.out.println("\n  SELECT FENCE LAYOUT PRESET:");
        for (int i = 0; i < presets.length; i++) {
            FencePreset p = presets[i];
            if (p.getPerimeter() == 0) {
                System.out.printf("  %2d. %-35s  (custom)%n", i + 1, p.getName());
            } else {
                System.out.printf("  %2d. %-35s  Perimeter: %.0f ft  Height: %.0f ft  —  %s%n",
                        i + 1, p.getName(), p.getPerimeter(), p.getHeightFt(), p.getTypicalUse());
            }
        }
        int fChoice = InputValidator.readMenuChoice("  Choice: ", presets.length);
        FencePreset fp = presets[fChoice - 1];

        double perimeter, heightFt;
        if (fp.getPerimeter() == 0) {
            perimeter = InputValidator.readStrictPositiveDouble("  Perimeter (ft): ");
            heightFt  = InputValidator.readStrictPositiveDouble("  Height (ft): ");
        } else {
            System.out.printf("  ✔ Selected: %s (Perimeter: %.0f ft, Height: %.0f ft)%n",
                    fp.getName(), fp.getPerimeter(), fp.getHeightFt());
            if (InputValidator.readYesNo("  Override?")) {
                perimeter = InputValidator.readStrictPositiveDouble("  Perimeter (ft): ");
                heightFt  = InputValidator.readStrictPositiveDouble("  Height (ft): ");
            } else {
                perimeter = fp.getPerimeter();
                heightFt  = fp.getHeightFt();
            }
        }

        // --- Height confirmation / custom ---
        System.out.println("\n  FENCE HEIGHT (confirm or change):");
        System.out.println("  1. 4 ft   2. 5 ft   3. 6 ft   4. 8 ft   5. 10 ft   6. Custom");
        int hChoice = InputValidator.readMenuChoice("  Choice (or just confirm with your preset height): ", 6);
        heightFt = switch (hChoice) {
            case 1 -> 4;
            case 2 -> 5;
            case 3 -> 6;
            case 4 -> 8;
            case 5 -> 10;
            default -> InputValidator.readStrictPositiveDouble("  Custom height (ft): ");
        };

        // --- Post spacing ---
        double postSpacing = InputValidator.readPositiveDouble("  Post spacing (ft) [default 10]: ");
        if (postSpacing == 0) postSpacing = 10;

        // --- Gauge ---
        System.out.println("\n  MESH GAUGE:");
        System.out.println("  1. 9-gauge  (heavy)");
        System.out.println("  2. 11-gauge (standard)");
        System.out.println("  3. 11.5-gauge (light)");
        int gChoice = InputValidator.readMenuChoice("  Choice: ", 3);
        String gaugeKey = switch (gChoice) { case 1 -> "9"; case 2 -> "11"; default -> "11.5"; };

        // --- Post type ---
        System.out.println("\n  POST TYPE:");
        System.out.println("  1. Galvanized Steel (standard)");
        System.out.println("  2. Aluminum (lightweight)");
        System.out.println("  3. Schedule 40 (heavy duty)");
        int pChoice = InputValidator.readMenuChoice("  Choice: ", 3);
        String postType = switch (pChoice) { case 1 -> "galvanized"; case 2 -> "aluminum"; default -> "schedule40"; };
        double postPrice = switch (pChoice) {
            case 2 -> prices.getOrDefault("post_aluminum", 28.0);
            case 3 -> prices.getOrDefault("post_schedule40", 35.0);
            default -> prices.getOrDefault("post_galvanized", 22.5);
        };

        // --- Gates ---
        System.out.println("\n  GATES:");
        int singleGates  = InputValidator.readNonNegativeInt("  Single walk gates  (0 = none): ");
        int doubleGates  = InputValidator.readNonNegativeInt("  Double drive gates (0 = none): ");
        int slidingGates = InputValidator.readNonNegativeInt("  Sliding gates      (0 = none): ");

        // --- Top treatment ---
        System.out.println("\n  TOP TREATMENT:");
        System.out.println("  1. None");
        System.out.println("  2. Barbed Wire – 1 strand");
        System.out.println("  3. Barbed Wire – 2 strands");
        System.out.println("  4. Barbed Wire – 3 strands");
        System.out.println("  5. Razor Wire Coil");
        System.out.println("  6. Privacy Slats");
        int ttChoice = InputValidator.readMenuChoice("  Choice: ", 6);
        String topTreatment = switch (ttChoice) {
            case 2 -> "barbed1"; case 3 -> "barbed2"; case 4 -> "barbed3";
            case 5 -> "razor";   case 6 -> "privacy"; default -> "none";
        };
        double topTreatmentPrice = switch (ttChoice) {
            case 2 -> prices.getOrDefault("barbed_wire_1strand", 0.35);
            case 3 -> prices.getOrDefault("barbed_wire_2strand", 0.65);
            case 4 -> prices.getOrDefault("barbed_wire_3strand", 0.90);
            case 5 -> prices.getOrDefault("razor_wire", 1.85);
            case 6 -> prices.getOrDefault("privacy_slats", 2.20);
            default -> 0.0;
        };

        // --- Overage ---
        double overage = InputValidator.readPercentage(
                "\n  Fence material overage % [default 5, range 0-20]: ", 0, 20);

        // --- Labor ---
        System.out.println("\n  FENCE LABOR:");
        double fenceLaborHrs  = InputValidator.readStrictPositiveDouble("  Total fence labor hours: ");
        double fenceLaborRate = selectLaborRate();

        // --- Discount ---
        System.out.println("\n  DISCOUNT (Fencing):");
        double fDiscPct   = InputValidator.readPercentage("  Discount % (0 = none): ", 0, 100);
        double fDiscFixed = InputValidator.readPositiveDouble("  Fixed $ discount (0 = none): $");

        // Fabric price based on height and gauge
        double fabricPrice = resolveFabricPrice(heightFt, gaugeKey);

        return new ChainLinkFence(
                perimeter, heightFt, postSpacing,
                gaugeKey, postType,
                singleGates, doubleGates, slidingGates,
                topTreatment, overage,
                fenceLaborHrs, fenceLaborRate,
                fDiscPct, fDiscFixed,
                fabricPrice, postPrice,
                prices.getOrDefault("post_concrete_bag", 8.5),
                prices.getOrDefault("top_rail", 2.75),
                prices.getOrDefault("gate_single", 185.0),
                prices.getOrDefault("gate_double", 650.0),
                prices.getOrDefault("gate_sliding", 1250.0),
                topTreatmentPrice,
                prices.getOrDefault("hardware_percentage", 0.08));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Price / Rate Editors
    // ═══════════════════════════════════════════════════════════════════════════
    private void viewEditPrices() {
        section("MATERIAL PRICES");
        prices = CsvManager.loadMaterialPrices();
        String[] keys = prices.keySet().toArray(new String[0]);
        for (int i = 0; i < keys.length; i++) {
            System.out.printf("  %2d. %-35s  $%.2f%n", i + 1, keys[i], prices.get(keys[i]));
        }
        if (InputValidator.readYesNo("\n  Edit a price?")) {
            int idx = InputValidator.readMenuChoice("  Select item number: ", keys.length);
            String key = keys[idx - 1];
            double newPrice = InputValidator.readStrictPositiveDouble(
                    "  New price for [" + key + "]: $");
            prices.put(key, newPrice);
            CsvManager.saveMaterialPrices(prices);
            System.out.println("  ✔ Price updated and saved.");
        }
    }

    private void viewEditLaborRates() {
        section("LABOR RATES");
        laborRates = CsvManager.loadLaborRates();
        String[] keys = laborRates.keySet().toArray(new String[0]);
        for (int i = 0; i < keys.length; i++) {
            System.out.printf("  %2d. %-25s  $%.2f / hr%n", i + 1, keys[i], laborRates.get(keys[i]));
        }
        if (InputValidator.readYesNo("\n  Edit a rate?")) {
            int idx = InputValidator.readMenuChoice("  Select item number: ", keys.length);
            String key = keys[idx - 1];
            double newRate = InputValidator.readStrictPositiveDouble(
                    "  New hourly rate for [" + key + "]: $");
            laborRates.put(key, newRate);
            CsvManager.saveLaborRates(laborRates);
            System.out.println("  ✔ Rate updated and saved.");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Helpers
    // ═══════════════════════════════════════════════════════════════════════════
    private double selectLaborRate() {
        System.out.println("  SELECT LABOR RATE:");
        String[] keys = laborRates.keySet().toArray(new String[0]);
        for (int i = 0; i < keys.length; i++) {
            System.out.printf("  %d. %-25s  $%.2f/hr%n", i + 1, keys[i], laborRates.get(keys[i]));
        }
        System.out.printf("  %d. Custom rate%n", keys.length + 1);
        int choice = InputValidator.readMenuChoice("  Choice: ", keys.length + 1);
        if (choice <= keys.length) {
            return laborRates.get(keys[choice - 1]);
        }
        return InputValidator.readStrictPositiveDouble("  Custom hourly rate: $");
    }

    private void showLaborRates() {
        System.out.println("  Available labor rates:");
        laborRates.forEach((k, v) -> System.out.printf("    %-25s  $%.2f/hr%n", k, v));
    }

    private double resolveFabricPrice(double heightFt, String gaugeKey) {
        // Pick closest preset fabric price key, then adjust by gauge premium
        String key;
        if      (heightFt <= 4)  key = "fence_fabric_4ft";
        else if (heightFt <= 5)  key = "fence_fabric_5ft";
        else if (heightFt <= 6)  key = "fence_fabric_6ft";
        else if (heightFt <= 8)  key = "fence_fabric_8ft";
        else                     key = "fence_fabric_10ft";

        double base = prices.getOrDefault(key, 6.75);
        // gauge premium: 9-gauge +15%, 11-gauge standard, 11.5-gauge -5%
        return switch (gaugeKey) {
            case "9"    -> base * 1.15;
            case "11.5" -> base * 0.95;
            default     -> base;
        };
    }

    private void section(String title) {
        System.out.println("\n  ═══════════════════════════════════════════════════════════");
        System.out.println("  " + title);
        System.out.println("  ═══════════════════════════════════════════════════════════");
    }
}
