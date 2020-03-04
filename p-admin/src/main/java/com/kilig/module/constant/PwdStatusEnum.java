package com.kilig.module.constant;

import lombok.Getter;

/**
 * 数字常量定义
 *
 * @author akihi
 */
@Getter
public enum PwdStatusEnum {
    /**
     * 0
     * 正常
     */
    NORMAl_STATUS(0, "正常"),
    /**
     * -1
     * 提交参数不合法
     */
    PARAM_ILLEGAL(-1, "提交参数不合法"),
    /**
     * -2
     * 找不到用户
     */
    NOT_USER(-2, "找不到用户"),
    /**
     * -3
     * 旧密码错误
     */
    OLD_PWD_ERROR(-3, "旧密码错误");

    private int status;
    private String desc;

    private PwdStatusEnum(int code, String desc) {
        this.status = code;
        this.desc = desc;
    }

}
