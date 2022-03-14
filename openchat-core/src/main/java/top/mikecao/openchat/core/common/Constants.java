package top.mikecao.openchat.core.common;

/**
 * @author caohailong
 */

public final class Constants {

    private Constants(){}

    /**
     * 没发生读操作触发IdleStateEvent的周期
     */
    public static final int READER_IDLE_TIME = 15;
    /**
     * 没发生写操作触发IdleStateEvent的周期
     */
    public static final int WRITER_IDLE_TIME = 5;
}

