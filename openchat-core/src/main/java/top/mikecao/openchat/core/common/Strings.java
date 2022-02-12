package top.mikecao.openchat.core.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * @author mike
 */
public final class Strings {

    private Strings(){}
    /**
     * <p>判断是否为空</p>
     * <p>null为空</p>
     * <p>空白字符为空</p>
     */
    public static boolean empty(String str){
        return Objects.isNull(str) || str.isEmpty() || str.trim().isEmpty();
    }

    public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    public static String format(Date date){
        DateFormat formatter = new SimpleDateFormat(DATE_TIME);
        return formatter.format(date);
    }
}
