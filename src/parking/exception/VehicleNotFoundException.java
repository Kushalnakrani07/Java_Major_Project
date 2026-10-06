package parking.exception;

/**
 * ============================================================
 * CONCEPT: Exception Inheritance (continued)
 * ============================================================
 * Thrown when a search or exit operation references a vehicle
 * registration number that is not present in the system.
 * ============================================================
 */
public class VehicleNotFoundException extends ParkingException {

    private final String registrationNumber; // which vehicle was not found

    public VehicleNotFoundException(String registrationNumber) {
        super("Vehicle with registration '" + registrationNumber + "' not found.",
              "VEHICLE_NOT_FOUND");
        this.registrationNumber = registrationNumber;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }
}
