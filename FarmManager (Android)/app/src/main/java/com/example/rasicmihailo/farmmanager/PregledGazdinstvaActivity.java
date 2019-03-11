package com.example.rasicmihailo.farmmanager;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class PregledGazdinstvaActivity extends Fragment {
    View view;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private TextView txtGazdinstvo;
    private TextView txtLokacija;
    private TextView txtImeIPrezVl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup containter, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.radnik_pregled_gazdinstva,containter,false);



        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();

        txtGazdinstvo = (TextView) view.findViewById(R.id.txtNazivFarme);
        txtLokacija = (TextView) view.findViewById(R.id.txtLokacijaFarme);
        txtImeIPrezVl = (TextView) view.findViewById(R.id.txtpImeIPezVlasnika);


        firebaseFirestore.collection("Users").document(mAuth.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            if (task.getResult().exists()) {

                                String idVlasnika = task.getResult().getString("vlasnik");

                                firebaseFirestore.collection("Gazdinstva").document(idVlasnika).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                                                if (task.isSuccessful()) {

                                                    if (task.getResult().exists()) {

                                                        txtGazdinstvo.setText(task.getResult().getString("naziv"));
                                                        txtLokacija.setText(task.getResult().getString("lokacija"));

                                                    }
                                                }
                                            }
                                        });

                                firebaseFirestore.collection("Users").document(idVlasnika).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                                                if (task.isSuccessful()) {

                                                    if (task.getResult().exists()) {

                                                        txtImeIPrezVl.setText(task.getResult().getString("ime") + " " + task.getResult().getString("prezime"));

                                                    }
                                                }
                                            }
                                        });

                            }
                        }
                    }
                });



        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if(user==null){
            startActivity(new Intent(getActivity(), MainActivity.class));
        }
        proveriZaposljenje();
    }


    public void proveriZaposljenje(){ //vrsi se provera da nije dobio otkaz

        firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    String zaposljen = task.getResult().getString("zaposljen");

                    if(zaposljen.matches("ne")){

                        startActivity(new Intent(getActivity(), MainActivity.class));
                    }
                }
            }
        });

    }


}
