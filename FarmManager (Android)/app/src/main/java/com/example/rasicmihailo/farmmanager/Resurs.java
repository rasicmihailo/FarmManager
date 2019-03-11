package com.example.rasicmihailo.farmmanager;



public class Resurs extends ResursID {

    private String tip, vrsta, kolicina;

    public Resurs(){

    }

    public Resurs(String tip, String vrsta, String kolicina) {
        this.tip = tip;
        this.vrsta = vrsta;
        this.kolicina = kolicina;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getKolicina() {
        return kolicina;
    }

    public void setKolicina(String kolicina) {
        this.kolicina = kolicina;
    }

    public String getVrsta() {
        return vrsta;
    }

    public void setVrsta(String vrsta) {
        this.vrsta = vrsta;
    }
}
