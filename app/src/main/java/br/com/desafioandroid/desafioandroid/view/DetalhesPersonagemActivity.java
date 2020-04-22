package br.com.desafioandroid.desafioandroid.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOError;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.desafioandroid.desafioandroid.R;
import br.com.desafioandroid.desafioandroid.adapter.ComicsAdapter;
import br.com.desafioandroid.desafioandroid.extras.Chaves;
import br.com.desafioandroid.desafioandroid.extras.Util;
import br.com.desafioandroid.desafioandroid.model.Revista;
import br.com.desafioandroid.desafioandroid.model.comics_.Exemplo;
import br.com.desafioandroid.desafioandroid.presenter.InicializarRetrofit;
import br.com.desafioandroid.desafioandroid.presenter.Services;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalhesPersonagemActivity extends AppCompatActivity {

    private String url;
    private String id;
    private String name;
    private String descricao;
    private List<String> comicsList;
    private List<Preco> precosList;
    private ArrayList<Revista> revistasList;
    Context context = DetalhesPersonagemActivity.this;
    Activity activity;
    private ImageView imageView;
    private TextView txt_personagem;
    private TextView txt_descricao;
    //private android.support.v7.widget.RecyclerView recyclerView;
    private Button buttonReload;
    private Button buttonRevCara;
    private LinearLayout loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        imageView = findViewById(R.id.det_img_personagem);
        txt_personagem = findViewById(R.id.det_txt_nome_personagem);
        txt_descricao = findViewById(R.id.det_txt_descricao_personagem);
        //recyclerView = findViewById(R.id.recyclerComics);
        ///loading = findViewById(R.id.details_loading);
        buttonReload = findViewById(R.id.details_Reload);
        buttonRevCara = findViewById(R.id.ver_revista_mais_cara);

        activity = DetalhesPersonagemActivity.this;

        Intent intent = getIntent();
        String idAdapter = intent.getStringExtra("id");
        String urlAdapter = intent.getStringExtra("url");
        String nameAdapter = intent.getStringExtra("name");
        String descricaoAdapter = intent.getStringExtra("descricao");

        if (idAdapter != null && urlAdapter != null && nameAdapter != null && descricaoAdapter != null) {
            id = idAdapter;
            url = urlAdapter;
            name = nameAdapter;
            descricao = descricaoAdapter;
        }

        Util util = new Util();

        txt_personagem.setText(name);
        txt_descricao.setText(descricao);


        Glide.with(this)
                .load(url)
                .apply(RequestOptions.centerCropTransform())
                .into(imageView);

        buttonRevCara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carregaLista(util);
            }
        });



        ///carregaLista(util);


    }

    private void carregaLista(final Util util) {
        try {
            InicializarRetrofit
                    .getGsonComics(id)
                    .create(Services.class)
                    .getComics(util.timestamp(), 100, Chaves.PUBLIC_KEY, util.md5())
                    .enqueue(new Callback<Exemplo>() {
                        @Override
                        public void onResponse(Call<Exemplo> call, Response<Exemplo> response) {

                            try {
                                if (response.body() != null) {
                                    response.body().getData().getCount();
                                    int total = Integer.parseInt(response.body().getData().getCount());
                                    buttonReload.setVisibility(View.GONE);

                                    comicsList = new ArrayList<>();
                                    precosList = new ArrayList<>();
                                    for (int i = 0; i < total; i++) {

                                        String extension = response.body().getData().getResults().get(i).getThumbnail().getExtension();
                                        String url = response.body().getData().getResults().get(i).getThumbnail().getPath() + "." + extension;

                                        List<String> precos;
                                        precos = new ArrayList<>();
                                        int tot_preco = response.body().getData().getResults().get(i).getPrices().size();
                                        for (int j = 0; j < tot_preco; j++) {
                                            String preco = response.body().getData().getResults().get(i).getPrices().get(j).getPrice();
                                            precos.add(preco);
                                        }
                                        if (precos.size() > 0) {
                                            precos.sort(String::compareToIgnoreCase);
                                            Preco ep = new Preco();
                                            ep.setIndice(i);
                                            ep.setPreco(precos.get(tot_preco - 1));
                                            precosList.add(ep);
                                        }
                                        comicsList.add(url);


                                        //if (comicsList.size() != 0) {
                                        //recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                                        //recyclerView.setAdapter(new ComicsAdapter(activity, comicsList));
                                        //loading.setVisibility(View.GONE);
                                        //}

                                    }
                                    if (precosList.size() > 0) {
                                        Collections.sort(precosList, new Comparator() {
                                            @Override
                                            public int compare(Object o1, Object o2) {
                                                Preco p1 = (Preco) o1;
                                                Preco p2 = (Preco) o2;
                                                return p1.getPreco().compareToIgnoreCase(p2.getPreco());
                                            }
                                        });
                                    }
                                    Preco maior_preco = precosList.get(precosList.size() - 1);
                                    System.out.println("Maior preco = " + maior_preco.getPreco());
                                    System.out.println("Indice = " + maior_preco.getIndice());
                                    int indice_rev = maior_preco.getIndice();
                                    ///Revista revista = new Revista();
                                    Revista revista = new Revista();
                                    String extension = response.body().getData().getResults().get(indice_rev).getThumbnail().getExtension();
                                    String url = response.body().getData().getResults().get(indice_rev).getThumbnail().getPath() + "." + extension;
                                    revista.setUrl(url);
                                    revista.setNome(response.body().getData().getResults().get(indice_rev).getTitle());
                                    revista.setDescricao(response.body().getData().getResults().get(indice_rev).getDescription());
                                    revista.setPreco(maior_preco.getPreco());
                                    //recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                                    //recyclerView.setAdapter(new ComicsAdapter(activity, comicsList));
                                    //loading.setVisibility(View.GONE);

                                    revistasList = new ArrayList<>();
                                    revistasList.add(revista);
                                    Intent goShowRev = new Intent(DetalhesPersonagemActivity.this, MostraRevistaActivity.class);
                                    //goShowRev.putCharSequenceArrayListExtra("lst_revistas",revistasList);
                                    //goShowRev.putExtra("lst_revistas",  revistasList);
                                    if (revista != null) {
                                        goShowRev.putExtra("revista", revista);
                                    }
                                    DetalhesPersonagemActivity.this.startActivity(goShowRev);

                                } else {
                                    errorAPI(util);
                                }


                            } catch (IOError error) {
                                Toast.makeText(context, "Não foi possível carregar os dados :(", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<Exemplo> call, Throwable t) {
                            System.out.println("Requisição errada!");
                            Toast.makeText(DetalhesPersonagemActivity.this, "Problemas com a conexão!", Toast.LENGTH_SHORT).show();
                            ///loading.setVisibility(View.GONE);
                            errorAPI(util);
                        }
                    });

        } catch (IOError e) {
            e.printStackTrace();
            Toast.makeText(activity, "Não foi possível carregar os dados :(", Toast.LENGTH_SHORT).show();
        }
    }

    private void errorAPI(final Util util) {
        Log.e("ERRO Response", "Erro ao carregar dados");
        Toast.makeText(activity, "Não foi possível carregar os dados :(", Toast.LENGTH_SHORT).show();
        buttonReload.setVisibility(View.VISIBLE);
        buttonReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carregaLista(util);
            }
        });
    }

    public class Preco {

        private int indice_revista;
        private String preco;

        public int getIndice() {
            return indice_revista;
        }

        public void setIndice(int indice) {
            this.indice_revista = indice;
        }

        public String getPreco() {
            return preco;
        }

        public void setPreco(String preco) {
            this.preco = preco;
        }
    }

    /*
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
*/

}

