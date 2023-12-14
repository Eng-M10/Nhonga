package dev.muvi.nhonga.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dev.muvi.nhonga.R;
import dev.muvi.nhonga.model.ItemRequest;
import dev.muvi.nhonga.model.Request;

/**
 * Created by jamiltondamasceno
 */

public class AdapterPedido extends RecyclerView.Adapter<AdapterPedido.MyViewHolder> {

    private List<Request> pedidos;

    public AdapterPedido(List<Request> pedidos) {
        this.pedidos = pedidos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pedidos, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        Request pedido = pedidos.get(i);
        holder.nome.setText( pedido.getNome() );
        holder.endereco.setText( "Endereço: "+pedido.getEndereco() );
        holder.observacao.setText( "Obs: "+ pedido.getObservacao() );

        List<ItemRequest> itens = new ArrayList<>();
        itens = pedido.getItens();
        String descricaoItens = "";

        int numeroItem = 1;
        Double total = 0.0;
        for( ItemRequest itemPedido : itens ){

            int qtde = itemPedido.getQuantity();
            Double preco = itemPedido.getProduct_price();
            total += (qtde * preco);

            String nome = itemPedido.getProduct_name();
            descricaoItens += numeroItem + ") " + nome + " / (" + qtde + " x MZN$ " + preco + ") \n";
            numeroItem++;
        }
        descricaoItens += "Total: MZN$ " + total;
        holder.itens.setText(descricaoItens);

        int metodoPagamento = pedido.getMetodoPagamento();
        String pagamento = "A vista";
        
        switch (metodoPagamento){
            case 0:
                pagamento   = "A vista";
                break;
            case 1:
                pagamento = "M-pesa";
                break;
            case 2:
                pagamento = "SIMO-REDE";
                break;
            case 3:
                pagamento = "e-Mola";
                break;
        }
    
        holder.pgto.setText( "pgto: " + pagamento );

    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome;
        TextView endereco;
        TextView pgto;
        TextView observacao;
        TextView itens;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome        = itemView.findViewById(R.id.textPedidoNome);
            endereco    = itemView.findViewById(R.id.textPedidoEndereco);
            pgto        = itemView.findViewById(R.id.textPedidoPgto);
            observacao  = itemView.findViewById(R.id.textPedidoObs);
            itens       = itemView.findViewById(R.id.textPedidoItens);
        }
    }

}
