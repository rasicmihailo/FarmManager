package com.example.rasicmihailo.farmmanager;

import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

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

public class ResursiPlodovi extends Fragment {
    View view;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;

    private List<Resurs> listaResursa;
    private ResursiRecycler resursiPlodoviRecycler;

    private RelativeLayout relativeLayout;
    private RelativeLayout relativeLayout2;
    private EditText editVrsta;
    private EditText editVrsta2;
    private EditText editKolicina;
    private EditText editKolicina2;
    private Button btnDodajPlodove;
    private Button btnCancel;
    private Button btnCancel2;
    private Button btnDodaj;
    private Button btnDodaj2;

    private Button btnObrisi2;


    private boolean aktivan;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_resursi_plodovi, container, false);

        aktivan = false;

        recyclerView = view.findViewById(R.id.resursRec);
        listaResursa = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();


        btnDodajPlodove = (Button) view.findViewById(R.id.buttonDodajResurs);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.relDodajResurs);
        relativeLayout2 = (RelativeLayout) view.findViewById(R.id.relDodajResurs2);

        editVrsta = (EditText) view.findViewById(R.id.editVrsta);
        editVrsta2 = (EditText) view.findViewById(R.id.editVrsta2);
        editKolicina = (EditText) view.findViewById(R.id.editKolicina);
        editKolicina2 = (EditText) view.findViewById(R.id.editKolicina2);

        btnDodaj = (Button) view.findViewById(R.id.allow);
        btnDodaj2 = (Button) view.findViewById(R.id.allow2);
        btnCancel = (Button) view.findViewById(R.id.deny);
        btnCancel2 = (Button) view.findViewById(R.id.deny2);


        btnObrisi2 = (Button) view.findViewById(R.id.delete2);


        resursiPlodoviRecycler = new ResursiRecycler(listaResursa, relativeLayout2, editVrsta2, editKolicina2, btnDodajPlodove, btnCancel2, btnDodaj2, btnObrisi2);

        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(resursiPlodoviRecycler);

        firebaseFirestore.collection("Gazdinstva").document(mAuth.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {

                            firebaseFirestore.collection("Gazdinstva/" + mAuth.getUid() + "/Resursi")
                                    .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                            for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                                                if(doc.getType() == DocumentChange.Type.ADDED){



                                                    String tip = doc.getDocument().getString("tip");

                                                    if(tip.matches("plodovi")) {

                                                        Resurs resurs = doc.getDocument().toObject(Resurs.class).withId(doc.getDocument().getId());
                                                        listaResursa.add(resurs);

                                                        resursiPlodoviRecycler.notifyDataSetChanged();
                                                    }
                                                }
                                            }

                                        }
                                    });


                        }else {

                            nadjiResurseZaRadnika();

                        }
                    }
                });


        btnDodajPlodove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!aktivan){
                    relativeLayout.setVisibility(View.VISIBLE);
                    aktivan = true;
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.INVISIBLE);
                aktivan = false;
            }
        });

        btnDodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dodajResurs();

                relativeLayout.setVisibility(View.INVISIBLE);
                aktivan = false;
            }
        });


        return view;
    }

    public void dodajResurs(){



        final String vrsta = editVrsta.getText().toString();
        final String kolicina = editKolicina.getText().toString();




        final Map<String, String> mapObjekat = new HashMap<>();

        mapObjekat.put("tip", "plodovi");
        mapObjekat.put("vrsta", vrsta);
        mapObjekat.put("kolicina", kolicina);
        firebaseFirestore.collection("Users").document(mAuth.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){

                            String tip = task.getResult().getString("tip");
                            if(tip.matches("vlasnik")){
                                firebaseFirestore.collection("Gazdinstva/" + mAuth.getUid() + "/Resursi").document().set(mapObjekat);
                            }else{
                                String vlasnik = task.getResult().getString("vlasnik");
                                firebaseFirestore.collection("Gazdinstva/" + vlasnik + "/Resursi").document().set(mapObjekat);

                            }

                        }

                    }
                });

    }


    public void nadjiResurseZaRadnika(){

        firebaseFirestore.collection("Users").document(mAuth.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            if (task.getResult().exists()) {


                                String idVlasnika = task.getResult().getString("vlasnik");




                                firebaseFirestore.collection("Gazdinstva/" + idVlasnika + "/Resursi")
                                        .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                                for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                                                    if(doc.getType() == DocumentChange.Type.ADDED){



                                                        String tip = doc.getDocument().getString("tip");

                                                        if(tip.matches("plodovi")) {

                                                            Resurs resurs = doc.getDocument().toObject(Resurs.class).withId(doc.getDocument().getId());
                                                            listaResursa.add(resurs);

                                                            resursiPlodoviRecycler.notifyDataSetChanged();
                                                        }
                                                    }
                                                }

                                            }
                                        });



                            } else {

                            }

                        } else {

                        }


                    }
                });

    }

    public void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if(user==null){
            Intent i = new Intent(getActivity(), MainActivity.class);
            startActivity(i);
        }
    }
}
