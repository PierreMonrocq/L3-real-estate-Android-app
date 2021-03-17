package unicaen.tp.programmationandroid.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.io.IOException;
import java.util.List;

import unicaen.tp.programmationandroid.R;
import unicaen.tp.programmationandroid.classes.Propriete;
import unicaen.tp.programmationandroid.db.Db;
import unicaen.tp.programmationandroid.db.DbContract;
import unicaen.tp.programmationandroid.db.DbOpener;

import static unicaen.tp.programmationandroid.classes.Conversion.conversionEntierEuro;
import static unicaen.tp.programmationandroid.classes.Conversion.conversionMinusculeMajuscule;

public class VisualisationActivity extends AppCompatActivity {


    private DbOpener dbo;
    private Db db;
    private Intent intent;
    private CarouselView carouselView;
    private List<String> images;
    private Propriete propriete;
    private EditText editText_commentaire;
    private String commentaire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualisation);

        //ToolBar
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Détails de la propriété");

        //DB
        this.dbo = new DbOpener(getApplicationContext(), DbContract.BD_NAME,null,DbContract.BD_VERSION);
        this.db = new Db(this.dbo);


        //Intent
        intent = getIntent();
        propriete = intent.getParcelableExtra("Propriete");


        commentaire = db.obtenirCommentaire(propriete.getId());
        editText_commentaire = findViewById(R.id.textedit_visualisation_remarques);
        editText_commentaire.setText(commentaire);

        //Chargement des proprietes deja sauvegardees
        if(db.annonceDejaSauvegardee(propriete.getId())){
            ImageView image_favoris = findViewById(R.id.imageview_visualisation_sauvegarde);
            image_favoris.setImageResource(R.drawable.baseline_favorite_green_18dp);
        }

        //TextViews Propriete
        TextView textview_titre = findViewById(R.id.textview_titre);
        textview_titre.setText(propriete.getTitre());

        TextView textview_desc = findViewById(R.id.textview_visualisation_desc);
        textview_desc.setText(propriete.getDescription());

        TextView textview_prix = findViewById(R.id.textview_visualisation_prix);
        textview_prix.setText(conversionEntierEuro(propriete.getPrix()));

        TextView textview_ville = findViewById(R.id.textview_visualisation_ville);
        textview_ville.setText(propriete.getVille() + " " + propriete.getCodePostal());

        TextView textview_date = findViewById(R.id.textview_visualisation_date);
        String prefix = getString(R.string.textview_visualisation_date);
        textview_date.setText(prefix + " " + propriete.getDateString());

        TextView textview_pieces = findViewById(R.id.textview_visualisation_pieces);
        String suffix = getString(R.string.textview_visualisation_nbpieces);
        textview_pieces.setText(propriete.getNbPiecesPrincipales() + " " + suffix);

        //Affichage de la liste des caracteristiques
        TextView textview_liste_caracteristiques = findViewById(R.id.textview_visualisation_liste_caracteristique);
        if(!propriete.getListeCaracteristiques().isEmpty() ) {
            String res = "";
            List<String> caracteristiques = propriete.getListeCaracteristiques();
            for(int i=0;i<caracteristiques.size();i++){
                res += conversionMinusculeMajuscule(caracteristiques.get(i));
                if(i+1<caracteristiques.size()){
                    res += " - ";
                }
            }
            textview_liste_caracteristiques.setText(res);
        }else{
            textview_liste_caracteristiques.setVisibility(View.GONE);
        }

        //Carousel
        images = propriete.getListeUrlImages();
        if(images.size() !=0) {
            carouselView = (CarouselView) findViewById(R.id.carouselView);
            carouselView.setPageCount(images.size());
                carouselView.setImageListener(imageListener);
        }else{
            carouselView = (CarouselView) findViewById(R.id.carouselView);
            carouselView.setVisibility(View.GONE);
        }


        //TextView Vendeur
        TextView textview_vendeur = findViewById(R.id.textview_visualisation_vendeur_nom);
        textview_vendeur.setText(propriete.getVendeur().getPrenom() + " "+ propriete.getVendeur().getNom());

        TextView textview_vendeur_mail = findViewById(R.id.textview_visualisation_vendeur_mail);
        textview_vendeur_mail.setText(propriete.getVendeur().getEmail());

        TextView textview_vendeur_telephone = findViewById(R.id.textview_visualisation_vendeur_telephone);
        textview_vendeur_telephone.setText(propriete.getVendeur().getTelephone());
    }

    //listener du carousel
    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            Glide
                    .with(getApplicationContext())
                    .load(images.get(position))
                    .apply(new RequestOptions()
                            .centerCrop())
                    .into(imageView);
        }
    };

    //ajout d'un commentaire
    public void ajouterCommentaire(View v){
        if(!db.annonceDejaSauvegardee(propriete.getId())){
            sauvegarderActivity(v);
        }
        commentaire = editText_commentaire.getText().toString();
        db.ajouterCommentaire(commentaire, propriete.getId());
        editText_commentaire.clearFocus();
    }

    //sauvegarde une propriete
    public void sauvegarderActivity(View v) {

        final ImageView image_favoris = findViewById(R.id.imageview_visualisation_sauvegarde);

        if (!db.annonceDejaSauvegardee(propriete.getId())) {
            image_favoris.setImageResource(R.drawable.baseline_favorite_green_18dp);
            db.ajouterPropriete(propriete);
            Toast.makeText(v.getContext(), getString(R.string.string_annonce_sauvegardee), Toast.LENGTH_SHORT).show();

            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<Propriete> adapter = moshi.adapter(Propriete.class);
            String monSuperJson = adapter.toJson(propriete);

        } else {
            final String id = propriete.getId();

            if(commentaire.isEmpty()){
                db.supprimerPropriete(DbContract.ANNONCES_TABLE, id);
                image_favoris.setImageResource(R.drawable.baseline_favorite_border_black_18dp);
                Toast.makeText(v.getContext(), getString(R.string.string_annonce_supprimee), Toast.LENGTH_SHORT).show();
            }else{
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle(getString(R.string.string_confirmer_suppr))
                .setMessage(getString(R.string.string_confirmer_suppr_message))
                .setPositiveButton(getString(R.string.string_valider), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        db.supprimerCommentaire(id);
                        db.supprimerPropriete(DbContract.ANNONCES_TABLE, id);
                        editText_commentaire.setText("");
                        image_favoris.setImageResource(R.drawable.baseline_favorite_border_black_18dp);
                        commentaire = "";
                    }
                }).setNegativeButton(getString(R.string.string_annuler), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    dialoginterface.cancel();
                }}).show();
            }
        }

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
