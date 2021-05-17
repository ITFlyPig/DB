package com.wyl.db.manager;

import android.text.TextUtils;

import com.wyl.db.DB;
import com.wyl.db.DBConfiguration;

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
    public <T> int insert(List<T> entitys) {
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
    public <T> List<T> query(long id) {
        return null;
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
