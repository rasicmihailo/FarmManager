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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PregledFarmiNeradnik extends Fragment {

    View view;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private List<Gazdinstvo> lista;
    private PregledFarmiNeradnikRecycler pregledFarmiNeradnikRecycler;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.neradnik_pregled_farmi, container, false);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = (RecyclerView)view.findViewById(R.id.recViewPregledGazdinstva);

        lista = new ArrayList<>();


        pregledFarmiNeradnikRecycler = new PregledFarmiNeradnikRecycler(lista);

        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(pregledFarmiNeradnikRecycler);


        firebaseFirestore.collection("Gazdinstva").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if(task.isSuccessful()){

                             for(DocumentSnapshot doc : task.getResult()){
                                    Gazdinstvo gazdinstvo = doc.toObject(Gazdinstvo.class).withId(doc.getId());

                                    lista.add(gazdinstvo);

                                    pregledFarmiNeradnikRecycler.notifyDataSetChanged();
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
