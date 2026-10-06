package parking.ui;

import parking.exception.ParkingException;
import parking.model.ParkingRecord;
import parking.model.ParkingSlot;
import parking.model.Vehicle;
import parking.service.ParkingService;
import parking.util.Validator;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * ============================================================
 * CONCEPT: Java Swing — GUI (Graphical User Interface)
 * ============================================================
 *
 * Swing is Java's built-in GUI toolkit (javax.swing package).
 * Key classes used:
 *
 *   JFrame         — The main application window (top-level container)
 *   JPanel         — A general-purpose container for grouping components
 *   JTabbedPane    — A container with clickable tabs (like browser tabs)
 *   JTable         — Displays data in rows and columns
 *   DefaultTableModel — Provides data to JTable (MVC pattern)
 *   JTextField     — Single-line text input box
 *   JComboBox      — Drop-down selection list
 *   JButton        — Clickable button that fires ActionEvents
 *   JTextArea      — Multi-line text display area
 *   JScrollPane    — Adds scroll bars to any component
 *   JLabel         — Non-editable text or icon
 *   JOptionPane    — Popup dialogs (info, warning, error, input)
 *
 * EVENT-DRIVEN PROGRAMMING
 * ─────────────────────────
 * Swing uses the Observer pattern via ActionListeners.
 * When the user clicks a button, an ActionEvent is fired
 * and the registered ActionListener.actionPerformed() method runs.
 * We use Lambda expressions as ActionListeners (Java 8+):
 *   button.addActionListener(e -> handleButtonClick());
 *
 * LAYOUT MANAGERS
 * ─────────────────────────
 * Layout managers position components automatically:
 *   BorderLayout  — divides panel into NORTH/SOUTH/EAST/WEST/CENTER
 *   GridLayout    — equal-sized grid of cells
 *   FlowLayout    — left-to-right flow (like words in a paragraph)
 *   GridBagLayout — flexible grid with per-cell constraints
 *
 * MVC IN SWING
 * ─────────────────────────
 * Model : ParkingService (data + business logic)
 * View  : ParkingUI (this class — what the user sees)
 * Controller: ActionListeners (bridge between user actions and model)
 * ============================================================
 */
public class ParkingUI extends JFrame {

    // ── Color scheme (constants for consistent styling) ─────────
    private static final Color COLOR_PRIMARY    = new Color(0x1A237E); // dark blue
    private static final Color COLOR_ACCENT     = new Color(0x0288D1); // sky blue
    private static final Color COLOR_SUCCESS    = new Color(0x2E7D32); // green
    private static final Color COLOR_WARNING    = new Color(0xE65100); // orange
    private static final Color COLOR_BG         = new Color(0xF5F5F5); // light grey
    private static final Color COLOR_WHITE      = Color.WHITE;
    private static final Font  FONT_TITLE       = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font  FONT_HEADER      = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font  FONT_BODY        = new Font("Segoe UI", Font.PLAIN, 13);

    // ── Service Layer (Model) ────────────────────────────────────
    private final ParkingService service;

    // ── Table Models — hold data for JTables ────────────────────
    private DefaultTableModel vehicleTableModel;
    private DefaultTableModel slotTableModel;
    private DefaultTableModel historyTableModel;
    private DefaultTableModel activeTableModel;

    // ── Input fields ─────────────────────────────────────────────
    // Registration fields
    private JTextField tfRegNumber, tfOwnerName, tfContact;
    private JComboBox<String> cbVehicleType;

    // Entry fields
    private JTextField tfEntryReg;
    private JComboBox<String> cbSlot;

    // Exit / payment fields
    private JTextField tfExitReg, tfPayReg, tfSearchReg;

    // Report area
    private JTextArea taReport;

    // ── Slot status panel (visual grid) ─────────────────────────
    private JPanel slotGridPanel;
    private JLabel[] slotLabels; // one label per slot

    // Date-time formatter
    private static final DateTimeFormatter DTF =
            DateTimeFormatter.ofPattern("dd-MMM HH:mm");

    /**
     * Constructor: sets up the entire UI.
     * @param service the parking service (model)
     */
    public ParkingUI(ParkingService service) {
        this.service = service;
        initUI();   // build and show the window
    }

