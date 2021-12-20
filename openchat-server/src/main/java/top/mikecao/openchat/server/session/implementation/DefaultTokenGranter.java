package top.mikecao.openchat.server.session.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.server.session.User;
import top.mikecao.openchat.server.session.TokenGranter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * 默认的token颁发器
 * <p>模拟JWT，使用固定md5进行签名</p>
 * <pre>
 *     token = base64({"id":123,"name":"12312"}).base64(md5(content+secret))
 * </pre>
 * @author caohailong
 */

@Slf4j
@Component
public class DefaultTokenGranter implements TokenGranter {

    @Value("${token.secret:$IbHJ$^ZpBNsSUWd*K}")
    private String secret;
    @Autowired
    private ObjectMapper mapper;
    private final Base64.Encoder encoder = Base64.getMimeEncoder();
    private final Base64.Decoder decoder = Base64.getMimeDecoder();

    @Override
    public String grant(User auth) {
        String content = "";
        try {
            content = mapper.writeValueAsString(auth);
        }catch (JsonProcessingException e){
            log.error("颁发Token异常>>", e);
            //todo 怎样处理该异常
        }
        String prefix = encoder.encodeToString(content.getBytes(StandardCharsets.UTF_8));
        String suffix = encoder.encodeToString(md5(content + secret));
        return prefix + "." + suffix;
    }

    @Override
    public User resolve(String token) {
        String[] parts = token.split("\\.");
        if(parts.length != 2){
            //todo 怎样处理
            return null;
        }
        String prefix = parts[0];
        String content = new String(decoder.decode(prefix.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        String suffix = parts[1];
        String calcsign = encoder.encodeToString(md5(content + secret));
        if(!calcsign.equalsIgnoreCase(suffix)){
            //签名不一致
            //todo
        }
        try {
            return mapper.readValue(content, User.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            //todo
        }
        return null;
    }

    private byte[] md5(String s) {
        try {
            //1. 获得md5加密算法工具类
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            //2. 加密的结果为十进制
            return messageDigest.digest(s.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            //如果产生错误则抛出异常
            //todo
            return null;
        }
    }

}
