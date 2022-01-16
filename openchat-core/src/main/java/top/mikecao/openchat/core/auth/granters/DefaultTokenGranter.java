package top.mikecao.openchat.core.auth.granters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import top.mikecao.openchat.core.exception.AppAuthException;
import top.mikecao.openchat.core.exception.AppServerException;
import top.mikecao.openchat.core.auth.Account;
import top.mikecao.openchat.core.auth.TokenGranter;

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
public class DefaultTokenGranter implements TokenGranter {

    private static final String SECRET = "$IbHJ$^ZpBNsSUWd*K";
    private final ObjectMapper mapper;
    private final Base64.Encoder encoder = Base64.getMimeEncoder();
    private final Base64.Decoder decoder = Base64.getMimeDecoder();

    public DefaultTokenGranter(ObjectMapper objectMapper){
        this.mapper = objectMapper;
    }

    @Override
    public String grant(Account account) {
        String content;
        try {
            content = mapper.writeValueAsString(account);
        }catch (JsonProcessingException e){
            log.error("颁发Token异常>>", e);
            throw new AppServerException("办法Token异常", e);
        }
        String prefix = encoder.encodeToString(content.getBytes(StandardCharsets.UTF_8));
        String suffix = encoder.encodeToString(md5(content + SECRET));
        return prefix + "." + suffix;
    }

    @Override
    public Account resolve(String token) {
        int size = 2;
        String[] parts = token.split("\\.");
        if(parts.length != size){
            throw new AppAuthException("Token格式解析异常");
        }
        String prefix = parts[0];
        String content = new String(decoder.decode(prefix.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        String suffix = parts[1];
        String calcSign = encoder.encodeToString(md5(content + SECRET));
        if(!calcSign.equalsIgnoreCase(suffix)){
            throw new AppAuthException("Token签名错误");
        }
        try {
            return mapper.readValue(content, Account.class);
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
