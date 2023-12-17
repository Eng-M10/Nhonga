package dev.muvi.nhonga.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dev.muvi.nhonga.R;
import dev.muvi.nhonga.adapter.AdapterEmpresa;
import dev.muvi.nhonga.helper.FirebaseConfig;
import dev.muvi.nhonga.listener.RecyclerItemClickListener;
import dev.muvi.nhonga.model.Advertiser;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth auth;
    MaterialSearchView searchView;
    private RecyclerView recyclerViewAdvertisers;
    private List<Advertiser> advertisers = new ArrayList<>();
    private DatabaseReference dbref;
    private AdapterEmpresa adapterEmpresa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().
                getColor(R.color.black_gray));
        setContentView(R.layout.activity_home);
        initComponents();
        searchView = findViewById(R.id.searchview);
        auth = FirebaseConfig.getReferenceAuth();
        dbref = FirebaseConfig.getReferenceFirebase();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        recyclerViewAdvertisers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdvertisers.setHasFixedSize(true);
        adapterEmpresa = new AdapterEmpresa(advertisers);
        recyclerViewAdvertisers.setAdapter(adapterEmpresa);

        retrieveAdvertisers();
        
        searchView.setHint("Pesquisar Anunciantes");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchAdvertisers(newText);
                return true;
            }
        });

        recyclerViewAdvertisers.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerViewAdvertisers,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Advertiser selected_adv = advertisers.get(position);
                                Intent i = new Intent(HomeActivity.this,ProductDetailsActivity.class);
                                i.putExtra("advertiser",selected_adv);
                                startActivity(i);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {


                            }

                            @Override
                            public void onItemLongClickConfirmed(View view, int position, String action) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );




    }

    private void searchAdvertisers(String pesquisa) {
        pesquisa = pesquisa.toLowerCase(Locale.ROOT);
        DatabaseReference adsRef = dbref.child("advertiser");

        Query query = adsRef.orderByChild("filter_name")
                .startAt(pesquisa)
                        .endAt(MessageFormat.format("{0}null", pesquisa));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                advertisers.clear();

                for(DataSnapshot ds: snapshot.getChildren()){
                    advertisers.add(ds.getValue(Advertiser.class));

                }

                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                adapterEmpresa.notifyDataSetChanged();
            }
        });


    }



    private void retrieveAdvertisers() {
            DatabaseReference adsRef = dbref.child("advertiser");

            adsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    advertisers.clear();

                    for(DataSnapshot ds : snapshot.getChildren()){
                        advertisers.add(ds.getValue(Advertiser.class));
                    }

                    adapterEmpresa.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


    }

    private void initComponents() {
        recyclerViewAdvertisers = (RecyclerView) findViewById(R.id.recyclerproductsAds);

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_users, menu);

        MenuItem item = menu.findItem(R.id.searchmenu);

    //    searchView
        searchView.setMenuItem(item);



        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.exit){
            logOut();
        } else if (item.getItemId() == R.id.config) {
            openConfig();
        }else if(item.getItemId() == R.id.newProduct){
            search();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logOut(){
        try{
            auth.signOut();
            finish();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void openConfig(){
        startActivity( new Intent(HomeActivity.this, UserConfigActivity.class));
    }
    private void search(){}

}