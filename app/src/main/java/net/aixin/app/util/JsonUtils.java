package net.aixin.app.util;

import com.networkbench.com.google.gson.Gson;
import com.networkbench.com.google.gson.JsonParseException;

/**
 * Created by gzp on 2015/12/8.
 */
public class JsonUtils {

    /**
     * json转javaBean
     * @param type
     * @param bytes
     * @param <T>
     * @return
     */
    public static <T> T toBean(Class<T> type, byte[] bytes){
        if (bytes == null) return null;
        return jsonToBean(new String(bytes), type);
    }

    /**
     * json转bean
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T jsonToBean(String json,Class<T> clazz){
        Gson jGson = new Gson();
        T t = null;
        try {
            t = jGson.fromJson(json, clazz);
            return t;
        } catch (JsonParseException e) {
            return null;
        }
    }


}
