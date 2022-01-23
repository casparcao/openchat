package top.mikecao.openchat.core.http;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import top.mikecao.openchat.core.exception.AppServerException;
import top.mikecao.openchat.core.serialize.Json;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * @author caohailong
 */

@Slf4j
public final class Client {

    private Client(){}

    private static final HttpClient INSTANCE = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    public static HttpClient get(){
        return INSTANCE;
    }

    public static <T> T request(String uri, Object payload, TypeReference<T> type){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(Json.to(payload)))
                .build();
        HttpResponse<String> response;
        try {
            response = INSTANCE.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            log.error("请求失败>>", e);
            throw new AppServerException("请求服务器资源失败");
        } catch (InterruptedException e) {
            log.error("线程异常中断>>", e);
            Thread.currentThread().interrupt();
            throw new AppServerException("线程异常中断");
        }
        return Json.from(response.body(), type);
    }
}
