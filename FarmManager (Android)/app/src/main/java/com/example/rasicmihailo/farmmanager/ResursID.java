package com.example.rasicmihailo.farmmanager;


import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

public class ResursID {

    @Exclude
    public String resursID;

    public <T extends ResursID> T withId(@NonNull  final String id){
        this.resursID=id;

        return (T) this;
    }
}
