package br.com.desafioandroid.desafioandroid.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOError;
import java.util.ArrayList;
import java.util.List;

import br.com.desafioandroid.desafioandroid.R;
import br.com.desafioandroid.desafioandroid.adapter.PersonagemAdapter;
import br.com.desafioandroid.desafioandroid.extras.Chaves;
import br.com.desafioandroid.desafioandroid.extras.Util;
import br.com.desafioandroid.desafioandroid.model.Example;
import br.com.desafioandroid.desafioandroid.model.Personagem;
import br.com.desafioandroid.desafioandroid.model.Result;
import br.com.desafioandroid.desafioandroid.presenter.PaginationScrollListener;
import br.com.desafioandroid.desafioandroid.presenter.InicializarRetrofit;
import br.com.desafioandroid.desafioandroid.presenter.Services;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrincipalActivity extends AppCompatActivity {

// MVP


    private android.support.v7.widget.RecyclerView recyclerView;
    private RelativeLayout loading;
    private TextView txtEmptyMessage;
    private Button buttonAtualizar;

    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 0;
    private int PAGE_SIZE = 10;
    private int currentPage = PAGE_START;

    private Util util;
    private long inicio = 0;

    PersonagemAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    List<Personagem> personagemList;
    SharedPreferences sharedPreferences;
    Activity activity;
    Context context = PrincipalActivity.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerPersonagens);
        loading = findViewById(R.id.main_loading);
        txtEmptyMessage = findViewById(R.id.main_txtEmptyMessage);
        buttonAtualizar = findViewById(R.id.main_buttonReload);
        activity = PrincipalActivity.this;
        loading.setVisibility(View.VISIBLE);

        currentPage = PAGE_START;
        isLoading = false;
        isLastPage = false;
        util = new Util();

        RelativeLayout item = (RelativeLayout)findViewById(R.id.main_relative);
        final View view = getLayoutInflater().inflate(R.layout.activity_main, null);
        item.addView(view);

        carregaLista(view);
    }

    private void carregaLista(final View view) {
        try{
        InicializarRetrofit
                .getGsonListCharacters()
                .create(Services.class)
                .lista(util.timestamp(),inicio, Chaves.PUBLIC_KEY, util.md5())
                .enqueue(new Callback<Example>() {
                    @Override
                    public void onResponse(Call<Example> call, Response<Example> response) {
                        personagemList = new ArrayList<>();

                        int total = Integer.parseInt(response.body().getData().getCount());

                        for (int i = 0; i < (total - 1); i++) {

                            String id = response.body().getData().getResults().get(i).getId();
                            String nome = response.body().getData().getResults().get(i).getName();
                            String descricao = response.body().getData().getResults().get(i).getDescription();
                            String thumbnail_url = response.body().getData().getResults().get(i).getThumbnail().getPath()
                                    + "." + response.body().getData().getResults().get(i).getThumbnail().getExtension();

                            sharedPreferences = PrincipalActivity.this.getSharedPreferences("personagem_information", Context.MODE_PRIVATE);
                            sharedPreferences.edit().putString("personagem_id", id).apply();
                            sharedPreferences.edit().putString("personagem_nome", nome).apply();
                            sharedPreferences.edit().putString("personagem_descricao", descricao).apply();
                            sharedPreferences.edit().putString("personagem_url", thumbnail_url).apply();

                            Personagem personagem = new Personagem(id, nome, descricao, thumbnail_url);

                            personagemList.add(personagem);


                        }


                        if (personagemList.size() != 0) {
                            adapter = new PersonagemAdapter(activity);
                            linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(adapter);

                            loading.setVisibility(View.GONE);
                            System.out.println("TESTE1 = " + currentPage);

                            recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
                                @Override
                                protected void loadMoreItems() {
//                                    progressBarRecycle.setVisibility(View.VISIBLE);
                                    isLoading = true;
                                    currentPage += 1;
                                    inicio += 20;
                                    loadPage(view);
                                }

                                @Override
                                public int getTotalPageCount() {
                                    return TOTAL_PAGES;
                                }

                                @Override
                                public boolean isLastPage() {
                                    return isLastPage;
                                }

                                @Override
                                public boolean isLoading() {
                                    return isLoading;
                                }
                            });

                            loadPage(view);


                        } else {
                            Toast.makeText(activity, "Não há personagens!", Toast.LENGTH_SHORT).show();
                            desabilitaProgress();
                        }
                    }



                    @Override
                    public void onFailure(Call<Example> call, Throwable t) {
                        Toast.makeText(PrincipalActivity.this, "Problemas com a conexão!", Toast.LENGTH_SHORT).show();
                        System.out.println("Requisição errada!");
                        desabilitaProgress();
                    }
                });
        } catch (IOError e){
            e.printStackTrace();
            Toast.makeText(activity, "Não foi possível carregar os dados :(", Toast.LENGTH_SHORT).show();
        }
    }

    private void desabilitaProgress() {
        loading.setVisibility(View.GONE);
    }

    private void loadPage(View view) {
                    loadData(view, currentPage);
                }

    private void loadData(final View view, final int page) {

        try {
            InicializarRetrofit
                    .getGsonListCharacters()
                    .create(Services.class)
                    .lista(util.timestamp(), inicio, Chaves.PUBLIC_KEY, util.md5())
                    .enqueue(new Callback<Example>() {
                        @Override
                        public void onResponse(Call<Example> call, Response<Example> response) {
                            if (response.body() != null) {

                                Example example = response.body();


                                if (Integer.parseInt(example.getData().getTotal()) > 0) {
                                    if (page == 0) {
                                        TOTAL_PAGES = (int) (Math.floor(Integer.parseInt(example.getData().getTotal()) / PAGE_SIZE));
                                        if (Integer.parseInt(example.getData().getTotal()) % PAGE_SIZE != 0) {
                                            TOTAL_PAGES++;
                                        }
                                    }

                                    adapter.addAll((ArrayList<Result>) example.getData().getResults());
                                    adapter.removeLoadingFooter();
                                    isLoading = false;
                                    buttonAtualizar.setVisibility(View.GONE);

                                    System.out.println("CurrentPage: " + currentPage);
                                    System.out.println("TOTAL_PAGES: " + TOTAL_PAGES);

                                    if (currentPage + 1 < TOTAL_PAGES) {
                                        adapter.addLoadingFooter();
                                    } else {
                                        isLastPage = true;
                                    }

                                } else {
                                    txtEmptyMessage.setVisibility(View.VISIBLE);
                                }
                            } else {
                                errorAPI(view);
                            }

                        }

                        @Override
                        public void onFailure(Call<Example> call, Throwable t) {
                            Toast.makeText(PrincipalActivity.this, "Problemas com a conexão!", Toast.LENGTH_SHORT).show();
                            errorAPI(view);
                            System.out.println("Requisição errada!");
                            desabilitaProgress();
                        }
                    });
        } catch (IOError e){
            e.printStackTrace();
            Toast.makeText(activity, "Não foi possível carregar os dados :(", Toast.LENGTH_SHORT).show();
        }


    }

    private void errorAPI(final View view) {
        Log.e("ERRO Response Timeline", "Erro ao carregar dados da timeline");
        Toast.makeText(activity, "Não foi possível carregar os dados :(", Toast.LENGTH_SHORT).show();
        buttonAtualizar.setVisibility(View.VISIBLE);
        buttonAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inicio = 0;
                carregaLista(view);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_toolbar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_pesquisar:

                Intent goSearch = new Intent(PrincipalActivity.this, BuscaPersonagemActivity.class);
                PrincipalActivity.this.startActivity(goSearch);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}