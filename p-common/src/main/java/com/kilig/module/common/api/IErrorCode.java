package com.kilig.module.common.api;

/**
 * 封装api的错误代码
 *
 * @author L.Willian
 * @date 2019/11/25
 */
public interface IErrorCode {
    /**
     * 状态码
     *
     * @return
     */
    long getCode();

    /**
     * 状态信息
     *
     * @return
     */
    String getMessage();
}
