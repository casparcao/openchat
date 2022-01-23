package top.mikecao.openchat.core.file;

import lombok.extern.slf4j.Slf4j;
import top.mikecao.openchat.core.exception.AppServerException;
import top.mikecao.openchat.core.serialize.Json;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * 本地文件的读写，文件内容为json格式
 * @author caohailong
 */

@Slf4j
public final class Storage {

    private Storage(){}

    /**
     * 读取指定路径文件的内容，并转化为指定类型的对象形式；
     * 如果路径指向的文件不存在，则返回null
     * @param path 文件路径
     * @param clazz 目标对象类型
     * @param <T> 目标类型
     * @return 对象
     */
    public static <T> T load(String path, Class<? extends T> clazz){
        Path p = Paths.get(path);
        boolean exists = Files.exists(p, LinkOption.NOFOLLOW_LINKS);
        if(!exists){
            return null;
        }
        String content;
        try {
            content = Files.readString(p, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("读取文件异常>>", e);
            throw new AppServerException("读取文件异常");
        }
        return Json.from(content, clazz);
    }

    public static void store(String path, Object object){
        Path p = Paths.get(path);
        String content = Json.to(object);
        try {
            Files.writeString(p, content, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE);
        } catch (IOException e) {
            log.error("写入文件异常>>", e);
            throw new AppServerException("写入文件异常");
        }
    }
}
