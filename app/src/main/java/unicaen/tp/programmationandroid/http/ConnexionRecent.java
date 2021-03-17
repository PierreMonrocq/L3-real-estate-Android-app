package unicaen.tp.programmationandroid.http;

import android.app.Activity;
import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;
import unicaen.tp.programmationandroid.activities.affichage.RecyclerAdapter;
import unicaen.tp.programmationandroid.activities.listeners.ConnexionCallback;
import unicaen.tp.programmationandroid.classes.Propriete;
import unicaen.tp.programmationandroid.db.Db;

public class ConnexionRecent extends Connexion {

    private Db db;
    private RecyclerAdapter adapter;
    private List<Propriete> proprietes;
    private ConnexionCallback callback;


    public ConnexionRecent(Activity activity, Db db, ConnexionCallback callback) {
        super(activity);
        this.db = db;
        this.callback = callback;
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (!response.isSuccessful()) {
            throw new IOException(response.toString());
        }
        String body = response.body().string();

        Moshi moshi = new Moshi.Builder().add(new ApiListProprieteAdapter()).build();

        Type type = Types.newParameterizedType(List.class, Propriete.class);

        JsonAdapter<List<Propriete>> jsonAdapter = moshi.adapter(type);

        proprietes = jsonAdapter.fromJson(body);


        callback.update(proprietes);
    }
}
