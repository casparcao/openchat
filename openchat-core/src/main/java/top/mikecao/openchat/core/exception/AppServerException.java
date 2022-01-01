package top.mikecao.openchat.core.exception;

/**
 * @author caohailong
 */

public class AppServerException extends RuntimeException{

    public AppServerException(String message){
        super(message);
    }

    public AppServerException(String message, Throwable e){
        super(message, e);
    }
}
