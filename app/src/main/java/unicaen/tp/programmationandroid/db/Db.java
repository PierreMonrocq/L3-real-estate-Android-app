package unicaen.tp.programmationandroid.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import unicaen.tp.programmationandroid.classes.Propriete;
import unicaen.tp.programmationandroid.classes.Vendeur;

import static unicaen.tp.programmationandroid.db.DbContract.ANNONCE_ID;
import static unicaen.tp.programmationandroid.db.DbContract.ANNONCES_TABLE;
import static unicaen.tp.programmationandroid.db.DbContract.COMMENTAIRE;
import static unicaen.tp.programmationandroid.db.DbContract.COMMENTAIRES_TABLE;
import static unicaen.tp.programmationandroid.db.DbContract.CONTENU;
import static unicaen.tp.programmationandroid.db.DbContract.EMAIL;
import static unicaen.tp.programmationandroid.db.DbContract.NOM;
import static unicaen.tp.programmationandroid.db.DbContract.PRENOM;
import static unicaen.tp.programmationandroid.db.DbContract.TELEPHONE;
import static unicaen.tp.programmationandroid.db.DbContract.UTILISATEUR_TABLE;

public class Db {

    private JsonAdapter<Propriete> adapter;

    private DbOpener dbOpener;

    public Db(DbOpener dbOpener){
        this.dbOpener = dbOpener;
        Moshi moshi = new Moshi.Builder().build();
        this.adapter = moshi.adapter(Propriete.class);
    }

    public void ajouterPropriete(Propriete propriete){
        ContentValues valeurs = new ContentValues();
        String json = adapter.toJson(propriete);
        valeurs.put(ANNONCE_ID, propriete.getId());
        valeurs.put(CONTENU, json);
        SQLiteDatabase db = dbOpener.getWritableDatabase();
        db.insert(ANNONCES_TABLE,null, valeurs);
    }

    public void ajouterCommentaire(String commentaire, String id){
        ContentValues valeurs = new ContentValues();
        valeurs.put(COMMENTAIRE, commentaire);
        valeurs.put(ANNONCE_ID, id);
        SQLiteDatabase db = dbOpener.getWritableDatabase();

        supprimerCommentaire(id);
        db.insert(COMMENTAIRES_TABLE,null, valeurs);
    }

    public void supprimerCommentaire(String id) {
        SQLiteDatabase db = dbOpener.getWritableDatabase();
        supprimerPropriete(COMMENTAIRES_TABLE, id);
    }

    public void ajouterUtilisateur(Vendeur vendeur){

        ContentValues valeurs = new ContentValues();
        valeurs.put(NOM, vendeur.getNom());
        valeurs.put(PRENOM, vendeur.getPrenom());
        valeurs.put(EMAIL, vendeur.getEmail());
        valeurs.put(TELEPHONE, vendeur.getTelephone());
        SQLiteDatabase db = dbOpener.getWritableDatabase();
        db.execSQL("delete from "+ UTILISATEUR_TABLE);
        db.insert(UTILISATEUR_TABLE,null, valeurs);
    }

    private Propriete jsonToPropriete(String json) {
        Propriete result = null;

        try {
            result = adapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private Cursor queryForColumns(String table, String[] colonnes, String where) {
        return dbOpener.getReadableDatabase().query(table, colonnes, where, null, null, null, null);
    }

    public Propriete obtenir(String id) {
        String[] colonnes = new String[]{CONTENU,};
        Cursor cursor = queryForColumns(ANNONCES_TABLE, colonnes, ANNONCE_ID + "='" + id + "'");

        Propriete result = null;
        if(cursor.getCount() > 0) {
            result = jsonToPropriete(cursor.getString(0));
        }
        cursor.close();

        return result;
    }


    public List<Propriete> obtenirTouteProprietes() {
        ArrayList<Propriete> result = new ArrayList<>();
        String[] colonnes = new String[]{CONTENU,};
        Cursor cursor = queryForColumns(ANNONCES_TABLE, colonnes, null);

        while(cursor.moveToNext()) {
            Propriete it = jsonToPropriete(cursor.getString(0));
            result.add(it);
        }
        cursor.close();

        return result;
    }

    public Vendeur obtenirUtilisateur() {
        String selection = "SELECT * FROM " + UTILISATEUR_TABLE;

        SQLiteDatabase db = dbOpener.getReadableDatabase();
        Cursor cursor = db.rawQuery(selection, null);
        if(cursor.moveToNext()) {
            return new Vendeur("", cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
        }
        return null;
    }

    public String obtenirCommentaire(String id) {
        String[] colonnes = new String[]{COMMENTAIRE,};
        Cursor cursor = queryForColumns(COMMENTAIRES_TABLE, colonnes, ANNONCE_ID + "='" + id + "'");

        String result = "";
        if(cursor.moveToNext()) {
            result = cursor.getString(0);
        }
        cursor.close();


        return result;
    }

    public boolean supprimerPropriete(String table, String id){
        SQLiteDatabase db = dbOpener.getWritableDatabase();
        return db.delete(table, ANNONCE_ID+"='"+id + "'", null) > 0;
    }

    public boolean annonceDejaSauvegardee(String id) {
        String[] colonnes = new String[]{ANNONCE_ID,};
        Cursor cursor = queryForColumns(ANNONCES_TABLE, colonnes, ANNONCE_ID + "='" + id + "'");

        boolean result = (cursor.getCount() > 0);
        cursor.close();
        return result;
    }

    public void destruction(String table){
        SQLiteDatabase db = dbOpener.getReadableDatabase();
        db.execSQL("delete from "+ table);
    }
}
