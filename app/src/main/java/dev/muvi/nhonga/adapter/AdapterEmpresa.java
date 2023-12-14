package dev.muvi.nhonga.adapter;



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
import dev.muvi.nhonga.model.Advertiser;

/**
 * Created by Jamilton
 * Modified by Muvi
 */

public class AdapterEmpresa extends RecyclerView.Adapter<AdapterEmpresa.MyViewHolder> {

    private List<Advertiser> empresas;

    public AdapterEmpresa(List<Advertiser> empresas) {
        this.empresas = empresas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_empresa, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Advertiser empresa = empresas.get(i);
        holder.nomeEmpresa.setText(empresa.getName());
        holder.categoria.setText("Categoria : "+empresa.getCategory());
        holder.endereco.setText("Endereço : "+empresa.getAddress());

        //Carregar imagem
        String urlImagem = empresa.getImg_url();
        Picasso.get().load( urlImagem ).into( holder.imagemEmpresa );

    }

    @Override
    public int getItemCount() {
        return empresas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imagemEmpresa;
        TextView nomeEmpresa;
        TextView categoria;
        TextView endereco;

        public MyViewHolder(View itemView) {
            super(itemView);

            nomeEmpresa = itemView.findViewById(R.id.textNomeEmpresa);
            categoria = itemView.findViewById(R.id.textCategoriaEmpresa);
            endereco = itemView.findViewById(R.id.textEndereco);
            imagemEmpresa = itemView.findViewById(R.id.imageEmpresa);
        }
    }
}
