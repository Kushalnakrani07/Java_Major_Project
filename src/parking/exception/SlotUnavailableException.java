package parking.exception;

/**
 * ============================================================
 * CONCEPT: Exception Inheritance
 * ============================================================
 * SlotUnavailableException IS-A ParkingException.
 * This is thrown whenever a caller tries to park in a slot
 * that is already occupied or does not exist.
 *
 * Benefits of a specific subclass:
 *  - catch (SlotUnavailableException e) catches ONLY this case.
 *  - catch (ParkingException e) catches this AND other parking errors.
 *  - Slot number stored here helps the caller recover gracefully.
 * ============================================================
 */
public class SlotUnavailableException extends ParkingException {

    private final int slotNumber; // which slot caused the problem

    public SlotUnavailableException(int slotNumber) {
        // Delegates to ParkingException(message, errorCode)
        super("Parking slot " + slotNumber + " is unavailable or already occupied.",
              "SLOT_UNAVAILABLE");
        this.slotNumber = slotNumber;
    }

    public int getSlotNumber() {
        return slotNumber;
    }
}
