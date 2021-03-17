package unicaen.tp.programmationandroid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.lang.reflect.Type;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import unicaen.tp.programmationandroid.activities.BienvenueActivity;
import unicaen.tp.programmationandroid.activities.DeposerBienActivity;
import unicaen.tp.programmationandroid.activities.ListeActivity;
import unicaen.tp.programmationandroid.activities.ProfilActivity;
import unicaen.tp.programmationandroid.activities.VisualisationActivity;
import unicaen.tp.programmationandroid.activities.affichage.RecyclerAdapter;
import unicaen.tp.programmationandroid.activities.listeners.ClickListener;
import unicaen.tp.programmationandroid.activities.listeners.ConnexionCallback;
import unicaen.tp.programmationandroid.classes.Propriete;
import unicaen.tp.programmationandroid.classes.Vendeur;
import unicaen.tp.programmationandroid.db.Db;
import unicaen.tp.programmationandroid.db.DbContract;
import unicaen.tp.programmationandroid.db.DbOpener;
import unicaen.tp.programmationandroid.http.ApiListProprieteAdapter;
import unicaen.tp.programmationandroid.http.ConnexionListManager;
import unicaen.tp.programmationandroid.http.ConnexionManager;
import unicaen.tp.programmationandroid.http.ConnexionRecent;

public class MainActivity extends AppCompatActivity implements ConnexionCallback {

    private final OkHttpClient client = new OkHttpClient();
    private DbOpener dbo;
    private Db db;
    private RecyclerAdapter adapter;
    private RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Ecran bienvenue utilisateur
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.getBoolean(
                BienvenueActivity.COMPLETED_ONBOARDING_PREF_NAME, false)) {
            startActivity(new Intent(this, BienvenueActivity.class));
        }

        //Initialisation db
        this.dbo = new DbOpener(getApplicationContext(), DbContract.BD_NAME,null,DbContract.BD_VERSION);
        this.db = new Db(this.dbo);

        // Récupération de la requête avec moshi
        Moshi moshi = new Moshi.Builder().add(new ApiListProprieteAdapter()).build();
        Type type = Types.newParameterizedType(List.class, Propriete.class);
        JsonAdapter<List<Propriete>> jsonAdapter = moshi.adapter(type);

        //Requete pour les dernieres annonces de la page d'accueil
        ConnexionRecent connexionRecent = new ConnexionRecent(this, this.db, this);
        Request request2 = new Request.Builder().url("https://android-estate/mock-api/dernieres.json").build();
        client.newCall(request2).enqueue(connexionRecent);

        {
            recycler = findViewById(R.id.recycler_main);
            recycler.setLayoutManager(new LinearLayoutManager(this));
            recycler.setNestedScrollingEnabled(false);
            //Adapter initialisé pour le onResume()
            adapter = new RecyclerAdapter(null, null, this);
        }

        {
            Vendeur utilisateur = db.obtenirUtilisateur();
            if(utilisateur == null) {
                utilisateur = new Vendeur();
                db.ajouterUtilisateur(utilisateur);
            }
        }

        this.dbo.close();
    }

    //Vue liste
    public void lancerActiviteListe(View v){
        ConnexionListManager connexionList = new ConnexionListManager(this, ListeActivity.class);

        Request request2 = new Request.Builder().url("https://android-estate/mock-api/liste.json").build();
        client.newCall(request2).enqueue(connexionList);
    }

    //Vue deposer bien
    public void lancerActiviteDeposer(View v){
        Intent intent = new Intent(this, DeposerBienActivity.class);
        startActivity(intent);
    }

    //Vue profil
    public void lancerActiviteProfil(View v){
        Intent intent = new Intent(this, ProfilActivity.class);
        startActivity(intent);
    }

    //Vue annonce du jour
    public void lancerActiviteBienHasard(View v){
        ConnexionManager connexion = new ConnexionManager(this, VisualisationActivity.class);

        if(!connexion.isConnected()){
            Snackbar.make(v, getString(R.string.string_erreur_connexion), Snackbar.LENGTH_LONG).show();
            return;
        }

        Request request = new Request.Builder().url("https://android-estate/mock-api/immobilier.json").build();
        client.newCall(request).enqueue(connexion);
    }

    //Mise a jour des informations apres un retour sur la vue
    @Override
    public void onResume() {
        super.onResume();

        Vendeur utilisateur = db.obtenirUtilisateur();
        TextView textViewBienvenue = findViewById(R.id.textview_main_bienvenue);
        textViewBienvenue.setText(getString(R.string.textview_main_bienvenue) + " " + utilisateur.getPrenom() + " !");

        adapter.notifyDataSetChanged();
        dbo.close();
    }

    //Mise en place du recycler sur la vue principale
    @Override
    public void update(final List<Propriete> proprietes) {
        final ClickListener clickListener = new ClickListener() {

            @Override
            public void onPositionClicked(int position, Activity activity) {

                Intent intent = new Intent(activity, VisualisationActivity.class);
                intent.putExtra("Propriete", proprietes.get(position));
                startActivity(intent);
            }
        };
        final Activity _this = this;

        //necessaire car la visualisation ne peut mettre a jour le thread principal
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new RecyclerAdapter(proprietes, clickListener, _this);
                recycler.setAdapter(adapter);
            }
        });
    }
}
