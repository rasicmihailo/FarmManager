package com.example.rasicmihailo.farmmanager;

public class Vlasnik {


    public String tip, prezime, jezik, ime, datum;

    public Vlasnik(String tip, String prezime, String jezik, String ime, String datum) {
        this.tip = tip;
        this.prezime = prezime;
        this.jezik = jezik;
        this.ime = ime;
        this.datum = datum;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getJezik() {
        return jezik;
    }

    public void setJezik(String jezik) {
        this.jezik = jezik;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }
}
