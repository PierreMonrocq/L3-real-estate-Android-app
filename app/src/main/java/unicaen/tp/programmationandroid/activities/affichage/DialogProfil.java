package unicaen.tp.programmationandroid.activities.affichage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import unicaen.tp.programmationandroid.R;
import unicaen.tp.programmationandroid.activities.listeners.FragmentCallback;
import unicaen.tp.programmationandroid.classes.Vendeur;
import unicaen.tp.programmationandroid.db.Db;
import unicaen.tp.programmationandroid.db.DbContract;
import unicaen.tp.programmationandroid.db.DbOpener;

public class DialogProfil extends DialogFragment {

    private DbOpener dbo;
    private Db db;
    private Vendeur utilisateur;
    private FragmentCallback callback;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);

        this.dbo = new DbOpener(getActivity().getApplicationContext(), DbContract.BD_NAME,null,DbContract.BD_VERSION);
        this.db = new Db(this.dbo);


        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_profil, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.string_sauvegarder, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {


                        EditText nom = getDialog().findViewById(R.id.textfield_bienvenue_prenom);
                        EditText prenom = getDialog().findViewById(R.id.dialog_profil_prenom);
                        EditText email = getDialog().findViewById(R.id.textfield_bienvenue_email);
                        EditText telephone = getDialog().findViewById(R.id.textfield_bienvenue_telephone);
                        Vendeur vendeur = new Vendeur("",nom.getText().toString(),prenom.getText().toString(),email.getText().toString(),telephone.getText().toString());

                        db.ajouterUtilisateur(vendeur);
                        callback.update();
                        dbo.close();

                    }
                })
                .setNegativeButton(R.string.string_annuler, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });




        builder.setTitle(getString(R.string.string_dialog_modification));
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.dbo = new DbOpener(getActivity().getApplicationContext(), DbContract.BD_NAME,null,DbContract.BD_VERSION);
        this.db = new Db(this.dbo);

        utilisateur = this.db.obtenirUtilisateur();

        EditText nom = getDialog().findViewById(R.id.textfield_bienvenue_prenom);
        nom.setText(utilisateur.getNom());
        EditText prenom = getDialog().findViewById(R.id.dialog_profil_prenom);
        prenom.setText(utilisateur.getPrenom());
        EditText email = getDialog().findViewById(R.id.textfield_bienvenue_email);
        email.setText(utilisateur.getEmail());
        EditText telephone = getDialog().findViewById(R.id.textfield_bienvenue_telephone);
        telephone.setText(utilisateur.getTelephone());
        dbo.close();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (FragmentCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement Fragment Two.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

}
