import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;

// Адаптер для корректной сериализации LocalDateTime.
public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.value ("null");
        } else {
            out.value (value.toString ());
        }
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        String value = in.nextString ();
        if (value.equals ("null")) {
            return null;
        } else {
            return LocalDateTime.parse (value);
        }
    }

}