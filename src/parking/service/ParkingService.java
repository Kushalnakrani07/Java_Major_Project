package parking.service;

import parking.exception.ParkingException;
import parking.exception.SlotUnavailableException;
import parking.exception.VehicleNotFoundException;
import parking.model.ParkingRecord;
import parking.model.ParkingSlot;
import parking.model.Vehicle;

import java.util.*;

/**
 * ============================================================
 * CONCEPT: ArrayList | LinkedList | HashMap | TreeMap
 *          CRUD Operations | Searching | Sorting
 * ============================================================
 *
 * This is the BRAIN of the application — the Service Layer.
 * It holds all data structures and implements every business rule.
 *
 * DATA STRUCTURES USED
 * ──────────────────────────────────────────────────────────────
 *
 * 1. ARRAY  ── ParkingSlot[]
 *    Fixed-size, index-based layout.
 *    Maps directly to physical slot numbers in the parking lot.
 *    Fast O(1) access by slot index.
 *
 * 2. ARRAYLIST  ── ArrayList<Vehicle>
 *    Ordered, resizable list of registered vehicles.
 *    Allows duplicates (by design, registration is validated separately).
 *    Used to iterate all vehicles in insertion order.
 *    O(n) search, O(1) amortised add.
 *
 * 3. LINKEDLIST  ── LinkedList<ParkingRecord>
 *    Doubly-linked list used for the ENTRY/EXIT HISTORY log.
 *    New events are added to the FRONT (addFirst) — most recent first.
 *    O(1) insertion at head; perfect for an activity log.
 *
 * 4. HASHMAP  ── HashMap<String, ParkingRecord>
 *    Maps vehicle registration number → its active ParkingRecord.
 *    Key hashing gives O(1) average-case look-up.
 *    Ideal for "find vehicle's current slot in constant time".
 *
 * 5. TREEMAP  ── TreeMap<Integer, ParkingSlot>
 *    Maps slot number → ParkingSlot, SORTED by slot number ascending.
 *    TreeMap maintains Red-Black Tree internally → O(log n) operations.
 *    Useful for displaying slots in numerical order without explicit sort.
 *
 * CRUD OPERATIONS
 * ──────────────────────────────────────────────────────────────
 *   C – Create : registerVehicle(), vehicleEntry()
 *   R – Read   : findVehicle(), getAllVehicles(), getActiveRecords()
 *   U – Update : processPayment()
 *   D – Delete : removeVehicle(), vehicleExit()
 * ============================================================
 */
public class ParkingService {

    // ── 1. ARRAY — Parking lot physical layout ──────────────────
    private final ParkingSlot[] slots; // index 0 = Slot 1, index 1 = Slot 2, …
    private final int totalSlots;

    // ── 2. ARRAYLIST — Master list of registered vehicles ────────
    private final ArrayList<Vehicle> registeredVehicles;

    // ── 3. LINKEDLIST — Chronological activity log ───────────────
    private final LinkedList<ParkingRecord> entryExitHistory;

    // ── 4. HASHMAP — Fast reg-number → active record look-up ─────
    private final HashMap<String, ParkingRecord> activeRecords;

    // ── 5. TREEMAP — Slots in sorted (slot-number) order ─────────
    private final TreeMap<Integer, ParkingSlot> sortedSlots;

    /**
     * Constructor: initialises all data structures and sets up
     * the parking lot with the requested number of slots.
     *
     * @param totalSlots total number of bays in the parking lot
     */
    public ParkingService(int totalSlots) {
        this.totalSlots = totalSlots;

        // ── Array initialisation ────────────────────────────────
        slots = new ParkingSlot[totalSlots]; // fixed-size array
        registeredVehicles = new ArrayList<>();
        entryExitHistory   = new LinkedList<>();
        activeRecords      = new HashMap<>();
        sortedSlots        = new TreeMap<>();

        // Populate the array and TreeMap with slot objects
        // First 1/3 slots: TWO_WHEELER  |  next 1/3: FOUR_WHEELER  |  rest: HEAVY
        for (int i = 0; i < totalSlots; i++) {
            String type;
            if      (i < totalSlots / 3)       type = "TWO_WHEELER";
            else if (i < (2 * totalSlots) / 3) type = "FOUR_WHEELER";
            else                               type = "HEAVY";

            ParkingSlot slot = new ParkingSlot(i + 1, type); // 1-based slot number
            slots[i] = slot;            // store in array
            sortedSlots.put(i + 1, slot); // store in TreeMap (auto-sorted)
        }
    }

