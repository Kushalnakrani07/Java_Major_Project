package parking.model;

/**
 * ============================================================
 * CONCEPT: Classes & Objects  |  Array Usage  |  Encapsulation
 * ============================================================
 *
 * ARRAY CONCEPT (used inside ParkingSlot)
 * ----------------------------------------
 * Arrays store a fixed number of elements of the same type.
 * Here, each ParkingSlot holds its slot number (index-based).
 * The full parking lot layout is represented by an ARRAY of
 * ParkingSlot objects in ParkingService:
 *
 *   ParkingSlot[] slots = new ParkingSlot[totalSlots];
 *
 * This mirrors a real parking layout where slots are numbered
 * sequentially (0 … N-1 internally, shown as 1 … N to users).
 *
 * ParkingSlot is a VALUE OBJECT — it describes a single physical
 * parking bay and whether it is currently occupied.
 * ============================================================
 */
public class ParkingSlot {

    // ── Fields ──────────────────────────────────────────────────
    private final int    slotNumber;    // immutable slot id (final = set once)
    private       boolean occupied;    // true  → car is parked here
    private       String  vehicleType; // which vehicle types this slot accepts
    // e.g. "TWO_WHEELER", "FOUR_WHEELER", "HEAVY", "ANY"

    /**
     * Constructor: initialises a fresh (empty) parking slot.
     *
     * @param slotNumber  unique 1-based slot identifier
     * @param vehicleType the category this slot is designed for
     */
    public ParkingSlot(int slotNumber, String vehicleType) {
        this.slotNumber  = slotNumber;
        this.vehicleType = vehicleType.toUpperCase();
        this.occupied    = false; // a new slot is always empty
    }

    // ── Getters ─────────────────────────────────────────────────
    public int     getSlotNumber()  { return slotNumber; }
    public boolean isOccupied()     { return occupied;   }
    public String  getVehicleType() { return vehicleType; }

    // ── Mutators (occupation state changes) ─────────────────────
    /** Mark slot as occupied when a vehicle parks here. */
    public void occupy()   { this.occupied = true;  }
    /** Mark slot as free when a vehicle exits. */
    public void vacate()   { this.occupied = false; }

    /**
     * Returns a concise one-line description used in reports and the GUI.
     * Example: "Slot 3  [FOUR_WHEELER]  Status: OCCUPIED"
     */
    @Override
    public String toString() {
        return String.format("Slot %-3d [%-12s] Status: %s",
                slotNumber, vehicleType, occupied ? "OCCUPIED" : "AVAILABLE");
    }
}
