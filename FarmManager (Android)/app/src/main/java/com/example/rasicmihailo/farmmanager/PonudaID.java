package com.example.rasicmihailo.farmmanager;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

public class PonudaID {

    @Exclude
    public String neradnikID;

    public <T extends PonudaID> T withId(@NonNull final String id){
        this.neradnikID=id;

        return (T) this;
    }

}
