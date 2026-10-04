import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// ==========================================
// 1. BASE CLASS: Vehicle (Encapsulation & Abstraction)
// ==========================================
abstract class Vehicle {
    private String make;
    private String model;
    private double currentOdometer;

    public Vehicle(String make, String model, double initialOdometer) {
        if (initialOdometer < 0) {
            throw new IllegalArgumentException("Initial odometer reading cannot be negative.");
        }
        if (make == null || make.trim().isEmpty() || model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Make and Model cannot be empty.");
        }
        this.make = make.trim();
        this.model = model.trim();
        this.currentOdometer = initialOdometer;
    }

    public String getMake() { return make; }
    public String getModel() { return model; }
    public double getCurrentOdometer() { return currentOdometer; }

    // Enforces state integrity: Odometer can only move forward
    protected void setOdometer(double newOdometer) {
        if (newOdometer < this.currentOdometer) {
            throw new IllegalArgumentException(
                String.format("Invalid Odometer: %.2f km cannot be less than current reading (%.2f km).", 
                newOdometer, this.currentOdometer)
            );
        }
        this.currentOdometer = newOdometer;
    }

    public abstract void displayDetails();
}

// ==========================================
// 2. DERIVED CLASS: Motorcycle (Inheritance)
// ==========================================
class Motorcycle extends Vehicle {
    private int engineCapacityCC;
    private double lastServiceOdometer;
    private final double maintenanceIntervalKm;

    public Motorcycle(String make, String model, double currentOdometer, int engineCapacityCC, double maintenanceIntervalKm) {
        super(make, model, currentOdometer);
        if (engineCapacityCC <= 0) {
            throw new IllegalArgumentException("Engine capacity must be greater than zero.");
        }
        if (maintenanceIntervalKm <= 0) {
            throw new IllegalArgumentException("Maintenance interval threshold must be greater than zero.");
        }
        this.engineCapacityCC = engineCapacityCC;
        this.maintenanceIntervalKm = maintenanceIntervalKm;
        this.lastServiceOdometer = currentOdometer;
    }

    public double getLastServiceOdometer() { return lastServiceOdometer; }
    public double getMaintenanceIntervalKm() { return maintenanceIntervalKm; }

    public void updateOdometerAfterTrip(double tripDistance) {
        if (tripDistance <= 0) {
            throw new IllegalArgumentException("Trip distance must be positive.");
        }
        setOdometer(getCurrentOdometer() + tripDistance);
    }

    // Algorithmic check for maintenance thresholds
    public boolean isServiceDue() {
        return (getCurrentOdometer() - lastServiceOdometer) >= maintenanceIntervalKm;
    }

    public double getKmUntilNextService() {
        double distanceSinceService = getCurrentOdometer() - lastServiceOdometer;
        double remaining = maintenanceIntervalKm - distanceSinceService;
        return Math.max(0, remaining);
    }

    public void performService() {
        this.lastServiceOdometer = getCurrentOdometer();
    }

    @Override
    public void displayDetails() {
        System.out.printf("Vehicle        : %s %s (%d cc)%n", getMake(), getModel(), engineCapacityCC);
        System.out.printf("Current Odo    : %.2f km%n", getCurrentOdometer());
        System.out.printf("Last Service   : %.2f km%n", lastServiceOdometer);
        System.out.printf("Status         : %s (Next due in: %.2f km)%n",
                isServiceDue() ? "[ALERT] SERVICE DUE NOW" : "[OK] Vehicle Good", 
                getKmUntilNextService());
    }
}

// ==========================================
// 3. CLASS: Rider (Encapsulation)
// ==========================================
class Rider {
    private String name;
    private String licenseNumber;

    public Rider(String name, String licenseNumber) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Rider name cannot be empty.");
        }
        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("License number cannot be empty.");
        }
        this.name = name.trim();
        this.licenseNumber = licenseNumber.trim();
    }

    public String getName() { return name; }
    public String getLicenseNumber() { return licenseNumber; }
}

// ==========================================
// 4. CLASS: Trip (Composition)
// ==========================================
class Trip {
    private Rider rider;           // Composition
    private Motorcycle motorcycle; // Composition
    private double distanceKm;
    private double fuelUsedLiters;

    public Trip(Rider rider, Motorcycle motorcycle, double distanceKm, double fuelUsedLiters) {
        if (rider == null) throw new IllegalArgumentException("Trip requires a valid Rider.");
        if (motorcycle == null) throw new IllegalArgumentException("Trip requires a valid Motorcycle.");
        if (distanceKm <= 0) throw new IllegalArgumentException("Distance must be greater than 0.");
        if (fuelUsedLiters <= 0) throw new IllegalArgumentException("Fuel used must be greater than 0.");

        this.rider = rider;
        this.motorcycle = motorcycle;
        this.distanceKm = distanceKm;
        this.fuelUsedLiters = fuelUsedLiters;

        // Atomically update motorcycle odometer when a valid trip is logged
        this.motorcycle.updateOdometerAfterTrip(distanceKm);
    }

    public Rider getRider() { return rider; }
    public Motorcycle getMotorcycle() { return motorcycle; }
    public double getDistanceKm() { return distanceKm; }
    public double getFuelUsedLiters() { return fuelUsedLiters; }

    // Algorithmic Calculation: Per-trip efficiency
    public double calculateFuelEfficiency() {
        return distanceKm / fuelUsedLiters;
    }
}

