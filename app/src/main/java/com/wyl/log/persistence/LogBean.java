package com.wyl.log.persistence;

import com.wyl.db.annotations.PrimaryKey;
import com.wyl.db.annotations.Table;

/**
 * @author : yuelinwang
 * @time : 2021/6/3
 * @desc : 存储日志的表
 */

@Table(name = "log_table")
public class LogBean {
    @PrimaryKey(autoGenerate = true)
    public long id;
    /**
     * 日志的内容
     */
    public String content;

    /**
     * 日志的类型
     */
    public int type;

    /**
     * 日志产生的时间
     */
    public long time;

    public LogBean(String content, int type, long time) {
        this.content = content;
        this.type = type;
        this.time = time;
    }
}
