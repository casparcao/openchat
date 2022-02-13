package top.mikecao.openchat.client.config;

/**
 * @author caohailong
 */

public final class Constants {

    private Constants(){}

    public static final String WEB_SERVER_HOST = "http://localhost:8080";

    public static final String RELATIONS_ENDPOINT = WEB_SERVER_HOST + "/relations";

    public static final String TOKEN_STORE_LOCATION = "E:/temp/.auth.json";

    public static final String LOGIN_ENDPOINT = WEB_SERVER_HOST + "/login";

    public static final String ROOM_ENDPOINT = WEB_SERVER_HOST + "/rooms";

    public static final String CHAT_ENDPOINT = WEB_SERVER_HOST + "/chats";
}
