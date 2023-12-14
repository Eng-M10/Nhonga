package dev.muvi.nhonga.adapter;

import android.content.Context;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;


import dev.muvi.nhonga.R;
import dev.muvi.nhonga.model.Product;

/**
 * Created by Jamilton
 * Modified by Muvi
 */

public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder>{

    private List<Product> produtos;
    private Context context;

    public AdapterProduto(List<Product> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_produto, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Product produto = produtos.get(i);
        holder.nome.setText(produto.getProduct_name());
        holder.descricao.setText(produto.getProduct_description());
        holder.valor.setText("MZN$ " + produto.getProduct_price());


       if(produto.getImages().size() > 0){
           if(produto.getImages().size() == 1){
               Picasso.get()
                       .load(Uri.parse(produto.getImages().get(0)))
                       .into(holder.prod1);
               Picasso.get()
                       .load(Uri.parse(produto.getImages().get(0)))
                       .into(holder.prod2);
           }else{
               Picasso.get()
                       .load(Uri.parse(produto.getImages().get(0)))
                       .into(holder.prod1);
               Picasso.get()
                       .load(Uri.parse(produto.getImages().get(1)))
                       .into(holder.prod2);
           }
       }


    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome;
        TextView descricao;
        TextView valor;
        ImageView prod1,prod2;


        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textNomeRefeicao);
            descricao = itemView.findViewById(R.id.textDescricaoRefeicao);
            valor = itemView.findViewById(R.id.textPreco);
            prod1 = itemView.findViewById(R.id.imageProduto1);
            prod2 = itemView.findViewById(R.id.imageProduto2);
        }
    }
}
