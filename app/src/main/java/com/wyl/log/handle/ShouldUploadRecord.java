package com.wyl.log.handle;

/**
 * @author : yuelinwang
 * @time : 6/24/21
 * @desc : 应该上传的条数记录
 */
public class ShouldUploadRecord {
    private long num = -1;

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }


    public void add(long newNum) {
        num += newNum;
    }

    public void decrement(long newNum) {
        num -= newNum;
    }




}
