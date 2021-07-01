package com.wyl.db.manager.migration;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : yuelinwang
 * @time : 7/1/21
 * @desc : 测试版本的升级和降级管理
 */
public class MigrationContainerTest extends TestCase {
    private MigrationContainer migrationContainer;
    // 版本升级
    private Migration MIGRATION_1_2;
    private Migration MIGRATION_2_3;
    private Migration MIGRATION_3_4;
    // 版本降价
    private Migration MIGRATION_3_2;
    private Migration MIGRATION_2_1;

    public void setUp() throws Exception {
        super.setUp();
        migrationContainer = new MigrationContainer();
        MIGRATION_1_2 = new Migration(1, 2) {
            @Override
            public void migrate(SQLiteDatabaseWrapper databaseWrapper) {

            }
        };
        MIGRATION_2_3 = new Migration(2, 3) {
            @Override
            public void migrate(SQLiteDatabaseWrapper databaseWrapper) {

            }
        };
        MIGRATION_3_4 = new Migration(3, 4) {
            @Override
            public void migrate(SQLiteDatabaseWrapper databaseWrapper) {

            }
        };
        MIGRATION_3_2 = new Migration(3, 2) {
            @Override
            public void migrate(SQLiteDatabaseWrapper databaseWrapper) {

            }
        };
        MIGRATION_2_1 = new Migration(2, 1) {
            @Override
            public void migrate(SQLiteDatabaseWrapper databaseWrapper) {

            }
        };
        migrationContainer.addMigrations(MIGRATION_1_2);
        migrationContainer.addMigrations(MIGRATION_2_3);
        migrationContainer.addMigrations(MIGRATION_3_4);
        migrationContainer.addMigrations(MIGRATION_2_1);
        migrationContainer.addMigrations(MIGRATION_3_2);

    }

    public void testFindMigrationPath() {

        // 测试非跨版本升级 1 -> 2
        List<Migration> migrations_1_2 = migrationContainer.findMigrationPath(1, 2);
        List<Migration> test_1_2 = new ArrayList<>();
        test_1_2.add(MIGRATION_1_2);
        assertEquals(test_1_2, migrations_1_2);

        // 测试跨版本升级 1 -> 4
        List<Migration> migrations_1_4 = migrationContainer.findMigrationPath(1, 4);
        assertTrue(equals(migrations_1_4.get(0), MIGRATION_1_2));
        assertTrue(equals(migrations_1_4.get(1), MIGRATION_2_3));
        assertTrue(equals(migrations_1_4.get(2), MIGRATION_3_4));

        //测试一个版本的降级 3 -> 2
        List<Migration> migrations_3_2 = migrationContainer.findMigrationPath(3, 2);
        assertTrue(equals(migrations_3_2.get(0), MIGRATION_3_2));

        // 测试跨版本降级
        List<Migration> migrations_3_1 = migrationContainer.findMigrationPath(3, 1);
        assertTrue(equals(migrations_3_1.get(0), MIGRATION_3_2));
        assertTrue(equals(migrations_3_1.get(1), MIGRATION_2_1));

    }

    private boolean equals(Migration a, Migration b) {
        if (a == null && b == null) return true;
        if (a != null && b != null & a.getStartVersion() == b.getStartVersion() && a.getEndVersion() == b.getEndVersion()) {
            return true;
        }
        return false;
    }

}