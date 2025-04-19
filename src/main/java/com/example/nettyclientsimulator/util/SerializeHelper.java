package com.example.nettyclientsimulator.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class SerializeHelper {

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static final String CHARSET_NAME = "UTF-8";

    /**
     * serilize object
     */
    public static <T> byte[] serialize(T obj) {
        try {
            if (obj instanceof String) {
                return ((String) obj).getBytes(CHARSET_NAME);
            }
            return objectMapper.writeValueAsBytes(obj);
        } catch (Exception ex) {
            log.warn("Serialize failure, cause={}", ex);
            return null;
        }
    }

    /**
     * serilize object
     */
    public static <T> String parseToStr(T obj) {
        try {
            if (obj instanceof String) {
                return ((String) obj);
            }
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            log.warn("Serialize failure, cause={}", ex);
            return null;
        }
    }


    /**
     * deserialize object
     */
    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            if (clazz == String.class) {
                return (T) new String(bytes, CHARSET_NAME);
            }
            return objectMapper.readValue(bytes, clazz);
        } catch (IOException ex) {
            log.warn("Deserialize failure, cause={}", ex);
            return null;
        }
    }

    /**
     * deserialize object
     */
    public static <T> T deserialize(String data, Class<T> clazz) {
        try {
            if (clazz == String.class) {
                return (T) data;
            }
            return objectMapper.readValue(data, clazz);
        } catch (IOException ex) {
            log.warn("Deserialize failure, cause={}", ex);
            return null;
        }
    }

}

