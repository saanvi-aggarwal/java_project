import java.io.*;
import java.util.ArrayList;

public class FileHandler {

    private static final String DATA_FOLDER = "data";
    private static final String EVENT_FILE = DATA_FOLDER + "/events.txt";
    private static final String REGISTRATION_FILE =
            DATA_FOLDER + "/registrations.txt";

    // Create data folder and files
    public static void initializeFiles() {

        File folder = new File(DATA_FOLDER);

        if (!folder.exists()) {
            folder.mkdir();
        }

        try {
            File eventFile = new File(EVENT_FILE);
            File registrationFile = new File(REGISTRATION_FILE);

            if (!eventFile.exists()) {
                eventFile.createNewFile();
            }

            if (!registrationFile.exists()) {
                registrationFile.createNewFile();
            }

        } catch (IOException e) {
            System.out.println("Error creating files: " + e.getMessage());
        }
    }

    // Save all events
    public static void saveEvents(ArrayList<Event> events) {

        initializeFiles();

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(EVENT_FILE))) {

            for (Event event : events) {
                writer.write(event.toFileString());
                writer.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error saving events: " + e.getMessage());
        }
    }

    // Load events from file
    public static ArrayList<Event> loadEvents() {

        initializeFiles();

        ArrayList<Event> events = new ArrayList<>();

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(EVENT_FILE))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split("\\|", -1);

                if (data.length == 5) {
                    try {
                        Event event = new Event(
                                Integer.parseInt(data[0]),
                                data[1],
                                data[2],
                                data[3],
                                Integer.parseInt(data[4])
                        );

                        events.add(event);

                    } catch (NumberFormatException e) {
                        System.out.println("Skipping invalid event record.");
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading events: " + e.getMessage());
        }

        return events;
    }

    // Save all registrations
    public static void saveRegistrations(
            ArrayList<Registration> registrations) {

        initializeFiles();

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(REGISTRATION_FILE))) {

            for (Registration registration : registrations) {

                writer.write(registration.toFileString());
                writer.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error saving registrations: " + e.getMessage());
        }
    }

    // Load registrations from file
    public static ArrayList<Registration> loadRegistrations() {

        initializeFiles();

        ArrayList<Registration> registrations = new ArrayList<>();

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(REGISTRATION_FILE))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split("\\|", -1);

                if (data.length == 6) {

                    try {
                        Participant participant = new Participant(
                                data[1],
                                data[2],
                                data[3]
                        );

                        Registration registration = new Registration(
                                data[0],
                                participant,
                                Integer.parseInt(data[4]),
                                data[5]
                        );

                        registrations.add(registration);

                    } catch (NumberFormatException e) {
                        System.out.println("Skipping invalid registration.");
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading registrations: " + e.getMessage());
        }

        return registrations;
    }
}