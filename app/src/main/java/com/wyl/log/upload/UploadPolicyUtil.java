package com.wyl.log.upload;

import com.wyl.log.persistence.LogBean;

/**
 * @author : yuelinwang
 * @time : 6/8/21
 * @desc : 全局的上传工具
 */
public class UploadPolicyUtil {
    private static IUploadPolicy uploadPolicy;
    static {
        uploadPolicy = new LimitNumPolicy(LogBean.class, LimitNumPolicy.DEFAULT_LIMIT);
    }

    public static void checkUpload() {
        if (uploadPolicy.shouldUpload()) {

        }

    }
}
