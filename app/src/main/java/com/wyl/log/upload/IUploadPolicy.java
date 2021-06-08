package com.wyl.log.upload;

/**
 * @author : yuelinwang
 * @time : 6/8/21
 * @desc : 上传策略，用于出发上传
 */
public interface IUploadPolicy {
    /**
     * 是否应该上传
     * @return
     */
    boolean shouldUpload();
}