    /** Initialises the JFrame and all child components. */
    private void initUI() {
        // ── JFrame setup ────────────────────────────────────────
        setTitle("🚗  Smart Parking Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // close app when window closes
        setSize(1100, 750);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null); // centre on screen
        setBackground(COLOR_BG);

        // ── Root layout ─────────────────────────────────────────
        // BorderLayout: NORTH = header, CENTER = tabbed pane
        setLayout(new BorderLayout(0, 0));

        // ── Header panel ────────────────────────────────────────
        add(createHeaderPanel(), BorderLayout.NORTH);

        // ── JTabbedPane: each tab = one module ──────────────────
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(FONT_HEADER);
        tabs.setBackground(COLOR_BG);

        // Add tabs corresponding to each Expected Module from the case study
        tabs.addTab("🏠 Dashboard",       createDashboardTab());
        tabs.addTab("🚗 Register Vehicle", createRegisterTab());
        tabs.addTab("📥 Vehicle Entry",    createEntryTab());
        tabs.addTab("📤 Vehicle Exit",     createExitTab());
        tabs.addTab("🔍 Search & Sort",    createSearchTab());
        tabs.addTab("📊 Report",           createReportTab());

        add(tabs, BorderLayout.CENTER);

        // ── Status bar at the bottom ─────────────────────────────
        add(createStatusBar(), BorderLayout.SOUTH);

        // Show the window
        setVisible(true);

        // Initial data load
        refreshAllTables();
    }

