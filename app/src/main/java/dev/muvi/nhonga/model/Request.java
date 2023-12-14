package dev.muvi.nhonga.model;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import dev.muvi.nhonga.helper.FirebaseConfig;

public class Request {

    private String idUsuario;
    private String idEmpresa;
    private String idPedido;
    private String nome;
    private String endereco;
    private List<ItemRequest> itens;
    private Double total;
    private String status = "pendente";
    private int metodoPagamento = 0;
    private String observacao = "";

    public Request() {
    }

    public Request(String idUser ,String idAdv) {

        setIdUsuario(idUser);
        setIdEmpresa(idAdv);

        DatabaseReference dbref = FirebaseConfig.getReferenceFirebase();
        DatabaseReference reqRef = dbref
                .child("user_request")
                .child(idAdv)
                .child(idUser);
        setIdPedido(reqRef.push().getKey());

    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public List<ItemRequest> getItens() {
        return itens;
    }

    public void setItens(List<ItemRequest> itens) {
        this.itens = itens;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(int metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public void salvar() {

        DatabaseReference firebaseRef = FirebaseConfig.getReferenceFirebase();
        DatabaseReference pedidoRef = firebaseRef
                .child("user_request")
                .child( getIdEmpresa() )
                .child( getIdUsuario() );
        pedidoRef.setValue( this );

    }
}
