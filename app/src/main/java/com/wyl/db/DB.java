package com.wyl.db;

import android.content.ContentValues;

import com.wyl.db.constant.Codes;
import com.wyl.db.manager.IOperation;
import com.wyl.db.manager.WUOperationImpl;

import java.util.List;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/8
 * 描述    : 对外暴露的数据库操作，使用接口IOperation隔离具体的实现
 * @author yuelinwang
 */
public class DB {
    private static final String TAG = DB.class.getSimpleName();
    /**
     * 配置，对使用者不可见，包可见，方便直接引用
     */
    private static DBConfiguration conf;

    private static IOperation operation;
    /**
     * 标识框架是否已初始化
     */
    private static volatile boolean isInit;

    public static void init(DBConfiguration conf) {
        DB.conf = conf;
        if (conf == null) {
            throw new IllegalArgumentException("DB.init 参数 conf 不能为空");
        }
        operation = new WUOperationImpl();
        isInit = true;
    }

    /**
     * 插入单个对象
     *
     * @param bean
     * @param <T>
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public static <T> long insert(T bean) {
        if (!isInit) {
            return Codes.ERROR_CODE;
        }
        return operation.<T>insert(bean);
    }

    /**
     * 插入对象集合，注意：该方法启动了事务，只要有插入失败的，就不会插入到数据库中
     *
     * @param entitys
     * @param <T>
     * @return
     */
    public static <T> void insert(List<T> entitys) {
        if (!isInit) {
            return;
        }
        operation.insert(entitys);
    }

    /**
     * 查询
     *
     * @param sql           sql语句
     * @param selectionArgs 查询的条件
     * @param entityClz     需要查询返回的数据模型
     * @param <T>
     * @return
     */
    public static <T> List<T> query(Class<T> entityClz, String sql, String... selectionArgs) {
        if (!isInit) {
            return null;
        }
        return operation.<T>query(sql, selectionArgs, entityClz);
    }

    /**
     * 据id查询对应的实体
     *
     * @param id
     * @param <T>
     * @return
     */
    public static <T> T queryById(long id, Class<T> entityClz) {
        if (!isInit) {
            return null;
        }
        return operation.query(id, entityClz);


    }

    /**
     * 删除实体对应的表中的记录，注意：实体必须有主键才能使用这个方法
     *
     * @param entity
     * @param <T>
     * @return
     */
    public static <T> int delete(T entity) {
        if (!isInit) {
            return Codes.ERROR_CODE;
        }
        return operation.delete(entity);
    }

    /**
     * 批量删除，只要有一个删除失败，那么此次操作就不会有任何删除
     *
     * @param entitys
     * @param <T>
     * @return
     */
    public static <T> int delete(List<T> entitys) {
        if (!isInit) {
            return Codes.ERROR_CODE;
        }
        return operation.delete(entitys);
    }

    /**
     * 据主键更新实体在数据库中对应的数据，注意：传入的实体必须有主键
     *
     * @param entity
     * @param <T>
     * @return
     */
    public static <T> long update(T entity) {
        if (!isInit) {
            return Codes.ERROR_CODE;
        }
        return operation.update(entity);
    }

    /**
     * 据自定义条件更新数据库，注意：此方法不要求必须有主键
     *
     * @param entity
     * @param whereClause
     * @param whereArgs
     * @param <T>
     * @return
     */
    public static <T> long update(T entity, String whereClause, String... whereArgs) {
        if (!isInit) {
            return Codes.ERROR_CODE;
        }
        return operation.update(entity, whereClause, whereArgs);

    }

    public static DBConfiguration getConf() {
        return conf;
    }

    /**
     * 获取日志TAG
     * @return
     */
    public static String tag() {
        if (conf == null || conf.getLogTag() == null) {
            return TAG;
        }
        return conf.getLogTag();
    }

    /**
     * 统计数量
     *
     * @param entityClz
     * @param whereClause
     * @param whereArgs
     * @param <T>
     * @return -1：失败；>= 0：表示获取到的数量
     */
    public static  <T> long count(Class<T> entityClz, String whereClause, String... whereArgs) {
        return operation.count(entityClz, whereClause, whereArgs);
    }

    /**
     * 删除
     * @param entityClz
     * @param whereClause 如果有where，必须得自己写 where xxxx >= xxxx 类似的
     * @param whereArgs
     * @param <T>
     * @return -1：失败；>= 0：影响的行数
     */
    public static  <T> long delete(Class<T> entityClz, String whereClause, String... whereArgs) {
        return operation.delete(entityClz, whereClause, whereArgs);
    }

    /**
     * 更新
     * @param entityClz
     * @param values
     * @param whereClause
     * @param whereArgs
     * @param <T>
     * @return 受影响的行数
     */
    public static  <T> long update(Class<T> entityClz, ContentValues values, String whereClause, String... whereArgs) {
        return operation.update(entityClz, values, whereClause, whereArgs);
    }
}
