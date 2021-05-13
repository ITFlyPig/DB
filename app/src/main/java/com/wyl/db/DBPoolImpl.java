package com.wyl.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/9
 * 描述    : 数据库连接池的实现
 */
public class DBPoolImpl implements IDBPool {
    // 默认的核心连接数
    private static final int DEFAULT_CORE = 1;
    // 默认的最大连接数
    private static final int DEFAULT_MAX = 3;
    // 核心数据库连接数
    private final int coreNum;
    // 最多可能的数据库连接
    private final int maxNum;
    public static  SQLiteOpenHelper sqLiteOpenHelper;
    // 存放数据库连接
    private LinkedBlockingQueue<SQLiteDatabase> connectionQueue;
    //记录借出去的连接数
    private AtomicInteger borrowedNum = new AtomicInteger(0);
    // 记录哪些对象已经归还了
    private final List<Integer> returnRecords = Collections.synchronizedList(new ArrayList<>());
    // 表示连接池是否关闭
    private volatile boolean isClosed;

    public DBPoolImpl(@NonNull SQLiteOpenHelper sqLiteOpenHelper) {
        this(DEFAULT_CORE, DEFAULT_MAX, sqLiteOpenHelper);
    }

    public DBPoolImpl(int coreNum, int maxNum, @NonNull SQLiteOpenHelper sqLiteOpenHelper) {
        this.coreNum = coreNum;
        this.maxNum = maxNum;
        this.sqLiteOpenHelper = sqLiteOpenHelper;
        connectionQueue = new LinkedBlockingQueue<>(maxNum);
        if (sqLiteOpenHelper == null) {
            throw new IllegalArgumentException("DBPoolImpl 参数 sqLiteOpenHelper 不能为空");
        }
        createCoreConnection();
    }

    /**
     * 创建核心连接
     */
    private void createCoreConnection() {
        if (coreNum <= 0) return;
        for (int i = 0; i < coreNum; i++) {
            connectionQueue.offer(sqLiteOpenHelper.getWritableDatabase());
        }
    }


    @Override
    public SQLiteDatabase borrowSQLiteDatabase() {
        return acquire(new AcquireCallBack() {
            @Override
            public SQLiteDatabase acquire() {
                SQLiteDatabase sqLiteDatabase = null;
                try {
                    sqLiteDatabase = connectionQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return sqLiteDatabase;
            }
        });
    }


    @Override
    public SQLiteDatabase borrowSQLiteDatabase(long timeout, TimeUnit unit) {
        return acquire(new AcquireCallBack() {
            @Override
            public SQLiteDatabase acquire() {
                SQLiteDatabase sqLiteDatabase = null;
                try {
                    sqLiteDatabase = connectionQueue.poll(timeout, unit);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return sqLiteDatabase;
            }
        });
    }

    private SQLiteDatabase acquire(AcquireCallBack acquireCallBack) {
        SQLiteDatabase sqLiteDatabase = null;

        // 连接池已关闭
        if (isClosed) {
            return sqLiteDatabase;
        }

        // 尝试创建新的连接
        if (connectionQueue.isEmpty()) {
            sqLiteDatabase = tryCreateConnection();
            if (sqLiteDatabase != null) {
                // 借出去的记录加1
                borrowedNum.incrementAndGet();
            }
        }

        if (sqLiteDatabase != null) return sqLiteDatabase;

        sqLiteDatabase = acquireCallBack.acquire();
        if (sqLiteDatabase != null) {
            // 借出去的记录加1
            borrowedNum.incrementAndGet();
        }
        return sqLiteDatabase;

    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    /**
     * 尝试创建一个新的连接
     *
     * @return
     */
    private SQLiteDatabase tryCreateConnection() {
        // 检查是否还可以创建
        if (borrowedNum.get() + connectionQueue.size() >= maxNum) {
            return null;
        }

        return getSafeWritableDatabase();

    }

    /**
     * 安全地获取连接
     *
     * @return
     */
    private SQLiteDatabase getSafeWritableDatabase() {
        try {
            return sqLiteOpenHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void returnSQLiteDatabase(SQLiteDatabase database) {
        if (database == null) return;

        // 对于关闭的连接池，还回来的连接，直接关闭
        if (isClosed) {
            closeConnection(database);
            return;
        }

        // 检查，避免对于同一个对象多次调用归还导致的borrowedNum不准确
        if (connectionQueue.contains(database)) return;
        if (returnRecords.contains(database.hashCode())) return;

        borrowedNum.decrementAndGet();

        // 对于不是打开的连接直接不在放入队列；对于打开的连接，尝试放入队列，如果失败则关闭它
        if (!database.isOpen() || !connectionQueue.offer(database)) {
            closeConnection(database);
            returnRecords.add(database.hashCode());
        }
    }

    @Override
    public synchronized void close() {
        isClosed = true;
        // 关闭队列中的连接
        for (SQLiteDatabase sqLiteDatabase : connectionQueue) {
            closeConnection(sqLiteDatabase);
        }
        connectionQueue.clear();
    }

    /**
     * 关闭连接
     *
     * @param database
     */
    private void closeConnection(SQLiteDatabase database) {
        if (database == null) return;
        database.close();
    }
}
