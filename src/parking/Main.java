package parking;

import parking.exception.ParkingException;
import parking.model.Vehicle;
import parking.service.ParkingService;
import parking.ui.ParkingUI;

import javax.swing.*;

/**
 * ============================================================
 * CONCEPT: Entry Point | main() | SwingUtilities.invokeLater()
 * ============================================================
 *
 * main() IS THE ENTRY POINT
 * --------------------------
 * Every Java application must have exactly one:
 *   public static void main(String[] args)
 * The JVM calls this method to start execution.
 *
 * SwingUtilities.invokeLater()
 * ─────────────────────────────
 * All Swing GUI operations MUST happen on the
 * Event Dispatch Thread (EDT), a dedicated thread
 * that handles all painting and user interactions.
 *
 * invokeLater(Runnable r) schedules the Runnable to
 * run on the EDT, ensuring thread safety.
 * ============================================================
 */
public class Main {

    public static void main(String[] args) {

        // ── Set system look-and-feel for native appearance ───────
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to default Java L&F — not critical
        }

        // ── Initialise the service (model) ───────────────────────
        // 30 total slots: 10 Two-Wheeler | 10 Four-Wheeler | 10 Heavy
        ParkingService service = new ParkingService(30);

        // ── Pre-load sample data for demonstration ────────────────
        loadSampleData(service);

        // ── Launch GUI on the Event Dispatch Thread ───────────────
        // Lambda implements Runnable (single abstract method interface)
        SwingUtilities.invokeLater(() -> new ParkingUI(service));
    }

    /**
     * Loads sample vehicles and parking events so the UI is
     * populated with meaningful data right from startup.
     *
     * This demonstrates CRUD operations programmatically.
     */
    private static void loadSampleData(ParkingService service) {
        try {
            // ── Register sample vehicles ─────────────────────────
            service.registerVehicle(new Vehicle("MH12AB1234", "Ramesh Patel",    "FOUR_WHEELER", "9876543210"));
            service.registerVehicle(new Vehicle("MH14CD5678", "Sunita Sharma",   "TWO_WHEELER",  "9123456789"));
            service.registerVehicle(new Vehicle("GJ01EF9012", "Kiran Mehta",     "FOUR_WHEELER", "9988776655"));
            service.registerVehicle(new Vehicle("DL4CAB0001", "Arjun Singh",     "HEAVY",        "8877665544"));
            service.registerVehicle(new Vehicle("KA03GH3456", "Priya Nair",      "TWO_WHEELER",  "7766554433"));
            service.registerVehicle(new Vehicle("MH20IJ7890", "Deepak Joshi",    "FOUR_WHEELER", "6655443322"));
            service.registerVehicle(new Vehicle("RJ14KL2345", "Anita Verma",     "TWO_WHEELER",  "5544332211"));
            service.registerVehicle(new Vehicle("TN09MN6789", "Vijay Kumar",     "FOUR_WHEELER", "4433221100"));
            service.registerVehicle(new Vehicle("MH04OP0123", "Sanjay Gupta",    "HEAVY",        "3322110099"));
            service.registerVehicle(new Vehicle("WB22QR4567", "Meena Das",       "FOUR_WHEELER", "2211009988"));

            // ── Simulate parking events ───────────────────────────
            // Park some vehicles (vehicle entry)
            service.vehicleEntry("MH12AB1234", 11); // Four-Wheeler in slot 11
            service.vehicleEntry("MH14CD5678", 1);  // Two-Wheeler  in slot 1
            service.vehicleEntry("GJ01EF9012", 12); // Four-Wheeler in slot 12
            service.vehicleEntry("DL4CAB0001", 21); // Heavy        in slot 21
            service.vehicleEntry("KA03GH3456", 2);  // Two-Wheeler  in slot 2

            // Exit one vehicle so history has a completed record
            service.vehicleExit("KA03GH3456");

            System.out.println("✅ Sample data loaded successfully.");
            System.out.println("📊 " + service.generateReport());

        } catch (ParkingException e) {
            // Print error but do NOT crash — app continues without that record
            System.err.println("⚠ Sample data error: " + e);
        }
    }
}
