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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PosloviRadnikActivity extends Fragment {
    View view;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;

    private List<Posao> listaPoslovi;
    private PosaoRecyclerRadnik posaoRecycler;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup containter, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.radnik_poslovi, containter, false);



        recyclerView = view.findViewById(R.id.posloviRecRadnik);
        listaPoslovi = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();



        posaoRecycler = new PosaoRecyclerRadnik(listaPoslovi);

        recyclerView.setLayoutManager(new LinearLayoutManager(containter.getContext()));
        recyclerView.setAdapter(posaoRecycler);





        firebaseFirestore.collection("Users").document(mAuth.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            if (task.getResult().exists()) {


                                String idVlasnika = task.getResult().getString("vlasnik");


                                firebaseFirestore.collection("Gazdinstva/" + idVlasnika + "/Poslovi")
                                        .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                                for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                                                    if(doc.getType() == DocumentChange.Type.ADDED){

                                                        String idRadnika = doc.getDocument().getString("radnik");

                                                        if(mAuth.getUid().matches(idRadnika)) {

                                                            Posao posao = doc.getDocument().toObject(Posao.class).withId(doc.getDocument().getId());
                                                            listaPoslovi.add(posao);

                                                            posaoRecycler.notifyDataSetChanged();
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



        return view;
    }






    public void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if(user==null){
            Intent i = new Intent(getActivity(), MainActivity.class);
            startActivity(i);
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
