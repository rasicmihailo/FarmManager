package com.example.rasicmihailo.farmmanager;

import android.view.LayoutInflater;
import android.view.View;

public class Ponuda extends PonudaID {

    private  String vlasnik, radnovreme, plata;

    public Ponuda(String vlasnik, String radnovreme, String plata) {
        this.vlasnik = vlasnik;
        this.radnovreme = radnovreme;
        this.plata = plata;
    }
    public Ponuda(){

    }

    public String getVlasnik() {
        return vlasnik;
    }

    public void setVlasnik(String vlasnik) {
        this.vlasnik = vlasnik;
    }

    public String getRadnovreme() {
        return radnovreme;
    }

    public void setRadnovreme(String radnovreme) {
        this.radnovreme = radnovreme;
    }

    public String getPlata() {
        return plata;
    }

    public void setPlata(String plata) {
        this.plata = plata;
    }
}
