package com.wyl.log.persistence;

/**
 * @author : yuelinwang
 * @time : 6/9/21
 * @desc : 日志的状态
 */
public interface LogStatus {
    /**
     * 初始状态
     */
    int INIT = 0;

    /**
     * 正在上传状态
     */
    int UPLOADING = 1;

}
