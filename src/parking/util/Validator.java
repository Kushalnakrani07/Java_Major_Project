package parking.util;

/**
 * ============================================================
 * CONCEPT: Utility Class | Static Methods | Validation
 * ============================================================
 *
 * A utility class groups STATELESS helper methods that don't
 * belong to any particular domain object.
 *
 * STATIC METHODS
 * --------------
 * Static methods belong to the CLASS, not an instance.
 * You call them as: Validator.isValidRegistration("MH12AB1234")
 * No need to create a Validator object.
 *
 * PRIVATE CONSTRUCTOR
 * -------------------
 * Making the constructor private prevents anyone from
 * accidentally doing `new Validator()`.  This is the standard
 * pattern for utility classes.
 *
 * REGULAR EXPRESSIONS (regex)
 * ---------------------------
 * String.matches() uses Java regex to validate formats.
 * Pattern used for vehicle registration:
 *   [A-Z]{2}   – 2 uppercase letters (state code)
 *   [0-9]{2}   – 2 digits (district code)
 *   [A-Z]{1,2} – 1 or 2 letters (series)
 *   [0-9]{4}   – 4-digit number
 *   Example valid: MH12AB1234, DL4CAB0001
 * ============================================================
 */
public final class Validator {

    // Private constructor — prevents instantiation
    private Validator() {
        throw new UnsupportedOperationException("Utility class — do not instantiate.");
    }

    /**
     * Validates Indian vehicle registration number format.
     * Pattern: 2 letters + 2 digits + 1-2 letters + 4 digits
     *
     * @param regNum registration number string to validate
     * @return true if format is valid
     */
    public static boolean isValidRegistration(String regNum) {
        if (regNum == null) return false;
        // Trim and convert to uppercase before matching
        return regNum.trim().toUpperCase().matches("[A-Z]{2}[0-9]{2}[A-Z]{1,2}[0-9]{4}");
    }

    /**
     * Validates a slot number is within [1, totalSlots].
     *
     * @param slotNumber  the number entered by the user
     * @param totalSlots  maximum slot count
     * @return true if slot number is in valid range
     */
    public static boolean isValidSlotNumber(int slotNumber, int totalSlots) {
        return slotNumber >= 1 && slotNumber <= totalSlots;
    }

    /**
     * Validates a contact/phone number — must be exactly 10 digits.
     *
     * @param contact contact string
     * @return true if valid 10-digit number
     */
    public static boolean isValidContact(String contact) {
        if (contact == null) return false;
        return contact.trim().matches("[0-9]{10}");
    }

    /**
     * Validates vehicle type is one of the accepted values.
     *
     * @param type vehicle type string
     * @return true if type is recognised
     */
    public static boolean isValidVehicleType(String type) {
        if (type == null) return false;
        String t = type.trim().toUpperCase();
        return t.equals("TWO_WHEELER") || t.equals("FOUR_WHEELER") || t.equals("HEAVY");
    }

    /**
     * Formats a double as a currency string.
     * Example: 123.5 → "₹123.50"
     */
    public static String formatCurrency(double amount) {
        return String.format("₹%.2f", amount);
    }
}
