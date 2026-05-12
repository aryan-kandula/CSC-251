# CSC-251: Advanced Java Programming

> **Fayetteville Technical Community College — Spring 2026**  
> Aryan Kandula

---

## 📖 Course Overview

**CSC-251** covers advanced Java programming concepts including object-oriented design, GUI development with Java Swing, file I/O, exception handling, and project-based application development. The course emphasizes building real, working programs from the ground up — not just understanding syntax, but applying it to complete, structured projects.

---

## 🗂 Repository Contents

| Folder | Project | Description |
|---|---|---|
| `FarmManagementSystem/` | Farm Management System | Full-featured Java Swing desktop app — group project (Modules 2 & 3) |
| `IndividualProject_KandulaAryan/` | Concrete & Fence Estimator | Java Swing GUI estimator — individual project (Modules 5 & 6) |

---

## 🌾 Farm Management System — Modules 2 & 3

### What It Does

A fully-featured farm business management desktop application built in Java Swing. The system manages store inventory, animal sales, veterinary and grooming services, and business reporting — all through a themed, animated GUI.

Data saves automatically to `farm_data.csv` on exit so nothing is lost between sessions. Every record supports inline editing without needing to delete and re-enter it.

### Key Features

- **Animated splash screen** — custom-painted farm scene using Java 2D Graphics
- **Tabbed dashboard** — Store, Animals, Services, and Reports all in one window
- **Live color-coded tables** — low stock in amber, out-of-stock in red, paid services in green
- **File I/O** — auto-save and auto-load via CSV using `PrintWriter` and `Scanner`
- **Inline editing** — pre-filled forms for every record type
- **Custom Swing components** — themed buttons with gradient hover effects, custom dialogs, status bar
- **Input validation** — negative value checks, empty field handling, number format guards

### What I Built

- Designed all four modules from scratch: Store/Inventory, Animal Sales, Services & Payments, Business Reporting
- Built the full Swing GUI including `FarmBackground` splash screen, `FarmButton`, `FarmTheme`, and `FarmDialog` classes
- Implemented `saveToCSV()` and `loadFromCSV()` with edge case handling (empty files, malformed lines, revenue reconstruction on restart)
- Managed the full GitHub workflow: issue → branch → pull request → code review → merge
- Used GitHub Copilot to review the pull request and worked through flagged issues including CSV column consistency, input trimming, and empty file handling

### How to Run

**Prerequisites:** Java JDK 17+ and VS Code with the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)

```bash
# Navigate to the FarmManagementSystem folder
javac FarmManagementSystem_Enhanced_AryanKandula.java
java FarmManagementSystem_Enhanced_AryanKandula
```

> If `farm_data.csv` is missing, the app loads built-in sample data automatically.

---

## 🏗 Concrete & Fence Estimator — Individual Project (Modules 5 & 6)

### What It Does

A construction cost estimator for concrete pads and chain-link fencing, built as a Java Swing desktop application. The estimator takes project dimensions and crew details as input and calculates material costs, concrete volume, waste factor adjustments, and labor totals.

### Features

- Tabbed layout with Project Info, Concrete Pad, Chain-Link Fence, and Report panels
- Preset configurations for common pad and fence sizes (`PadPreset`, `FencePreset`)
- Inputs: dimensions, slab thickness, crew size, hours per employee, hourly rate
- Calculates area, cubic yards, 8% waste factor, adjusted volume, material cost, and labor total
- Material prices loaded from `data/material_prices.csv` and editable via a built-in dialog
- Labor rates loaded from `data/labor_rates.csv`
- Estimates saved to `data/estimates_log.csv` and exported as formatted `.txt` reports to `estimates/`
- Input validation via `InputValidator` with clear error handling throughout
- Custom app theme via `AppTheme` for consistent Swing styling

### How to Run

```bash
cd IndividualProject_KandulaAryan
# Windows
run.bat
# Mac/Linux
./run.sh
# Or manually
javac src/Main.java
java -cp src Main
```

---

## 💡 What I Learned

Starting CSC-251, I understood Java at a surface level. By the end of the course, I had built two complete, working applications from scratch and learned what it actually takes to write software that holds up — not just code that runs once.

A few things that stuck with me:

- **The environment is part of the skill.** Knowing how to compile from the terminal, read a stack trace, and structure a project folder matters just as much as knowing the language.
- **Building something complete changes how you think.** Working across multiple modules that all connect forces you to think about architecture, not just methods. The Farm Management System taught me what it means to design a program rather than just write one.
- **File I/O makes programs real.** Writing `saveToCSV()` and `loadFromCSV()` meant thinking through what happens when a program restarts — edge cases, data integrity, and state reconstruction across sessions.
- **Code review is a skill.** Going through GitHub Copilot's pull request feedback and deciding what to fix, what to document, and what to leave as a known limitation was one of the most practically useful things I did this semester.
Development is iterative. The Farm Management System I submitted at the end of Module 3 looked nothing like the skeleton I drafted in Module 2 — and that progression was the point.

---

## 🛠 Technologies Used

| Technology | Purpose |
|---|---|
| Java 17+ | Core language |
| Java Swing | GUI framework |
| Java 2D Graphics | Custom drawing and animations |
| File I/O (PrintWriter / Scanner) | Data persistence via CSV |
| GitHub | Version control, issues, pull requests |
| VS Code + Extension Pack for Java | Development environment |

---

> ⚠️ **Note:** The `assets` folder contains all media used in this README including GIFs and images.
