package com.wyl.log.upload;

import com.wyl.log.handle.ShouldUploadRecord;

import junit.framework.TestCase;

import org.junit.Assert.*;

/**
 * @author : yuelinwang
 * @time : 7/6/21
 * @desc : 测试基于存储条数的上传策略
 */
public class LimitNumPolicyTest extends TestCase {

    public void testShouldUpload() {
        ShouldUploadRecord shouldUploadRecord = new ShouldUploadRecord();
        int limit = 100;
        IUploadPolicy uploadPolicy = new LimitNumPolicy(limit, shouldUploadRecord);
        for (int i = 0; i < 200; i++) {
            // 增加一条记录
            shouldUploadRecord.add(1);

            if (i < limit) {
                assertFalse(uploadPolicy.shouldUpload());
            } else {
                assertTrue(uploadPolicy.shouldUpload());
            }

        }
    }
}