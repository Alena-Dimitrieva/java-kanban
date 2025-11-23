import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter out, Duration value) throws IOException {
        if (value == null) {
            out.value("null");
        } else {
            out.value(value.toMinutes()); // сериализуем как количество минут
        }
    }


    @Override
    public Duration read(JsonReader in) throws IOException {
        String value = in.nextString();
        if (value.equals("null")) {
            return null;
        } else {
            long minutes = Long.parseLong(value);
            return Duration.ofMinutes(minutes);
        }
    }
}