package com.example.rasicmihailo.farmmanager;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

public class NeradnikID {

    @Exclude
    public String neradnikID;
    public int id;

    public <T extends NeradnikID> T withId(@NonNull  final String id,@NonNull int idN){
     this.neradnikID=id;
     this.id= idN;

     return (T) this;
    }

}
