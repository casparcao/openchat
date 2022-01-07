package top.mikecao.openchat.server.session.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.core.exception.AppAuthException;
import top.mikecao.openchat.core.exception.AppServerException;
import top.mikecao.openchat.server.session.Auth;
import top.mikecao.openchat.server.session.TokenGranter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    public String grant(Auth auth) {
        String content;
        try {
            content = mapper.writeValueAsString(auth);
        }catch (JsonProcessingException e){
            log.error("颁发Token异常>>", e);
            throw new AppServerException("办法Token异常", e);
        }
        String prefix = encoder.encodeToString(content.getBytes(StandardCharsets.UTF_8));
        String suffix = encoder.encodeToString(md5(content + secret));
        return prefix + "." + suffix;
    }

    @Override
    public Auth resolve(String token) {
        int size = 2;
        String[] parts = token.split("\\.");
        if(parts.length != size){
            throw new AppAuthException("Token格式解析异常");
        }
        String prefix = parts[0];
        String content = new String(decoder.decode(prefix.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        String suffix = parts[1];
        String calcSign = encoder.encodeToString(md5(content + secret));
        if(!calcSign.equalsIgnoreCase(suffix)){
            throw new AppAuthException("Token签名错误");
        }
        try {
            return mapper.readValue(content, Auth.class);
        } catch (JsonProcessingException e) {
            log.error("Token解析异常", e);
            throw new AppServerException("Token解析异常", e);
        }
    }

    private byte[] md5(String s) {
        try {
            //1. 获得md5加密算法工具类
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            //2. 加密的结果为十进制
            return messageDigest.digest(s.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            //不应该出现该异常
            throw new AppServerException("不支持MD5摘要算法");
        }
    }

}
