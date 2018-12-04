package be.thomasmore.tm_parkeerwachter.Classes;

public class Parkeerwachter {
    // Attributen
    private String id;
    private String username;
    private String pincode;
    private String voornaam;
    private String naam;

    // Constructoren
    public Parkeerwachter() {}

    public Parkeerwachter(String id, String username, String pincode, String voornaam, String naam) {
        this.id = id;
        this.username = username;
        this.pincode = pincode;
        this.voornaam = voornaam;
        this.naam = naam;
    }

    // Methoden

    // Getters & Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getVoornaam() {
        return voornaam;
    }

    public void setVoornaam(String voornaam) {
        this.voornaam = voornaam;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    // Overrides
    @Override
    public String toString() {
        return this.username;
    }
}
