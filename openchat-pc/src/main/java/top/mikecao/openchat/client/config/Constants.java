package top.mikecao.openchat.client.config;

/**
 * @author caohailong
 */

public interface Constants {

    String WEB_SERVER_HOST = "http://localhost:8080";

    String FRIENDS_ENDPOINT = WEB_SERVER_HOST + "/friends";

    String TOKEN_STORE_LOCATION = "E:/temp/.auth.json";

    String LOGIN_ENDPOINT = WEB_SERVER_HOST + "/login";
}
