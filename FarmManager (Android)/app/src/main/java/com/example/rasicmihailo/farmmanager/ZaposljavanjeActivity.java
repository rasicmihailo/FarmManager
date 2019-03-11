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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZaposljavanjeActivity extends Fragment {
    View view;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private Map<Integer, Neradnik> listaNeradnika;
    private NeradnikRecycler neradnikRecycler;
    private RelativeLayout relativeLayout;
    private TextView txtIme;
    private TextView txtPrez;
    private TextView txtDat;
    private TextView txtZap;
    private TextView txtPlata;
    private TextView txtVreme;
    private Button btnCancel;
    private Button btnZaposli;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.zaposljavanje, container, false);


        recyclerView = view.findViewById(R.id.zapRec);


        listaNeradnika = new HashMap<>();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();


        relativeLayout = (RelativeLayout) view.findViewById(R.id.relProba);


        txtIme = (TextView)view.findViewById(R.id.txtime);
        txtPrez = (TextView)view.findViewById(R.id.txtprez);
        txtDat = (TextView)view.findViewById(R.id.txtdatum);
        txtZap = (TextView)view.findViewById(R.id.txtzan);
        txtPlata = (TextView)view.findViewById(R.id.plata);
        txtVreme = (TextView)view.findViewById(R.id.radnoVreme);

        btnCancel = (Button) view.findViewById(R.id.deny);
        btnZaposli = (Button) view.findViewById(R.id.allow);






//mora kroz konstruktor da se salje, onamo nece jer nije isti view ovde i tamo, iz ovaj view moze bilo sta da se pribavi,
        //dok iz klasu neradnikRecycler ne moze, samo iz radnik_jedan_podatak
        neradnikRecycler = new NeradnikRecycler(listaNeradnika, relativeLayout,
                txtIme, txtPrez, txtDat, txtZap, btnCancel, btnZaposli, txtPlata, txtVreme);


        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(neradnikRecycler);



                firebaseFirestore.collection("Users")
                        .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                int i=0;
                                for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                                    if(doc.getType() == DocumentChange.Type.ADDED){

                                        String tip = doc.getDocument().getString("tip");
                                        String zaposljen = doc.getDocument().getString("zaposljen");
                                        if(tip.matches("radnik") && zaposljen.matches("ne")){

                                            String neradnikID = doc.getDocument().getId();
                                            Neradnik neradnik = doc.getDocument().toObject(Neradnik.class).withId(neradnikID,i);
                                            listaNeradnika.put(i, neradnik);
                                            i++;
                                            neradnikRecycler.notifyDataSetChanged();

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
