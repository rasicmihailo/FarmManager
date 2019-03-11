package com.example.rasicmihailo.farmmanager;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

public class RadnikID  {

    @Exclude
    public String radnikID;

    public <T extends RadnikID> T withId(@NonNull final String id){
        this.radnikID=id;

        return (T) this;
    }

}