    // ════════════════════════════════════════════════════════════
    // ── C R U D ─ CREATE ────────────────────────────────────────
    // ════════════════════════════════════════════════════════════

    /**
     * [CREATE] Register a new vehicle in the system.
     * Adds to ArrayList<Vehicle>.
     *
     * @throws ParkingException if vehicle is already registered
     */
    public void registerVehicle(Vehicle vehicle) throws ParkingException {
        // Check for duplicate registration (linear search on ArrayList)
        for (Vehicle v : registeredVehicles) {
            if (v.getRegistrationNumber().equalsIgnoreCase(vehicle.getRegistrationNumber())) {
                throw new ParkingException(
                    "Vehicle " + vehicle.getRegistrationNumber() + " is already registered.",
                    "DUPLICATE_VEHICLE");
            }
        }
        registeredVehicles.add(vehicle); // ArrayList.add() — O(1) amortised
    }

    /**
     * [CREATE] Record vehicle entry: assigns a slot and logs it.
     *
     * CONCEPT: HashMap.put(key, value) — stores in hash table O(1)
     * CONCEPT: LinkedList.addFirst() — O(1) head insertion
     *
     * @param registrationNumber vehicle plate number
     * @param slotNumber         slot to assign (1-based)
     * @throws SlotUnavailableException if slot is occupied/invalid
     * @throws VehicleNotFoundException if vehicle not registered
     * @throws ParkingException         if vehicle already inside
     */
    public ParkingRecord vehicleEntry(String registrationNumber, int slotNumber)
            throws ParkingException {

        // Step 1: Validate vehicle is registered
        Vehicle vehicle = findVehicle(registrationNumber); // throws if not found

        // Step 2: Validate vehicle is not already inside
        if (activeRecords.containsKey(registrationNumber.toUpperCase())) {
            throw new ParkingException(
                "Vehicle " + registrationNumber + " is already parked.",
                "ALREADY_PARKED");
        }

        // Step 3: Validate the slot (array bounds + occupancy check)
        if (slotNumber < 1 || slotNumber > totalSlots) {
            throw new SlotUnavailableException(slotNumber);
        }
        ParkingSlot slot = slots[slotNumber - 1]; // array index is 0-based
        if (slot.isOccupied()) {
            throw new SlotUnavailableException(slotNumber);
        }

        // Step 4: Mark slot as occupied
        slot.occupy();

        // Step 5: Create a new parking record
        ParkingRecord record = new ParkingRecord(registrationNumber, slotNumber);

        // Step 6: Store in HashMap for O(1) future look-up
        activeRecords.put(registrationNumber.toUpperCase(), record);

        // Step 7: Prepend to LinkedList history (most-recent first)
        entryExitHistory.addFirst(record);

        return record;
    }

    // ════════════════════════════════════════════════════════════
    // ── C R U D ─ READ ──────────────────────────────────────────
    // ════════════════════════════════════════════════════════════

    /**
     * [READ] Find a registered vehicle by registration number.
     *
     * CONCEPT: Linear Search on ArrayList — O(n)
     *   - We iterate each element and compare until a match is found.
     *   - Suitable here because registration numbers are compared,
     *     not indices, and the list size is typically manageable.
     *
     * @throws VehicleNotFoundException if no match found
     */
    public Vehicle findVehicle(String registrationNumber) throws VehicleNotFoundException {
        String target = registrationNumber.toUpperCase().trim();
        // Linear search — iterate ArrayList
        for (Vehicle v : registeredVehicles) {
            if (v.getRegistrationNumber().equals(target)) {
                return v; // found — return immediately
            }
        }
        // Reached end without match
        throw new VehicleNotFoundException(registrationNumber);
    }

    /**
     * [READ] Get the active ParkingRecord for a vehicle.
     * Uses HashMap — O(1) average look-up.
     */
    public ParkingRecord getActiveRecord(String registrationNumber) throws VehicleNotFoundException {
        ParkingRecord record = activeRecords.get(registrationNumber.toUpperCase());
        if (record == null) {
            throw new VehicleNotFoundException(registrationNumber + " (not currently parked)");
        }
        return record;
    }

    /** [READ] Return an unmodifiable view of all registered vehicles. */
    public List<Vehicle> getAllVehicles() {
        return Collections.unmodifiableList(registeredVehicles);
    }

