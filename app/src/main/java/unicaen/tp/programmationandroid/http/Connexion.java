package unicaen.tp.programmationandroid.http;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Connexion implements Callback {

    private Activity activity;


    public Connexion(Activity activity){
        this.activity = activity;
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public void onFailure(Call call, IOException e) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("Connexion", "Erreur, Failure");
            }
        });
    }

    public Activity getClientActivity() {
        return activity;
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {

    }
}
