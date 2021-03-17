package unicaen.tp.programmationandroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpener extends SQLiteOpenHelper {

    private static final String ANNONCE_CREATE = "create table " + DbContract.ANNONCES_TABLE + "(" + DbContract.IDENTIFIANT +
            " INTEGER PRIMARY KEY, " + DbContract.ANNONCE_ID + " VARCHAR(64) UNIQUE, " + DbContract.CONTENU + " VARCHAR(1024));";

    private static final String USER_CREATE = "create table " + DbContract.UTILISATEUR_TABLE + "(" + DbContract.NOM +" VARCHAR(64), " + DbContract.PRENOM +" VARCHAR(64), " + DbContract.EMAIL + " VARCHAR(64), " + DbContract.TELEPHONE + " VARCHAR(64));";


    private static final String COMMENTAIRE_CREATE = "create table " + DbContract.COMMENTAIRES_TABLE + "(" + DbContract.IDENTIFIANT + " INTEGER PRIMARY KEY, " + DbContract.COMMENTAIRE + " VARCHAR(1024)," + DbContract.ANNONCE_ID + " VARCHAR(64) UNIQUE);";


    public DbOpener(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ANNONCE_CREATE);
        db.execSQL(USER_CREATE);
        db.execSQL(COMMENTAIRE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + DbContract.ANNONCES_TABLE);
        db.execSQL("drop table " + DbContract.UTILISATEUR_TABLE);
        db.execSQL("drop table " + DbContract.COMMENTAIRES_TABLE);
        onCreate(db);
    }

    public void close(SQLiteDatabase db){
        db.close();
    }
}

