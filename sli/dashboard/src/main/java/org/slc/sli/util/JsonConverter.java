package org.slc.sli.util;

import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Json converter util
 *
 * @author dwu
 *
 */
public class JsonConverter {

    private static final Gson GSON = new GsonBuilder().create();

    public static String toJson(Object o) {
        return GSON.toJson(o);
    }
    /**
     * TODO: switch to something more efficient than creating deserializer everytime and use jackson
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return json == null ? null : GSON.fromJson(json, clazz);
    }
    /**
     * TODO: switch to something more efficient than creating deserializer everytime and use jackson
     * @param reader
     * @param clazz
     * @return
     */
    public static <T> T fromJson(Reader reader, Class<T> clazz) {
        return GSON.fromJson(reader, clazz);
    }
}