    /** [READ] Return all currently active (parked) records from HashMap. */
    public Collection<ParkingRecord> getActiveRecords() {
        return Collections.unmodifiableCollection(activeRecords.values());
    }

    /** [READ] Return the full entry/exit history (LinkedList). */
    public LinkedList<ParkingRecord> getHistory() {
        return entryExitHistory;
    }

    /** [READ] Return slots sorted by slot number (TreeMap natural order). */
    public TreeMap<Integer, ParkingSlot> getSortedSlots() {
        return sortedSlots;
    }

    /** [READ] Return count of available slots. */
    public long getAvailableSlotCount() {
        long count = 0;
        for (ParkingSlot s : slots) { // iterate array
            if (!s.isOccupied()) count++;
        }
        return count;
    }

    /** [READ] Return array of all slots (for GUI rendering). */
    public ParkingSlot[] getAllSlots() {
        return slots;
    }

    // ════════════════════════════════════════════════════════════
    // ── C R U D ─ UPDATE ────────────────────────────────────────
    // ════════════════════════════════════════════════════════════

    /**
     * [UPDATE] Mark a parking record as paid.
     *
     * CONCEPT: HashMap.get() — retrieve record by key O(1),
     *          then mutate it in place.
     */
    public void processPayment(String registrationNumber) throws VehicleNotFoundException {
        ParkingRecord record = getActiveRecord(registrationNumber);
        record.markPaymentDone();
    }

    // ════════════════════════════════════════════════════════════
    // ── C R U D ─ DELETE ────────────────────────────────────────
    // ════════════════════════════════════════════════════════════

    /**
     * [DELETE / EXIT] Process vehicle exit.
     *   1. Computes charge.
     *   2. Frees the parking slot.
     *   3. Removes from activeRecords HashMap.
     *
     * CONCEPT: HashMap.remove(key) — O(1) deletion.
     */
    public ParkingRecord vehicleExit(String registrationNumber) throws ParkingException {
        // Find active record — throws VehicleNotFoundException if absent
        ParkingRecord record = getActiveRecord(registrationNumber);

        // Retrieve vehicle type for charge calculation
        Vehicle vehicle = findVehicle(registrationNumber);
        record.recordExit(vehicle.getVehicleType()); // computes exit time + charge

        // Free the slot (mark as available)
        int slotIndex = record.getSlotNumber() - 1; // convert 1-based → 0-based
        slots[slotIndex].vacate();                   // array access O(1)

        // Remove from active records HashMap
        activeRecords.remove(registrationNumber.toUpperCase()); // O(1)

        // Note: record stays in LinkedList history (audit trail)
        return record;
    }

    /**
     * [DELETE] Remove a vehicle from the registered list.
     * Uses ArrayList.removeIf() — functional-style removal.
     *
     * @throws VehicleNotFoundException if vehicle not in list
     * @throws ParkingException         if vehicle is currently parked
     */
    public void removeVehicle(String registrationNumber) throws ParkingException {
        String target = registrationNumber.toUpperCase().trim();

        // Cannot remove a vehicle that is currently inside
        if (activeRecords.containsKey(target)) {
            throw new ParkingException(
                "Cannot remove vehicle " + target + ": it is currently parked.",
                "VEHICLE_PARKED");
        }

        // removeIf returns true if anything was removed
        boolean removed = registeredVehicles.removeIf(
            v -> v.getRegistrationNumber().equals(target));

        if (!removed) {
            throw new VehicleNotFoundException(registrationNumber);
        }
    }

    // ════════════════════════════════════════════════════════════
    // ── SEARCHING ───────────────────────────────────────────────
    // ════════════════════════════════════════════════════════════

    /**
     * CONCEPT: Binary Search (after sorting)
     * ──────────────────────────────────────
     * Binary search requires the list to be SORTED first.
     * It works by repeatedly halving the search space:
     *   Compare target with middle element.
     *   If match → found.  If less → search left half.  If more → right half.
     * Time complexity: O(log n) vs linear search O(n).
     *
     * Here we sort a copy of the ArrayList by registration number,
     * then use Collections.binarySearch() with a Comparator.
     *
     * @param registrationNumber registration number to search
     * @return index in sorted list, or negative if not found
     */
    public int binarySearchVehicle(String registrationNumber) {
        // Create a sorted copy (do NOT mutate the original ArrayList)
        ArrayList<Vehicle> sorted = new ArrayList<>(registeredVehicles);
        // Sort alphabetically by registration number
        sorted.sort(Comparator.comparing(Vehicle::getRegistrationNumber));

        // Binary search using a Comparator
        int idx = Collections.binarySearch(
            sorted,
            new Vehicle(registrationNumber, "", "TWO_WHEELER"), // dummy key object
            Comparator.comparing(Vehicle::getRegistrationNumber)
        );
        return idx; // ≥ 0 means found; negative means not found
    }

