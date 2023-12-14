package dev.muvi.nhonga.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dev.muvi.nhonga.R;
import dev.muvi.nhonga.adapter.AdapterPedido;
import dev.muvi.nhonga.helper.FirebaseConfig;
import dev.muvi.nhonga.helper.UserFirebase;
import dev.muvi.nhonga.listener.RecyclerItemClickListener;
import dev.muvi.nhonga.model.ItemRequest;
import dev.muvi.nhonga.model.Request;
import dmax.dialog.SpotsDialog;

public class RequestActivity extends AppCompatActivity {

    private RecyclerView recyclerPedidos;
    private AdapterPedido adapterPedido;
    private List<Request> pedidos = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference dbref;
    private String advertiserID;
    private ItemRequest itemRequest;
    private  RecyclerItemClickListener.OnItemClickListener mListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().
                getColor(R.color.black_gray));
        setContentView(R.layout.activity_request);

        initComponents();
        dbref = FirebaseConfig.getReferenceFirebase();
        advertiserID = UserFirebase.getUserID();


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Requisições");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Configura recyclerview
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidos.setHasFixedSize(true);
        adapterPedido = new AdapterPedido(pedidos);
        recyclerPedidos.setAdapter( adapterPedido );

        retrieveRequests();


        recyclerPedidos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerPedidos,
                      mListener =  new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                                showConfirmationDialog(position);


                            }

                            @Override
                            public void onItemLongClickConfirmed(View view, int position, String action) {
                                if ("Despachar".equals(action)) {
                                    updateRequest(position);
                                }

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );


        
    }

    private void updateRequest(int position) {
        Request pedido = pedidos.get( position );
        pedido.setStatus("finalizado");
        pedido.atualizarStatus();

    }

    private void showConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Despachar "+ pedidos.get(position).getItens().get(position).getProduct_name() +" - para-  "+ pedidos.get(position).getNome())
                .setItems(new CharSequence[]{"Despachar"}, (dialog, which) -> {
                    String[] actions = {"Despachar"};
                    mListener.onItemLongClickConfirmed(null, position, actions[which]);
                })
                .setCancelable(true)
                .show();
    }



    private void retrieveRequests() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable( false )
                .build();
        dialog.show();

        DatabaseReference reqRef = dbref
                .child("request")
                .child(advertiserID);
        Query requestSearch = reqRef.orderByChild("status")
                .equalTo("Confirmado");


        requestSearch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                pedidos.clear();
                if( snapshot.getValue() != null ){
                    for (DataSnapshot ds: snapshot.getChildren()){
                        Request pedido = ds.getValue(Request.class);
                        pedidos.add(pedido);
                    }
                    adapterPedido.notifyDataSetChanged();
                    dialog.dismiss();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private void initComponents() {
        recyclerPedidos = findViewById(R.id.recyclerRequestsAds);
    }
}

