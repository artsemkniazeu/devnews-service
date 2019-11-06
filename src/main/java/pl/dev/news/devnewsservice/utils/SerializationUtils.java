package pl.dev.news.devnewsservice.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

@UtilityClass
public class SerializationUtils {

    @SneakyThrows
    public Object deserialize(final String s) {
        final byte[] data = Base64.getDecoder().decode(s);
        Object o;
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            o = ois.readObject();
        }
        return o;
    }

    @SneakyThrows
    public String serialize(final Serializable o) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
