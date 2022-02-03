package top.mikecao.openchat.core.auth;

/**
 * @author caohailong
 */

public interface TokenGranter {

    String HEADER = "Authorization";

    /**
     * 颁发token，根据认证信息颁发token
     * @param account 认证信息
     * @return token
     */
    String grant(Account account);

    /**
     * 根据token信息，解析为对应的认证对象
     * @param token token
     * @return auth
     */
    Account resolve(String token);
}