    // ════════════════════════════════════════════════════════════
    // ── HEADER PANEL ────────────────────────────────────────────
    // ════════════════════════════════════════════════════════════

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PRIMARY);
        header.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel title = new JLabel("🅿  Smart Parking Management System");
        title.setFont(FONT_TITLE);
        title.setForeground(COLOR_WHITE);

        JLabel subtitle = new JLabel("ITM Skills University — B.Tech CSE 2025-29  |  Java Programming");
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        subtitle.setForeground(new Color(0xBBDEFB)); // light blue

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(subtitle);
        header.add(textPanel, BorderLayout.CENTER);

        return header;
    }

    // ════════════════════════════════════════════════════════════
    // ── TAB 1: DASHBOARD ────────────────────────────────────────
    // ════════════════════════════════════════════════════════════

    /**
     * Dashboard shows:
     *  - Summary statistics (stat cards)
     *  - Visual slot grid (green = free, red = occupied)
     *  - Active parkings table
     */
    private JPanel createDashboardTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // ── Stat cards ──────────────────────────────────────────
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setOpaque(false);
        // (cards are updated in refreshDashboard())
        statsPanel.add(createStatCard("Total Slots", String.valueOf(service.getAllSlots().length), COLOR_ACCENT));
        statsPanel.add(createStatCard("Available",   String.valueOf(service.getAvailableSlotCount()), COLOR_SUCCESS));
        statsPanel.add(createStatCard("Occupied",    String.valueOf(service.getActiveRecords().size()), COLOR_WARNING));

        panel.add(statsPanel, BorderLayout.NORTH);

        // ── Slot grid (visual) ──────────────────────────────────
        JPanel gridContainer = new JPanel(new BorderLayout());
        gridContainer.setBackground(COLOR_WHITE);
        gridContainer.setBorder(createTitledBorder("Slot Availability Grid"));

        int total = service.getAllSlots().length;
        int cols  = 10; // 10 slots per row
        slotGridPanel = new JPanel(new GridLayout(0, cols, 5, 5));
        slotGridPanel.setBackground(COLOR_WHITE);
        slotGridPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        slotLabels = new JLabel[total];
        for (int i = 0; i < total; i++) {
            ParkingSlot s = service.getAllSlots()[i];
            JLabel lbl = new JLabel(String.valueOf(s.getSlotNumber()), SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setOpaque(true);
            lbl.setBackground(s.isOccupied() ? COLOR_WARNING : COLOR_SUCCESS);
            lbl.setForeground(COLOR_WHITE);
            lbl.setBorder(new LineBorder(Color.GRAY, 1));
            lbl.setToolTipText(s.toString()); // tooltip on hover
            slotLabels[i] = lbl;
            slotGridPanel.add(lbl);
        }

        JScrollPane gridScroll = new JScrollPane(slotGridPanel);
        gridContainer.add(gridScroll, BorderLayout.CENTER);

        // Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legend.setBackground(COLOR_WHITE);
        legend.add(createColorBox(COLOR_SUCCESS)); legend.add(new JLabel(" Available  "));
        legend.add(createColorBox(COLOR_WARNING)); legend.add(new JLabel(" Occupied"));
        gridContainer.add(legend, BorderLayout.SOUTH);

        panel.add(gridContainer, BorderLayout.CENTER);

        // ── Active Records table ─────────────────────────────────
        String[] activeCols = {"Reg. Number", "Slot", "Entry Time", "Duration", "Vehicle Type"};
        activeTableModel = new DefaultTableModel(activeCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; } // read-only
        };
        JTable activeTable = createStyledTable(activeTableModel);
        JScrollPane activeScroll = new JScrollPane(activeTable);
        activeScroll.setBorder(createTitledBorder("Currently Parked Vehicles"));
        activeScroll.setPreferredSize(new Dimension(0, 200));
        panel.add(activeScroll, BorderLayout.SOUTH);

        // Refresh button
        JButton btnRefresh = createStyledButton("🔄 Refresh Dashboard", COLOR_ACCENT);
        btnRefresh.addActionListener(e -> refreshAllTables()); // lambda ActionListener
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(btnRefresh);
        gridContainer.add(btnPanel, BorderLayout.NORTH);

        return panel;
    }

    // ════════════════════════════════════════════════════════════
    // ── TAB 2: VEHICLE REGISTRATION ─────────────────────────────
    // ════════════════════════════════════════════════════════════

    /**
     * CONCEPT: CRUD — Create
     * Allows registering a new vehicle (adds to ArrayList<Vehicle>).
     * Also demonstrates: input validation, JTable with DefaultTableModel.
     */
    private JPanel createRegisterTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // ── Form panel ──────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(COLOR_WHITE);
        form.setBorder(createTitledBorder("Register New Vehicle"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Registration Number
        addFormRow(form, gbc, 0, "Registration Number:", tfRegNumber = new JTextField(15));
        // Owner Name
        addFormRow(form, gbc, 1, "Owner Name:", tfOwnerName = new JTextField(15));
        // Vehicle Type drop-down
        cbVehicleType = new JComboBox<>(new String[]{"TWO_WHEELER", "FOUR_WHEELER", "HEAVY"});
        cbVehicleType.setFont(FONT_BODY);
        addFormRow(form, gbc, 2, "Vehicle Type:", cbVehicleType);
        // Contact Number
        addFormRow(form, gbc, 3, "Contact Number:", tfContact = new JTextField(15));

        // Buttons
        JButton btnRegister = createStyledButton("✅ Register Vehicle", COLOR_SUCCESS);
        JButton btnClear    = createStyledButton("🗑 Clear", new Color(0x757575));
        JButton btnRemove   = createStyledButton("❌ Remove Vehicle", COLOR_WARNING);

        // ── ActionListener: Register ─────────────────────────────
        btnRegister.addActionListener(e -> handleRegisterVehicle());
        btnClear.addActionListener(e -> clearRegisterForm());
        btnRemove.addActionListener(e -> handleRemoveVehicle());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.setOpaque(false);
        btnPanel.add(btnRegister);
        btnPanel.add(btnClear);
        btnPanel.add(btnRemove);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        form.add(btnPanel, gbc);

        panel.add(form, BorderLayout.NORTH);

        // ── Registered Vehicles Table ────────────────────────────
        String[] cols = {"Registration No.", "Owner Name", "Vehicle Type", "Contact"};
        vehicleTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable vehicleTable = createStyledTable(vehicleTableModel);
        JScrollPane scroll = new JScrollPane(vehicleTable);
        scroll.setBorder(createTitledBorder("Registered Vehicles"));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ── Handler: Register Vehicle ────────────────────────────────
    private void handleRegisterVehicle() {
        String reg     = tfRegNumber.getText().trim().toUpperCase();
        String owner   = tfOwnerName.getText().trim();
        String type    = (String) cbVehicleType.getSelectedItem();
        String contact = tfContact.getText().trim();

        // ── Validation ───────────────────────────────────────────
        if (!Validator.isValidRegistration(reg)) {
            showError("Invalid registration number.\nFormat: 2 letters + 2 digits + 1-2 letters + 4 digits\nExample: MH12AB1234");
            return;
        }
        if (owner.isBlank()) { showError("Owner name cannot be empty."); return; }
        if (!Validator.isValidContact(contact)) {
            showError("Contact number must be exactly 10 digits."); return;
        }

        try {
            Vehicle v = new Vehicle(reg, owner, type, contact);
            service.registerVehicle(v);                      // CRUD — Create
            refreshVehicleTable();
            showSuccess("Vehicle " + reg + " registered successfully!");
            clearRegisterForm();
        } catch (ParkingException ex) {
            showError(ex.getMessage());
        }
    }

    private void handleRemoveVehicle() {
        String reg = JOptionPane.showInputDialog(this,
            "Enter Registration Number to remove:", "Remove Vehicle",
            JOptionPane.WARNING_MESSAGE);
        if (reg == null || reg.isBlank()) return;
        try {
            service.removeVehicle(reg.trim());               // CRUD — Delete
            refreshVehicleTable();
            showSuccess("Vehicle " + reg.toUpperCase() + " removed.");
        } catch (ParkingException ex) {
            showError(ex.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════
    // ── TAB 3: VEHICLE ENTRY ────────────────────────────────────
    // ════════════════════════════════════════════════════════════

    private JPanel createEntryTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(COLOR_WHITE);
        form.setBorder(createTitledBorder("Vehicle Entry — Assign Parking Slot"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tfEntryReg = new JTextField(15);
        addFormRow(form, gbc, 0, "Registration Number:", tfEntryReg);

        // Build slot drop-down from TreeMap (sorted order)
        cbSlot = new JComboBox<>();
        cbSlot.setFont(FONT_BODY);
        refreshSlotCombo();  // populate with available slots
        addFormRow(form, gbc, 1, "Select Slot:", cbSlot);

        JButton btnEntry = createStyledButton("📥 Record Entry", COLOR_SUCCESS);
        btnEntry.addActionListener(e -> handleVehicleEntry());

        JButton btnRefreshSlots = createStyledButton("🔄 Refresh Slots", COLOR_ACCENT);
        btnRefreshSlots.addActionListener(e -> refreshSlotCombo());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnEntry); btnPanel.add(btnRefreshSlots);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        form.add(btnPanel, gbc);

        panel.add(form, BorderLayout.NORTH);

        // Slot status table
        String[] cols = {"Slot No.", "Vehicle Type", "Status"};
        slotTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable slotTable = createStyledTable(slotTableModel);
        // Custom cell renderer to colour rows
        slotTable.setDefaultRenderer(Object.class, new SlotStatusRenderer());
        JScrollPane scroll = new JScrollPane(slotTable);
        scroll.setBorder(createTitledBorder("All Slots (sorted by number — TreeMap)"));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void handleVehicleEntry() {
        String reg = tfEntryReg.getText().trim().toUpperCase();
        if (reg.isBlank()) { showError("Enter registration number."); return; }

        Object selectedSlot = cbSlot.getSelectedItem();
        if (selectedSlot == null) { showError("No available slots. Parking lot is full!"); return; }

        int slotNum;
        try {
            slotNum = Integer.parseInt(selectedSlot.toString().split(" ")[1]);
        } catch (Exception ex) {
            showError("Please select a valid slot."); return;
        }

        try {
            ParkingRecord record = service.vehicleEntry(reg, slotNum); // CRUD — Create record
            refreshAllTables();
            showSuccess("Vehicle " + reg + " parked at Slot " + slotNum + "\nEntry Time: " + record.getEntryTime().format(DTF));
            tfEntryReg.setText("");
        } catch (ParkingException ex) {
            showError(ex.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════
    // ── TAB 4: VEHICLE EXIT ─────────────────────────────────────
    // ════════════════════════════════════════════════════════════

    private JPanel createExitTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Exit form
        JPanel exitForm = new JPanel(new GridBagLayout());
        exitForm.setBackground(COLOR_WHITE);
        exitForm.setBorder(createTitledBorder("Vehicle Exit & Charge Calculation"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tfExitReg = new JTextField(15);
        addFormRow(exitForm, gbc, 0, "Registration Number:", tfExitReg);

        JButton btnExit = createStyledButton("📤 Process Exit & Calculate Charge", COLOR_WARNING);
        btnExit.addActionListener(e -> handleVehicleExit());

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        exitForm.add(btnExit, gbc);

        panel.add(exitForm, BorderLayout.NORTH);

        // Payment form
        JPanel payForm = new JPanel(new GridBagLayout());
        payForm.setBackground(COLOR_WHITE);
        payForm.setBorder(createTitledBorder("Payment / Billing"));
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(10, 12, 10, 12);
        gbc2.fill = GridBagConstraints.HORIZONTAL;

        tfPayReg = new JTextField(15);
        addFormRow(payForm, gbc2, 0, "Registration Number:", tfPayReg);

        JButton btnPay = createStyledButton("💳 Mark Payment Done", COLOR_SUCCESS);
        btnPay.addActionListener(e -> handlePayment());

        gbc2.gridx = 0; gbc2.gridy = 1; gbc2.gridwidth = 2;
        payForm.add(btnPay, gbc2);

        JPanel formsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        formsPanel.setOpaque(false);
        formsPanel.add(exitForm);
        formsPanel.add(payForm);
        panel.add(formsPanel, BorderLayout.NORTH);

        // History table
        String[] cols = {"Reg. Number", "Slot", "Entry", "Exit", "Duration", "Charge", "Paid"};
        historyTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable historyTable = createStyledTable(historyTableModel);
        JScrollPane scroll = new JScrollPane(historyTable);
        scroll.setBorder(createTitledBorder("Entry/Exit History (LinkedList — most recent first)"));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void handleVehicleExit() {
        String reg = tfExitReg.getText().trim().toUpperCase();
        if (reg.isBlank()) { showError("Enter registration number."); return; }

        try {
            ParkingRecord record = service.vehicleExit(reg);  // CRUD — Update + Delete from active
            refreshAllTables();
            String msg = String.format(
                "✅ Exit Processed!\n\nVehicle     : %s\nSlot        : %d\nDuration    : %s\nCharge      : %s\n\nPlease proceed to payment counter.",
                reg, record.getSlotNumber(), record.getDurationString(),
                Validator.formatCurrency(record.getChargeAmount()));
            JOptionPane.showMessageDialog(this, msg, "Exit Successful", JOptionPane.INFORMATION_MESSAGE);
            tfExitReg.setText("");
        } catch (ParkingException ex) {
            showError(ex.getMessage());
        }
    }

    private void handlePayment() {
        String reg = tfPayReg.getText().trim().toUpperCase();
        if (reg.isBlank()) { showError("Enter registration number."); return; }
        try {
            // Find the last record for this vehicle in history
            for (ParkingRecord r : service.getHistory()) {
                if (r.getRegistrationNumber().equals(reg) && !r.isActive()) {
                    r.markPaymentDone();                         // CRUD — Update
                    refreshAllTables();
                    showSuccess("Payment recorded for " + reg + ". Amount: " +
                        Validator.formatCurrency(r.getChargeAmount()));
                    tfPayReg.setText("");
                    return;
                }
            }
            showError("No completed (exited) record found for " + reg);
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════
    // ── TAB 5: SEARCH & SORT ────────────────────────────────────
    // ════════════════════════════════════════════════════════════

    /**
     * CONCEPT: Linear Search, Binary Search, Sorting with Comparators
     */
    private JPanel createSearchTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // ── Search panel ─────────────────────────────────────────
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(COLOR_WHITE);
        searchPanel.setBorder(createTitledBorder("Search Vehicle by Registration Number"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tfSearchReg = new JTextField(15);
        addFormRow(searchPanel, gbc, 0, "Registration Number:", tfSearchReg);

        JButton btnLinear = createStyledButton("🔍 Linear Search (O(n))", COLOR_ACCENT);
        JButton btnBinary = createStyledButton("⚡ Binary Search (O(log n))", COLOR_SUCCESS);

        // Linear Search — iterates ArrayList one by one
        btnLinear.addActionListener(e -> {
            String reg = tfSearchReg.getText().trim().toUpperCase();
            if (reg.isBlank()) { showError("Enter a registration number."); return; }
            try {
                Vehicle v = service.findVehicle(reg); // linear search inside
                showSuccess("LINEAR SEARCH — Vehicle Found!\n\n" + v);
            } catch (ParkingException ex) {
                showError("LINEAR SEARCH — " + ex.getMessage());
            }
        });

        // Binary Search — sorts list first, then binary search
        btnBinary.addActionListener(e -> {
            String reg = tfSearchReg.getText().trim().toUpperCase();
            if (reg.isBlank()) { showError("Enter a registration number."); return; }
            int idx = service.binarySearchVehicle(reg);
            if (idx >= 0) {
                showSuccess("BINARY SEARCH — Found at sorted index " + idx + "!\nRegistration: " + reg);
            } else {
                showError("BINARY SEARCH — Vehicle '" + reg + "' not found.\n(Index returned: " + idx + ")");
            }
        });

        JPanel searchBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        searchBtns.setOpaque(false);
        searchBtns.add(btnLinear);
        searchBtns.add(btnBinary);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        searchPanel.add(searchBtns, gbc);

        // ── Sort panel ───────────────────────────────────────────
        JPanel sortPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        sortPanel.setBackground(COLOR_BG);
        sortPanel.setBorder(createTitledBorder("Sort Records"));

        JTextArea taSortResult = new JTextArea(10, 30);
        taSortResult.setFont(new Font("Courier New", Font.PLAIN, 12));
        taSortResult.setEditable(false);
        JScrollPane sortScroll = new JScrollPane(taSortResult);

        JButton btnSortEntry = createStyledButton("📅 Sort by Entry Time ↑", COLOR_PRIMARY);
        JButton btnSortCharge = createStyledButton("💰 Sort by Charge ↓", COLOR_WARNING);
        JButton btnSortReg = createStyledButton("🔤 Sort by Reg. No.", COLOR_ACCENT);

        // Sort by entry time (ascending)
        btnSortEntry.addActionListener(e -> {
            List<ParkingRecord> sorted = service.sortHistoryByEntryTime();
            StringBuilder sb = new StringBuilder("SORTED BY ENTRY TIME (ascending):\n\n");
            sorted.forEach(r -> sb.append(r).append("\n"));
            taSortResult.setText(sb.toString());
        });

        // Sort by charge (descending)
        btnSortCharge.addActionListener(e -> {
            List<ParkingRecord> sorted = service.sortByChargeDescending();
            StringBuilder sb = new StringBuilder("SORTED BY CHARGE (highest first):\n\n");
            sorted.forEach(r -> sb.append(r).append("\n"));
            taSortResult.setText(sb.toString());
        });

        // Sort vehicles by reg number
        btnSortReg.addActionListener(e -> {
            List<Vehicle> sorted = service.sortVehiclesByRegistration();
            StringBuilder sb = new StringBuilder("VEHICLES SORTED BY REGISTRATION NO.:\n\n");
            sorted.forEach(v -> sb.append(v).append("\n"));
            taSortResult.setText(sb.toString());
        });

        sortPanel.add(btnSortEntry);
        sortPanel.add(btnSortCharge);
        sortPanel.add(btnSortReg);

        panel.add(searchPanel, BorderLayout.NORTH);

        JPanel midPanel = new JPanel(new BorderLayout(10, 10));
        midPanel.setOpaque(false);
        midPanel.add(sortPanel, BorderLayout.NORTH);
        midPanel.add(sortScroll, BorderLayout.CENTER);
        panel.add(midPanel, BorderLayout.CENTER);

        return panel;
    }

    // ════════════════════════════════════════════════════════════
    // ── TAB 6: REPORT ───────────────────────────────────────────
    // ════════════════════════════════════════════════════════════

    private JPanel createReportTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        taReport = new JTextArea();
        taReport.setFont(new Font("Courier New", Font.PLAIN, 12));
        taReport.setEditable(false);
        taReport.setBackground(new Color(0xFAFAFA));
        JScrollPane scroll = new JScrollPane(taReport);
        scroll.setBorder(createTitledBorder("Parking Report"));

        JButton btnGenerate = createStyledButton("📊 Generate Report", COLOR_PRIMARY);
        JButton btnClear    = createStyledButton("🗑 Clear", new Color(0x757575));
        btnGenerate.addActionListener(e -> taReport.setText(service.generateReport()));
        btnClear.addActionListener(e -> taReport.setText(""));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnGenerate);
        btnPanel.add(btnClear);

        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ════════════════════════════════════════════════════════════
    // ── HELPER: REFRESH ALL TABLES ──────────────────────────────
    // ════════════════════════════════════════════════════════════

    /** Refreshes every table model and visual component. */
    private void refreshAllTables() {
        refreshVehicleTable();
        refreshSlotTable();
        refreshHistoryTable();
        refreshActiveTable();
        refreshSlotGrid();
        refreshSlotCombo();
    }

    private void refreshVehicleTable() {
        if (vehicleTableModel == null) return;
        vehicleTableModel.setRowCount(0); // clear existing rows
        for (Vehicle v : service.getAllVehicles()) {
            vehicleTableModel.addRow(new Object[]{
                v.getRegistrationNumber(), v.getOwnerName(),
                v.getVehicleType(), v.getContactNumber()
            });
        }
    }

    private void refreshSlotTable() {
        if (slotTableModel == null) return;
        slotTableModel.setRowCount(0);
        // TreeMap iteration → automatically sorted by slot number
        for (Map.Entry<Integer, ParkingSlot> entry : service.getSortedSlots().entrySet()) {
            ParkingSlot s = entry.getValue();
            slotTableModel.addRow(new Object[]{
                s.getSlotNumber(), s.getVehicleType(),
                s.isOccupied() ? "OCCUPIED" : "AVAILABLE"
            });
        }
    }

    private void refreshHistoryTable() {
        if (historyTableModel == null) return;
        historyTableModel.setRowCount(0);
        for (ParkingRecord r : service.getHistory()) { // LinkedList iteration
            historyTableModel.addRow(new Object[]{
                r.getRegistrationNumber(),
                r.getSlotNumber(),
                r.getEntryTime().format(DTF),
                r.isActive() ? "—" : r.getExitTime().format(DTF),
                r.getDurationString(),
                Validator.formatCurrency(r.getChargeAmount()),
                r.isPaymentDone() ? "✅" : "❌"
            });
        }
    }

    private void refreshActiveTable() {
        if (activeTableModel == null) return;
        activeTableModel.setRowCount(0);
        try {
            for (ParkingRecord r : service.getActiveRecords()) {
                Vehicle v = service.findVehicle(r.getRegistrationNumber());
                activeTableModel.addRow(new Object[]{
                    r.getRegistrationNumber(),
                    r.getSlotNumber(),
                    r.getEntryTime().format(DTF),
                    r.getDurationString(),
                    v.getVehicleType()
                });
            }
        } catch (ParkingException ignored) {}
    }

    private void refreshSlotGrid() {
        if (slotLabels == null) return;
        ParkingSlot[] allSlots = service.getAllSlots();
        for (int i = 0; i < allSlots.length && i < slotLabels.length; i++) {
            slotLabels[i].setBackground(allSlots[i].isOccupied() ? COLOR_WARNING : COLOR_SUCCESS);
            slotLabels[i].setToolTipText(allSlots[i].toString());
        }
        if (slotGridPanel != null) slotGridPanel.repaint();
    }

    private void refreshSlotCombo() {
        if (cbSlot == null) return;
        cbSlot.removeAllItems();
        for (Map.Entry<Integer, ParkingSlot> entry : service.getSortedSlots().entrySet()) {
            ParkingSlot s = entry.getValue();
            if (!s.isOccupied()) {
                cbSlot.addItem("Slot " + s.getSlotNumber() + " (" + s.getVehicleType() + ")");
            }
        }
    }

    // ════════════════════════════════════════════════════════════
    // ── UI HELPER METHODS ───────────────────────────────────────
    // ════════════════════════════════════════════════════════════

    /** Creates a styled JButton with custom colours. */
    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setBackground(bg);
        btn.setForeground(COLOR_WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
            new LineBorder(bg.darker(), 1),
            new EmptyBorder(6, 14, 6, 14)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Hover effect using mouse listener
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    /** Creates a stat card panel (used in dashboard). */
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(COLOR_WHITE);

        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 40));
        lblValue.setForeground(COLOR_WHITE);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    /** Creates a titled border for panels. */
    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_ACCENT, 1),
            " " + title + " ",
            TitledBorder.LEFT, TitledBorder.TOP,
            FONT_HEADER, COLOR_PRIMARY);
        return border;
    }

    /** Creates a styled JTable with alternating row colours. */
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(FONT_BODY);
        table.setRowHeight(26);
        table.getTableHeader().setFont(FONT_HEADER);
        table.getTableHeader().setBackground(COLOR_PRIMARY);
        table.getTableHeader().setForeground(COLOR_WHITE);
        table.setGridColor(new Color(0xE0E0E0));
        table.setSelectionBackground(new Color(0xBBDEFB));
        // Alternating row renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) setBackground(row % 2 == 0 ? COLOR_WHITE : new Color(0xF3F4F6));
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
        return table;
    }

    /** Adds a label + component pair to a form using GridBagLayout. */
    private void addFormRow(JPanel panel, GridBagConstraints gbc,
                            int row, String labelText, JComponent field) {
        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = row;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(FONT_BODY);
        panel.add(lbl, gbc);
        gbc.gridx = 1;
        if (field instanceof JTextField) ((JTextField) field).setFont(FONT_BODY);
        panel.add(field, gbc);
    }

    /** Small coloured square for legend. */
    private JPanel createColorBox(Color color) {
        JPanel box = new JPanel();
        box.setBackground(color);
        box.setPreferredSize(new Dimension(18, 18));
        return box;
    }

    /** Status bar at the bottom of the window. */
    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bar.setBackground(COLOR_PRIMARY);
        bar.setBorder(new EmptyBorder(3, 10, 3, 10));
        JLabel lbl = new JLabel("ITM Skills University — Smart Parking System v1.0  |  B.Tech CSE 2025-29");
        lbl.setForeground(new Color(0xBBDEFB));
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        bar.add(lbl);
        return bar;
    }

    /** Show an error dialog. */
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Show a success dialog. */
    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Clear all fields in the registration form. */
    private void clearRegisterForm() {
        tfRegNumber.setText(""); tfOwnerName.setText(""); tfContact.setText("");
        cbVehicleType.setSelectedIndex(0);
    }

    // ════════════════════════════════════════════════════════════
    // ── INNER CLASS: Custom Cell Renderer ───────────────────────
    // ════════════════════════════════════════════════════════════

    /**
     * CONCEPT: Inner Class | Inheritance | Polymorphism
     * ──────────────────────────────────────────────────
     * SlotStatusRenderer extends DefaultTableCellRenderer
     * (which implements TableCellRenderer — an interface).
     *
     * By overriding getTableCellRendererComponent() we customise
     * how each cell is drawn — green for available, red for occupied.
     *
     * This is POLYMORPHISM: the JTable holds a TableCellRenderer
     * reference but calls OUR overridden method at runtime.
     * ──────────────────────────────────────────────────
     */
    private static class SlotStatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected && column == 2) { // "Status" column
                String status = value != null ? value.toString() : "";
                setBackground("AVAILABLE".equals(status) ? new Color(0xC8E6C9)  // light green
                                                         : new Color(0xFFCDD2)); // light red
                setForeground("AVAILABLE".equals(status) ? COLOR_SUCCESS : COLOR_WARNING);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
            } else if (!isSelected) {
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF3F4F6));
                setForeground(Color.BLACK);
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
            }
            setBorder(new EmptyBorder(0, 8, 0, 8));
            return this;
        }
    }
}
