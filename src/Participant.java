public class Participant {

    private String name;
    private String email;
    private String phone;

    public Participant(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void displayParticipant() {
        System.out.println("Name  : " + name);
        System.out.println("Email : " + email);
        System.out.println("Phone : " + phone);
    }
}