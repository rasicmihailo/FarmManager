package com.example.rasicmihailo.farmmanager;

public class Neradnik extends NeradnikID {


    public String zaposljen, zanimanje, tip, prezime, jezik, ime, datum;

    public Neradnik() {
    }

    public Neradnik(String zaposljen, String zanimanje, String tip, String prezime, String jezik, String ime, String datum) {
        this.zaposljen = zaposljen;
        this.zanimanje = zanimanje;
        this.tip = tip;
        this.prezime = prezime;
        this.jezik = jezik;
        this.ime = ime;
        this.datum = datum;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getDatumRodj() {
        return datum;
    }

    public void setDatumRodj(String datumRodj) {
        this.datum = datumRodj;
    }

    public String getJezik() {
        return jezik;
    }

    public void setJezik(String jezik) {
        this.jezik = jezik;
    }

    public String getZanimanje() {
        return zanimanje;
    }

    public void setZanimanje(String zanimanje) {
        this.zanimanje = zanimanje;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getZaposljen() {
        return zaposljen;
    }

    public void setZaposljen(String zaposljen) {
        this.zaposljen = zaposljen;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }
}

