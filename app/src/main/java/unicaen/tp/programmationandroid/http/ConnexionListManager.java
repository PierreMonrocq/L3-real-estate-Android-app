package unicaen.tp.programmationandroid.http;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;

import java.io.IOException;
import java.lang.reflect.Array;

import java.util.ArrayList;
import java.util.List;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import unicaen.tp.programmationandroid.classes.Propriete;


    public class ConnexionListManager extends Connexion {

        private Class act2;

        public ConnexionListManager(Activity activity,final Class<? extends Activity> ActivityToOpen) {
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
                Intent intent = new Intent(getClientActivity(),act2);
                intent.putExtra("ProprieteList",  body);
                getClientActivity().startActivity(intent);
        }

    }


