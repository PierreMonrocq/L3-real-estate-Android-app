package unicaen.tp.programmationandroid.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import unicaen.tp.programmationandroid.R;
import unicaen.tp.programmationandroid.activities.affichage.DialogProfil;
import unicaen.tp.programmationandroid.activities.affichage.RecyclerAdapter;
import unicaen.tp.programmationandroid.activities.listeners.ClickListener;
import unicaen.tp.programmationandroid.activities.listeners.FragmentCallback;
import unicaen.tp.programmationandroid.classes.Propriete;
import unicaen.tp.programmationandroid.classes.Vendeur;
import unicaen.tp.programmationandroid.db.Db;
import unicaen.tp.programmationandroid.db.DbContract;
import unicaen.tp.programmationandroid.db.DbOpener;

public class ProfilActivity extends AppCompatActivity implements FragmentCallback {

    private RecyclerAdapter adapter;
    private List<Propriete> proprietes;
    private DbOpener dbo;
    private Vendeur utilisateur;
    private Db db;
    private FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        //Toolbar
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.string_tab_profil));

        //initialisation db
        this.dbo = new DbOpener(getApplicationContext(), DbContract.BD_NAME,null,DbContract.BD_VERSION);
        this.db = new Db(this.dbo);
        proprietes = this.db.obtenirTouteProprietes();
        utilisateur = this.db.obtenirUtilisateur();

        //Affichage des donnees de la base sur le profil
        if(utilisateur != null){
            TextView nom = (TextView) findViewById(R.id.textview_favoris_nom_valeur);
            nom.setText(utilisateur.getNom());
            TextView prenom = (TextView) findViewById(R.id.textview_favoris_prenom_valeur);
            prenom.setText(utilisateur.getPrenom());
            TextView email = (TextView) findViewById(R.id.textview_favoris_email_valeur);
            email.setText(utilisateur.getEmail());
            TextView telephone = (TextView) findViewById(R.id.textview_favoris_telephone);
            telephone.setText(utilisateur.getTelephone());
        }


        RecyclerView recycler = findViewById(R.id.recycler_profil);
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

    //affiche la boite de dialogue edition des informations
    public void editerInformation(View v){
        DialogProfil dialogProfil = new DialogProfil();
        dialogProfil.show(fm,"");
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        dbo.close();;
    }

    //Active la toolbar
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    //Met a jours les proprietes sur la vue à partir de la boite de dialogue
    @Override
    public void update() {
        utilisateur = this.db.obtenirUtilisateur();
        TextView nom = (TextView) findViewById(R.id.textview_favoris_nom_valeur);
        nom.setText(utilisateur.getNom());
        TextView prenom = (TextView) findViewById(R.id.textview_favoris_prenom_valeur);
        prenom.setText(utilisateur.getPrenom());
        TextView email = (TextView) findViewById(R.id.textview_favoris_email_valeur);
        email.setText(utilisateur.getEmail());
        TextView telephone = (TextView) findViewById(R.id.textview_favoris_telephone);
        telephone.setText(utilisateur.getTelephone());

        dbo.close();
    }
}
