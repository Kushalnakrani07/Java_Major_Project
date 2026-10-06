# 🅿 Smart Parking Management System
### Case Study 15 · B.Tech CSE 2025-29 · Java Programming · ITM Skills University

---

## 📌 Table of Contents

1. [Problem Statement](#1-problem-statement)
2. [Project Structure](#2-project-structure)
3. [How to Compile & Run](#3-how-to-compile--run)
4. [Part 1 — Exception Classes](#4-part-1--exception-classes)
5. [Part 2 — Model Classes](#5-part-2--model-classes)
6. [Part 3 — Service Layer](#6-part-3--service-layer)
7. [Part 4 — Validator Utility](#7-part-4--validator-utility)
8. [Part 5 — Swing GUI](#8-part-5--swing-gui)
9. [Part 6 — Main Entry Point](#9-part-6--main-entry-point)
10. [Data Flow Diagram](#10-data-flow-diagram)
11. [Java Concepts Quick Reference](#11-java-concepts-quick-reference)
12. [Charge Calculation](#12-charge-calculation)
13. [Sample Output](#13-sample-output)

---

## 1. Problem Statement

> A parking facility requires a system to manage parking slots, vehicles, entry and exit times, and parking charges. The system should display available slots and maintain vehicle parking records.

**Objectives:**
- Manage parking slot information
- Register vehicles
- Allocate available parking slots
- Track vehicle entry and exit
- Calculate parking charges
- Search and sort parking records

---

## 2. Project Structure

```
SmartParkingSystem/
│
├── README.md                              ← You are here
│
├── src/
│   └── parking/
│       │
│       ├── Main.java                      ← Application entry point
│       │
│       ├── exception/                     ← Custom exception classes
│       │   ├── ParkingException.java      ← Base exception
│       │   ├── SlotUnavailableException.java
│       │   └── VehicleNotFoundException.java
│       │
│       ├── model/                         ← Domain data objects
│       │   ├── Vehicle.java               ← Vehicle data
│       │   ├── ParkingSlot.java           ← Single parking bay
│       │   └── ParkingRecord.java         ← One parking event
│       │
│       ├── service/
│       │   └── ParkingService.java        ← ALL business logic + data structures
│       │
│       ├── ui/
│       │   └── ParkingUI.java             ← Swing GUI (6 tabs)
│       │
│       └── util/
│           └── Validator.java             ← Static validation helpers
│
└── out/                                   ← Compiled .class files (auto-generated)
```

---

## 3. How to Compile & Run

### ✅ Compile
```bash
# From the SmartParkingSystem folder
javac -d out src/parking/exception/*.java \
             src/parking/model/*.java \
             src/parking/util/*.java \
             src/parking/service/*.java \
             src/parking/ui/*.java \
             src/parking/Main.java
```

### ▶ Run
```bash
java -cp out parking.Main
```


```
File → Open → select SmartParkingSystem folder
Right-click src → Mark Directory as → Sources Root
Run → Main.java
```

---

## 4. Part 1 — Exception Classes

> 📁 `src/parking/exception/`

These three files implement **custom exception handling** — a way to create your own error types that are meaningful to your domain.

---

### 4.1 `ParkingException.java` — Base Exception

```java
public class ParkingException extends Exception {
    private final String errorCode;

    public ParkingException(String message, String errorCode) {
        super(message);           // calls Exception(message)
        this.errorCode = errorCode;
    }

    public String getErrorCode() { return errorCode; }
}
```

**What it does:**
- Extends `Exception` (Java's built-in checked exception class)
- Adds an `errorCode` field (e.g., `"SLOT_UNAVAILABLE"`) for categorising errors
- `super(message)` passes the human-readable description up to `Exception → Throwable`

**Why checked exception?**
- Checked exceptions (extending `Exception`) MUST be declared with `throws` or caught
- This forces callers to consciously handle parking errors — no accidental ignoring

---

### 4.2 `SlotUnavailableException.java`

```java
public class SlotUnavailableException extends ParkingException {
    private final int slotNumber;

    public SlotUnavailableException(int slotNumber) {
        super("Parking slot " + slotNumber + " is unavailable.", "SLOT_UNAVAILABLE");
        this.slotNumber = slotNumber;
    }
}
```

**What it does:**
- Thrown when someone tries to park in a slot that is full or out of range
- Stores `slotNumber` so the caller knows WHICH slot failed
- Inherits `getMessage()` and `getErrorCode()` from `ParkingException`

---

### 4.3 `VehicleNotFoundException.java`

```java
public class VehicleNotFoundException extends ParkingException {
    private final String registrationNumber;

    public VehicleNotFoundException(String registrationNumber) {
        super("Vehicle '" + registrationNumber + "' not found.", "VEHICLE_NOT_FOUND");
        this.registrationNumber = registrationNumber;
    }
}
```

**What it does:**
- Thrown when searching for a vehicle that was never registered
- Stores the attempted registration number for helpful error messages

**Exception Hierarchy:**
```
Throwable
 └── Exception  (checked)
      └── ParkingException          [errorCode field]
           ├── SlotUnavailableException   [slotNumber field]
           └── VehicleNotFoundException   [registrationNumber field]
```

---

## 5. Part 2 — Model Classes

> 📁 `src/parking/model/`

Model classes are **plain data objects** (also called POJOs — Plain Old Java Objects).
They hold data and have no knowledge of the GUI or the database.

---

### 5.1 `Vehicle.java` — Represents a Registered Vehicle

```java
public class Vehicle {
    // Private fields — Encapsulation
    private String registrationNumber;
    private String ownerName;
    private String vehicleType;     // "TWO_WHEELER" | "FOUR_WHEELER" | "HEAVY"
    private String contactNumber;

    // Constructor 1 — full details
    public Vehicle(String registrationNumber, String ownerName,
                   String vehicleType, String contactNumber) {
        if (registrationNumber == null || registrationNumber.isBlank())
            throw new IllegalArgumentException("Registration cannot be empty.");
        this.registrationNumber = registrationNumber.toUpperCase().trim();
        this.ownerName          = ownerName;
        this.vehicleType        = vehicleType.toUpperCase();
        this.contactNumber      = contactNumber;
    }

    // Constructor 2 — overloaded (contact defaults to "N/A")
    public Vehicle(String registrationNumber, String ownerName, String vehicleType) {
        this(registrationNumber, ownerName, vehicleType, "N/A");
    }

    // Getters only (no setters for reg. number → immutable identity)
    public String getRegistrationNumber() { return registrationNumber; }
    public String getOwnerName()          { return ownerName; }
    public String getVehicleType()        { return vehicleType; }
    public String getContactNumber()      { return contactNumber; }

    @Override
    public String toString() {
        return String.format("Vehicle[Reg=%-12s | Owner=%-15s | Type=%-12s | Contact=%s]",
                registrationNumber, ownerName, vehicleType, contactNumber);
    }
}
```

**Key concepts demonstrated:**

| Concept | Where |
|---|---|
| **Encapsulation** | All fields are `private`; access via getters only |
| **Constructor Overloading** | Two constructors — 4 params and 3 params |
| **Constructor Chaining** | `this(...)` in Constructor 2 delegates to Constructor 1 |
| **Validation** | `isBlank()` check in constructor prevents bad objects |
| **`toString()` Override** | Custom string representation for printing |
| **`final`-like immutability** | Registration number is set once (no setter provided) |

---

### 5.2 `ParkingSlot.java` — Represents One Parking Bay

```java
public class ParkingSlot {
    private final int    slotNumber;   // 1-based, immutable (final)
    private       boolean occupied;   // changes when car parks/leaves
    private       String  vehicleType; // which vehicles can use this slot

    public ParkingSlot(int slotNumber, String vehicleType) {
        this.slotNumber  = slotNumber;
        this.vehicleType = vehicleType.toUpperCase();
        this.occupied    = false;      // every new slot starts empty
    }

    public void occupy() { this.occupied = true;  }  // called on entry
    public void vacate() { this.occupied = false; }  // called on exit

    public boolean isOccupied()     { return occupied;   }
    public int     getSlotNumber()  { return slotNumber; }
    public String  getVehicleType() { return vehicleType; }
}
```

**Key concepts demonstrated:**

| Concept | Where |
|---|---|
| **`final` field** | `slotNumber` is set once in constructor and never changes |
| **State management** | `occupied` boolean changes over the slot's lifetime |
| **Encapsulation** | State can only change through `occupy()` / `vacate()` |

**How slots are distributed (30 total):**
```
Slots  1-10  → TWO_WHEELER
Slots 11-20  → FOUR_WHEELER
Slots 21-30  → HEAVY
```

---

### 5.3 `ParkingRecord.java` — One Complete Parking Event

This is the most complex model. It records the full lifecycle: entry → parked → exit → charged.

```java
public class ParkingRecord {

    // Rate constants (static = belong to class, not object)
    public static final double RATE_TWO_WHEELER  = 20.0;  // ₹ per hour
    public static final double RATE_FOUR_WHEELER = 50.0;
    public static final double RATE_HEAVY        = 100.0;

    private final String        registrationNumber;
    private final int           slotNumber;
    private final LocalDateTime entryTime;   // set at construction (entry moment)
    private       LocalDateTime exitTime;    // null = still parked
    private       double        chargeAmount;
    private       boolean       paymentDone;

    // Constructor — called the moment a vehicle enters
    public ParkingRecord(String registrationNumber, int slotNumber) {
        this.registrationNumber = registrationNumber.toUpperCase().trim();
        this.slotNumber         = slotNumber;
        this.entryTime          = LocalDateTime.now();  // current timestamp
        this.exitTime           = null;                 // not exited yet
        this.chargeAmount       = 0.0;
        this.paymentDone        = false;
    }

    // Called when vehicle exits — sets exit time and calculates charge
    public void recordExit(String vehicleType) {
        this.exitTime = LocalDateTime.now();

        Duration duration = Duration.between(entryTime, exitTime);
        double hours = Math.max(duration.toMinutes() / 60.0, 1.0); // min 1 hr

        double rate = switch (vehicleType.toUpperCase()) {
            case "TWO_WHEELER"  -> RATE_TWO_WHEELER;
            case "FOUR_WHEELER" -> RATE_FOUR_WHEELER;
            case "HEAVY"        -> RATE_HEAVY;
            default             -> RATE_FOUR_WHEELER;
        };

        this.chargeAmount = Math.round(hours * rate * 100.0) / 100.0;
    }
}
```

**Key concepts demonstrated:**

| Concept | Where |
|---|---|
| **`static final` constants** | Rate constants shared by all ParkingRecord objects |
| **`java.time.LocalDateTime`** | Modern date-time, immutable, no timezone |
| **`java.time.Duration`** | Measures time between two `LocalDateTime` values |
| **Switch Expression** | Java 14+ compact `switch` that returns a value |
| **`Math.max()`** | Ensures minimum 1-hour billing |
| **`Math.round()`** | Rounds charge to 2 decimal places |
| **Null state** | `exitTime == null` means vehicle is still parked |

---

## 6. Part 3 — Service Layer

> 📁 `src/parking/service/ParkingService.java`

This is the **brain** of the application. It holds every data structure and implements every business rule. No GUI code is here — it is purely logic.

---

### 6.1 Data Structures Declared

```java
private final ParkingSlot[]                    slots;            // 1. Array
private final ArrayList<Vehicle>               registeredVehicles; // 2. ArrayList
private final LinkedList<ParkingRecord>        entryExitHistory; // 3. LinkedList
private final HashMap<String, ParkingRecord>   activeRecords;    // 4. HashMap
private final TreeMap<Integer, ParkingSlot>    sortedSlots;      // 5. TreeMap
```

**Why each data structure was chosen:**

| Structure | Reason |
|---|---|
| `ParkingSlot[]` | Fixed-size parking lot layout; O(1) access by slot index |
| `ArrayList<Vehicle>` | Ordered, resizable list of registered vehicles |
| `LinkedList<ParkingRecord>` | O(1) front-insertion for chronological history log |
| `HashMap<String, ParkingRecord>` | O(1) look-up of active record by registration number |
| `TreeMap<Integer, ParkingSlot>` | Auto-sorted by slot number — no manual sorting needed |

---

### 6.2 Constructor — Slot Initialisation

```java
public ParkingService(int totalSlots) {
    this.totalSlots = totalSlots;
    slots           = new ParkingSlot[totalSlots];   // create fixed array
    registeredVehicles = new ArrayList<>();
    entryExitHistory   = new LinkedList<>();
    activeRecords      = new HashMap<>();
    sortedSlots        = new TreeMap<>();

    for (int i = 0; i < totalSlots; i++) {
        String type;
        if      (i < totalSlots / 3)        type = "TWO_WHEELER";
        else if (i < (2 * totalSlots) / 3)  type = "FOUR_WHEELER";
        else                                type = "HEAVY";

        ParkingSlot slot = new ParkingSlot(i + 1, type);
        slots[i] = slot;            // store in array (0-based index)
        sortedSlots.put(i + 1, slot); // store in TreeMap (1-based key)
    }
}
```

---

### 6.3 CRUD — Create (Register Vehicle)

```java
public void registerVehicle(Vehicle vehicle) throws ParkingException {
    // Linear search for duplicate check
    for (Vehicle v : registeredVehicles) {
        if (v.getRegistrationNumber().equalsIgnoreCase(vehicle.getRegistrationNumber())) {
            throw new ParkingException("Already registered.", "DUPLICATE_VEHICLE");
        }
    }
    registeredVehicles.add(vehicle);  // ArrayList.add() — O(1) amortised
}
```

---

### 6.4 CRUD — Create (Vehicle Entry)

```java
public ParkingRecord vehicleEntry(String registrationNumber, int slotNumber)
        throws ParkingException {

    Vehicle vehicle = findVehicle(registrationNumber);        // must be registered

    if (activeRecords.containsKey(registrationNumber))        // can't park twice
        throw new ParkingException("Already parked.", "ALREADY_PARKED");

    if (slotNumber < 1 || slotNumber > totalSlots)            // range check
        throw new SlotUnavailableException(slotNumber);

    ParkingSlot slot = slots[slotNumber - 1];                 // array access O(1)
    if (slot.isOccupied())
        throw new SlotUnavailableException(slotNumber);

    slot.occupy();                                            // mark slot OCCUPIED
    ParkingRecord record = new ParkingRecord(registrationNumber, slotNumber);
    activeRecords.put(registrationNumber.toUpperCase(), record); // HashMap O(1)
    entryExitHistory.addFirst(record);                        // LinkedList head O(1)
    return record;
}
```

---

### 6.5 CRUD — Read (Find Vehicle)

```java
// Linear Search — O(n) — iterates ArrayList one by one
public Vehicle findVehicle(String registrationNumber) throws VehicleNotFoundException {
    String target = registrationNumber.toUpperCase().trim();
    for (Vehicle v : registeredVehicles) {           // for-each on ArrayList
        if (v.getRegistrationNumber().equals(target)) {
            return v;                                // found — return immediately
        }
    }
    throw new VehicleNotFoundException(registrationNumber); // not found
}
```

---

### 6.6 CRUD — Update (Vehicle Exit)

```java
public ParkingRecord vehicleExit(String registrationNumber) throws ParkingException {
    ParkingRecord record = getActiveRecord(registrationNumber); // HashMap.get() O(1)
    Vehicle vehicle      = findVehicle(registrationNumber);

    record.recordExit(vehicle.getVehicleType());    // sets exit time + charge

    int slotIndex = record.getSlotNumber() - 1;
    slots[slotIndex].vacate();                       // array access O(1)

    activeRecords.remove(registrationNumber.toUpperCase()); // HashMap.remove() O(1)
    // record stays in LinkedList history (audit trail — never deleted)
    return record;
}
```

---

### 6.7 CRUD — Delete (Remove Vehicle)

```java
public void removeVehicle(String registrationNumber) throws ParkingException {
    String target = registrationNumber.toUpperCase().trim();

    if (activeRecords.containsKey(target))       // can't remove a parked vehicle
        throw new ParkingException("Vehicle is currently parked.", "VEHICLE_PARKED");

    boolean removed = registeredVehicles.removeIf(   // ArrayList functional removal
        v -> v.getRegistrationNumber().equals(target));

    if (!removed)
        throw new VehicleNotFoundException(registrationNumber);
}
```

---

### 6.8 Searching

#### Linear Search — O(n)
```java
// Inside findVehicle() — checks each element one by one
for (Vehicle v : registeredVehicles) {
    if (v.getRegistrationNumber().equals(target)) return v;
}
```

#### Binary Search — O(log n)
```java
public int binarySearchVehicle(String registrationNumber) {
    // Step 1: Sort a COPY (binary search needs sorted data)
    ArrayList<Vehicle> sorted = new ArrayList<>(registeredVehicles);
    sorted.sort(Comparator.comparing(Vehicle::getRegistrationNumber));

    // Step 2: Binary search using Collections utility
    // Collections.binarySearch divides the list in half each step
    int idx = Collections.binarySearch(
        sorted,
        new Vehicle(registrationNumber, "", "TWO_WHEELER"), // dummy key
        Comparator.comparing(Vehicle::getRegistrationNumber)
    );
    return idx; // >= 0 means found; negative means not found
}
```

**Comparison:**
```
Linear Search:  1000 vehicles → up to 1000 comparisons
Binary Search:  1000 vehicles → up to 10 comparisons (log₂ 1000 ≈ 10)
```

---

### 6.9 Sorting

```java
// Sort history by entry time (ascending)
public List<ParkingRecord> sortHistoryByEntryTime() {
    List<ParkingRecord> sorted = new ArrayList<>(entryExitHistory); // copy
    sorted.sort(Comparator.comparing(ParkingRecord::getEntryTime));
    return sorted;
}

// Sort completed records by charge (descending — highest first)
public List<ParkingRecord> sortByChargeDescending() {
    List<ParkingRecord> completed = new ArrayList<>();
    for (ParkingRecord r : entryExitHistory)
        if (!r.isActive()) completed.add(r);

    completed.sort(Comparator.comparingDouble(ParkingRecord::getChargeAmount).reversed());
    return completed;
}

// Sort vehicles alphabetically by registration number
public List<Vehicle> sortVehiclesByRegistration() {
    List<Vehicle> sorted = new ArrayList<>(registeredVehicles);
    sorted.sort(Comparator.comparing(Vehicle::getRegistrationNumber));
    return sorted;
}
```

**How `Comparator` works:**
```
Comparator.comparing(Vehicle::getRegistrationNumber)
   → compares two Vehicle objects by their registration number (String comparison)
   → uses natural alphabetical order

.reversed()
   → flips the order (ascending becomes descending)
```

---

## 7. Part 4 — Validator Utility

> 📁 `src/parking/util/Validator.java`

```java
public final class Validator {

    // Private constructor — prevents "new Validator()" from outside
    private Validator() { throw new UnsupportedOperationException(); }

    // Validates Indian vehicle registration format: MH12AB1234
    public static boolean isValidRegistration(String regNum) {
        if (regNum == null) return false;
        return regNum.trim().toUpperCase().matches("[A-Z]{2}[0-9]{2}[A-Z]{1,2}[0-9]{4}");
    }

    // Validates slot number is within range
    public static boolean isValidSlotNumber(int slotNumber, int totalSlots) {
        return slotNumber >= 1 && slotNumber <= totalSlots;
    }

    // Validates contact number is exactly 10 digits
    public static boolean isValidContact(String contact) {
        if (contact == null) return false;
        return contact.trim().matches("[0-9]{10}");
    }

    // Formats double as currency: 123.5 → "₹123.50"
    public static String formatCurrency(double amount) {
        return String.format("₹%.2f", amount);
    }
}
```

**Regex patterns explained:**

| Pattern | Meaning | Example |
|---|---|---|
| `[A-Z]{2}` | Exactly 2 uppercase letters | `MH` |
| `[0-9]{2}` | Exactly 2 digits | `12` |
| `[A-Z]{1,2}` | 1 or 2 uppercase letters | `AB` |
| `[0-9]{4}` | Exactly 4 digits | `1234` |
| `[0-9]{10}` | Exactly 10 digits | `9876543210` |

**Concepts demonstrated:**
- `static` methods — call without creating an object
- `final` class — cannot be extended/subclassed
- `private` constructor — cannot be instantiated
- Regular expressions (regex) for pattern matching

---

## 8. Part 5 — Swing GUI

> 📁 `src/parking/ui/ParkingUI.java`

The GUI is built using Java Swing (javax.swing). It extends `JFrame` to become the main window and contains 6 tabs corresponding to all Expected Modules from the case study.

---

### 8.1 Class Declaration

```java
public class ParkingUI extends JFrame {
    // extends JFrame → our class IS-A JFrame (Inheritance)

    private final ParkingService service;  // reference to the model (MVC)

    // Table models — hold data for JTable displays
    private DefaultTableModel vehicleTableModel;
    private DefaultTableModel historyTableModel;
    // ...
}
```

---

### 8.2 Constructor & Window Setup

```java
public ParkingUI(ParkingService service) {
    this.service = service;
    initUI();
}

private void initUI() {
    setTitle("🚗 Smart Parking Management System");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1100, 750);
    setLocationRelativeTo(null);    // centres window on screen
    setLayout(new BorderLayout());  // root layout manager

    add(createHeaderPanel(), BorderLayout.NORTH);   // dark blue header

    JTabbedPane tabs = new JTabbedPane();            // tab container
    tabs.addTab("🏠 Dashboard",        createDashboardTab());
    tabs.addTab("🚗 Register Vehicle", createRegisterTab());
    tabs.addTab("📥 Vehicle Entry",    createEntryTab());
    tabs.addTab("📤 Vehicle Exit",     createExitTab());
    tabs.addTab("🔍 Search & Sort",    createSearchTab());
    tabs.addTab("📊 Report",           createReportTab());
    add(tabs, BorderLayout.CENTER);

    add(createStatusBar(), BorderLayout.SOUTH);
    setVisible(true);
    refreshAllTables();             // populate tables with sample data
}
```

---

### 8.3 Tab 1 — Dashboard

```java
private JPanel createDashboardTab() {
    // Stat cards: Total Slots | Available | Occupied
    JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
    statsPanel.add(createStatCard("Total Slots", "30", COLOR_ACCENT));
    statsPanel.add(createStatCard("Available",   "26", COLOR_SUCCESS));
    statsPanel.add(createStatCard("Occupied",    "4",  COLOR_WARNING));

    // Visual slot grid — each slot is a JLabel (green = free, red = occupied)
    slotLabels = new JLabel[total];
    for (int i = 0; i < total; i++) {
        JLabel lbl = new JLabel(String.valueOf(i + 1), SwingConstants.CENTER);
        lbl.setBackground(slot.isOccupied() ? RED : GREEN);
        lbl.setOpaque(true);
        slotGridPanel.add(lbl);
    }
}
```

**What it shows:**
- 3 stat cards (counts)
- 30 coloured boxes (green = available, orange = occupied)
- Live table of currently parked vehicles

---

### 8.4 Tab 2 — Register Vehicle

```java
// Form with GridBagLayout
JTextField tfRegNumber = new JTextField(15);
JTextField tfOwnerName = new JTextField(15);
JComboBox<String> cbVehicleType = new JComboBox<>(
    new String[]{"TWO_WHEELER", "FOUR_WHEELER", "HEAVY"});
JTextField tfContact = new JTextField(15);

// Button with Lambda ActionListener (event-driven programming)
JButton btnRegister = new JButton("✅ Register Vehicle");
btnRegister.addActionListener(e -> handleRegisterVehicle());

// Handler method — called when button is clicked
private void handleRegisterVehicle() {
    String reg = tfRegNumber.getText().trim().toUpperCase();

    if (!Validator.isValidRegistration(reg)) {   // validate format
        showError("Invalid registration number.");
        return;
    }
    try {
        Vehicle v = new Vehicle(reg, owner, type, contact);
        service.registerVehicle(v);              // CRUD — Create
        refreshVehicleTable();
        showSuccess("Vehicle registered!");
    } catch (ParkingException ex) {
        showError(ex.getMessage());
    }
}
```

---

### 8.5 Tab 3 — Vehicle Entry

```java
// Slot dropdown populated from TreeMap (automatically sorted)
cbSlot = new JComboBox<>();
for (Map.Entry<Integer, ParkingSlot> entry : service.getSortedSlots().entrySet()) {
    ParkingSlot s = entry.getValue();
    if (!s.isOccupied()) {                       // only show available slots
        cbSlot.addItem("Slot " + s.getSlotNumber() + " (" + s.getVehicleType() + ")");
    }
}

// Entry handler
private void handleVehicleEntry() {
    String reg     = tfEntryReg.getText().trim().toUpperCase();
    int    slotNum = parseSelectedSlot();        // extract slot number from combo

    try {
        ParkingRecord record = service.vehicleEntry(reg, slotNum); // service call
        refreshAllTables();                      // update all UI components
        showSuccess("Vehicle parked at Slot " + slotNum);
    } catch (ParkingException ex) {
        showError(ex.getMessage());              // display error popup
    }
}
```

---

### 8.6 Tab 4 — Vehicle Exit & Payment

```java
private void handleVehicleExit() {
    String reg = tfExitReg.getText().trim().toUpperCase();
    try {
        ParkingRecord record = service.vehicleExit(reg); // calculates charge
        refreshAllTables();
        // Display charge summary popup
        String msg = String.format(
            "Vehicle: %s\nDuration: %s\nCharge: %s",
            reg,
            record.getDurationString(),
            Validator.formatCurrency(record.getChargeAmount())
        );
        JOptionPane.showMessageDialog(this, msg, "Exit", JOptionPane.INFORMATION_MESSAGE);
    } catch (ParkingException ex) {
        showError(ex.getMessage());
    }
}
```

---

### 8.7 Tab 5 — Search & Sort

```java
// Linear Search button
btnLinear.addActionListener(e -> {
    try {
        Vehicle v = service.findVehicle(reg);      // O(n) linear search
        showSuccess("Found: " + v);
    } catch (ParkingException ex) {
        showError(ex.getMessage());
    }
});

// Binary Search button
btnBinary.addActionListener(e -> {
    int idx = service.binarySearchVehicle(reg);    // O(log n) binary search
    if (idx >= 0) showSuccess("Found at sorted index " + idx);
    else          showError("Not found. Index: " + idx);
});

// Sort buttons — each updates a JTextArea with sorted results
btnSortEntry.addActionListener(e -> {
    List<ParkingRecord> sorted = service.sortHistoryByEntryTime();
    StringBuilder sb = new StringBuilder();
    sorted.forEach(r -> sb.append(r).append("\n"));
    taSortResult.setText(sb.toString());
});
```

---

### 8.8 Custom Cell Renderer (Inner Class)

```java
// INNER CLASS — defined inside ParkingUI
private static class SlotStatusRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (!isSelected && column == 2) {        // "Status" column only
            String status = value.toString();
            setBackground("AVAILABLE".equals(status)
                ? new Color(0xC8E6C9)            // light green
                : new Color(0xFFCDD2));           // light red
        }
        return this;
    }
}
```

**Concepts demonstrated:**
- Inner class (class defined inside another class)
- Inheritance (`extends DefaultTableCellRenderer`)
- Polymorphism (JTable calls our overridden method at runtime)
- Method overriding (`@Override`)

---

## 9. Part 6 — Main Entry Point

> 📁 `src/parking/Main.java`

```java
public class Main {

    public static void main(String[] args) {
        // Set native OS look-and-feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Create the service with 30 slots
        ParkingService service = new ParkingService(30);

        // Load sample data (10 vehicles, 5 parking events)
        loadSampleData(service);

        // Launch GUI on the Event Dispatch Thread (thread safety for Swing)
        SwingUtilities.invokeLater(() -> new ParkingUI(service));
        //                          ↑ lambda implements Runnable
    }

    private static void loadSampleData(ParkingService service) {
        try {
            service.registerVehicle(new Vehicle("MH12AB1234", "Ramesh Patel", "FOUR_WHEELER", "9876543210"));
            // ... 9 more vehicles

            service.vehicleEntry("MH12AB1234", 11);
            // ... 4 more entries

            service.vehicleExit("KA03GH3456");    // one completed record
        } catch (ParkingException e) {
            System.err.println("Sample data error: " + e);
        }
    }
}
```

**Key concepts:**
- `main()` — JVM entry point, must be `public static void`
- `SwingUtilities.invokeLater()` — ensures GUI runs on the Event Dispatch Thread (EDT)
- Lambda `() -> new ParkingUI(service)` implements `Runnable`

---

## 10. Data Flow Diagram

```
USER ACTION (click button)
        │
        ▼
  ParkingUI.java               ← View (Swing components)
  (ActionListener fires)
        │
        ▼
  Validate input               ← Validator.java
        │
        ▼
  ParkingService.java          ← Business Logic
  ┌─────────────────────────────────────────────────┐
  │  ParkingSlot[]           (Array)                │
  │  ArrayList<Vehicle>      (Registered vehicles)  │
  │  LinkedList<ParkingRecord>(History log)         │
  │  HashMap<String,Record>  (Active parkings)      │
  │  TreeMap<Integer,Slot>   (Sorted slots)         │
  └─────────────────────────────────────────────────┘
        │
        ▼ (result / exception)
  ParkingUI.java
  (refreshes tables / shows dialog)
        │
        ▼
  USER SEES RESULT
```

---

## 11. Java Concepts Quick Reference

| Concept | File | Line(s) |
|---|---|---|
| Class definition | `Vehicle.java` | `public class Vehicle` |
| Object creation | `Main.java` | `new Vehicle(...)` |
| Constructor | `Vehicle.java` | `public Vehicle(...)` |
| Constructor chaining | `Vehicle.java` | `this(reg, owner, type, "N/A")` |
| Encapsulation | All model files | `private` fields + getters |
| `toString()` override | All model files | `@Override public String toString()` |
| Array | `ParkingService.java` | `ParkingSlot[] slots` |
| ArrayList | `ParkingService.java` | `ArrayList<Vehicle>` |
| LinkedList | `ParkingService.java` | `LinkedList<ParkingRecord>` |
| HashMap | `ParkingService.java` | `HashMap<String, ParkingRecord>` |
| TreeMap | `ParkingService.java` | `TreeMap<Integer, ParkingSlot>` |
| CRUD Create | `ParkingService.java` | `registerVehicle()`, `vehicleEntry()` |
| CRUD Read | `ParkingService.java` | `findVehicle()`, `getActiveRecords()` |
| CRUD Update | `ParkingService.java` | `processPayment()` |
| CRUD Delete | `ParkingService.java` | `removeVehicle()`, `vehicleExit()` |
| Linear Search | `ParkingService.java` | `findVehicle()` |
| Binary Search | `ParkingService.java` | `binarySearchVehicle()` |
| Sorting | `ParkingService.java` | `sortHistoryByEntryTime()` etc. |
| Comparator + Lambda | `ParkingService.java` | `Comparator.comparing(...)` |
| Custom Exception | `exception/` package | All 3 files |
| Exception Throwing | `ParkingService.java` | `throw new SlotUnavailableException(...)` |
| Exception Catching | `ParkingUI.java` | `catch (ParkingException ex)` |
| Validation | `Validator.java` | `isValidRegistration()` etc. |
| Regex | `Validator.java` | `.matches("[A-Z]{2}...")` |
| Static methods | `Validator.java` | All methods |
| Swing JFrame | `ParkingUI.java` | `extends JFrame` |
| Event listener | `ParkingUI.java` | `addActionListener(e -> ...)` |
| Inner class | `ParkingUI.java` | `SlotStatusRenderer` |
| Inheritance | `ParkingUI.java` | `extends JFrame`, `extends DefaultTableCellRenderer` |
| Polymorphism | `ParkingUI.java` | Overridden `getTableCellRendererComponent()` |
| java.time API | `ParkingRecord.java` | `LocalDateTime`, `Duration` |
| Switch expression | `ParkingRecord.java` | `switch (vehicleType) { ... }` |

---

## 12. Charge Calculation

```
Charge = max(duration_in_minutes / 60, 1.0) × rate_per_hour

Rates:
  TWO_WHEELER   →  ₹20/hour
  FOUR_WHEELER  →  ₹50/hour
  HEAVY         → ₹100/hour

Minimum: 1 hour is always billed, even if parked for 5 minutes.

Example:
  Vehicle type: FOUR_WHEELER
  Entry: 10:00 AM
  Exit:  12:45 PM
  Duration: 2h 45m = 2.75 hours
  Charge: 2.75 × ₹50 = ₹137.50
```

---

## 13. Sample Output (Console on startup)

```
✅ Sample data loaded successfully.

SLOT SUMMARY
  Total Slots : 30
  Available   : 26
  Occupied    : 4

REGISTERED VEHICLES (10)
  Vehicle[Reg=MH12AB1234 | Owner=Ramesh Patel   | Type=FOUR_WHEELER | Contact=9876543210]
  Vehicle[Reg=MH14CD5678 | Owner=Sunita Sharma  | Type=TWO_WHEELER  | Contact=9123456789]
  ...

ACTIVE PARKINGS (4)
  Record[Reg=MH12AB1234 | Slot=11 | Entry=05-Oct-2026 20:27 | Exit=STILL PARKED | Charge=₹0.00]
  ...

FULL HISTORY — sorted by entry time
  Record[Reg=KA03GH3456 | Slot=2 | Entry=... | Exit=... | Charge=₹20.00 | Paid=NO]
  ...
```

---

> **Developed by:** B.Tech CSE 2025-29 · ITM Skills University
> **Subject:** Java Programming · Case Study 15
> **Concepts:** OOP · Collections · Exception Handling · Swing GUI
