# Concrete Pad & Chain-Link Fence Estimator
### CSC-251 | Module 5 | Aryan Kandula — Individual Project

---

## Overview

A Java-based GUI application that helps construction business owners generate professional, itemized estimates for:

- Concrete pad projects (residential, commercial, warehouse, custom)
- Optional chain-link fence installation around the pad
- Full cost breakdowns: materials, labor, equipment, discounts, contingency
- CSV-backed pricing — all rates are editable without touching code
- Automatic estimate report saved to the `estimates/` folder

---

## Quick Start (Windows)

1. **Install Java JDK 17+** if not already installed: [https://adoptium.net/](https://adoptium.net/)

2. **Verify Java is on your PATH** — open Command Prompt and type:
   ```
   java -version
   ```

3. Place the entire `ConcreteEstimator/` folder anywhere on your PC.

4. Open Command Prompt and navigate to the folder:
   ```
   cd C:\Users\YourName\Desktop\ConcreteEstimator
   ```

5. Run the build + launch script:
   ```
   run.bat
   ```
   Or manually:
   ```
   javac -d out -sourcepath src src\Main.java
   java -cp out Main
   ```

---

## Project Structure

```
ConcreteEstimator/
│
├── src/
│   ├── Main.java                          ← Entry point
│   └── estimator/
│       ├── model/
│       │   ├── ProjectInfo.java           ← Project header data
│       │   ├── PadPreset.java             ← 12 pad size presets
│       │   ├── ConcretePad.java           ← All pad calculations
│       │   ├── FencePreset.java           ← 10 fence layout presets
│       │   ├── ChainLinkFence.java        ← All fence calculations
│       │   └── Estimate.java             ← Combines pad + fence
│       ├── csv/
│       │   └── CsvManager.java            ← Read/write all CSV files
│       ├── ui/
│       │   ├── MainWindow.java            ← Main application window
│       │   ├── AppTheme.java              ← Dark UI theme & component styles
│       │   ├── ProjectInfoPanel.java      ← Step 1: project details
│       │   ├── ConcretePadPanel.java      ← Step 2: pad configuration
│       │   ├── FencePanel.java            ← Step 3: fence module
│       │   ├── ReportPanel.java           ← Step 4: estimate report
│       │   └── PricesEditorDialog.java    ← In-app price/rate editor
│       └── util/
│           ├── InputValidator.java        ← Input validation (all types)
│           └── ReportBuilder.java         ← Formats the printed report
│
├── data/
│   ├── material_prices.csv               ← Editable material unit prices
│   ├── labor_rates.csv                   ← Editable hourly labor rates
│   └── estimates_log.csv                 ← Auto-appended estimate log
│
├── estimates/                            ← Generated estimate .txt files
├── out/                                  ← Compiled .class files (auto-created)
├── run.bat                               ← Windows one-click build & run
├── run.sh                                ← Mac/Linux build & run
└── README.md                             ← This file
```

---

## Application Features

### Step 1 — Project Information
- Project name, client name, location/address
- Estimate date (auto-fills today's date)
- Estimator name (defaults to Aryan Kandula) and optional notes

### Step 2 — Concrete Pad Configuration

**12 Presets available:**

| Preset | Dimensions | Typical Use |
|---|---|---|
| Small Residential Pad | 20' x 20' | AC unit / shed base |
| Garage / Workshop Pad | 20' x 40' | 2-car garage |
| Equipment Pad | 10' x 10' | Generator / HVAC unit |
| Dumpster Pad | 12' x 20' | Commercial waste enclosure |
| RV / Boat Storage Slab | 14' x 40' | Storage |
| Basketball / Sport Court | 50' x 84' | Sport court |
| Small Warehouse Slab | 50' x 100' | Small warehouse |
| Mid Warehouse Slab | 100' x 150' | Mid-size warehouse |
| Large Warehouse Slab | 150' x 200' | Large warehouse |
| Commercial Loading Apron | 60' x 80' | Truck / loading dock |
| Parking Lot Section | 100' x 200' | Commercial parking |
| Custom | User-defined | Any size |

**Configuration options:**
- Slab thickness: 4", 5", 6", 8", or custom
- Waste percentage: 5–15% (default 8%)
- Labor: employees × hours × rate (CSV or custom)
- Add-ons: rebar, wire mesh, equipment rental
- Discount: percentage and/or fixed dollar
- Contingency: 0–50% added to subtotal

### Step 3 — Chain-Link Fence Module (Optional)

**10 Presets available:**

| Preset | Size | Height |
|---|---|---|
| Small Yard Enclosure | 20' x 40' | 4 ft |
| Garage / Shop Perimeter | 20' x 40' | 6 ft |
| Dumpster Enclosure | 12' x 20' | 6 ft |
| Equipment Yard | 100' x 100' | 8 ft |
| Small Warehouse Perimeter | 50' x 100' | 6 ft |
| Mid Warehouse Perimeter | 100' x 150' | 8 ft |
| Large Facility Perimeter | 150' x 200' | 8 ft |
| Parking Lot Perimeter | 100' x 200' | 4 ft |
| Security Compound | 200' x 300' | 10 ft |
| Custom | User-defined | User-defined |

**Configuration options:**
- Height: 4 ft, 5 ft, 6 ft, 8 ft, 10 ft, or custom
- Mesh gauge: 9-gauge (heavy), 11-gauge (standard), 11.5-gauge (light)
- Post type: galvanized steel, aluminum, schedule 40
- Post spacing: configurable (default 10 ft on center)
- Gates: single walk, double drive, sliding (any quantity)
- Top treatment: none, barbed wire (1–3 strands), razor wire, privacy slats
- Material overage: 0–20% (default 5%)

### Step 4 — Estimate Report
- Full formatted report displayed in-app
- Copy to clipboard, save as `.txt`, or print
- Every estimate auto-logged to `data/estimates_log.csv`

---

## Calculations

### Concrete
```
Area               = Length × Width (sq ft)
Raw Volume (CY)    = (Length × Width × Thickness_ft) / 27
Adjusted Volume    = Raw Volume × (1 + waste%)
Material Cost      = Adjusted CY × price per CY
Rebar              = grid at 18" spacing (perimeter + interior runs)
Wire Mesh          = area × $/sq ft
```

### Fence
```
Fabric LF          = perimeter − gate openings (+ overage%)
Line posts         = (fabric LF / post spacing) − 1
Terminal posts     = 4 corners + 2 per gate
Post concrete      = 1 bag per post
Hardware           = 8% of (fabric + post + rail cost)
```

---

## CSV Data Reference

### `data/material_prices.csv`

| Item | Default Price |
|---|---|
| Concrete | $165.00 / CY |
| Rebar | $0.85 / LF |
| Wire mesh | $0.45 / sq ft |
| Fence fabric (4 ft) | $4.50 / LF |
| Fence fabric (10 ft) | $13.00 / LF |
| Post — galvanized | $22.50 each |
| Post — aluminum | $28.00 each |
| Post — schedule 40 | $35.00 each |
| Post concrete bag | $8.50 each |
| Top rail | $2.75 / LF |
| Gate — single walk | $185.00 |
| Gate — double drive | $650.00 |
| Gate — sliding | $1,250.00 |
| Equipment rental | $450.00 / day |

### `data/labor_rates.csv`

| Role | Default Rate |
|---|---|
| Concrete finisher | $42.00 / hr |
| Laborer | $28.50 / hr |
| Foreman | $58.00 / hr |
| Fence installer | $38.00 / hr |
| Equipment operator | $52.00 / hr |

> All prices can be edited directly in Excel/Notepad **or** through the in-app price editor (sidebar → Edit Prices & Rates).

---

## Sample Report Output

```
=================================================================
       CONCRETE PAD & CHAIN-LINK FENCE ESTIMATOR
                   ESTIMATE REPORT
=================================================================

  PROJECT INFORMATION
-----------------------------------------------------------------
  Estimate #:              1001
  Project Name:            Riverside Distribution Center
  Client Name:             ABC Logistics LLC
  Location:                1234 Commerce Blvd, Fayetteville NC
  Estimate Date:           2025-10-14
  Estimator:               Aryan Kandula

  CONCRETE PAD SUMMARY
-----------------------------------------------------------------
  Pad Dimensions:          50 ft x 100 ft
  Total Area:              5,000.00 sq ft
  Slab Thickness:          6.0 inches (0.5000 ft)
  Raw Volume:              92.59 cubic yards
  Waste Factor:            8.0%
  Adjusted Volume:         100.00 cubic yards

  ... (full cost breakdown)

  *** PROJECT GRAND TOTAL ***          $XX,XXX.XX
=================================================================
```

---

## Requirements Met

- [x] Java GUI application (Swing, no external libraries)
- [x] CSV integration: material prices, labor rates, estimate log, report output
- [x] 12 pad presets + 10 fence presets with override capability
- [x] All 4 slab thickness presets + custom input
- [x] Waste percentage (5–15%), configurable
- [x] Labor: employees × hours × rate, rate selectable from CSV
- [x] Optional add-ons: rebar, wire mesh, equipment rental
- [x] Full fence module: fabric, posts, concrete, rail, gates, top treatment
- [x] Hardware calculated as % of material cost
- [x] Discount system: percentage + fixed dollar, per section
- [x] Contingency percentage on pad total
- [x] Full input validation: no negatives, no non-numeric, no blank required fields
- [x] All values accurate to 2 decimal places
- [x] Report saved to file + logged to `estimates_log.csv`
- [x] Prices and labor rates editable in-app and saved back to CSV

---

## Author

| Field | Info |
|---|---|
| Name | Aryan Kandula |
| Course | CSC-251 Advanced Java Programming |
| Module | 5 — Individual Project |
| Language | Java 17+ |
| Version | 1.2 |