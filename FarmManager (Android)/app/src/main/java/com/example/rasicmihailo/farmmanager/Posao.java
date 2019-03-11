package com.example.rasicmihailo.farmmanager;



public class Posao extends PosaoID{

    private String opis, radnik, zavrsen, ime, prezime;

    public Posao(){

    }

    public Posao(String radnik, String opis, String zavrsen, String ime, String prezime) {
        this.radnik = radnik;
        this.opis = opis;
        this.zavrsen = zavrsen;
        this.ime = ime;
        this.prezime = prezime;
    }


    public String getRadnik() {
        return radnik;
    }

    public void setRadnik(String radnik) {
        this.radnik = radnik;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public String getZavrsen() {
        return zavrsen;
    }

    public void setZavrsen(String zavrsen) {
        this.zavrsen = zavrsen;
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
}
