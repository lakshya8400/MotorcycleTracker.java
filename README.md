# MotorcycleTracker.java
# Motorcycle Ride & Maintenance Tracker

A robust, object-oriented Java console application designed to track motorcycle trip logs, calculate fuel efficiency, maintain rolling mileage records, and issue maintenance alerts.

## Key Features
- **Data Integrity & Validation:** Prevents negative inputs, invalid fuel logs, and odometer tampering (disallows decreasing mileage readings).
- **Algorithmic Calculations:** Computes per-trip fuel efficiency ($\text{km/L}$), aggregate distance/fuel consumption, and rolling maintenance thresholds.
- **Robust Object-Oriented Architecture:** Demonstrates core OOP principles including Inheritance (`Vehicle` $\rightarrow$ `Motorcycle`), Encapsulation, and Composition (`Trip` composed of `Rider` and `Motorcycle`).
- **Crash-Free Exception Handling:** Handles non-numeric keyboard input and runtime exceptions gracefully.

## How to Run
1. Ensure Java JDK 8 or higher is installed.
2. Clone this repository:
   ```bash
   git clone [https://github.com/your-username/motorcycle-ride-tracker.git](https://github.com/your-username/motorcycle-ride-tracker.git)
   cd motorcycle-ride-tracker