    // ════════════════════════════════════════════════════════════
    // ── SORTING ─────────────────────────────────────────────────
    // ════════════════════════════════════════════════════════════

    /**
     * CONCEPT: Sorting with Comparator
     * ──────────────────────────────────────────────────────────
     * Comparator<T> is a functional interface with one method:
     *   int compare(T o1, T o2)
     *
     * We use lambda expressions (Java 8+) as compact Comparators.
     * Collections.sort() and List.sort() use TimSort internally O(n log n).
     *
     * We return NEW sorted lists so the original ArrayList is unchanged.
     * ──────────────────────────────────────────────────────────
     */

    /**
     * Sort all parking records (history) by ENTRY TIME ascending.
     * @return new sorted list
     */
    public List<ParkingRecord> sortHistoryByEntryTime() {
        List<ParkingRecord> sorted = new ArrayList<>(entryExitHistory);
        // Lambda Comparator: compare entryTime of two records
        sorted.sort(Comparator.comparing(ParkingRecord::getEntryTime));
        return sorted;
    }

    /**
     * Sort completed records by CHARGE AMOUNT descending (highest first).
     * Demonstrates Comparator.reversed() for descending order.
     * @return new sorted list of completed (exited) records
     */
    public List<ParkingRecord> sortByChargeDescending() {
        List<ParkingRecord> completed = new ArrayList<>();
        for (ParkingRecord r : entryExitHistory) {
            if (!r.isActive()) completed.add(r); // only exited vehicles
        }
        // Reversed comparator → descending
        completed.sort(Comparator.comparingDouble(ParkingRecord::getChargeAmount).reversed());
        return completed;
    }

    /**
     * Sort registered vehicles by registration number (alphabetical).
     * @return new sorted list of vehicles
     */
    public List<Vehicle> sortVehiclesByRegistration() {
        List<Vehicle> sorted = new ArrayList<>(registeredVehicles);
        sorted.sort(Comparator.comparing(Vehicle::getRegistrationNumber));
        return sorted;
    }

    // ════════════════════════════════════════════════════════════
    // ── REPORT GENERATION ───────────────────────────────────────
    // ════════════════════════════════════════════════════════════

    /**
     * Generate a plain-text parking report — used by both CLI and GUI.
     * Demonstrates StringBuilder for efficient string concatenation.
     */
    public String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("═".repeat(80)).append("\n");
        sb.append("                  SMART PARKING MANAGEMENT SYSTEM — REPORT\n");
        sb.append("═".repeat(80)).append("\n\n");

        sb.append("SLOT SUMMARY\n").append("─".repeat(50)).append("\n");
        sb.append("  Total Slots   : ").append(totalSlots).append("\n");
        sb.append("  Available     : ").append(getAvailableSlotCount()).append("\n");
        sb.append("  Occupied      : ").append(totalSlots - getAvailableSlotCount()).append("\n\n");

        sb.append("SLOT STATUS (sorted by slot number — TreeMap)\n").append("─".repeat(50)).append("\n");
        // Iterating a TreeMap gives keys in ascending order automatically
        for (Map.Entry<Integer, ParkingSlot> entry : sortedSlots.entrySet()) {
            sb.append("  ").append(entry.getValue()).append("\n");
        }

        sb.append("\nREGISTERED VEHICLES (").append(registeredVehicles.size()).append(")\n")
          .append("─".repeat(50)).append("\n");
        for (Vehicle v : registeredVehicles) {
            sb.append("  ").append(v).append("\n");
        }

        sb.append("\nACTIVE PARKINGS (").append(activeRecords.size()).append(")\n")
          .append("─".repeat(50)).append("\n");
        for (ParkingRecord r : activeRecords.values()) {
            sb.append("  ").append(r).append("\n");
        }

        sb.append("\nFULL HISTORY — sorted by entry time\n").append("─".repeat(50)).append("\n");
        List<ParkingRecord> sorted = sortHistoryByEntryTime();
        for (ParkingRecord r : sorted) {
            sb.append("  ").append(r).append("\n");
        }

        sb.append("\n").append("═".repeat(80)).append("\n");
        return sb.toString();
    }
}
