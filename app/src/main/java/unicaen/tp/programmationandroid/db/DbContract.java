package unicaen.tp.programmationandroid.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbContract {

    public static final String BD_NAME = "sauvegarde.db";
    public static final int BD_VERSION = 10;

    public static final String ANNONCES_TABLE = "annonce";
    public static final String UTILISATEUR_TABLE = "utilisateur";
    public static final String COMMENTAIRES_TABLE = "commentaires";

    public static final String IDENTIFIANT = "_id";

    public static final String NOM = "nom";
    public static final String PRENOM = "prenom";
    public static final String TELEPHONE = "telephone";
    public static final String EMAIL = "email";

    public static final String CONTENU = "contenu";
    public static final String COMMENTAIRE = "commentaire";
    public static final String ANNONCE_ID = "annonce_id";

    private DbOpener dbOpener;

    public DbContract(DbOpener dbo){
        this.dbOpener = dbo;
    }

    public Cursor getContent(){
        String[] colonnes = new String[]{IDENTIFIANT, ANNONCE_ID, CONTENU,};
        SQLiteDatabase db = dbOpener.getWritableDatabase();
        Cursor cursor = db.query(ANNONCES_TABLE,colonnes, null,null,null,null,null);
        return cursor;
    }
    public Cursor getUserContent(){
        String[] colonnes = new String[]{NOM, PRENOM, TELEPHONE, EMAIL};
        SQLiteDatabase db = dbOpener.getWritableDatabase();
        Cursor cursor = db.query(UTILISATEUR_TABLE,colonnes, null,null,null,null,null);
        return cursor;
    }
}
