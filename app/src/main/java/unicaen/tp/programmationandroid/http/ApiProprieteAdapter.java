package unicaen.tp.programmationandroid.http;

import android.util.Log;

import com.squareup.moshi.JsonReader;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import java.io.IOException;

import unicaen.tp.programmationandroid.classes.Propriete;

public class ApiProprieteAdapter {
    @FromJson
    Propriete fromJson(JsonReader reader, JsonAdapter<Propriete> delegate) throws IOException {
        Propriete result = null;
        //Demarre le parsing
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("success")) {
                boolean succes = reader.nextBoolean();
                if (!succes) {
                    throw new IOException("API a répondu FALSE");
                }
            }else if (name.equals("response")) {
                result = delegate.fromJson(reader);
            } else {
                //reader.skipValue();
                throw new IOException("Response contient des données non conformes");
            }
        }
        reader.endObject();
        return result;
    }
}

