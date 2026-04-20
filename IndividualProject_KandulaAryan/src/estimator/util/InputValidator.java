package estimator.util;

import java.util.Scanner;

public class InputValidator {

    private static final Scanner sc = new Scanner(System.in);

    /** Read a positive double; re-prompts on invalid input. */
    public static double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                double val = Double.parseDouble(input);
                if (val < 0) {
                    System.out.println("  ✗ Value cannot be negative. Please try again.");
                } else {
                    return val;
                }
            } catch (NumberFormatException e) {
                System.out.println("  ✗ Invalid number. Please enter a numeric value.");
            }
        }
    }

    /** Read a double that must be strictly > 0. */
    public static double readStrictPositiveDouble(String prompt) {
        while (true) {
            double val = readPositiveDouble(prompt);
            if (val <= 0) {
                System.out.println("  ✗ Value must be greater than zero.");
            } else {
                return val;
            }
        }
    }

    /** Read a non-negative integer. */
    public static int readNonNegativeInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                int val = Integer.parseInt(input);
                if (val < 0) {
                    System.out.println("  ✗ Value cannot be negative.");
                } else {
                    return val;
                }
            } catch (NumberFormatException e) {
                System.out.println("  ✗ Invalid integer. Please try again.");
            }
        }
    }

    /** Read a positive integer. */
    public static int readPositiveInt(String prompt) {
        while (true) {
            int val = readNonNegativeInt(prompt);
            if (val <= 0) {
                System.out.println("  ✗ Value must be at least 1.");
            } else {
                return val;
            }
        }
    }

    /** Read a percentage between min and max (inclusive). */
    public static double readPercentage(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                double val = Double.parseDouble(input);
                if (val < min || val > max) {
                    System.out.printf("  ✗ Please enter a value between %.1f and %.1f.%n", min, max);
                } else {
                    return val;
                }
            } catch (NumberFormatException e) {
                System.out.println("  ✗ Invalid number. Please try again.");
            }
        }
    }

    /** Read a menu choice between 1 and max. */
    public static int readMenuChoice(String prompt, int max) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                int val = Integer.parseInt(input);
                if (val < 1 || val > max) {
                    System.out.printf("  ✗ Please enter a number between 1 and %d.%n", max);
                } else {
                    return val;
                }
            } catch (NumberFormatException e) {
                System.out.println("  ✗ Invalid choice. Please try again.");
            }
        }
    }

    /** Read a yes/no answer; returns true for 'y'. */
    public static boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = sc.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) return true;
            if (input.equals("n") || input.equals("no"))  return false;
            System.out.println("  ✗ Please enter 'y' or 'n'.");
        }
    }

    /** Read a non-blank string. */
    public static String readNonBlank(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("  ✗ This field cannot be blank.");
        }
    }

    /** Read any string (may be blank). */
    public static String readString(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }
}
