package top.mikecao.openchat.web.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import top.mikecao.openchat.core.Result;
import top.mikecao.openchat.core.exception.AppClientException;
import top.mikecao.openchat.core.exception.AppServerException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author caohailong
 */

@Configuration
public class GlobalResultConfig {

    private final Log logger = LogFactory.getLog(GlobalResultConfig.class);

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handle(Throwable e){
        logger.error(e.getMessage(), e);
        return Result.error("请求处理失败，请联系在线客服。");
    }

    /**
     * 客户端异常捕获
     * @param e e
     * @return r
     */
    @ExceptionHandler(AppClientException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public Result<Void> handle(AppClientException e){
        logger.error(e.getMessage(), e);
        return Result.error(e.getMessage());
    }

    /**
     * 业务异常捕获
     * @param e e
     * @return r
     */
    @ExceptionHandler(AppServerException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handle(AppServerException e){
        logger.error(e.getMessage(), e);
        return Result.error(e.getMessage());
    }
    /**
     * 方法参数校验异常捕获
     * @param e 异常
     * @return 结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalParamException(MethodArgumentNotValidException e) {
        String msg = "";
        if (e.getBindingResult().hasErrors()){
            List<ObjectError> errors = e.getBindingResult().getAllErrors();
            //拼接错误信息
            msg = errors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(","));
        }
        return Result.error(msg);
    }

    /**
     * 请求参数不正确（包括，参数缺失，不匹配等情况）
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler({BindException.class, TypeMismatchException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public Result<Void> handle(Exception e){
        logger.error(e.getMessage(), e);
        return Result.error("请求参数不正确");
    }

    /**
     * 请求体不正确(包括:缺失,请求体json格式解析失败)
     * @param e 异常
     * @return 返回格式
     */
    @ExceptionHandler(HttpMessageConversionException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public Result<Void> handle(HttpMessageConversionException e){
        logger.error(e.getMessage(), e);
        return Result.error("请求参数不正确");
    }

}
