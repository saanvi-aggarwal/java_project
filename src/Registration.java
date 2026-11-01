public class Registration {

    private String registrationId;
    private Participant participant;
    private int eventId;
    private String status;

    public Registration(String registrationId,
                        Participant participant,
                        int eventId,
                        String status) {

        this.registrationId = registrationId;
        this.participant = participant;
        this.eventId = eventId;
        this.status = status;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public Participant getParticipant() {
        return participant;
    }

    public int getEventId() {
        return eventId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void displayRegistration(Event event) {

        System.out.println("\n================================");
        System.out.println("       REGISTRATION DETAILS");
        System.out.println("================================");

        System.out.println("Registration ID : " + registrationId);

        participant.displayParticipant();

        System.out.println("Event           : " + event.getEventName());
        System.out.println("Status          : " + status);

        System.out.println("================================");
    }

    public String toFileString() {

        Participant p = participant;

        return registrationId + "|" +
               p.getName() + "|" +
               p.getEmail() + "|" +
               p.getPhone() + "|" +
               eventId + "|" +
               status;
    }
}