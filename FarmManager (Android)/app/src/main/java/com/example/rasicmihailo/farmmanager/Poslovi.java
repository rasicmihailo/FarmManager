package com.example.rasicmihailo.farmmanager;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Poslovi extends Fragment {

    View view;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private List<Posao> listaPoslovi;
    private PosaoRecycler posaoRecycler;

    private RelativeLayout relativeLayout;

    private TextView txtIme;
    private TextView txtPrezime;
    private EditText txtOpis;

    private Button btnDodajPosao;
    private Button btnCancel;
    private Button btnDodaj;

    private boolean aktivan;



    PosaoRecyclerPomocni posaoRecyclerPomocni;
    List<Radnik> listaRadnika;
    RecyclerView recyclerView2;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.poslovi, container, false);

        aktivan = false;

        recyclerView = view.findViewById(R.id.posloviRec);
        listaPoslovi = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();


        btnDodajPosao = (Button) view.findViewById(R.id.buttonDodajPosao);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.relDodajPosao);
        txtIme = (TextView) view.findViewById(R.id.txtimeradnika);
        txtPrezime = (TextView) view.findViewById(R.id.txtprezimeradnika);
        txtOpis = (EditText) view.findViewById(R.id.txtopisposla);
        btnDodaj = (Button) view.findViewById(R.id.dozvoli);
        btnCancel = (Button) view.findViewById(R.id.nemoj);

        posaoRecycler = new PosaoRecycler(listaPoslovi);

        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(posaoRecycler);



        //

        listaRadnika = new ArrayList<>();
        posaoRecyclerPomocni = new PosaoRecyclerPomocni(listaRadnika, relativeLayout, txtIme, txtPrezime, btnDodaj, btnCancel);
        recyclerView2 = view.findViewById(R.id.recRadniciPosao);

        recyclerView2.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView2.setAdapter(posaoRecyclerPomocni);


        //

        firebaseFirestore.collection("Gazdinstva/" + mAuth.getUid() + "/Poslovi")
                .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                            if(doc.getType() == DocumentChange.Type.ADDED){


                                Posao posao = doc.getDocument().toObject(Posao.class);
                                listaPoslovi.add(posao);

                                posaoRecycler.notifyDataSetChanged();

                            }
                        }

                    }
                });






        btnDodajPosao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!aktivan){

                    //

                    firebaseFirestore.collection("Users")
                            .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                    for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                                        if(doc.getType() == DocumentChange.Type.ADDED){


                                            String tip = doc.getDocument().getString("tip");


                                            if(tip.matches("radnik") && doc.getDocument().getString("vlasnik")!=null ){
                                                String vlasnik = doc.getDocument().getString("vlasnik");
                                                if(vlasnik.matches(mAuth.getCurrentUser().getUid())){
                                                    String radnikId = doc.getDocument().getId();
                                                    Radnik radnik = doc.getDocument().toObject(Radnik.class).withId(radnikId);
                                                    listaRadnika.add(radnik);

                                                    posaoRecyclerPomocni.notifyDataSetChanged();
                                                }
                                            }else{

                                            }
                                        }
                                    }

                                }
                            });


                    //

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

                dodajPosao();

                relativeLayout.setVisibility(View.INVISIBLE);
                aktivan = false;
            }
        });



        return view;
    }


    public void dodajPosao(){



        final String ime = txtIme.getText().toString();
        final String prezime = txtPrezime.getText().toString();
        final String opis = txtOpis.getText().toString();


        firebaseFirestore.collection("Users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                            if(doc.getType() == DocumentChange.Type.ADDED){


                                String imee = doc.getDocument().getString("ime");
                                String prezimee = doc.getDocument().getString("prezime");
                                String vlasnikk = doc.getDocument().getString("vlasnik");

                                if(imee.matches(ime) && prezimee.matches(prezime) && vlasnikk.matches(mAuth.getUid())){

                                    Map<String, String> mapObjekat = new HashMap<>();

                                    mapObjekat.put("opis", opis);
                                    mapObjekat.put("radnik", doc.getDocument().getId());
                                    mapObjekat.put("zavrsen", "ne");
                                    mapObjekat.put("ime", ime);
                                    mapObjekat.put("prezime", prezime);


                                    firebaseFirestore.collection("Gazdinstva/" + mAuth.getUid() + "/Poslovi").document().set(mapObjekat);



                                }else{

                                }
                            }
                        }

                    }
                });

    }



    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if(user==null){
            Intent i = new Intent(getActivity(), MainActivity.class);
            startActivity(i);
        }
    }



}
