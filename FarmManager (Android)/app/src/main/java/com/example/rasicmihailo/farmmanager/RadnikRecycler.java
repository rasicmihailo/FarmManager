package com.example.rasicmihailo.farmmanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RadnikRecycler extends RecyclerView.Adapter<RadnikRecycler.ViewHolder> {

    private List<Radnik> lista;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private RelativeLayout relativeLayout;
    private TextView txtIme;
    private TextView txtPrez;
    private Button btnOtkazi;
    private Button btnSacuvaj;
    private TextView txtZanimanje;
    private EditText edtxtPlata;
    private boolean aktivan = false;
    private Button btnOtpusti;

    //objasnjeno je u zaposljavanjeActivity zasto ovakav konstruktor
    public RadnikRecycler(List<Radnik> listaRadnika, RelativeLayout rel, TextView t,
                          TextView p, TextView zan, Button sacuvaj, Button otkazi, EditText plata, Button btnOtp){

        this.lista = listaRadnika;
        this.relativeLayout = rel;
        this.txtIme=t;
        this.txtPrez = p;
        this.txtZanimanje = zan;
        this.edtxtPlata = plata;
        this.btnOtkazi = otkazi;
        this.btnSacuvaj  = sacuvaj;
        this.btnOtpusti = btnOtp;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.radnik_jedan_podatak, parent, false);
        context = parent.getContext();

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final String radnikId = lista.get(position).radnikID;

        final String ime = lista.get(position).getIme();
        final String prezime = lista.get(position).getPrezime();
        final String posao = lista.get(position).getZanimanje();
        final String plata = lista.get(position).getPlata();

        holder.setTxtPrikaz(ime + " " + prezime + ", " + posao + ", " + plata);


        holder.jedan_podatak.setOnClickListener(new View.OnClickListener() {

          //  final Map<String, String> mapObjekat = new HashMap<>();
            @Override
            public void onClick(View v) {

                if(!aktivan) { //ako se ne prikazuje trenutno radnik, prikazi, inace nemoj dok ne otkaze
                    firebaseFirestore.collection("Users").document(radnikId).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if (task.isSuccessful()) {

                                        if (task.getResult().exists()) {

                                            Radnik radnik = task.getResult().toObject(Radnik.class);

                                            txtIme.setText(radnik.getIme());
                                            txtPrez.setText(radnik.getPrezime());
                                            txtZanimanje.setText(radnik.getZanimanje());
                                            edtxtPlata.setText(radnik.getPlata());


                                            relativeLayout.setVisibility(View.VISIBLE);

                                            aktivan = true;

                                            btnSacuvaj.setOnClickListener(new View.OnClickListener() {
                                                                              @Override
                                                                              public void onClick(View v) {//sacuvaj radnika sa platom

                                                                                  String Novi = edtxtPlata.getText().toString();

                                                                                  firebaseFirestore.collection("Users").document(radnikId).update("plata", Novi);

                                                                              }
                                                                          });



                                                    btnOtpusti.setOnClickListener(new View.OnClickListener() {//otpusti radnika
                                                        final Map<String, String> mapObjekat = new HashMap<>();

                                                        @Override
                                                        public void onClick(View v) {


                                                            firebaseFirestore.collection("Users").document(radnikId).get()
                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                                            if(task.isSuccessful()){

                                                                                Radnik r = task.getResult().toObject(Radnik.class);

                                                                                Neradnik neradnik = new Neradnik("ne", r.getZanimanje(), r.getTip(), r.getPrezime(),
                                                                                        r.getJezik(), r.getIme(), r.getDatum());


                                                                                mapObjekat.put("zaposljen", "ne");
                                                                                mapObjekat.put("zanimanje", neradnik.getZanimanje());
                                                                                mapObjekat.put("tip", neradnik.getTip());
                                                                                mapObjekat.put("prezime", neradnik.getPrezime());
                                                                                mapObjekat.put("jezik", neradnik.getJezik());
                                                                                mapObjekat.put("ime", neradnik.getIme());
                                                                                mapObjekat.put("datum", neradnik.getDatum());

                                                                                firebaseFirestore.collection("Users").document(radnikId).set(mapObjekat);

                                                                                Activity activity = (Activity) context;
                                                                                FragmentManager fragmentManager = activity.getFragmentManager();

                                                                                fragmentManager.beginTransaction()
                                                                                        .replace(R.id.vlasnik_nav, new PregledajSvojeRadnike())
                                                                                        .commit();
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        } else {

                                        }

                                    } else {

                                    }


                                }
                            });
                }
            }
        });
        btnOtkazi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.INVISIBLE);
                aktivan=false;
            }
        });



    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtPrikaz;
        private View view;
        private TextView jedan_podatak;

        @SuppressLint("ResourceType")
        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            jedan_podatak = (TextView) view.findViewById(R.id.txtPrikaz);
        }

        public void setTxtPrikaz(String txt){

            txtPrikaz = view.findViewById(R.id.txtPrikaz);

            txtPrikaz.setText(txt);
        }
    }


}