// ==========================================
// 5. MAIN SYSTEM & INTERACTIVE CONSOLE DRIVER
// ==========================================
public class MotorcycleTracker {
    private List<Trip> tripHistory;
    private Motorcycle motorcycle;
    private Rider rider;

    public MotorcycleTracker(Motorcycle motorcycle, Rider rider) {
        this.tripHistory = new ArrayList<>();
        this.motorcycle = motorcycle;
        this.rider = rider;
    }

    public void logTrip(double distance, double fuel) {
        Trip trip = new Trip(this.rider, this.motorcycle, distance, fuel);
        tripHistory.add(trip);
        System.out.printf("%n[SUCCESS] Trip logged! Fuel Efficiency: %.2f km/L%n", trip.calculateFuelEfficiency());
    }

    public void displayAggregateStatistics() {
        if (tripHistory.isEmpty()) {
            System.out.println("\n[INFO] No trips logged in the system yet.");
            return;
        }

        double totalDistance = 0;
        double totalFuel = 0;

        for (Trip t : tripHistory) {
            totalDistance += t.getDistanceKm();
            totalFuel += t.getFuelUsedLiters();
        }

        double aggregateEfficiency = totalFuel > 0 ? (totalDistance / totalFuel) : 0;

        System.out.println("\n=============================================");
        System.out.println("         AGGREGATE TRAVEL STATISTICS         ");
        System.out.println("=============================================");
        System.out.printf("Total Trips Recorded : %d%n", tripHistory.size());
        System.out.printf("Total Distance Traveled: %.2f km%n", totalDistance);
        System.out.printf("Total Fuel Consumed   : %.2f L%n", totalFuel);
        System.out.printf("Average Fleet Efficiency: %.2f km/L%n", aggregateEfficiency);
        System.out.println("---------------------------------------------");
        motorcycle.displayDetails();
        System.out.println("=============================================");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("====================================================");
        System.out.println("   MOTORCYCLE RIDE & MAINTENANCE TRACKER SYSTEM     ");
        System.out.println("====================================================");

        // Robust Rider Initialization
        Rider rider = null;
        while (rider == null) {
            try {
                System.out.print("Enter Rider Name: ");
                String name = scanner.nextLine();
                System.out.print("Enter License Number: ");
                String license = scanner.nextLine();
                rider = new Rider(name, license);
            } catch (IllegalArgumentException e) {
                System.out.println("[VALIDATION ERROR] " + e.getMessage() + " Try again.\n");
            }
        }

        // Robust Motorcycle Initialization
        Motorcycle motorcycle = null;
        while (motorcycle == null) {
            try {
                System.out.print("Enter Motorcycle Make (e.g., Yamaha): ");
                String make = scanner.nextLine();
                System.out.print("Enter Motorcycle Model (e.g., MT-07): ");
                String model = scanner.nextLine();

                double initialOdo = readDouble(scanner, "Enter Current Odometer Reading (km): ");
                int cc = readInt(scanner, "Enter Engine Capacity (cc): ");
                double interval = readDouble(scanner, "Enter Maintenance Alert Threshold (km): ");

                motorcycle = new Motorcycle(make, model, initialOdo, cc, interval);
            } catch (IllegalArgumentException e) {
                System.out.println("[VALIDATION ERROR] " + e.getMessage() + " Try again.\n");
            }
        }

        MotorcycleTracker tracker = new MotorcycleTracker(motorcycle, rider);

        boolean running = true;
        while (running) {
            System.out.println("\n--- SYSTEM MENU ---");
            System.out.println("1. Log a New Trip");
            System.out.println("2. View Aggregate Statistics & Vehicle Details");
            System.out.println("3. Check Maintenance Alert Status");
            System.out.println("4. Perform Maintenance Service");
            System.out.println("5. Exit System");
            System.out.print("Select an option (1-5): ");

            int choice = readInt(scanner, "");

            switch (choice) {
                case 1:
                    try {
                        double distance = readDouble(scanner, "Enter Trip Distance (km): ");
                        double fuel = readDouble(scanner, "Enter Fuel Consumed (Liters): ");
                        tracker.logTrip(distance, fuel);

                        if (motorcycle.isServiceDue()) {
                            System.out.println("\n[WARNING] Maintenance interval reached! Service required.");
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("[ERROR] " + e.getMessage());
                    }
                    break;

                case 2:
                    tracker.displayAggregateStatistics();
                    break;

                case 3:
                    System.out.println("\n--- MAINTENANCE ALERT STATUS ---");
                    motorcycle.displayDetails();
                    break;

                case 4:
                    motorcycle.performService();
                    System.out.println("\n[SUCCESS] Maintenance recorded. Service threshold benchmark updated.");
                    break;

                case 5:
                    running = false;
                    System.out.println("\nExiting Motorcycle Tracker System. Ride safe!");
                    break;

                default:
                    System.out.println("[ERROR] Invalid choice. Please select between 1 and 5.");
            }
        }
        scanner.close();
    }

    // Helper functions for exception-safe non-numeric console input handling
    private static double readDouble(Scanner scanner, String prompt) {
        while (true) {
            if (!prompt.isEmpty()) System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("[INVALID INPUT] Non-numeric input detected. Please enter a valid decimal number.");
            }
        }
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            if (!prompt.isEmpty()) System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[INVALID INPUT] Non-numeric input detected. Please enter a valid integer.");
            }
        }
    }
}
