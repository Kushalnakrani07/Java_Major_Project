package parking.model;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

/**
 * ============================================================
 * CONCEPT: Classes & Objects  |  java.time API  |  Encapsulation
 * ============================================================
 *
 * ParkingRecord is the CORE DATA OBJECT of the system.
 * It captures the full lifecycle of one parking event:
 *   vehicle arrives → occupies slot → leaves → charge calculated.
 *
 * java.time.LocalDateTime
 * -----------------------
 * A modern, immutable date-time class (no time-zone).
 * We use it to record exact entry and exit timestamps.
 *
 * java.time.Duration
 * ------------------
 * Represents an amount of time (hours, minutes, seconds).
 * Duration.between(entry, exit) gives the parking duration,
 * which is then used to calculate the charge.
 *
 * VALIDATION concept
 * ------------------
 * The vehicle number and slot number are validated in the
 * constructor.  Invalid state is caught early rather than
 * causing silent bugs later.
 * ============================================================
 */
public class ParkingRecord {

    // ── Charge rate constants ────────────────────────────────────
    // Using `static final` makes them class-level constants —
    // they belong to the class, not any individual object.
    public static final double RATE_TWO_WHEELER   = 20.0;  // ₹ per hour
    public static final double RATE_FOUR_WHEELER  = 50.0;
    public static final double RATE_HEAVY         = 100.0;
    public static final double MIN_CHARGE_HOURS   = 1.0;   // at least 1 hour billed

    // Date-time formatter for display (e.g., "05-Oct-2026 20:30:00")
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

    // ── Fields ──────────────────────────────────────────────────
    private final String        registrationNumber; // FK → Vehicle
    private final int           slotNumber;         // FK → ParkingSlot
    private final LocalDateTime entryTime;          // when vehicle entered
    private       LocalDateTime exitTime;           // set on exit (null = still parked)
    private       double        chargeAmount;       // calculated on exit
    private       boolean       paymentDone;        // true once user pays

    /**
     * Constructor: called when a vehicle ENTERS the parking lot.
     *
     * CONCEPT: LocalDateTime.now() captures the exact moment.
     *
     * @param registrationNumber vehicle plate number
     * @param slotNumber         slot assigned to this vehicle
     */
    public ParkingRecord(String registrationNumber, int slotNumber) {
        // ── Validation ──────────────────────────────────────────
        if (registrationNumber == null || registrationNumber.isBlank()) {
            throw new IllegalArgumentException("Registration number cannot be blank.");
        }
        if (slotNumber <= 0) {
            throw new IllegalArgumentException("Slot number must be positive.");
        }
        this.registrationNumber = registrationNumber.toUpperCase().trim();
        this.slotNumber         = slotNumber;
        this.entryTime          = LocalDateTime.now(); // current system timestamp
        this.exitTime           = null;                // not yet exited
        this.chargeAmount       = 0.0;
        this.paymentDone        = false;
    }

    // ── Business method: calculate and record exit ───────────────
    /**
     * Called when the vehicle exits.
     * Sets exitTime and computes charge based on vehicle type and duration.
     *
     * CONCEPT: Duration.between() — measures elapsed time precisely.
     *
     * @param vehicleType type of vehicle (affects rate)
     */
    public void recordExit(String vehicleType) {
        this.exitTime = LocalDateTime.now();

        // Compute duration in fractional hours
        Duration duration = Duration.between(entryTime, exitTime);
        double hours = Math.max(duration.toMinutes() / 60.0, MIN_CHARGE_HOURS);
        // Bill at least 1 hour even if the car leaves in 5 minutes

        // Choose rate based on vehicle type (switch expression — Java 14+)
        double rate = switch (vehicleType.toUpperCase()) {
            case "TWO_WHEELER"  -> RATE_TWO_WHEELER;
            case "FOUR_WHEELER" -> RATE_FOUR_WHEELER;
            case "HEAVY"        -> RATE_HEAVY;
            default             -> RATE_FOUR_WHEELER; // fallback
        };

        this.chargeAmount = Math.round(hours * rate * 100.0) / 100.0; // round to 2 dp
    }

    /** Mark the parking charge as paid. */
    public void markPaymentDone() { this.paymentDone = true; }

    // ── Getters ─────────────────────────────────────────────────
    public String        getRegistrationNumber() { return registrationNumber; }
    public int           getSlotNumber()         { return slotNumber;  }
    public LocalDateTime getEntryTime()          { return entryTime;   }
    public LocalDateTime getExitTime()           { return exitTime;    }
    public double        getChargeAmount()       { return chargeAmount;}
    public boolean       isPaymentDone()         { return paymentDone; }

    /** Convenience: true while the vehicle is still in the lot. */
    public boolean isActive() { return exitTime == null; }

    /**
     * Computes parking duration string for display.
     * If the vehicle is still parked, shows time elapsed so far.
     */
    public String getDurationString() {
        LocalDateTime end = (exitTime != null) ? exitTime : LocalDateTime.now();
        Duration d = Duration.between(entryTime, end);
        return String.format("%dh %02dm", d.toHours(), d.toMinutesPart());
    }

    @Override
    public String toString() {
        return String.format(
            "Record[Reg=%-12s | Slot=%-3d | Entry=%-22s | Exit=%-22s | Charge=₹%-8.2f | Paid=%s]",
            registrationNumber,
            slotNumber,
            entryTime.format(FORMATTER),
            (exitTime != null ? exitTime.format(FORMATTER) : "STILL PARKED        "),
            chargeAmount,
            paymentDone ? "YES" : "NO"
        );
    }
}
