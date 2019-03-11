package com.example.rasicmihailo.farmmanager;

public class Gazdinstvo extends GazdinstvoID{

    private String naziv, lokacija, finansije;

    public Gazdinstvo(){

    }

    public Gazdinstvo(String naziv, String lokacija, String finansije) {
        this.naziv = naziv;
        this.lokacija = lokacija;
        this.finansije = finansije;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getLokacija() {
        return lokacija;
    }

    public void setLokacija(String lokacija) {
        this.lokacija = lokacija;
    }

    public String getFinansije() {
        return finansije;
    }

    public void setFinansije(String finansije) {
        this.finansije = finansije;
    }
}
