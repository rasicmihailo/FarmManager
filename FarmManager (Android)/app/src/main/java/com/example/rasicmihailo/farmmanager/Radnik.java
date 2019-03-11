package com.example.rasicmihailo.farmmanager;

public class Radnik extends RadnikID {

   private String zaposljen, zanimanje, tip, prezime, jezik, ime, datum, plata, vlasnik;

   public Radnik(){

   }

    public Radnik(String zaposljen, String zanimanje, String tip, String prezime,
                  String jezik, String ime, String datum, String plata, String vlasnik) {
        this.zaposljen = zaposljen;
        this.zanimanje = zanimanje;
        this.tip = tip;
        this.prezime = prezime;
        this.jezik = jezik;
        this.ime = ime;
        this.datum = datum;
        this.plata = plata;
        this.vlasnik = vlasnik;
    }


    public String getZaposljen() {
        return zaposljen;
    }

    public void setZaposljen(String zaposljen) {
        this.zaposljen = zaposljen;
    }

    public String getZanimanje() {
        return zanimanje;
    }

    public void setZanimanje(String zanimanje) {
        this.zanimanje = zanimanje;
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

    public String getPlata() {
        return plata;
    }

    public void setPlata(String plata) {
        this.plata = plata;
    }

    public String getVlasnik() {
        return vlasnik;
    }

    public void setVlasnik(String vlasnik) {
        this.vlasnik = vlasnik;
    }
}
