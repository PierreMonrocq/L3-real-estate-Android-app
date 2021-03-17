package unicaen.tp.programmationandroid.activities.affichage;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;

import unicaen.tp.programmationandroid.R;
import unicaen.tp.programmationandroid.activities.listeners.ClickListener;
import unicaen.tp.programmationandroid.classes.Propriete;
import unicaen.tp.programmationandroid.db.Db;
import unicaen.tp.programmationandroid.db.DbContract;
import unicaen.tp.programmationandroid.db.DbOpener;

import static unicaen.tp.programmationandroid.classes.Conversion.conversionEntierEuro;

public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener  {

    private Activity activity;
    private Propriete propriete;
    private WeakReference<ClickListener> listenerRef;
    private ImageView iconImageView;
    private Db db;

    public RecyclerViewHolder(@NonNull View itemView, Activity activity, ClickListener listener) {
        super(itemView);
        this.listenerRef = new WeakReference<>(listener);
        this.activity = activity;

        DbOpener dbo = new DbOpener(activity.getApplicationContext(), DbContract.BD_NAME,null,DbContract.BD_VERSION);
        this.db = new Db(dbo);

        itemView.setOnClickListener(this);
        iconImageView = (ImageView) itemView.findViewById(R.id.imageview_liste_sauvegarde);
        iconImageView.setOnClickListener(this);

    }

    public void bind(Propriete p) {
        propriete = p;
        ((TextView)(itemView.findViewById(R.id.textview_titre_listeitem))).setText(p.getTitre());
        ((TextView)(itemView.findViewById(R.id.textview_ville_listeitem))).setText(p.getVille() +" " + p.getCodePostal());
        ((TextView)(itemView.findViewById(R.id.textview_prix_listeitem))).setText(conversionEntierEuro(p.getPrix()));

        TextView textview_date = itemView.findViewById(R.id.textview_date_listeitem);
        String prefix = activity.getString(R.string.textview_visualisation_date);
        textview_date.setText(prefix + " " + propriete.getDateString());

        if(db.annonceDejaSauvegardee(p.getId())){
            iconImageView.setImageResource(R.drawable.baseline_favorite_green_18dp);
        }else{
            iconImageView.setImageResource(R.drawable.baseline_favorite_border_black_18dp);
        }
        if(!p.getListeUrlImages().isEmpty()){
            Glide.with(activity).load(p.getListeUrlImages().get(0)).into((ImageView)itemView.findViewById(R.id.imageview_listeitem));
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() != iconImageView.getId()) {
            listenerRef.get().onPositionClicked(getAdapterPosition(),activity);
        }else{
            if (!db.annonceDejaSauvegardee(propriete.getId())) {
                iconImageView.setImageResource(R.drawable.baseline_favorite_green_18dp);
                db.ajouterPropriete(propriete);
                Toast.makeText(v.getContext(), activity.getString(R.string.string_annonce_sauvegardee), Toast.LENGTH_SHORT).show();
            } else {
                iconImageView.setImageResource(R.drawable.baseline_favorite_border_black_18dp);
                db.supprimerPropriete(DbContract.ANNONCES_TABLE, propriete.getId());
                Toast.makeText(v.getContext(),  activity.getString(R.string.string_annonce_supprimee), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
