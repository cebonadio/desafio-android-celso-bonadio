package br.com.desafioandroid.desafioandroid.model;

public class Personagem {

//     MODEL:

    private String id;
    private String nome;
    private String descricao;
    private String url;

    public Personagem(String id, String nome, String descricao, String url){
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
        this.nome = descricao;
    }
}
