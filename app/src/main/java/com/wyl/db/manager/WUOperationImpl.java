package com.wyl.db.manager;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import com.wyl.db.DB;
import com.wyl.db.DBConfiguration;
import com.wyl.db.constant.Codes;
import com.wyl.db.util.LogUtil;
import com.wyl.db.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/8
 * 描述    : db操作的实现
 * @author yuelinwang
 */
public class WUOperationImpl implements IOperation {
    private DBManager dbManager;

    public WUOperationImpl() {
        DBConfiguration conf = DB.getConf();
        SQLiteHelper.init(conf.getContext(), conf.getDbName(), conf.getVersion(), new SQLLiteImpl());
        dbManager = new DBManager();
    }

    @Override
    public <T> long insert(T bean) {
        if (bean == null) {
            return Codes.ERROR_CODE;
        }
        return dbManager.insert(bean);
    }

    @Override
    public <T> void insert(List<T> entitys) {
        dbManager.bulkInsert(entitys);
    }

    @Override
    public <T> List<T> query(String sql, String[] selectionArgs, Class<T> entityClz) {
        if (TextUtils.isEmpty(sql) || entityClz == null) {
            return new ArrayList<>(0);
        }
        return dbManager.query(sql, selectionArgs, entityClz);
    }

    @Override
    public <T> T query(long id, Class<T> entityClz) {
        if (entityClz == null) {
            return null;
        }
        // 获取表名
        String tableName = ReflectionUtil.getTableName(entityClz);
        if (TextUtils.isEmpty(tableName)) {
            LogUtil.w(DB.tag(), "据主键query失败：获取到的表名为空");
            return null;
        }
        // 找到主键名称
        Field primaryKeyField = ReflectionUtil.getPrimaryKeyField(entityClz);
        if (primaryKeyField == null) {
            LogUtil.w(DB.tag(), "据主键query失败：获取到的主键字段为空");
            return null;
        }
        String primaryKeyName = ReflectionUtil.getColumnName(primaryKeyField);
        if (TextUtils.isEmpty(primaryKeyName)) {
            LogUtil.w(DB.tag(), "据主键query失败：获取到的主键名字为空");
            return null;
        }

        // 取第一个：据主键查询，那肯定不会有多个啊
        List<T> results = query("select * from " + tableName + " where " + primaryKeyName + " = ?", new String[]{String.valueOf(id)}, entityClz);
        int len = results == null ? 0 : results.size();
        if (len == 0) {
            return null;
        }
        return results.get(0);
    }

    @Override
    public <T> int delete(T entity) {
        return dbManager.delete(entity);
    }

    @Override
    public <T> int delete(List<T> entitys) {
        return dbManager.delete(entitys);
    }

    /**
     * 据传入的entity的主键进行更新
     * @param entity
     * @param <T>
     * @return
     */
    @Override
    public <T> long update(T entity) {
        return dbManager.update(entity);
    }

    /**
     * 将符合条件的数据都修改为entity那样的
     * @param entity
     * @param whereClause
     * @param whereArgs
     * @param <T>
     * @return
     */
    @Override
    public <T> long update(T entity, String whereClause, String[] whereArgs) {
        return dbManager.update(entity, whereClause, whereArgs);
    }

    @Override
    public <T> long count(Class<T> entityClz, String whereClause, String[] whereArgs) {
        return dbManager.count(entityClz, whereClause, whereArgs);
    }

    @Override
    public <T> long delete(Class<T> entityClz, String whereClause, String[] whereArgs) {
        return dbManager.delete(entityClz, whereClause, whereArgs);
    }

    @Override
    public <T> long update(Class<T> entityClz, ContentValues values, String whereClause, String[] whereArgs) {
        return dbManager.update(entityClz, values, whereClause, whereArgs);
    }


}
