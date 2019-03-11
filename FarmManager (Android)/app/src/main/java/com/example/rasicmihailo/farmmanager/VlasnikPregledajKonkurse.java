package com.example.rasicmihailo.farmmanager;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class VlasnikPregledajKonkurse extends Fragment{


    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private List<Konkurs> listaKonkursa;
    private RecyclerView recyclerView;
    private VlasnikKonkursRecycler vlasnikKonkursRecycler;


    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.vlasnik_pregledaj_konkurse, container,false);


        recyclerView = (RecyclerView) view.findViewById(R.id.vlasnikKonkursiRecView);
        listaKonkursa = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();

        vlasnikKonkursRecycler = new VlasnikKonkursRecycler(listaKonkursa);

        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(vlasnikKonkursRecycler);


        firebaseFirestore.collection("Users/" + mAuth.getCurrentUser().getUid() + "/Konkurisu")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                Konkurs konkurs = doc.getDocument().toObject(Konkurs.class);
                                listaKonkursa.add(konkurs);

                                vlasnikKonkursRecycler.notifyDataSetChanged();
                            }
                        }
                    }
                });




        return view;
    }
}
