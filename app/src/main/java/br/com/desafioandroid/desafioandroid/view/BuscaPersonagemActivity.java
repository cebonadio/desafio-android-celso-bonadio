package br.com.desafioandroid.desafioandroid.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOError;
import java.util.ArrayList;
import java.util.List;

import br.com.desafioandroid.desafioandroid.R;
import br.com.desafioandroid.desafioandroid.adapter.Personagem1Adapter;
import br.com.desafioandroid.desafioandroid.extras.Chaves;
import br.com.desafioandroid.desafioandroid.extras.Util;
import br.com.desafioandroid.desafioandroid.model.Example;
import br.com.desafioandroid.desafioandroid.model.Personagem;
import br.com.desafioandroid.desafioandroid.presenter.InicializarRetrofit;
import br.com.desafioandroid.desafioandroid.presenter.Services;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuscaPersonagemActivity extends AppCompatActivity {

    private android.support.design.widget.TextInputEditText editPesquisa;
    private ImageView buttonSearch;
    private RelativeLayout loading;
    private android.support.v7.widget.RecyclerView recyclerView;

    SharedPreferences sharedPreferences;
    List<Personagem> personagemList;
    Context context = BuscaPersonagemActivity.this;
    Activity activity = BuscaPersonagemActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        editPesquisa = findViewById(R.id.edtPesquisa);
        buttonSearch = findViewById(R.id.search_button);
        loading = findViewById(R.id.search_loading);
        recyclerView = findViewById(R.id.recyclerSearch);


        final Util util = new Util();

        editPesquisa.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    apertaBuscar(util);
                    return true;
                }
                return false;
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apertaBuscar(util);
            }
        });





    }

    private void apertaBuscar(Util util) {
        try {
            verificaParaBusca(util);
        } catch (IOError error){
            error.printStackTrace();
            Toast.makeText(context, "Aguarde...", Toast.LENGTH_SHORT).show();
        }
    }

    private void verificaParaBusca(Util util) {
        String texto = editPesquisa.getText().toString();
        if (!texto.isEmpty()) {
            buscar(util, texto);
            loading.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(context, "Preencha o campo!", Toast.LENGTH_SHORT).show();
        }
    }

    public void buscar(final Util util, final String textoParaBuscar){
        try {
            InicializarRetrofit
                    .getGsonListCharacters()
                    .create(Services.class)
                    .getSearch(util.timestamp(), 100, textoParaBuscar, Chaves.PUBLIC_KEY, util.md5())
                    .enqueue(new Callback<Example>() {
                        @Override
                        public void onResponse(Call<Example> call, Response<Example> response) {
                            System.out.println("Requisição certa!");

                            personagemList = new ArrayList<>();

                            int total = Integer.parseInt(response.body().getData().getCount());


                            for (int i = 0; i < total; i++) {

                                String id = response.body().getData().getResults().get(i).getId();
                                String nome = response.body().getData().getResults().get(i).getName();
                                String descricao = response.body().getData().getResults().get(i).getDescription();
                                String thumbnail_url = response.body().getData().getResults().get(i).getThumbnail().getPath()
                                        + "." + response.body().getData().getResults().get(i).getThumbnail().getExtension();


                                sharedPreferences = BuscaPersonagemActivity.this.getSharedPreferences("personagem_information", Context.MODE_PRIVATE);
                                sharedPreferences.edit().putString("personagem_id", id).apply();
                                sharedPreferences.edit().putString("personagem_nome", nome).apply();
                                sharedPreferences.edit().putString("personagem_descricao", descricao).apply();
                                sharedPreferences.edit().putString("personagem_url", thumbnail_url).apply();

                                Personagem personagem = new Personagem(id, nome, descricao, thumbnail_url);

                                personagemList.add(personagem);



                                if (personagemList.size() != 0) {
                                    recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                                    recyclerView.setAdapter(new Personagem1Adapter(activity, personagemList));
                                    loading.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(context, "Nenhum personagem encontrado!", Toast.LENGTH_SHORT).show();
                                }


                            }

                        }

                        @Override
                        public void onFailure(Call<Example> call, Throwable t) {
                            Toast.makeText(BuscaPersonagemActivity.this, "Problemas com a conexão!", Toast.LENGTH_SHORT).show();
                            System.out.println("Requisição errada!");
                            loading.setVisibility(View.GONE);
                        }
                    });
        } catch (IOError e){
            e.printStackTrace();
            Toast.makeText(activity, "Não foi possível carregar os dados :(", Toast.LENGTH_SHORT).show();
        }
    }
}
