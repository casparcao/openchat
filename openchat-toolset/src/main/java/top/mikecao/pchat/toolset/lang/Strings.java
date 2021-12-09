package top.mikecao.pchat.toolset.lang;

import java.util.Objects;

public final class Strings {

    /**
     * <p>判断是否为空</p>
     * <p>null为空</p>
     * <p>空白字符为空</p>
     * @param str
     * @return
     */
    public static boolean empty(String str){
        return Objects.isNull(str) || str.isEmpty() || str.trim().isEmpty();
    }

}
