package com.example.rasicmihailo.farmmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PregledFarmiNeradnikRecycler  extends RecyclerView.Adapter<PregledFarmiNeradnikRecycler.ViewHolder>{

    private List<Gazdinstvo> lista;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;



    public PregledFarmiNeradnikRecycler(List<Gazdinstvo> lis){
        this.lista=lis;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pregled_gazdinstva_jedan_podatak, parent, false);
        context = parent.getContext();

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        return new PregledFarmiNeradnikRecycler.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        String naziv = lista.get(position).getNaziv();
        String lokacija = lista.get(position).getLokacija();
        final String idGazdinstva = lista.get(position).neradnikID;

        holder.postaviNazivLokaciju(naziv, lokacija);


        final Map<String, String> objekat = new HashMap<>();
        holder.btnKonkurisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.layout.setVisibility(View.VISIBLE); //otvara se layout za dodatne podatke
                holder.btnKonkurisi.setVisibility(View.INVISIBLE); //prethodno dugme se gasi

                holder.btnPosalji.setOnClickListener(new View.OnClickListener() {//f-ja kad se salje ponuda

                    @Override
                    public void onClick(View v) {

                        objekat.put("radnik", mAuth.getCurrentUser().getUid()); //vlasnikov id
                        objekat.put("vreme", holder.txtVreme.getText().toString()); //radnikovo predlozeno radno vreme
                        objekat.put("plata", holder.txtPlata.getText().toString()); //radnikova predlozena plata

                        firebaseFirestore.collection("Users/" + idGazdinstva + "/Konkurisu").add(objekat)
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {

                                        if(task.isSuccessful()){

                                            holder.layout.setVisibility(View.INVISIBLE);
                                            holder.btnKonkurisi.setVisibility(View.VISIBLE);

                                        }
                                    }
                                });
                    }
                });
            }

        });

        holder.btnOtkazi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.layout.setVisibility(View.INVISIBLE);
                holder.btnKonkurisi.setVisibility(View.VISIBLE);

            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private TextView txtNaziv;
        private TextView lokacija;
        private Button btnKonkurisi;
        private EditText txtVreme;
        private EditText txtPlata;
        private Button btnPosalji;
        private Button btnOtkazi;
        private ConstraintLayout layout;


        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            txtNaziv = (TextView) view.findViewById(R.id.txtNazivGazdinstvoJedno);
            lokacija = (TextView) view.findViewById(R.id.txtLokacijaGazdinstvoJedno);
            btnKonkurisi = (Button) view.findViewById(R.id.konkurisi);

            txtVreme = (EditText) view.findViewById(R.id.editTextRadnoVreme);
            txtPlata = (EditText) view.findViewById(R.id.editTextPlataKonkurs);
            btnPosalji = (Button) view.findViewById(R.id.posalji);
            btnOtkazi = (Button) view.findViewById(R.id.nemoj);
            layout = (ConstraintLayout) view.findViewById(R.id.layoutDodatno);

        }

        public void postaviNazivLokaciju(String naziv, String lok){

            txtNaziv.setText(naziv);
            lokacija.setText(lok);
        }
    }
}
