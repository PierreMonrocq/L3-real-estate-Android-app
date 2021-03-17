package unicaen.tp.programmationandroid.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class Vendeur implements Parcelable {

    private String id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    public Vendeur(String id, String nom, String prenom, String email, String telephone) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
    }

    public Vendeur() {
        this("","","","","");
    }

    protected Vendeur(Parcel in) {
        id = in.readString();
        nom = in.readString();
        prenom = in.readString();
        email = in.readString();
        telephone = in.readString();
    }

    public static final Creator<Vendeur> CREATOR = new Creator<Vendeur>() {
        @Override
        public Vendeur createFromParcel(Parcel in) {
            return new Vendeur(in);
        }

        @Override
        public Vendeur[] newArray(int size) {
            return new Vendeur[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nom);
        dest.writeString(prenom);
        dest.writeString(email);
        dest.writeString(telephone);
    }
}
