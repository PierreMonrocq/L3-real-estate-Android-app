package unicaen.tp.programmationandroid.classes;
import android.os.Parcelable;
import android.os.Parcel;

import java.text.SimpleDateFormat;
import java.util.List;

import unicaen.tp.programmationandroid.R;

public class Propriete implements Parcelable {

    private String id;
    private String titre;
    private String description;
    private int nbPieces;
    private List<String> caracteristiques;
    private int prix;
    private String ville;
    private String codePostal;
    private Vendeur vendeur;
    private List<String> images;
    private long date;

    public Propriete(String id, String titre,
                     String description, int nbPieces,
                     int prix, String ville,
                     String code_postal, Vendeur vendeur,
                     long date, List<String> caracteristiques, List<String> imageUrls) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.nbPieces = nbPieces;
        this.caracteristiques = caracteristiques;
        this.prix = prix;
        this.ville = ville;
        this.codePostal = code_postal;
        this.vendeur = vendeur;
        this.images = imageUrls;
        this.date = date;
    }

    protected Propriete(Parcel in) {
        id = in.readString();
        titre = in.readString();
        description = in.readString();
        nbPieces = in.readInt();
        caracteristiques= in.createStringArrayList();
        prix = in.readInt();
        ville = in.readString();
        codePostal = in.readString();
        vendeur = in.readParcelable(Vendeur.class.getClassLoader());
        images = in.createStringArrayList();
        date = Long.parseLong(in.readString());
    }

    public static final Creator<Propriete> CREATOR = new Creator<Propriete>() {
        @Override
        public Propriete createFromParcel(Parcel in) {
            return new Propriete(in);
        }

        @Override
        public Propriete[] newArray(int size) {
            return new Propriete[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public int getNbPiecesPrincipales() {
        return nbPieces;
    }

    public List<String> getListeCaracteristiques() {
        return caracteristiques;
    }

    public int getPrix() {
        return prix;
    }

    public String getVille() {
        return ville;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public Vendeur getVendeur() {
        return vendeur;
    }

    public List<String> getListeUrlImages() {
        return images;
    }

    public long getDate() {
        return date;
    }

    public String getDateString()
    {
        SimpleDateFormat newDate = new SimpleDateFormat("dd/MM/yyyy à h:mm");
        return newDate.format(date);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(titre);
        dest.writeString(description);
        dest.writeInt(nbPieces);
        dest.writeStringList(caracteristiques);
        dest.writeInt(prix);
        dest.writeString(ville);
        dest.writeString(codePostal);
        dest.writeParcelable(vendeur, flags);
        dest.writeStringList(images);
        dest.writeString(String.valueOf(date));
    }


}
