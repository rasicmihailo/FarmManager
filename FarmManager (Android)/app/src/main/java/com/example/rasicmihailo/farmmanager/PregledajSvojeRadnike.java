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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PregledajSvojeRadnike extends Fragment {

    View view;


    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private List<Radnik> listaRadnika;
    private RadnikRecycler radnikRecycler;
    private RelativeLayout relativeLayout;
    private TextView txtIme;
    private TextView txtPrez;
    //private TextView txtDat;
    // private TextView txtZap;
    private Button btnOtkazi;
    private Button btnCuvaj;
    private Button btnOtpusti;
    private Button btnIzmeniPlatu;
    private TextView txtZanimanje;
    private EditText txtPlata;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pregledaj_svoje_radnike, container, false);


        recyclerView =  view.findViewById(R.id.recPregledajSvojeRadnike);

        listaRadnika = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();
        relativeLayout = (RelativeLayout) view.findViewById(R.id.relPregledajSvojeRadnike);

        txtIme = (TextView)view.findViewById(R.id.txtime);
        txtPrez = (TextView)view.findViewById(R.id.txtprez);
        txtZanimanje = (TextView)view.findViewById(R.id.txtZanimanje);
        txtPlata = (EditText) view.findViewById(R.id.txtPlata);
        btnOtkazi = (Button) view.findViewById(R.id.btnOtkaziPregledajSvojeRadnike);
        btnCuvaj = (Button) view.findViewById(R.id.btnSacuvaj);
        btnOtpusti = (Button) view.findViewById(R.id.btnOtpustiRadnika);

        radnikRecycler = new RadnikRecycler(listaRadnika, relativeLayout,
                txtIme, txtPrez, txtZanimanje, btnCuvaj, btnOtkazi, txtPlata, btnOtpusti);

        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(radnikRecycler);

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

                                        radnikRecycler.notifyDataSetChanged();
                                    }
                                }else{

                                }
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
            Intent i = new Intent(getActivity(), MainActivity.class);
            startActivity(i);
        }
    }
}
