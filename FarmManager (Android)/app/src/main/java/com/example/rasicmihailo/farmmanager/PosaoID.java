package com.example.rasicmihailo.farmmanager;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

public class PosaoID {

    @Exclude
    public String posaoID;

    public <T extends com.example.rasicmihailo.farmmanager.PosaoID> T withId(@NonNull  final String id){
        this.posaoID=id;

        return (T) this;
    }

}
