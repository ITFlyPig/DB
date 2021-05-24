package com.wyl.db;

import android.content.Context;
import android.text.TextUtils;

import com.wyl.db.converter.ITypeConverter;
import com.wyl.db.manager.ISQLLite;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/10
 * 描述    : 配置
 */
public class DBConfiguration {
    private Context context;
    /**
     * 数据类型的转换类
     */
    private ITypeConverter converter;
    /**
     * 数据库名
     */
    private String dbName;
    /**
     * 数据库版本
     */
    private int version;
    /**
     * 数据库的更新
     */
    private ISQLLite isqlLite;
    /**
     * 数据模型集合，会据里面的字段自动创建表
     */
    private Class<?>[] entitys;
    /**
     * log TAG
     */
    private String logTag;

    private DBConfiguration(Context context, ITypeConverter converter, String dbName, int version, ISQLLite isqlLite, Class<?>[] entitys, String logTag) {
        this.context = context;
        this.converter = converter;
        this.dbName = dbName;
        this.version = version;
        this.isqlLite = isqlLite;
        this.entitys = entitys;
        this.logTag = logTag;

    }

    public Class<?>[] getEntitys() {
        return entitys;
    }

    public String getLogTag() {
        return logTag;
    }

    public Context getContext() {
        return context;
    }

    public ITypeConverter getConverter() {
        return converter;
    }

    public String getDbName() {
        return dbName;
    }

    public int getVersion() {
        return version;
    }

    public ISQLLite getIsqlLite() {
        return isqlLite;
    }

    public static class Builder {
        private Context context;
        private ITypeConverter converter;
        private String dbName;
        private int version;
        private ISQLLite isqlLite;
        private Class<?>[] entitys;
        private String logTag;

        public Builder setLogTag(String logTag) {
            this.logTag = logTag;
            return this;
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setConverter(ITypeConverter converter) {
            this.converter = converter;
            return this;
        }

        public Builder setDbName(String dbName) {
            this.dbName = dbName;
            return this;
        }

        public Builder setVersion(int version) {
            this.version = version;
            return this;
        }

        public Builder setIsqlLite(ISQLLite isqlLite) {
            this.isqlLite = isqlLite;
            return this;
        }

        public Builder setEntities(Class<?> ...entity) {
            this.entitys = entity;
            return this;
        }

        public DBConfiguration build() {
            String illegalArgumentMsg = null;
            if (context == null) {
                illegalArgumentMsg = "参数Context不能为空";
            }
            if (TextUtils.isEmpty(dbName)) {
                illegalArgumentMsg = "参数dbName不能为空";
            }
            if (version <= 0) {
                illegalArgumentMsg = "版本号version必须大于0";
            }
            if (illegalArgumentMsg != null) {
                throw  new IllegalArgumentException(illegalArgumentMsg);
            }

            if (TextUtils.isEmpty(this.logTag)) {
                logTag = "DB";

            }

            return new DBConfiguration(this.context, this.converter, this.dbName, this.version, this.isqlLite, this.entitys, this.logTag);
        }
    }
}
