package top.mikecao.openchat.core.serialize;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import top.mikecao.openchat.core.common.Strings;
import top.mikecao.openchat.core.exception.AppServerException;

import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * @author caohailong
 */

@Slf4j
public final class Json {

    private Json(){}

    private static ObjectMapper mapper;

    public static ObjectMapper mapper(){
        if(Objects.nonNull(mapper)){
            return mapper;
        }
        synchronized (Json.class){
            if(Objects.nonNull(mapper)){
                return mapper;
            }
            mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            mapper.setDateFormat(new SimpleDateFormat(Strings.DATE_TIME));
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper;
        }
    }

    public static <T> T from (String json, TypeReference<T> tr){
        try {
            return mapper().readValue(json, tr);
        } catch (JsonProcessingException e) {
            log.error("JSON反序列化失败>>", e);
            throw new AppServerException("反序列化失败");
        }
    }

    public static <T> T from (String json, Class<T> clazz){
        try {
            return mapper().readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("JSON反序列化失败>>", e);
            throw new AppServerException("反序列化失败");
        }
    }

    public static String to (Object object){
        try {
            return mapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败>>", e);
            throw new AppServerException("序列化失败");
        }
    }
}
