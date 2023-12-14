package dev.muvi.nhonga.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import dev.muvi.nhonga.R;
import dev.muvi.nhonga.adapter.AdapterProduto;
import dev.muvi.nhonga.helper.FirebaseConfig;
import dev.muvi.nhonga.helper.UserFirebase;
import dev.muvi.nhonga.listener.RecyclerItemClickListener;
import dev.muvi.nhonga.model.Advertiser;
import dev.muvi.nhonga.model.ItemRequest;
import dev.muvi.nhonga.model.Product;
import dev.muvi.nhonga.model.Request;
import dev.muvi.nhonga.model.User;
import dmax.dialog.SpotsDialog;

public class ProductDetailsActivity extends AppCompatActivity {

    private TextView txtAdvertiserNameProduct,textQtd,textTotal;
    private ImageView imgAdvertiserProduct, imgProduct1 , imgProduct2;
    private Advertiser selectedAdvertiser;
    private RecyclerView recyclerViewProducts;
    private DatabaseReference dbref;
    private AlertDialog dialog;
    private AdapterProduto adapterProduto;
    private String logeduserID;
    private List<Product> products = new ArrayList<>();
    private List<ItemRequest> itemsrequested = new ArrayList<>();
    private String advertiserID;
    private User user;
    private Request requestRetrived;

    private int QtdItems;
    private Double totalRequested;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().
                getColor(R.color.black_gray));
        setContentView(R.layout.activity_product_details);

        initComponents();
        dbref = FirebaseConfig.getReferenceFirebase();
        logeduserID = UserFirebase.getUserID();


        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            selectedAdvertiser = (Advertiser) bundle.getSerializable("advertiser");

            txtAdvertiserNameProduct.setText(selectedAdvertiser.getName());
            advertiserID = selectedAdvertiser.getUser_id();
            String url_img = selectedAdvertiser.getImg_url();

            Picasso.get().load(url_img).into(imgAdvertiserProduct);

        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Produtos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProducts.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(products,this);
        recyclerViewProducts.setAdapter(adapterProduto);

        //Click on RecyclerView

        recyclerViewProducts.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerViewProducts,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                confirmQtd(position);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemLongClickConfirmed(View view, int position, String action) {

                            }
                        }

                )
        );





        //
        retriveProduts();

        //Retrive User Data
        retriveUserData();

    }

    private void confirmQtd(int position) {
        EditText edtQtd = new EditText(this);
        edtQtd.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtQtd.setText("1");
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogCustom);
        builder.setTitle("Quantidade")
        .setMessage("Digite a quantidade")
        .setView(edtQtd)
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String qtd = edtQtd.getText().toString();

                if(!qtd.isEmpty() && !qtd.equals("0") && !qtd.contains("-")){
                    Product selectedProduct = products.get(position);
                    ItemRequest itemRequest = new ItemRequest();

                        int i = Integer.parseInt(qtd);
                    boolean itemExists = false;
                    for (ItemRequest existingItem : itemsrequested) {
                        if (existingItem.getProductID().equals(selectedProduct.getProduct_id())) {
                            // Item já existe, apenas atualize a quantidade
                            existingItem.setQuantity(existingItem.getQuantity() + i);
                            itemExists = true;
                            break;
                        }
                    }

                    if (!itemExists) {
                        // Item não existe na lista, adicione um novo
                        itemRequest.setProductID(selectedProduct.getProduct_id());
                        itemRequest.setProduct_name(selectedProduct.getProduct_name());
                        itemRequest.setProduct_price(selectedProduct.getProduct_price().toString());
                        itemRequest.setQuantity(i);
                        itemsrequested.add(itemRequest);
                    }

                    if(requestRetrived == null){
                        requestRetrived = new Request(logeduserID,advertiserID);

                    }

                    requestRetrived.setNome(user.getName());
                    requestRetrived.setEndereco(user.getAddress());
                    requestRetrived.setItens(itemsrequested);
                    requestRetrived.salvar();


                }else{
                    Toast.makeText(ProductDetailsActivity.this,"Adicione um número válido",Toast.LENGTH_LONG).show();
                }

            }
        })

        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create();


       builder.show();



    }

    private void retriveUserData() {


        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable( false )
                .build();
        dialog.show();

        DatabaseReference userRef = dbref
                .child("user")
                .child(logeduserID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue() != null){

                    user = snapshot.getValue(User.class);

                }

                retriveRequest();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void retriveRequest() {

        DatabaseReference reqRef = dbref.child("user_request")
                        .child(advertiserID)
                                .child(logeduserID);

        reqRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue() != null){
                    requestRetrived = snapshot.getValue(Request.class);

                    itemsrequested = requestRetrived.getItens();

                    QtdItems = 0;
                    totalRequested =0.0;

                    for(ItemRequest item : itemsrequested){
                        int qtde = item.getQuantity();
                        Double price = item.getProduct_price();

                        totalRequested += (qtde * price);
                        QtdItems += qtde;


                    }

                }

                DecimalFormat df = new DecimalFormat("0.00");

                textQtd.setText("qtd: "+String.valueOf(QtdItems));
                textTotal.setText("MZN$" +df.format(totalRequested));


                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






    }

    private void retriveProduts() {

        DatabaseReference productsRef = dbref
                .child("products")
                .child(advertiserID);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_details_product, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.confirmRequest){

            //Method to comfirm Request


        }

        return super.onOptionsItemSelected(item);
    }


    private void initComponents(){
        recyclerViewProducts = findViewById(R.id.recyclerProductsDetails);
        txtAdvertiserNameProduct = findViewById(R.id.textNameAdvertiserDetails);
        imgAdvertiserProduct = findViewById(R.id.imageAvertiserDetails);


        textQtd = findViewById(R.id.textQtd);
        textTotal = findViewById(R.id.textTotal);
    }

}


