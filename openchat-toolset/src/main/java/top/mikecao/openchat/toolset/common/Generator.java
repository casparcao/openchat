package top.mikecao.openchat.toolset.common;

/**
 * 生成器接口，实现类可按需生成某类型的值
 *
 * @param <T> 期望生成值的类型
 * @author caohailong
 */
public interface Generator<T> {

    /**
     * 生成一个期望类型的值
     *
     * @return 返回生成的值
     */
    T next();

}
