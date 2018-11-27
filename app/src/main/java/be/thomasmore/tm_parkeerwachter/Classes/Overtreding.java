package be.thomasmore.tm_parkeerwachter.Classes;

import java.util.Date;

public class Overtreding {
    // Attributen
    private String _id;
    private Date datum;
    private String nummerplaat;
    private String nummerplaatUrl;
    private String breedtegraad;
    private String lengtegraad;
    private String opmerking;
    private String parkeerwachterId;
    private String gevolgTypeId;

    // Constructoren
    public Overtreding(String _id, Date datum, String nummerplaat, String nummerplaatUrl, String breedtegraad, String lengtegraad, String opmerking, String parkeerwachterId, String gevolgTypeId) {
        this._id = _id;
        this.datum = datum;
        this.nummerplaat = nummerplaat;
        this.nummerplaatUrl = nummerplaatUrl;
        this.breedtegraad = breedtegraad;
        this.lengtegraad = lengtegraad;
        this.opmerking = opmerking;
        this.parkeerwachterId = parkeerwachterId;
        this.gevolgTypeId = gevolgTypeId;
    }

    // Methoden

    // Getters & Setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public String getNummerplaat() {
        return nummerplaat;
    }

    public void setNummerplaat(String nummerplaat) {
        this.nummerplaat = nummerplaat;
    }

    public String getNummerplaatUrl() {
        return nummerplaatUrl;
    }

    public void setNummerplaatUrl(String nummerplaatUrl) {
        this.nummerplaatUrl = nummerplaatUrl;
    }

    public String getBreedtegraad() {
        return breedtegraad;
    }

    public void setBreedtegraad(String breedtegraad) {
        this.breedtegraad = breedtegraad;
    }

    public String getLengtegraad() {
        return lengtegraad;
    }

    public void setLengtegraad(String lengtegraad) {
        this.lengtegraad = lengtegraad;
    }

    public String getOpmerking() {
        return opmerking;
    }

    public void setOpmerking(String opmerking) {
        this.opmerking = opmerking;
    }

    public String getParkeerwachterId() {
        return parkeerwachterId;
    }

    public void setParkeerwachterId(String parkeerwachterId) {
        this.parkeerwachterId = parkeerwachterId;
    }

    public String getGevolgTypeId() {
        return gevolgTypeId;
    }

    public void setGevolgTypeId(String gevolgTypeId) {
        this.gevolgTypeId = gevolgTypeId;
    }
}
