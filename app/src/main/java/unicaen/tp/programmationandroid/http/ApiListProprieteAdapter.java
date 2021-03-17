package unicaen.tp.programmationandroid.http;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;

import java.io.IOException;
import java.util.List;


import unicaen.tp.programmationandroid.classes.Propriete;

public class ApiListProprieteAdapter {
    @FromJson
    List<Propriete> fromJson(JsonReader reader, JsonAdapter<List<Propriete>> delegate) throws IOException {
        List<Propriete> result = null;

        reader.beginObject();
        while (reader.hasNext()) {
            if (reader.nextName().equals("response")){

                result = delegate.fromJson(reader);
            } else { reader.skipValue();}
        }
        reader.endObject();
        return result;
    }
}
