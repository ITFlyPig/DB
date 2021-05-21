package com.wyl.db.manager;

import android.text.TextUtils;

import com.wyl.db.DB;
import com.wyl.db.DBConfiguration;
import com.wyl.db.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/8
 * 描述    : db操作的实现
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
        if (bean == null) return -1;
        return dbManager.insert(bean);
    }

    @Override
    public <T> long insert(List<T> entitys) {
        return dbManager.bulkInsert(entitys);
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
        if (entityClz == null) return null;
        // 获取表名
        String tableName = ReflectionUtil.getTableName(entityClz);
        if (TextUtils.isEmpty(tableName)) {
            return null;
        }
        // 找到主键名称
        Field primaryKeyField = ReflectionUtil.getPrimaryKeyField(entityClz);
        if (primaryKeyField == null) return null;
        String primaryKeyName = ReflectionUtil.getColumnName(primaryKeyField);
        if (TextUtils.isEmpty(primaryKeyName)) {
            return null;
        }

        // 取第一个：据主键查询，那肯定不会有多个啊
        List<T> results = query("select * from " + tableName + " where " + primaryKeyName + " = ?", new String[]{String.valueOf(id)}, entityClz);
        int len = results == null ? 0 : results.size();
        if (len == 0) return null;
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

    @Override
    public <T> long update(T entity) {
        return dbManager.update(entity);
    }

    @Override
    public <T> long update(T entity, String whereClause, String[] whereArgs) {
        return dbManager.update(entity, whereClause, whereArgs);
    }


}
