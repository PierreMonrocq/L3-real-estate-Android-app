package unicaen.tp.programmationandroid.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import unicaen.tp.programmationandroid.MainActivity;
import unicaen.tp.programmationandroid.R;
import unicaen.tp.programmationandroid.classes.Vendeur;
import unicaen.tp.programmationandroid.db.Db;
import unicaen.tp.programmationandroid.db.DbContract;
import unicaen.tp.programmationandroid.db.DbOpener;

public class BienvenueActivity extends AppCompatActivity {

    public static final String COMPLETED_ONBOARDING_PREF_NAME = "bienvenue" ;

    public static final int TEXT_PERSON_NAME = 8289;
    public static final int TEXT_EMAIL = 33;
    public static final int TEXT_TELEPHONE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenue);
    }

    public void finBienvenue(View v){
        EditText nom = findViewById(R.id.textfield_bienvenue_nom);
        EditText prenom = findViewById(R.id.textfield_bienvenue_prenom);
        EditText email = findViewById(R.id.textfield_bienvenue_email);
        EditText telephone = findViewById(R.id.textfield_bienvenue_telephone);

        boolean fieldsOK = validate(new EditText[] { nom, prenom, email,telephone });

        if(fieldsOK){
            DbOpener dbo = new DbOpener(getApplicationContext(), DbContract.BD_NAME,null,DbContract.BD_VERSION);
            Db db = new Db(dbo);
            SharedPreferences.Editor sharedPreferencesEditor =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
            sharedPreferencesEditor.putBoolean(
                    COMPLETED_ONBOARDING_PREF_NAME, true);
            sharedPreferencesEditor.apply();
            startActivity(new Intent(this, MainActivity.class));
            Vendeur vendeur = new Vendeur("",nom.getText().toString(),prenom.getText().toString(),email.getText().toString(),telephone.getText().toString());
            db.ajouterUtilisateur(vendeur);
            dbo.close();
        }else{
            Toast.makeText(v.getContext(), getString(R.string.string_erreur_saisie_incomplete), Toast.LENGTH_SHORT).show();
        }
    }


    private boolean validate(EditText[] fields){
        boolean result = true;
        for(int i = 0; i < fields.length; i++){
            EditText currentField = fields[i];
            if(!hasContentEditText(currentField)){
                result = false;
            }
        }
        return result;
    }


    private boolean hasContentEditText(EditText et){
        String s = et.getText().toString();
        int inputTypeValue = et.getInputType();
        if(inputTypeValue == TEXT_PERSON_NAME){
            Pattern pattern = Pattern.compile(new String ("^[a-zA-Z\\s]*$"));
            Matcher matcher = pattern.matcher(s);
            if(!matcher.matches() || s.isEmpty()) {
                et.setError(getString(R.string.string_erreur_saisie));
                et.setFocusable(true);
                return false;
            }
            return true;
        }else if(inputTypeValue == TEXT_EMAIL){
            if(!isValidEmail(s)){
                et.setError(getString(R.string.string_erreur_email));
                et.setFocusable(true);
                return false;
            }
            return true;
        }else if(inputTypeValue == TEXT_TELEPHONE){
            if(!validCellPhone(s)){
                et.setError(getString(R.string.string_erreur_telephone));
                et.setFocusable(true);
                return false;
            }
            return true;
        }
        return false;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean validCellPhone(String number)
    {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }
}
