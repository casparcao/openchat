package top.mikecao.openchat.server.session;

/**
 * @author caohailong
 */

public interface TokenGranter {

    /**
     * 颁发token，根据认证信息颁发token
     * @param auth 认证信息
     * @return token
     */
    String grant(Auth auth);

    /**
     * 根据token信息，解析为对应的认证对象
     * @param token token
     * @return auth
     */
    Auth resolve(String token);
}
