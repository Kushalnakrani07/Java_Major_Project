package parking.exception;

/**
 * ============================================================
 * CONCEPT: Custom Exception / Exception Handling
 * ============================================================
 * Java allows you to create your own exception classes by
 * extending the built-in Exception class.
 *
 * Why custom exceptions?
 *  - They give meaningful names to domain-specific error conditions.
 *  - They carry extra context (like error codes or slot numbers).
 *  - They let callers catch ONLY parking-related errors, not all exceptions.
 *
 * Hierarchy used here:
 *   Throwable
 *    └── Exception          (checked — must be declared or caught)
 *         └── ParkingException   ← our custom checked exception
 *              ├── SlotUnavailableException
 *              └── VehicleNotFoundException
 * ============================================================
 */
public class ParkingException extends Exception {

    // A code to categorise the kind of parking error
    private final String errorCode;

    /**
     * Constructor chaining: calls the parent (Exception) constructor
     * that accepts a detail message.
     *
     * @param message   human-readable description of the problem
     * @param errorCode short code identifying the error category
     */
    public ParkingException(String message, String errorCode) {
        super(message);          // passes message to Exception → Throwable
        this.errorCode = errorCode;
    }

    /** @return the short error-category code */
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        // Overriding toString() gives a nicer output when printing the exception
        return "[" + errorCode + "] " + getMessage();
    }
}
