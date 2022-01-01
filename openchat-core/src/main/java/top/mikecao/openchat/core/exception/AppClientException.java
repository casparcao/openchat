package top.mikecao.openchat.core.exception;

/**
 * @author caohailong
 */

public class AppClientException extends RuntimeException{

    private final int code;
    public AppClientException(String message){
        this(400, message);
    }
    public AppClientException(int code, String message){
        super(message);
        this.code = code;
    }
    public AppClientException(int code, String message, Throwable e){
        super(message, e);
        this.code = code;
    }

    public int code(){
        return this.code;
    }
}
