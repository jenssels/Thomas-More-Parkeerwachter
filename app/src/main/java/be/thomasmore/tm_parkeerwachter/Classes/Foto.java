package be.thomasmore.tm_parkeerwachter.Classes;

public class Foto {
    // Attributen
    private String _id;
    private String naam;
    private String url;
    private String lokaleUrl;
    private String overtredingId;

    // Constructoren
    public Foto(String _id, String naam, String url, String lokaleUrl, String overtredingId) {
        this._id = _id;
        this.naam = naam;
        this.url = url;
        this.lokaleUrl = lokaleUrl;
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

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLokaleUrl() {
        return lokaleUrl;
    }

    public void setLokaleUrl(String lokaleUrl) {
        this.lokaleUrl = lokaleUrl;
    }

    public String getOvertredingId() {
        return overtredingId;
    }

    public void setOvertredingId(String overtredingId) {
        this.overtredingId = overtredingId;
    }
}
