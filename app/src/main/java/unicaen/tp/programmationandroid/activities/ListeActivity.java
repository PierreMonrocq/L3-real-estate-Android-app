package unicaen.tp.programmationandroid.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import unicaen.tp.programmationandroid.R;
import unicaen.tp.programmationandroid.activities.affichage.RecyclerAdapter;
import unicaen.tp.programmationandroid.activities.listeners.ClickListener;
import unicaen.tp.programmationandroid.classes.Propriete;
import unicaen.tp.programmationandroid.classes.Vendeur;
import unicaen.tp.programmationandroid.db.Db;
import unicaen.tp.programmationandroid.db.DbContract;
import unicaen.tp.programmationandroid.db.DbOpener;
import unicaen.tp.programmationandroid.http.ApiListProprieteAdapter;

public class ListeActivity extends AppCompatActivity {

    private RecyclerAdapter adapter;
    private List<Propriete> proprietes;
    private DbOpener dbo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.string_tab_annonces));

        this.dbo = new DbOpener(getApplicationContext(), DbContract.BD_NAME,null,DbContract.BD_VERSION);


        // Récupération de la requête avec moshi

        Moshi moshi = new Moshi.Builder().add(new ApiListProprieteAdapter()).build();
        Type type = Types.newParameterizedType(List.class, Propriete.class);
        JsonAdapter<List<Propriete>> jsonAdapter = moshi.adapter(type);

        try {

            Intent intent = getIntent();
            String s = intent.getStringExtra("ProprieteList");

            proprietes = jsonAdapter.fromJson(s);


        } catch (IOException e) {
            Log.i("Connexion", "Erreur I/O");
        }

        RecyclerView recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        ClickListener clickListener = new ClickListener() {

            @Override
            public void onPositionClicked(int position, Activity activity) {

                Intent intent = new Intent(activity, VisualisationActivity.class);
                intent.putExtra("Propriete", proprietes.get(position));
                startActivity(intent);
            }
        };

        adapter = new RecyclerAdapter(proprietes, clickListener,this);
        recycler.setAdapter(adapter);
        this.dbo.close();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        dbo.close();
    }
}
