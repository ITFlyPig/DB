package com.wyl.db.manager.migration;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/24
 * 描述    : 升级和降级
 * @author yuelinwang
 */
public abstract class Migration {
    /**
     * 开始版本
     */
    private int startVersion;

    /**
     * 结束版本
     */
    private int endVersion;

    public Migration(int startVersion, int endVersion) {
        this.startVersion = startVersion;
        this.endVersion = endVersion;
    }

    /**
     * 升级、降级
     */
    public abstract void migrate(SQLiteDatabaseWrapper databaseWrapper);

    public int getStartVersion() {
        return startVersion;
    }

    public int getEndVersion() {
        return endVersion;
    }

    @Override
    public String toString() {
        return "Migration{" +
                "startVersion=" + startVersion +
                ", endVersion=" + endVersion +
                '}';
    }
}
