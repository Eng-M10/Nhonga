    package dev.muvi.nhonga.activity;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.appcompat.widget.Toolbar;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import android.app.AlertDialog;
    import android.content.Intent;
    import android.graphics.Color;
    import android.os.Bundle;
    import android.view.Menu;
    import android.view.MenuInflater;
    import android.view.MenuItem;
    import android.view.MotionEvent;
    import android.view.View;
    import android.widget.AdapterView;

    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.ValueEventListener;

    import java.util.ArrayList;
    import java.util.List;

    import dev.muvi.nhonga.R;
    import dev.muvi.nhonga.adapter.AdapterProduto;
    import dev.muvi.nhonga.helper.FirebaseConfig;
    import dev.muvi.nhonga.helper.UserFirebase;
    import dev.muvi.nhonga.listener.RecyclerItemClickListener;
    import dev.muvi.nhonga.model.Product;
    import dmax.dialog.SpotsDialog;

    public class AdvertiserActivity extends AppCompatActivity {

        FirebaseAuth auth;
        private RecyclerView recyclerViewProducts;
        private AdapterProduto adapterProduto;
        private List<Product> products = new ArrayList<>();
        private DatabaseReference databaseReference;
        private String usr;
        private AlertDialog alertDialog;
        private RecyclerItemClickListener.OnItemClickListener mListener;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getWindow().setStatusBarColor(getResources().
                    getColor(R.color.black_gray));
            setContentView(R.layout.activity_advertiser);
            initComponents();
            databaseReference = FirebaseConfig.getReferenceFirebase();
            auth = FirebaseConfig.getReferenceAuth();
            usr = UserFirebase.getUserID();
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Nhonga - Anunciates");
            setSupportActionBar(toolbar);

            //recycler
            recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this ));
            recyclerViewProducts.setHasFixedSize(true);
            adapterProduto = new AdapterProduto(products, this);
            recyclerViewProducts.setAdapter(adapterProduto);

            retriveProducts();

            recyclerViewProducts.addOnItemTouchListener(
                    new RecyclerItemClickListener(
                            this,
                            recyclerViewProducts,
                            mListener = new RecyclerItemClickListener.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {

                                    // Mostrar as Informacoes dos produtos



                                }

                                @Override
                                public void onLongItemClick(View view, int position) {

                                        showConfirmationDialog(position);
                                }

                                @Override
                                public void onItemLongClickConfirmed(View view, int position, String action) {
                                    if ("Apagar".equals(action)) {
                                        deleteProducts(position);
                                    }
                                }

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                }


                            }
                    )
            );


        }

        private void showConfirmationDialog(final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(products.get(position).getProduct_name())
                    .setItems(new CharSequence[]{"Apagar"}, (dialog, which) -> {
                        String[] actions = {"Apagar"};
                        mListener.onItemLongClickConfirmed(null, position, actions[which]);
                    })
                    .setCancelable(true)
                    .show();
        }




        private void deleteProducts(int position){
            Product selectedproduct = products.get(position);
            selectedproduct.removeOn();
        }



        private void retriveProducts() {
            DatabaseReference productsRef = databaseReference
                    .child("products")
                    .child(usr);

            productsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    products.clear();

                    for(DataSnapshot ds : snapshot.getChildren()){
                        products.add(ds.getValue(Product.class));
                    }

                    adapterProduto.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        public void initComponents(){
            recyclerViewProducts = findViewById(R.id.recyclerproductsAds);
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_ads, menu);
            return super.onCreateOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {

           if(item.getItemId() == R.id.exit){
               logOut();
           } else if (item.getItemId() == R.id.config) {
               openConfig();
           }else if(item.getItemId() == R.id.newProduct){
               openNewProduct();
           } else if (item.getItemId() == R.id.requests) {
               openRequests();
               
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
         startActivity( new Intent(AdvertiserActivity.this, AdsConfigActivity.class));
        }
        private void openNewProduct(){
            startActivity(new Intent(AdvertiserActivity.this, ProductActivity.class));
        }
        private void openRequests(){
            startActivity(new Intent(AdvertiserActivity.this, RequestActivity.class));
        }



    }