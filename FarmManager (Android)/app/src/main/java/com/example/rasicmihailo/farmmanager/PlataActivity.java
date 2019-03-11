package com.example.rasicmihailo.farmmanager;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlataActivity extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private VlasnikPlateRecycler vlasnikPlateRecycler;
    private List<Radnik> listaRadnika;
    private RelativeLayout relIsplati;
    private TextView txtVlasnik;
    private TextView txtPlate;
    private Button isplata;

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.plata, container, false);


        recyclerView = view.findViewById(R.id.plataRecyclerView);
        listaRadnika = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();

        txtVlasnik = (TextView) view.findViewById(R.id.txtVlasnikFinansije);
        txtPlate = (TextView) view.findViewById(R.id.txtRadniciPlate);
        isplata = (Button) view.findViewById(R.id.btnIsplatiPlate);

        vlasnikPlateRecycler = new VlasnikPlateRecycler(listaRadnika, txtVlasnik, txtPlate, isplata);


        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(vlasnikPlateRecycler);


        firebaseFirestore.collection("Users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                int plate=0;
                for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                    if(doc.getType() == DocumentChange.Type.ADDED) {

                        String tip = doc.getDocument().getString("tip");
                        String zaposljen = doc.getDocument().getString("zaposljen");

                        if (tip.matches("radnik") && zaposljen.matches("da")) { //ako je radnik i zaposljen dodaj u listu

                            String vlasnik = doc.getDocument().getString("vlasnik"); //zato sto ovde mora da ima ovo polje

                            if (vlasnik.matches(mAuth.getCurrentUser().getUid())) {//samo radnike koje ima vlasnik
                                Radnik r = doc.getDocument().toObject(Radnik.class);
                                listaRadnika.add(r);
                                plate +=Integer.parseInt(r.getPlata());

                                vlasnikPlateRecycler.notifyDataSetChanged();

                            }
                        }
                    }
                }
                txtPlate.setText(String.valueOf(plate));

            }
        });


        napuniPodatke();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if(user==null){
            startActivity(new Intent(getActivity(), MainActivity.class));
        }
    }

    public void napuniPodatke(){
        firebaseFirestore.collection("Gazdinstva").document(mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){

                          String finansije = task.getResult().getString("finansije");

                            String v = getString(R.string.vlasnikF);
                            txtVlasnik.setText(v + finansije);
                        }
                    }
                });
    }
}
