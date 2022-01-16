package top.mikecao.openchat.core;

import lombok.Getter;
import lombok.ToString;

/**
 * @param <T> 返回值的类型
 * @author mike
 */
@ToString
public class Result<T> {
    @Getter
    private int code;
    @Getter
    private String msg;
    @Getter
    private T data;

    private Result(){
    }

    public boolean success(){
        return this.code == 0;
    }

    public boolean failure(){
        return !success();
    }

    /**
     * <p>
     * 获取成功值或者提供的other值
     * </p>
     * <p>
     *     success()?data:other
     * </p>
     *
     * @param other 失败时的默认值
     * @return 成功值或者other值
     */
    public T or(T other){
        return success()?data:other;
    }

    public static <T> Result<T> ok(){
        return ok(null);
    }

    public static <T> Result<T> ok(T data){
        return ok(data, "");
    }

    public static <T> Result<T> ok(T data, String msg){
        return done(0, data, msg);
    }

    public static <T> Result<T> done(int code, T data, String msg){
        Result<T> r = new Result<>();
        r.code = code;
        r.data = data;
        r.msg = msg;
        return r;
    }

    public static <T> Result<T> error(){
        return error("");
    }

    public static <T> Result<T> error(String msg) {
        return done(-1, null, msg);
    }

    public static <T> Result<T> error(T data, String msg){
        return done(-1, data, msg);
    }

    public Result<T> code(int code){
        this.code = code;
        return this;
    }

    public Result<T> msg(String message){
        this.msg = message;
        return this;
    }

    public Result<T> data(T data){
        this.data = data;
        return this;
    }

}
