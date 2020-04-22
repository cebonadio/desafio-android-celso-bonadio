package br.com.desafioandroid.desafioandroid.model;

import java.io.Serializable;

public class Revista implements Serializable {

    private String nome;

    private String descricao;

    private String url;

    private String preco;

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getPreco() {
        return preco;
    }
    public void setPreco(String preco) {
        this.preco = preco;
    }
}

