package top.mikecao.openchat.core;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * <pre>a:b;a:0;c:d;e:f;</pre>
 * <p>
 *     消息头，键可以重复，每个键对应一个值列表。
 * </p>
 * <p>
 *     键值对之间由分号分隔，键值之间由冒号分隔。
 * </p>
 */
public class Headers extends HashMap<String, List<String>> {

    private static final String DELIMITER = ";";

    public static Headers decode(byte[] bytes){
        String str = new String(bytes, StandardCharsets.UTF_8);
        String[] headers = str.split(DELIMITER);
        Headers instance = new Headers();
        for (String header: headers){
            String[] pair = header.split(":");
            List<String> values = instance.getOrDefault(pair[0], new ArrayList<>());
            values.add(pair[1]);
            instance.put(pair[0], values);
        }
        return instance;
    }

    public byte[] encode(){
        StringBuilder sb = new StringBuilder();
        Set<Entry<String, List<String>>> entries = this.entrySet();
        for (Entry<String, List<String>> entry: entries){
            String key = entry.getKey();
            List<String> values = entry.getValue();
            for (String v: values){
                sb.append(key).append(":").append(v).append(DELIMITER);
            }
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 返回指定键对应的第一个值, 或者默认值
     * @param key
     */
    public String first(String key, String defaultv){
        List<String> values = this.getOrDefault(key, new ArrayList<>());
        return values.isEmpty()?defaultv:values.get(0);
    }
    public String first(HKEY key, String defaultv){
        return first(key.getID(),defaultv);
    }
    public void set(String key, String value){
        List<String> values = getOrDefault(key, new ArrayList<>());
        values.clear();
        values.add(value);
        put(key, values);
    }
    public void set(HKEY key, String value){
        set(key.name(), value);
    }
}
