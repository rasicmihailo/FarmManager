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
import java.util.List;

public class PregledajPonudeNeradnik  extends Fragment{

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private List<Ponuda> listaPonuda;
    private RecyclerView recyclerView;
    private NeradnikPonudeRecycler ponudeRecycler;
    private TextView txtRadnoVr;
    private TextView txtPla;
    private TextView txtImePrez;
    private TextView txtNazivLok;
    private Button btnCancel;
    private Button btnPrihvati;

    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.pregledaj_ponude_neradnik, container,false);


        recyclerView = (RecyclerView) view.findViewById(R.id.pregledajPonude);
        listaPonuda = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();


        ponudeRecycler = new NeradnikPonudeRecycler(listaPonuda);

        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(ponudeRecycler);

        String neradnikId = mAuth.getCurrentUser().getUid();

        firebaseFirestore.collection("Users/" + neradnikId + "/Ponude")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                            if(doc.getType() == DocumentChange.Type.ADDED){

                               // Ponuda ponuda = doc.getDocument().toObject(Ponuda.class);
                                Ponuda ponuda = new Ponuda();
                                String vreme = doc.getDocument().getString("radnovreme");
                                String pl = doc.getDocument().getString("plata");
                                String vl = doc.getDocument().getString("vlasnik");

                                ponuda.setPlata(pl);
                                ponuda.setRadnovreme(vreme);
                                ponuda.setVlasnik(vl);

                                listaPonuda.add(ponuda);

                                ponudeRecycler.notifyDataSetChanged();
                            }
                        }

                    }
                });




        return view;
    }

    public void proveriZaposljenje(){

        firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    String zaposljen = task.getResult().getString("zaposljen");

                    if(zaposljen.matches("da")){

                        startActivity(new Intent(getActivity(), MainActivity.class));
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

        proveriZaposljenje();
    }

}
