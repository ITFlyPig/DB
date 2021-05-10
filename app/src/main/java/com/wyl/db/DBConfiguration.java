package com.wyl.db;

import android.content.Context;
import android.text.TextUtils;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/10
 * 描述    : 配置
 */
public class DBConfiguration {
    private Context context;
    //数据类型的转换类
    private IConverter converter;
    //数据库名
    private String dbName;
    // 数据库版本
    private int version;
    // 数据库的更新
    private ISQLLite isqlLite;

    private DBConfiguration(Context context, IConverter converter, String dbName, int version, ISQLLite isqlLite) {
        this.context = context;
        this.converter = converter;
        this.dbName = dbName;
        this.version = version;
        this.isqlLite = isqlLite;
    }

    public Context getContext() {
        return context;
    }

    public IConverter getConverter() {
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
        private IConverter converter;
        private String dbName;
        private int version;
        private ISQLLite isqlLite;

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setConverter(IConverter converter) {
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
            if (isqlLite == null) {
                illegalArgumentMsg = "数据库创建更新实现不能为空";
            }
            if (illegalArgumentMsg != null) {
                throw  new IllegalArgumentException(illegalArgumentMsg);
            }

            return new DBConfiguration(this.context, this.converter, this.dbName, this.version, this.isqlLite);
        }
    }
}
