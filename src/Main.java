import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);

    static ArrayList<Event> events;
    static ArrayList<Registration> registrations;

    public static void main(String[] args) {

        // Check if user requested GUI mode via command line argument
        if (args.length > 0 && args[0].equalsIgnoreCase("--gui")) {
            MainGUI.main(args);
            return;
        }

        // Initialize files
        FileHandler.initializeFiles();

        // Load existing data
        events = FileHandler.loadEvents();
        registrations = FileHandler.loadRegistrations();

        // Add sample events for first-time users
        if (events.isEmpty()) {
            addDefaultEvents();
        }

        System.out.println("\n========================================");
        System.out.println("       EVENT REGISTRATION SYSTEM");
        System.out.println("========================================");

        int choice;

        do {
            displayMenu();

            System.out.print("Enter your choice: ");

            choice = readInt();

            switch (choice) {

                case 1:
                    viewEvents();
                    break;

                case 2:
                    registerParticipant();
                    break;

                case 3:
                    searchRegistration();
                    break;

                case 4:
                    viewAllRegistrations();
                    break;

                case 5:
                    cancelRegistration();
                    break;

                case 6:
                    addNewEvent();
                    break;

                case 7:
                    generateReport();
                    break;

                case 8:
                    System.out.println("\nThank you for using the system!");
                    break;

                case 9:
                    System.out.println("\nLaunching Desktop GUI Application...");
                    MainGUI.main(new String[0]);
                    break;

                default:
                    System.out.println("\nInvalid choice. Try again.");
            }

        } while (choice != 8);

        sc.close();
    }

    // Display main menu
    static void displayMenu() {

        System.out.println("\n------------- MAIN MENU -------------");

        System.out.println("1. View Available Events");
        System.out.println("2. Register for an Event");
        System.out.println("3. Search Registration");
        System.out.println("4. View All Registrations");
        System.out.println("5. Cancel Registration");
        System.out.println("6. Add New Event");
        System.out.println("7. Generate Event Report");
        System.out.println("8. Exit CLI");
        System.out.println("9. Launch Desktop GUI Application");

        System.out.println("-------------------------------------");
    }

    // Read integer safely
    static int readInt() {

        while (!sc.hasNextInt()) {
            System.out.print("Enter a valid number: ");
            sc.nextLine();
        }

        int number = sc.nextInt();
        sc.nextLine();

        return number;
    }

    // Read and validate a non-empty text field
    static String readText(String message) {

        while (true) {

            System.out.print(message);

            String value = sc.nextLine().trim();

            if (!value.isEmpty() && !value.contains("|")) {
                return value;
            }

            System.out.println("Input cannot be empty or contain |.");
        }
    }

    // Sample events
    static void addDefaultEvents() {

        events.add(new Event(
                101, "Java Programming Workshop",
                "15-12-2026", "Seminar Hall", 50
        ));

        events.add(new Event(
                102, "AI Hackathon",
                "20-12-2026", "Innovation Lab", 100
        ));

        events.add(new Event(
                103, "Web Development Seminar",
                "25-12-2026", "Auditorium", 75
        ));

        FileHandler.saveEvents(events);
    }

    // Find event using event ID
    static Event findEvent(int eventId) {

        for (Event event : events) {

            if (event.getEventId() == eventId) {
                return event;
            }
        }

        return null;
    }

    // Count active registrations for an event
    static int getRegistrationCount(int eventId) {

        int count = 0;

        for (Registration registration : registrations) {

            if (registration.getEventId() == eventId &&
                registration.getStatus().equals("Confirmed")) {

                count++;
            }
        }

        return count;
    }

    // Display available events
    static void viewEvents() {

        System.out.println("\n========== AVAILABLE EVENTS ==========");

        if (events.isEmpty()) {
            System.out.println("No events available.");
            return;
        }

        for (Event event : events) {

            event.displayEvent();

            int registered = getRegistrationCount(event.getEventId());

            System.out.println("Registered : " + registered);
            System.out.println("Seats Left : " +
                    (event.getCapacity() - registered));
        }
    }

    // Register participant
    static void registerParticipant() {

        System.out.println("\n========== EVENT REGISTRATION ==========");

        viewEvents();

        System.out.print("\nEnter Event ID: ");

        int eventId = readInt();

        Event selectedEvent = findEvent(eventId);

        if (selectedEvent == null) {
            System.out.println("Event not found.");
            return;
        }

        if (getRegistrationCount(eventId) >=
                selectedEvent.getCapacity()) {

            System.out.println("Sorry! Event is fully booked.");
            return;
        }

        String name = readText("Enter Participant Name: ");
        String email = readText("Enter Email: ");
        String phone = readText("Enter Phone Number: ");

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            System.out.println("Invalid email address.");
            return;
        }

        if (!phone.matches("[0-9]{10}")) {
            System.out.println("Phone number must contain 10 digits.");
            return;
        }

        // Prevent duplicate registration for the same event
        for (Registration registration : registrations) {

            if (registration.getParticipant().getEmail()
                    .equalsIgnoreCase(email) &&
                registration.getEventId() == eventId &&
                registration.getStatus().equals("Confirmed")) {

                System.out.println("You are already registered for this event.");
                return;
            }
        }

        Participant participant =
                new Participant(name, email, phone);

        String registrationId = generateRegistrationId();

        Registration registration = new Registration(
                registrationId,
                participant,
                eventId,
                "Confirmed"
        );

        registrations.add(registration);

        FileHandler.saveRegistrations(registrations);

        System.out.println("\nRegistration Successful!");

        registration.displayRegistration(selectedEvent);
    }

    // Generate unique registration ID
    static String generateRegistrationId() {

        int maxId = 1000;

        for (Registration registration : registrations) {

            String id = registration.getRegistrationId();

            if (id.startsWith("REG")) {

                try {
                    int number = Integer.parseInt(id.substring(3));

                    if (number > maxId) {
                        maxId = number;
                    }

                } catch (NumberFormatException e) {
                    // Ignore malformed IDs
                }
            }
        }

        return "REG" + (maxId + 1);
    }

    // Search registration
    static void searchRegistration() {

        System.out.println("\n========== SEARCH REGISTRATION ==========");

        String id = readText("Enter Registration ID: ");

        for (Registration registration : registrations) {

            if (registration.getRegistrationId()
                    .equalsIgnoreCase(id)) {

                Event event = findEvent(registration.getEventId());

                if (event != null) {
                    registration.displayRegistration(event);
                } else {
                    System.out.println("Associated event not found.");
                }

                return;
            }
        }

        System.out.println("Registration not found.");
    }

    // Display all registrations
    static void viewAllRegistrations() {

        System.out.println("\n========== ALL REGISTRATIONS ==========");

        boolean found = false;

        for (Registration registration : registrations) {

            if (registration.getStatus().equals("Confirmed")) {

                Event event = findEvent(registration.getEventId());

                if (event != null) {
                    registration.displayRegistration(event);
                    found = true;
                }
            }
        }

        if (!found) {
            System.out.println("No active registrations found.");
        }
    }

    // Cancel registration
    static void cancelRegistration() {

        System.out.println("\n========== CANCEL REGISTRATION ==========");

        String id = readText("Enter Registration ID: ");

        for (int i = 0; i < registrations.size(); i++) {

            Registration registration = registrations.get(i);

            if (registration.getRegistrationId()
                    .equalsIgnoreCase(id)) {

                if (registration.getStatus().equals("Cancelled")) {
                    System.out.println("This registration is already cancelled.");
                    return;
                }

                registrations.set(i, new Registration(
                        registration.getRegistrationId(),
                        registration.getParticipant(),
                        registration.getEventId(),
                        "Cancelled"
                ));

                FileHandler.saveRegistrations(registrations);

                System.out.println("Registration cancelled successfully!");
                return;
            }
        }

        System.out.println("Registration ID not found.");
    }

    // Add a new event
    static void addNewEvent() {

        System.out.println("\n========== ADD NEW EVENT ==========");

        int eventId = 100;

        for (Event event : events) {

            if (event.getEventId() > eventId) {
                eventId = event.getEventId();
            }
        }

        eventId++;

        String name = readText("Enter Event Name: ");
        String date = readText("Enter Event Date (DD-MM-YYYY): ");
        String venue = readText("Enter Venue: ");

        System.out.print("Enter Maximum Capacity: ");

        int capacity = readInt();

        if (capacity <= 0) {
            System.out.println("Capacity must be greater than zero.");
            return;
        }

        Event event = new Event(
                eventId,
                name,
                date,
                venue,
                capacity
        );

        events.add(event);

        FileHandler.saveEvents(events);

        System.out.println("\nEvent added successfully!");
        event.displayEvent();
    }

    // Generate event-wise report
    static void generateReport() {

        System.out.println("\n========== EVENT REPORT ==========");

        if (events.isEmpty()) {
            System.out.println("No events available.");
            return;
        }

        int totalCapacity = 0;
        int totalRegistered = 0;

        for (Event event : events) {

            int registered = getRegistrationCount(event.getEventId());

            int available = event.getCapacity() - registered;

            double percentage =
                    (registered * 100.0) / event.getCapacity();

            System.out.println("\nEvent: " + event.getEventName());
            System.out.println("Event ID: " + event.getEventId());
            System.out.println("Total Seats: " + event.getCapacity());
            System.out.println("Registered: " + registered);
            System.out.println("Available Seats: " + available);

            System.out.printf("Registration Percentage: %.2f%%\n",
                    percentage);

            totalCapacity += event.getCapacity();
            totalRegistered += registered;
        }

        System.out.println("\n========== OVERALL SUMMARY ==========");

        System.out.println("Total Events: " + events.size());
        System.out.println("Total Capacity: " + totalCapacity);
        System.out.println("Total Registrations: " + totalRegistered);

        if (totalCapacity > 0) {

            double overallPercentage =
                    (totalRegistered * 100.0) / totalCapacity;

            System.out.printf("Overall Occupancy: %.2f%%\n",
                    overallPercentage);
        }
    }
}