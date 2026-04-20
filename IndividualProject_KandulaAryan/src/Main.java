import estimator.ui.AppTheme;
import estimator.ui.MainWindow;
import javax.swing.*;

/**
 * Concrete Pad & Chain-Link Fence Estimator  —  GUI Entry Point
 *
 * Author:   Aryan Kandula
 * Course:   CSC-251 Advanced Java Programming  |  Module 5 Individual Project
 * Version:  1.2
 *
 * Compile & Run (from ConcreteEstimator/ folder):
 *   javac -d out -sourcepath src src/Main.java
 *   java  -cp out Main
 *
 *  OR just double-click run.bat (Windows) / run.sh (Mac/Linux)
 */
public class Main {
    public static void main(String[] args) {
        // IMPORTANT: Use Metal (cross-platform) L&F ONLY.
        // Nimbus ignores setBackground() on spinners/combos entirely.
        // Windows System L&F overrides button and input colors.
        // Metal is the only L&F that fully respects UIManager color keys.
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception ignored) {}

        // Apply all dark theme colors after Metal is installed
        AppTheme.applyGlobalDefaults();

        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
