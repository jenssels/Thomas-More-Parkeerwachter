package be.thomasmore.tm_parkeerwachter.Classes;

public class Foto {
    // Attributen
    private String _id;
    private String url;
    private String overtredingId;

    // Constructoren
    public Foto(String _id, String url, String overtredingId) {
        this._id = _id;
        this.url = url;
        this.overtredingId = overtredingId;
    }

    public Foto() {}

    // Methoden

    // Getters & Setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOvertredingId() {
        return overtredingId;
    }

    public void setOvertredingId(String overtredingId) {
        this.overtredingId = overtredingId;
    }
}
