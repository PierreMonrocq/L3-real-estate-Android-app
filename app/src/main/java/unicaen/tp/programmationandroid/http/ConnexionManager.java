package unicaen.tp.programmationandroid.http;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import unicaen.tp.programmationandroid.classes.Propriete;

public class ConnexionManager extends Connexion{

    private Class act2;

    public ConnexionManager(Activity activity,final Class<? extends Activity> ActivityToOpen) {
        super(activity);
        this.act2 = ActivityToOpen;
}


    public boolean isConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getClientActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

        @Override
        public void onFailure(Call call, IOException e) {
            getClientActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("Connexion", "Erreur, Failure");
                }
            });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (!response.isSuccessful()) {
                throw new IOException(response.toString());
            }
            String body = response.body().string();

            Moshi moshi = new Moshi.Builder().add(new ApiProprieteAdapter()).build();

            JsonAdapter<Propriete> jsonAdapter = moshi.adapter(Propriete.class);

            try {
                Propriete prop = jsonAdapter.fromJson(body);
                Propriete propriete = new Propriete(prop.getId(),prop.getTitre(),prop.getDescription(),prop.getNbPiecesPrincipales()
                        ,prop.getPrix(),prop.getVille(),prop.getCodePostal(),prop.getVendeur(),prop.getDate(),prop.getListeCaracteristiques(),prop.getListeUrlImages());

                Intent intent = new Intent(getClientActivity(),act2);
                intent.putExtra("Propriete", propriete);
                getClientActivity().startActivity(intent);
            } catch (IOException e) {
                Log.i("Connexion", "Erreur I/O");
            }
        }


}
