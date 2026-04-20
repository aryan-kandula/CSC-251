package estimator.csv;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CsvManager {

    private static final String DATA_DIR     = "data/";
    private static final String PRICES_FILE  = DATA_DIR + "material_prices.csv";
    private static final String LABOR_FILE   = DATA_DIR + "labor_rates.csv";
    private static final String LOG_FILE     = DATA_DIR + "estimates_log.csv";
    private static final String ESTIMATES_DIR = "estimates/";

    // ── Material prices ───────────────────────────────────────────────────────
    public static Map<String, Double> loadMaterialPrices() {
        Map<String, Double> prices = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PRICES_FILE))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; } // skip header
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    try {
                        prices.put(parts[0].trim(), Double.parseDouble(parts[3].trim()));
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) {
            System.err.println("[CSV] Could not load material prices: " + e.getMessage());
        }
        return prices;
    }

    public static void saveMaterialPrices(Map<String, Double> prices) {
        // Read all rows first, update unit_price column
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"material_key","description","unit","unit_price"});
        try (BufferedReader br = new BufferedReader(new FileReader(PRICES_FILE))) {
            String line; boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 4 && prices.containsKey(parts[0].trim())) {
                    parts[3] = String.format("%.2f", prices.get(parts[0].trim()));
                }
                rows.add(parts);
            }
        } catch (IOException e) {
            System.err.println("[CSV] Read error: " + e.getMessage());
            return;
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(PRICES_FILE))) {
            for (String[] row : rows) {
                pw.println(String.join(",", row));
            }
        } catch (IOException e) {
            System.err.println("[CSV] Write error: " + e.getMessage());
        }
    }

    // ── Labor rates ───────────────────────────────────────────────────────────
    public static Map<String, Double> loadLaborRates() {
        Map<String, Double> rates = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(LABOR_FILE))) {
            String line; boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    try {
                        rates.put(parts[0].trim(), Double.parseDouble(parts[2].trim()));
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) {
            System.err.println("[CSV] Could not load labor rates: " + e.getMessage());
        }
        return rates;
    }

    public static void saveLaborRates(Map<String, Double> rates) {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"trade","description","hourly_rate"});
        try (BufferedReader br = new BufferedReader(new FileReader(LABOR_FILE))) {
            String line; boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 3 && rates.containsKey(parts[0].trim())) {
                    parts[2] = String.format("%.2f", rates.get(parts[0].trim()));
                }
                rows.add(parts);
            }
        } catch (IOException e) { System.err.println("[CSV] Read error: " + e.getMessage()); return; }
        try (PrintWriter pw = new PrintWriter(new FileWriter(LABOR_FILE))) {
            for (String[] row : rows) pw.println(String.join(",", row));
        } catch (IOException e) { System.err.println("[CSV] Write error: " + e.getMessage()); }
    }

    // ── Estimate log ──────────────────────────────────────────────────────────
    public static void appendEstimateLog(estimator.model.Estimate est) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            estimator.model.ProjectInfo pi  = est.getProjectInfo();
            estimator.model.ConcretePad pad = est.getConcretePad();
            double fencePerim = est.hasFencing() ? est.getChainLinkFence().getPerimeterFt() : 0;
            double fenceCost  = est.hasFencing() ? est.getChainLinkFence().getFenceGrandTotal() : 0;
            pw.printf("%d,%s,\"%s\",\"%s\",\"%s\",%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f%n",
                est.getEstimateId(),
                pi.getEstimateDate(),
                pi.getProjectName(),
                pi.getClientName(),
                pi.getLocation(),
                pi.getEstimatorName(),
                pad.getAreaSqFt(),
                pad.getAdjustedVolumeCY(),
                pad.getPadGrandTotal(),
                fencePerim,
                fenceCost,
                est.getProjectGrandTotal());
        } catch (IOException e) {
            System.err.println("[CSV] Could not write estimate log: " + e.getMessage());
        }
    }

    // ── Full estimate report CSV ───────────────────────────────────────────────
    public static String saveEstimateReport(estimator.model.Estimate est, String reportText) {
        try {
            Files.createDirectories(Paths.get(ESTIMATES_DIR));
        } catch (IOException ignored) {}

        String fileName = ESTIMATES_DIR + "estimate_" + est.getEstimateId() + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.print(reportText);
        } catch (IOException e) {
            System.err.println("[CSV] Could not save report: " + e.getMessage());
            return null;
        }
        return fileName;
    }
}
