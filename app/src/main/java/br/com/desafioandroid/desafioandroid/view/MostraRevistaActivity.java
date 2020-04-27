package br.com.desafioandroid.desafioandroid.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

import br.com.desafioandroid.desafioandroid.R;
import br.com.desafioandroid.desafioandroid.adapter.ComicsAdapter;
import br.com.desafioandroid.desafioandroid.model.Revista;

public class MostraRevistaActivity extends AppCompatActivity {

    ///private android.support.v7.widget.RecyclerView recyclerView;
    private ArrayList<Revista> revistas;
    private List<String> comicsList;
    Context context = MostraRevistaActivity.this;
    Activity activity = MostraRevistaActivity.this;
    private ImageView img_revista; //det_img_revista;
    private TextView nome_revista; //det_txt_nome_revista;
    private TextView preco_revista; //det_txt_preco_revista;
    private TextView descricao_revista; //det_txt_descricao_revista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mostra_revista);
        img_revista = findViewById(R.id.det_img_revista);
        nome_revista = findViewById(R.id.det_txt_nome_revista);
        preco_revista = findViewById(R.id.det_txt_preco_revista);
        descricao_revista = findViewById(R.id.det_txt_descricao_revista);
        activity = MostraRevistaActivity.this;

        comicsList = new ArrayList<>();
        revistas = new ArrayList<>();
        ///revistas = (ArrayList<Revista>) getIntent().getSerializableExtra("lst_revistas");
        Revista revista = (Revista) getIntent().getSerializableExtra("revista");

        if (revista != null) {
            String preco = "Preço: " + revista.getPreco();
            Glide.with(this)
                    .load(revista.getUrl())
                    //.apply(RequestOptions.centerCropTransform())
                    .apply(RequestOptions.centerInsideTransform())
                    .into(img_revista);

            nome_revista.setText(revista.getNome());
            preco_revista.setText(preco);
            descricao_revista.setMovementMethod(new ScrollingMovementMethod());
            descricao_revista.setText(revista.getDescricao());

        }

    }
}
