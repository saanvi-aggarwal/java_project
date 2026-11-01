public class Event {

    private int eventId;
    private String eventName;
    private String date;
    private String venue;
    private int capacity;

    public Event(int eventId, String eventName,
                 String date, String venue, int capacity) {

        this.eventId = eventId;
        this.eventName = eventName;
        this.date = date;
        this.venue = venue;
        this.capacity = capacity;
    }

    // Getters
    public int getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getDate() {
        return date;
    }

    public String getVenue() {
        return venue;
    }

    public int getCapacity() {
        return capacity;
    }

    // Display event details
    public void displayEvent() {
        System.out.println("------------------------------------");
        System.out.println("Event ID   : " + eventId);
        System.out.println("Event Name : " + eventName);
        System.out.println("Date       : " + date);
        System.out.println("Venue      : " + venue);
        System.out.println("Capacity   : " + capacity);
        System.out.println("------------------------------------");
    }

    // Convert event object into a file-friendly format
    public String toFileString() {
        return eventId + "|" + eventName + "|" +
               date + "|" + venue + "|" + capacity;
    }

    @Override
    public String toString() {
        return eventName + " (ID: " + eventId + ")";
    }
}