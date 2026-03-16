# Farm Management System
### Author: Aryan Kandula
### Java GUI — Custom Swing

---

## Overview

A fully-featured Farm Management System built in Java Swing. Manages store inventory, animal sales, veterinary/grooming services, and business reporting for a small family farm — all through a themed, animated desktop interface.

Data is **automatically saved to `farm_data.csv` on exit**, so nothing is ever lost. Every record can be edited inline without needing to delete and re-add it. An **Exit button** in the header bar provides a clean, deliberate way to close the app from anywhere.

---

## How to Run

### Prerequisites
- Java JDK 17+ installed
- Visual Studio Code with the **Extension Pack for Java** (Microsoft)

### Steps
1. Open VS Code → **File > Open Folder** → open the folder containing these files
2. Make sure `farm_data.csv` is in the same folder (included)
3. Open `FarmManagementSystem_Enhanced_AryanKandula.java`
4. Click **Run** at the top right, or run in the terminal:
   ```
   javac FarmManagementSystem_Enhanced_AryanKandula.java
   java FarmManagementSystem_Enhanced_AryanKandula
   ```

> If `farm_data.csv` is missing, the app loads built-in sample data automatically.

---

## Features

| Module | What It Does |
|---|---|
| Store / Inventory | Add, edit, remove items; sell; restock; color-coded stock levels |
| Animal Sales | Add farm/breeder animals; sell; edit; filter available vs. sold |
| Services & Payments | Schedule vet/grooming services; mark paid; edit/remove records |
| Reports | Full summary: inventory, animals sold, revenue, outstanding balance |
| Auto-Save | Data saves to `farm_data.csv` automatically when you close or exit |

---

## UI Features

- **Animated splash screen** — moving cloud farm scene on startup
- **Tabbed dashboard** — Store, Animals, Services, Reports all in one window
- **Exit button** — top-right of the header bar; triggers the same save/exit flow as closing the window
- **Auto-save toggle** — status bar has a Toggle button to turn auto-save ON or OFF at any time
- **Live color-coded tables** — low stock in amber, out-of-stock in red, sold animals greyed out, paid services in green
- **Inline editing** — click any row, then click Edit to correct it; all fields pre-fill with current values
- **Row-safe filtering** — Available Only / Unpaid Only filters work correctly with Edit and Remove
- **Status bar** — real-time feedback after every action (item counts, last operation, save status)
- **Animated buttons** — smooth gradient hover transitions on all buttons
- **Custom table headers** — fully visible white text on dark red, gold accent line; never overridden by the system theme

---

## Exit & Save Behavior

Clicking **Exit** (header button) or closing the window (X) both follow the same flow:

| Situation | Behavior |
|---|---|
| No unsaved changes | Exits immediately |
| Auto-Save ON, save succeeds | Saves silently, then exits |
| Auto-Save ON, save fails | Asks whether to exit anyway |
| Auto-Save OFF, unsaved changes | Prompts: Save and Exit / Exit Without Saving / Cancel |

A manual **Save Data to CSV** button is also available in the Reports tab.

---

## Class Design

| Class | Purpose |
|---|---|
| `FarmManagementSystem_Enhanced_AryanKandula` | Main class, window, all modules, entry point |
| `StoreItem` | Store product (name, price, quantity) with full getters/setters |
| `Animal` | Farm or breeder animal with full edit support |
| `ServiceRecord` | Scheduled service and payment status with full edit support |
| `FT` | Centralized color and font constants (cross-platform safe fonts) |
| `FarmButton` | Animated gradient button with smooth hover effect |
| `FarmBackground` | Animated farm scene (clouds, barn, fence, flowers) for the splash |
| `FarmHeaderRenderer` | Custom table header cell renderer — paints its own background so the LAF cannot override text visibility |
| `FarmDialog` | Custom themed dialogs with pre-fill support for editing |
| `StatusBar` | Live status strip with auto-save indicator and toggle button |

---

## Project Files

| File | Description |
|---|---|
| `FarmManagementSystem_Enhanced_AryanKandula.java` | Main application source |
| `farm_data.csv` | Data file — auto-loaded on start, auto-saved on exit |
| `README.md` | This file |

---

## Notes

- **Auto-save:** The Toggle button in the status bar switches auto-save on or off. When on, closing or clicking Exit saves silently. When off, you are prompted on exit.
- **Inline editing:** Select any row in any table and click Edit — fields pre-fill so you only change what needs fixing.
- **Filtering:** Use Available Only / Unpaid Only to focus on active records. Edit and Remove always operate on the correct underlying record even when filtered.
- **Missing CSV:** If `farm_data.csv` is not present on first run, sample data loads automatically. Save once from the Reports tab to write it to disk.
- **CSV format:** `TYPE, CATEGORY, FIELD1–6` — unchanged from the original, fully backward-compatible.
