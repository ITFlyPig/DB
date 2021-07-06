package com.wyl.log.upload;

/**
 * @author : yuelinwang
 * @time : 7/6/21
 * @desc : 上传的接口
 */
public interface UploadListener {
    /**
     * 上传成功
     * @param version 上传成功的数据对应的版本
     */
    void onSuccess(long version);

    /**
     * 上传失败
     * @param version 上传失败的数据对应的版本
     */
    void onFail(long version);
}
