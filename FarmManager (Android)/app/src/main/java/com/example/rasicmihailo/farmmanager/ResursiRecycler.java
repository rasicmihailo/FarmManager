package com.example.rasicmihailo.farmmanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResursiRecycler extends RecyclerView.Adapter<ResursiRecycler.ViewHolder> {

    private List<Resurs> lista;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;


    private RelativeLayout relativeLayout2;
    private EditText editVrsta;
    private EditText editKolicina;
    private Button btnDodajStoku;
    private Button btnCancel;
    private Button btnDodaj;
    private Button btnObrisi;

    private boolean aktivan = false;

    public ResursiRecycler(List<Resurs> lista, RelativeLayout relativeLayout, EditText editVrsta, EditText editKolicina, Button btnDodajStoku, Button btnCancel, Button btnDodaj, Button btnObrisi) {
        this.lista = lista;
        this.relativeLayout2 = relativeLayout;
        this.editVrsta = editVrsta;
        this.editKolicina = editKolicina;
        this.btnDodajStoku = btnDodajStoku;
        this.btnCancel = btnCancel;
        this.btnDodaj = btnDodaj;
        this.btnObrisi = btnObrisi;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.resurs_jedan_podatak, parent, false);
        context = parent.getContext();

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final String vrsta = lista.get(position).getVrsta();

        final String kolicina = lista.get(position).getKolicina();

        final String tip = lista.get(position).getTip();

        final String idResursa = lista.get(position).resursID;

        String vr = context.getString(R.string.vrstaResurs);
        String kol = context.getString(R.string.kolicinaResurs);

        holder.setTxtResurs(vr + vrsta + "\n\n" + kol + kolicina);


            holder.txtResurs.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    firebaseFirestore.collection("Users").document(mAuth.getUid()).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if(task.isSuccessful()){

                                        String tipV = task.getResult().getString("tip");
                                        if(tipV.matches("vlasnik")){


                                            if(!aktivan) {

                                                relativeLayout2.setVisibility(View.VISIBLE);

                                                editVrsta.setText(vrsta);
                                                editKolicina.setText(kolicina);

                                                aktivan = true;



                                                btnDodaj.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {


                                                        final String vrstaa = editVrsta.getText().toString();
                                                        final String kolicinaa = editKolicina.getText().toString();
                                                        Map<String, String> mapObjekat = new HashMap<>();

                                                        mapObjekat.put("tip", tip);
                                                        mapObjekat.put("vrsta", vrstaa);
                                                        mapObjekat.put("kolicina", kolicinaa);


                                                        firebaseFirestore.collection("Gazdinstva/" + mAuth.getUid() + "/Resursi").document(idResursa).set(mapObjekat);


                                                        relativeLayout2.setVisibility(View.INVISIBLE);

                                                        aktivan = false;



                                                        Activity activity = (Activity) context;
                                                        FragmentManager fragmentManager = activity.getFragmentManager();

                                                        if (tip.matches("stoka"))
                                                            fragmentManager.beginTransaction()
                                                                    .replace(R.id.vlasnik_nav, new ResursiStoka())
                                                                    .commit();
                                                        else if (tip.matches("njive"))
                                                            fragmentManager.beginTransaction()
                                                                    .replace(R.id.vlasnik_nav, new ResursiNjive())
                                                                    .commit();
                                                        else if (tip.matches("plodovi"))
                                                            fragmentManager.beginTransaction()
                                                                    .replace(R.id.vlasnik_nav, new ResursiPlodovi())
                                                                    .commit();
                                                        else if (tip.matches("mehanizacija"))
                                                            fragmentManager.beginTransaction()
                                                                    .replace(R.id.vlasnik_nav, new ResursiMehanizacija())
                                                                    .commit();
                                                    }
                                                });


                                                btnCancel.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {


                                                        relativeLayout2.setVisibility(View.INVISIBLE);

                                                        aktivan = false;
                                                    }
                                                });

                                                btnObrisi.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        firebaseFirestore.collection("Gazdinstva/" + mAuth.getUid() + "/Resursi").document(idResursa).delete();

                                                        relativeLayout2.setVisibility(View.INVISIBLE);

                                                        aktivan = false;

                                                        Activity activity = (Activity) context;
                                                        FragmentManager fragmentManager = activity.getFragmentManager();

                                                        if (tip.matches("stoka"))
                                                            fragmentManager.beginTransaction()
                                                                    .replace(R.id.vlasnik_nav, new ResursiStoka())
                                                                    .commit();
                                                        else if (tip.matches("njive"))
                                                            fragmentManager.beginTransaction()
                                                                    .replace(R.id.vlasnik_nav, new ResursiNjive())
                                                                    .commit();
                                                        else if (tip.matches("plodovi"))
                                                            fragmentManager.beginTransaction()
                                                                    .replace(R.id.vlasnik_nav, new ResursiPlodovi())
                                                                    .commit();
                                                        else if (tip.matches("mehanizacija"))
                                                            fragmentManager.beginTransaction()
                                                                    .replace(R.id.vlasnik_nav, new ResursiMehanizacija())
                                                                    .commit();
                                                    }
                                                });


                                            }



                                        }else{


                                        }

                                    }

                                }
                            });


                     }

            });

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtResurs;
        private View view;

        @SuppressLint("ResourceType")
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            txtResurs = view.findViewById(R.id.txtResurs);

        }

        public void setTxtResurs(String txt){


            txtResurs.setText(txt);
        }
    }

}
