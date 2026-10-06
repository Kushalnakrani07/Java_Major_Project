package parking.model;

/**
 * ============================================================
 * CONCEPT: Classes & Objects  |  Constructors  |  Encapsulation
 * ============================================================
 *
 * CLASS
 * -----
 * A class is a blueprint that defines data (fields) and behaviour
 * (methods).  Every concrete thing in the parking domain starts
 * as a class.
 *
 * OBJECT
 * ------
 * An object is a living instance of a class, created with `new`.
 *   Vehicle car = new Vehicle("MH12AB1234", "Toyota", "SEDAN");
 *
 * ENCAPSULATION
 * -------------
 * Fields are declared `private` so no outside code can corrupt
 * them directly.  Public getter / setter methods act as a
 * controlled gate — we can add validation inside setters later
 * without changing any caller code.
 *
 * CONSTRUCTORS
 * ------------
 * A constructor initialises an object right at creation time.
 * Java lets you have multiple constructors (constructor overloading).
 *   Constructor 1: requires all fields (full registration).
 *   Constructor 2: accepts only number + type (quick look-up).
 * ============================================================
 */
public class Vehicle {

    // ── Private fields (Encapsulation) ─────────────────────────
    private String registrationNumber;  // e.g. "MH12AB1234"
    private String ownerName;
    private String vehicleType;         // "TWO_WHEELER" | "FOUR_WHEELER" | "HEAVY"
    private String contactNumber;

    // ── Constructor 1: full registration ───────────────────────
    /**
     * Used when registering a new vehicle with complete details.
     *
     * @param registrationNumber unique plate number (used as key in HashMap)
     * @param ownerName          name of the vehicle owner
     * @param vehicleType        TWO_WHEELER / FOUR_WHEELER / HEAVY
     * @param contactNumber      owner's mobile number
     */
    public Vehicle(String registrationNumber, String ownerName,
                   String vehicleType, String contactNumber) {
        // Validation: registration number must not be blank
        if (registrationNumber == null || registrationNumber.isBlank()) {
            throw new IllegalArgumentException("Registration number cannot be empty.");
        }
        this.registrationNumber = registrationNumber.toUpperCase().trim();
        this.ownerName          = ownerName;
        this.vehicleType        = vehicleType.toUpperCase();
        this.contactNumber      = contactNumber;
    }

    // ── Constructor 2: minimal (for quick search / demo use) ───
    /**
     * Overloaded constructor: contact number defaults to "N/A".
     * Demonstrates CONSTRUCTOR OVERLOADING (same class, different params).
     */
    public Vehicle(String registrationNumber, String ownerName, String vehicleType) {
        this(registrationNumber, ownerName, vehicleType, "N/A"); // delegates to Constructor 1
    }

    // ── Getters (read-only access to private fields) ───────────
    public String getRegistrationNumber() { return registrationNumber; }
    public String getOwnerName()          { return ownerName; }
    public String getVehicleType()        { return vehicleType; }
    public String getContactNumber()      { return contactNumber; }

    // ── Setters (controlled mutation) ──────────────────────────
    public void setOwnerName(String ownerName)         { this.ownerName = ownerName; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    /**
     * ============================================================
     * CONCEPT: Method Overriding  |  toString()
     * ============================================================
     * toString() is defined in java.lang.Object (parent of ALL classes).
     * Overriding it lets us control what System.out.println(vehicle)
     * displays instead of the default "Vehicle@7852e922".
     * ============================================================
     */
    @Override
    public String toString() {
        return String.format("Vehicle[Reg=%-12s | Owner=%-15s | Type=%-12s | Contact=%s]",
                registrationNumber, ownerName, vehicleType, contactNumber);
    }
}
