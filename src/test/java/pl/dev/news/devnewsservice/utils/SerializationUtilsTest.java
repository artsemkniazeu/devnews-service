package pl.dev.news.devnewsservice.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class SerializationUtilsTest {

    @Test
    public void serializationDesearliaztionTest() {
        final HashMap<String, String> hashMap = new HashMap<>();
        final String serialized = SerializationUtils.serialize(hashMap);
        final Object deserialized = SerializationUtils.deserialize(serialized);
        Assert.assertTrue(deserialized instanceof HashMap);
    }

}
