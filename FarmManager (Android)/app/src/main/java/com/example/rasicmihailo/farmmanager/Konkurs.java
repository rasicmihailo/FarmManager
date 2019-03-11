package com.example.rasicmihailo.farmmanager;

public class Konkurs {


    String vreme, radnik, plata;

    public Konkurs(){

    }

    public Konkurs(String vreme, String radnik, String plata) {
        this.vreme = vreme;
        this.radnik = radnik;
        this.plata = plata;
    }

    public String getVreme() {
        return vreme;
    }

    public void setVreme(String vreme) {
        this.vreme = vreme;
    }

    public String getRadnik() {
        return radnik;
    }

    public void setRadnik(String radnik) {
        this.radnik = radnik;
    }

    public String getPlata() {
        return plata;
    }

    public void setPlata(String plata) {
        this.plata = plata;
    }
}
