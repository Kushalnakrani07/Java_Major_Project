# 🚗 Smart Parking Management System

[![Java Version](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![GUI](https://img.shields.io/badge/GUI-Java%20Swing-007396?style=for-the-badge&logo=java&logoColor=white)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![Architecture](https://img.shields.io/badge/Architecture-MVC%20Pattern-2ea44f?style=for-the-badge)](#-system-architecture)
[![License](https://img.shields.io/badge/Academic-B.Tech%20CSE%20Case%20Study-blue?style=for-the-badge)](#-academic-context)

> A desktop-based parking management system developed in Core Java and Java Swing. Features real-time slot tracking, vehicle registration, automated fee calculation, multiple search/sort algorithms, custom exception handling, and input validation.

---

## 📌 Table of Contents

- [Academic Context](#-academic-context)
- [Key Features](#-key-features)
- [System Architecture](#-system-architecture)
- [Core Java Concepts & Implementation](#-core-java-concepts--implementation)
- [Data Structures Breakdown](#-data-structures-breakdown)
- [Pricing & Billing Rules](#-pricing--billing-rules)
- [Project Structure](#-project-structure)
- [Installation & How to Run](#-installation--how-to-run)
- [GUI Modules (Tabs)](#-gui-modules-tabs)
- [Viva & Technical Interview Guide](#-viva--technical-interview-guide)

---

## 🎓 Academic Context

- **Program:** B.Tech Computer Science & Engineering (2025–2029)
- **Course:** Java Programming (Semester III)
- **Institution:** ITM Skills University
- **Problem Statement:** Case Study 15 — Smart Parking Management System

---

## 🌟 Key Features

- **Real-Time Visual Grid:** Color-coded slot availability matrix (Green = Available, Red/Orange = Occupied).
- **Automated Billing:** Uses Java Date-Time API (`LocalDateTime` & `Duration`) with hourly rates and minimum 1-hour charge.
- **Dual Search Support:** Fast linear search for arbitrary queries and binary search ($O(\log n)$) over sorted records.
- **Multi-Criteria Sorting:** Sort entry/exit history by arrival time, billing amount (descending), or registration number.
- **Robust Exception Handling:** Custom domain exceptions prevent runtime crashes and trigger informative Swing modals.
- **Strict Input Validation:** Regex-based validation for license plate formats, mobile numbers, and slot bounds.

---

## 🏛 System Architecture

The application follows the **Model-View-Controller (MVC)** architectural design:

```
                  ┌────────────────────────┐
                  │      ParkingUI         │  <--- [VIEW]
                  │   (Java Swing GUI)     │       User interaction & forms
                  └───────────┬────────────┘
                              │
                    User Actions / Events
                              ▼
                  ┌────────────────────────┐
                  │     ParkingService     │  <--- [CONTROLLER / SERVICE]
                  │   (Business Logic)     │       Coordinates data structures & rules
                  └───────────┬────────────┘
                              │
                    Manipulates / Queries
                              ▼
        ┌──────────────────────────────────────────────┐
        │                 [MODELS]                     │
        │  • Vehicle       • ParkingSlot               │
        │  • ParkingRecord • Validator                 │
        └──────────────────────────────────────────────┘
```

---

## 🧠 Core Java Concepts & Implementation

| Java Concept | Application in Code | Source File |
|---|---|---|
| **Classes & Objects** | Blueprint definitions for vehicles, parking slots, and parking tickets | [`Vehicle.java`](src/parking/model/Vehicle.java), [`ParkingSlot.java`](src/parking/model/ParkingSlot.java) |
| **Constructors** | Object initialization and constructor overloading (`this(...)` chaining) | [`Vehicle.java`](src/parking/model/Vehicle.java) |
| **Encapsulation** | `private` instance attributes accessed safely via `public` getters/setters | All Model classes |
| **Fixed Array** | Fixed-size allocation representing the 30 physical parking bays | [`ParkingService.java`](src/parking/service/ParkingService.java) |
| **ArrayList** | Dynamically-sized registry of registered vehicles | [`ParkingService.java`](src/parking/service/ParkingService.java) |
| **LinkedList** | Activity ledger with $O(1)$ head insertion (`addFirst`) for recent event logs | [`ParkingService.java`](src/parking/service/ParkingService.java) |
| **HashMap** | Key-Value mapping (`Registration Number -> ParkingRecord`) for $O(1)$ lookups | [`ParkingService.java`](src/parking/service/ParkingService.java) |
| **TreeMap** | Self-balancing Red-Black Tree maintaining slots sorted by slot number | [`ParkingService.java`](src/parking/service/ParkingService.java) |
| **CRUD Operations** | Complete lifecycle: Register, Search, Update payment status, and Vacate | [`ParkingService.java`](src/parking/service/ParkingService.java) |
| **Searching** | Linear search ($O(n)$) and Binary Search ($O(\log n)$) implementations | [`ParkingService.java`](src/parking/service/ParkingService.java) |
| **Sorting** | Custom Comparator lambdas sorting by timestamps, charge, and license plates | [`ParkingService.java`](src/parking/service/ParkingService.java) |
| **Exception Handling** | Custom checked exception hierarchy preventing application crashes | [`exception/`](src/parking/exception/) |
| **Regex Validation** | Input sanitization using regular expressions | [`Validator.java`](src/parking/util/Validator.java) |
| **Java Swing GUI** | Desktop GUI built on Swing's Event Dispatch Thread (`EDT`) | [`ParkingUI.java`](src/parking/ui/ParkingUI.java) |

---

## 📊 Data Structures Breakdown

Why each specific data structure was selected:

### 1. `ParkingSlot[]` (Fixed Array)
* **Purpose:** Represents the fixed physical layout of the parking lot (30 slots).
* **Rationale:** A physical facility cannot spontaneously grow slots. Arrays provide instantaneous $O(1)$ index-based access (`slots[slotNum - 1]`).

### 2. `ArrayList<Vehicle>`
* **Purpose:** Stores registered vehicles.
* **Rationale:** The total count of registered customers is dynamic. `ArrayList` dynamically resizes as users register and provides predictable iteration order.

### 3. `LinkedList<ParkingRecord>`
* **Purpose:** Historical entry/exit activity log.
* **Rationale:** New events are inserted at the head (`addFirst()`) so the latest movements appear first. `LinkedList` performs head insertion in $O(1)$ without memory reallocation or element shifting.

### 4. `HashMap<String, ParkingRecord>`
* **Purpose:** Active parkings lookup.
* **Rationale:** When a vehicle exits, finding its active record by plate number (e.g. `MH12AB1234`) must be immediate. `HashMap` provides $O(1)$ average-case retrieval.

### 5. `TreeMap<Integer, ParkingSlot>`
* **Purpose:** Numerical slot directory.
* **Rationale:** Internally backed by a Red-Black Tree, `TreeMap` maintains keys in ascending sorted order automatically ($1, 2, 3, \dots, 30$), eliminating the need for sorting before populating UI dropdowns.

---

## 💰 Pricing & Billing Rules

When a vehicle exits, the fee is computed via Java's `Duration` class:

$$\text{Billed Hours} = \max\left(\frac{\text{Duration in Minutes}}{60.0}, 1.0\right)$$

$$\text{Total Fee} = \text{Billed Hours} \times \text{Hourly Rate}$$

| Vehicle Category | Assigned Slots | Hourly Rate | Minimum Charge |
|---|---|---|---|
| **Two-Wheeler** (Bikes / Scooters) | Slots 1 – 10 | **₹20.00 / hr** | ₹20.00 |
| **Four-Wheeler** (Cars / Sedans / SUVs) | Slots 11 – 20 | **₹50.00 / hr** | ₹50.00 |
| **Heavy Vehicle** (Trucks / Buses) | Slots 21 – 30 | **₹100.00 / hr** | ₹100.00 |

---

## 📁 Project Structure

```
SmartParkingSystem/
├── README.md                                  # Documentation
├── .gitignore                                 # Git ignore file
├── src/
│   └── parking/
│       ├── Main.java                          # Bootstrap and sample data seeding
│       ├── exception/                         # Custom Exception classes
│       │   ├── ParkingException.java          # Base domain exception
│       │   ├── SlotUnavailableException.java  # Thrown when slot is full or invalid
│       │   └── VehicleNotFoundException.java  # Thrown when plate number is unlisted
│       ├── model/                             # Domain Entity models
│       │   ├── Vehicle.java                   # Vehicle entity & constructor overloading
│       │   ├── ParkingSlot.java               # Bay entity with occupancy flags
│       │   └── ParkingRecord.java             # Entry/exit session & billing calculation
│       ├── service/                           # Business logic layer
│       │   └── ParkingService.java            # Data structures, CRUD, algorithms
│       ├── ui/                                # Graphical interface
│       │   └── ParkingUI.java                 # Swing JFrame with 6 modular tabs
│       └── util/                              # Utilities
│           └── Validator.java                 # Static regex validators & formatters
```

---

## 🚀 Installation & How to Run

### Prerequisites
* Java Development Kit (JDK 17 or higher)
* Git

### Option 1: Terminal / Command Line

```bash
# 1. Clone the repository
git clone https://github.com/Kushalnakrani07/Java_Major_Project.git
cd Java_Major_Project

# 2. Compile all source files into the out directory
mkdir -p out
javac -d out $(find src -name "*.java")

# 3. Run the application
java -cp out parking.Main
```

### Option 2: IntelliJ IDEA or Eclipse
1. Open IDE $\rightarrow$ **Open / Import Project** $\rightarrow$ select the project folder.
2. Ensure the `src` directory is marked as **Sources Root**.
3. Open `src/parking/Main.java` and click **Run** (Green Play button).

---

## 🖥 GUI Modules (Tabs)

The graphical interface is organized into 6 functional tabs:

1. **🏠 Dashboard:** High-level metrics (Total, Available, Occupied), visual slot matrix (green/orange), and active parking table.
2. **🚗 Register Vehicle:** Form to register new vehicles with plate format, owner name, contact, and type.
3. **📥 Vehicle Entry:** Select registered vehicle and allocate an available slot from the sorted dropdown.
4. **📤 Vehicle Exit & Billing:** Process exit, view computed fee popup, free the bay, and mark payment.
5. **🔍 Search & Sort:** Test linear vs binary search side-by-side; sort records by entry time, charge, or plate.
6. **📊 Report:** Formatted system summary report ready for export or inspection.

---

## 🎯 Viva & Technical Interview Guide

<details>
<summary><b>Click to expand Viva Q&A Cheat Sheet</b></summary>

#### Q1: Why did you choose MVC architecture?
> *MVC decouples business logic (`ParkingService`) from user interface (`ParkingUI`). If we swap Swing for JavaFX or a Web API, the service and model layers remain unchanged.*

#### Q2: What is the difference between Array and ArrayList in this project?
> *`ParkingSlot[]` is fixed-size because physical bays are finite (30). `ArrayList<Vehicle>` is resizable because user registrations grow over time.*

#### Q3: Why both HashMap and TreeMap?
> *`HashMap` gives $O(1)$ constant time lookup for active parkings by plate number. `TreeMap` automatically maintains parking slots sorted by slot number ($O(\log n)$) for ordered display.*

#### Q4: Why use LinkedList for history?
> *New entry/exit events are pushed to the front (`addFirst()`). `LinkedList` performs head insertion in $O(1)$ time without shifting elements as an array would.*

#### Q5: How is thread safety maintained in Java Swing?
> *Swing components are not thread-safe. All GUI construction and updates run on the Event Dispatch Thread (EDT) using `SwingUtilities.invokeLater(() -> new ParkingUI(service));`.*

#### Q6: How does Binary Search work here?
> *Binary search requires a sorted list. We sort a copy of the vehicle list by license plate, then use `Collections.binarySearch(...)` to find vehicles in $O(\log n)$ instead of $O(n)$ linear scan.*

</details>

---

## 📄 License & Credits

Developed as part of the **B.Tech CSE Semester III Java Programming Course** at **ITM Skills University**.
Created by [Kushal Nakrani](https://github.com/Kushalnakrani07).
