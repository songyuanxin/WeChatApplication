package com.syx.core.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.NonNull;

/**
 * @author Quasar
 * @version 1.0.0
 * @date 2021/5/14 15:24
 */
public final class JsonUtils {
    private JsonUtils() {
    }

    public final static ObjectMapper SERIALIZER;

    static {
        SERIALIZER = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static String serializeObject(@NonNull Object obj) throws JsonProcessingException {
        return SERIALIZER.writeValueAsString(obj);
    }

    public static String serializeObjectOrNull(@NonNull Object obj) {
        try {
            return SERIALIZER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static Object deserializeObject(String jsonText, TypeReference type) throws JsonProcessingException {
        return SERIALIZER.readValue(jsonText, type);
    }

    public static <T> T deserializeObject(String jsonText, Class<T> beanClass) throws JsonProcessingException {
        return SERIALIZER.readValue(jsonText, beanClass);
    }

    public static JsonNode deserializeObject(String jsonText) throws JsonProcessingException {
        return SERIALIZER.readTree(jsonText);
    }
}
