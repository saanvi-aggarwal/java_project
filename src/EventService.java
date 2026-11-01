import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Service class handling all business logic, data persistence coordination,
 * validations, and state synchronization for the Event Registration System.
 */
public class EventService {

    private static EventService instance;

    private ArrayList<Event> events;
    private ArrayList<Registration> registrations;
    private final List<Runnable> listeners = new ArrayList<>();

    private EventService() {
        loadData();
    }

    public static synchronized EventService getInstance() {
        if (instance == null) {
            instance = new EventService();
        }
        return instance;
    }

    public synchronized void loadData() {
        FileHandler.initializeFiles();
        events = FileHandler.loadEvents();
        registrations = FileHandler.loadRegistrations();

        // Seed default events if file was empty
        if (events.isEmpty()) {
            addDefaultEvents();
        }
    }

    private void addDefaultEvents() {
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

    public synchronized List<Event> getEvents() {
        return new ArrayList<>(events);
    }

    public synchronized List<Registration> getRegistrations() {
        return new ArrayList<>(registrations);
    }

    public synchronized Event findEvent(int eventId) {
        for (Event event : events) {
            if (event.getEventId() == eventId) {
                return event;
            }
        }
        return null;
    }

    public synchronized int getRegistrationCount(int eventId) {
        int count = 0;
        for (Registration registration : registrations) {
            if (registration.getEventId() == eventId &&
                    "Confirmed".equalsIgnoreCase(registration.getStatus())) {
                count++;
            }
        }
        return count;
    }

    public synchronized int getAvailableSeats(int eventId) {
        Event event = findEvent(eventId);
        if (event == null) return 0;
        int registered = getRegistrationCount(eventId);
        int available = event.getCapacity() - registered;
        return Math.max(0, available);
    }

    public synchronized boolean isEventFull(int eventId) {
        return getAvailableSeats(eventId) <= 0;
    }

    public synchronized double getEventOccupancyPercentage(int eventId) {
        Event event = findEvent(eventId);
        if (event == null || event.getCapacity() == 0) return 0.0;
        int registered = getRegistrationCount(eventId);
        return (registered * 100.0) / event.getCapacity();
    }

    public synchronized int generateEventId() {
        int maxId = 100;
        for (Event event : events) {
            if (event.getEventId() > maxId) {
                maxId = event.getEventId();
            }
        }
        return maxId + 1;
    }

    public synchronized String generateRegistrationId() {
        int maxId = 1000;
        for (Registration registration : registrations) {
            String id = registration.getRegistrationId();
            if (id != null && id.toUpperCase().startsWith("REG")) {
                try {
                    int number = Integer.parseInt(id.substring(3).trim());
                    if (number > maxId) {
                        maxId = number;
                    }
                } catch (NumberFormatException e) {
                    // Ignore non-standard IDs
                }
            }
        }
        return "REG" + (maxId + 1);
    }

    public synchronized Event addEvent(String name, String date, String venue, int capacity) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Event name cannot be empty.");
        }
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException("Event date cannot be empty.");
        }
        if (venue == null || venue.trim().isEmpty()) {
            throw new IllegalArgumentException("Venue cannot be empty.");
        }
        if (name.contains("|") || date.contains("|") || venue.contains("|")) {
            throw new IllegalArgumentException("Fields cannot contain pipe ('|') character.");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero.");
        }

        int eventId = generateEventId();
        Event event = new Event(eventId, name.trim(), date.trim(), venue.trim(), capacity);
        events.add(event);
        FileHandler.saveEvents(events);
        notifyChange();
        return event;
    }

    public synchronized Registration registerParticipant(int eventId, String name, String email, String phone) {
        Event selectedEvent = findEvent(eventId);
        if (selectedEvent == null) {
            throw new IllegalArgumentException("Selected event does not exist.");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Participant name cannot be empty.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email address cannot be empty.");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty.");
        }

        if (name.contains("|") || email.contains("|") || phone.contains("|")) {
            throw new IllegalArgumentException("Fields cannot contain pipe ('|') character.");
        }

        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!email.trim().matches(emailPattern)) {
            throw new IllegalArgumentException("Invalid email format (e.g., user@example.com).");
        }

        String phoneTrimmed = phone.trim();
        if (!phoneTrimmed.matches("^[0-9]{10}$")) {
            throw new IllegalArgumentException("Phone number must contain exactly 10 digits.");
        }

        if (getRegistrationCount(eventId) >= selectedEvent.getCapacity()) {
            throw new IllegalArgumentException("Sorry! This event is already fully booked.");
        }

        // Prevent duplicate active registration for the same event and email
        for (Registration reg : registrations) {
            if (reg.getEventId() == eventId &&
                    reg.getParticipant().getEmail().equalsIgnoreCase(email.trim()) &&
                    "Confirmed".equalsIgnoreCase(reg.getStatus())) {
                throw new IllegalArgumentException("This email is already registered for this event.");
            }
        }

        String registrationId = generateRegistrationId();
        Participant participant = new Participant(name.trim(), email.trim(), phoneTrimmed);
        Registration registration = new Registration(registrationId, participant, eventId, "Confirmed");

        registrations.add(registration);
        FileHandler.saveRegistrations(registrations);
        notifyChange();
        return registration;
    }

    public synchronized boolean cancelRegistration(String registrationId) {
        if (registrationId == null || registrationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration ID cannot be empty.");
        }

        String targetId = registrationId.trim();
        for (int i = 0; i < registrations.size(); i++) {
            Registration reg = registrations.get(i);
            if (reg.getRegistrationId().equalsIgnoreCase(targetId)) {
                if ("Cancelled".equalsIgnoreCase(reg.getStatus())) {
                    throw new IllegalArgumentException("This registration is already cancelled.");
                }

                reg.setStatus("Cancelled");
                FileHandler.saveRegistrations(registrations);
                notifyChange();
                return true;
            }
        }

        throw new IllegalArgumentException("Registration ID '" + targetId + "' not found.");
    }

    public synchronized Registration searchRegistration(String registrationId) {
        if (registrationId == null || registrationId.trim().isEmpty()) {
            return null;
        }
        String targetId = registrationId.trim();
        for (Registration reg : registrations) {
            if (reg.getRegistrationId().equalsIgnoreCase(targetId)) {
                return reg;
            }
        }
        return null;
    }

    public synchronized List<Registration> filterRegistrations(String keyword, String statusFilter) {
        List<Registration> result = new ArrayList<>();
        String kw = (keyword == null) ? "" : keyword.trim().toLowerCase();
        String sf = (statusFilter == null) ? "All" : statusFilter.trim();

        for (Registration reg : registrations) {
            // Status check
            if (!sf.equalsIgnoreCase("All") && !reg.getStatus().equalsIgnoreCase(sf)) {
                continue;
            }

            // Keyword check
            if (kw.isEmpty()) {
                result.add(reg);
            } else {
                Event ev = findEvent(reg.getEventId());
                String eventName = (ev != null) ? ev.getEventName().toLowerCase() : "";
                if (reg.getRegistrationId().toLowerCase().contains(kw) ||
                        reg.getParticipant().getName().toLowerCase().contains(kw) ||
                        reg.getParticipant().getEmail().toLowerCase().contains(kw) ||
                        reg.getParticipant().getPhone().contains(kw) ||
                        eventName.contains(kw) ||
                        String.valueOf(reg.getEventId()).contains(kw)) {
                    result.add(reg);
                }
            }
        }
        return result;
    }

    // System-wide metric aggregations
    public synchronized int getTotalEvents() {
        return events.size();
    }

    public synchronized int getTotalCapacity() {
        int total = 0;
        for (Event event : events) {
            total += event.getCapacity();
        }
        return total;
    }

    public synchronized int getTotalConfirmedRegistrations() {
        int count = 0;
        for (Registration reg : registrations) {
            if ("Confirmed".equalsIgnoreCase(reg.getStatus())) {
                count++;
            }
        }
        return count;
    }

    public synchronized int getTotalAvailableSeats() {
        int totalCapacity = getTotalCapacity();
        int confirmed = getTotalConfirmedRegistrations();
        return Math.max(0, totalCapacity - confirmed);
    }

    public synchronized double getOverallOccupancyPercentage() {
        int capacity = getTotalCapacity();
        if (capacity == 0) return 0.0;
        return (getTotalConfirmedRegistrations() * 100.0) / capacity;
    }

    public synchronized void addChangeListener(Runnable listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public synchronized void removeChangeListener(Runnable listener) {
        listeners.remove(listener);
    }

    public void notifyChange() {
        List<Runnable> copy;
        synchronized (this) {
            copy = new ArrayList<>(listeners);
        }
        for (Runnable r : copy) {
            SwingUtilities.invokeLater(r);
        }
    }
}
