package be.thomasmore.tm_parkeerwachter.Classes;

public class GevolgType {
    // Attributen
    private String _id;
    private String naam;

    // Constructoren
    public GevolgType(String _id, String naam) {
        this._id = _id;
        this.naam = naam;
    }

    public GevolgType() { }

    // Methoden

    // Getters & Setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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
        return this.naam;
    }
}
