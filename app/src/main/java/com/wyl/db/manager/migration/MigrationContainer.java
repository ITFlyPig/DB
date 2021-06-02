package com.wyl.db.manager.migration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wyl.db.DB;
import com.wyl.db.util.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * 管理升级和降级
 * @author yuelinwang
 */
public class MigrationContainer {
    private HashMap<Integer, TreeMap<Integer, Migration>> mMigrations = new HashMap<>();

    /**
     * Adds the given migrations to the list of available migrations. If 2 migrations have the
     * same start-end versions, the latter migration overrides the previous one.
     *
     * @param migrations List of available migrations.
     */
    public void addMigrations(@NonNull Migration... migrations) {
        for (Migration migration : migrations) {
            addMigration(migration);
        }
    }

    private void addMigration(Migration migration) {
        final int start = migration.getStartVersion();
        final int end = migration.getEndVersion();
        TreeMap<Integer, Migration> targetMap = mMigrations.get(start);
        if (targetMap == null) {
            targetMap = new TreeMap<>();
            mMigrations.put(start, targetMap);
        }
        Migration existing = targetMap.get(end);
        if (existing != null) {
            LogUtil.w(DB.tag(), "Overriding migration " + existing + " with " + migration);
        }
        targetMap.put(end, migration);
    }

    /**
     * Finds the list of migrations that should be run to move from {@code start} version to
     * {@code end} version.
     *
     * @param start The current database version
     * @param end   The target database version
     * @return An ordered list of {@link Migration} objects that should be run to migrate
     * between the given versions. If a migration path cannot be found, returns {@code null}.
     */
    @SuppressWarnings("WeakerAccess")
    @Nullable
    public List<Migration> findMigrationPath(int start, int end) {
        if (start == end) {
            return Collections.emptyList();
        }
        boolean migrateUp = end > start;
        List<Migration> result = new ArrayList<>();
        return findUpMigrationPath(result, migrateUp, start, end);
    }

    private List<Migration> findUpMigrationPath(List<Migration> result, boolean upgrade,
                                                int start, int end) {
        while (upgrade ? start < end : start > end) {
            TreeMap<Integer, Migration> targetNodes = mMigrations.get(start);
            if (targetNodes == null) {
                return null;
            }
            // keys are ordered so we can start searching from one end of them.
            Set<Integer> keySet;
            if (upgrade) {
                keySet = targetNodes.descendingKeySet();
            } else {
                keySet = targetNodes.keySet();
            }
            boolean found = false;
            for (int targetVersion : keySet) {
                final boolean shouldAddToPath;
                if (upgrade) {
                    shouldAddToPath = targetVersion <= end && targetVersion > start;
                } else {
                    shouldAddToPath = targetVersion >= end && targetVersion < start;
                }
                if (shouldAddToPath) {
                    result.add(targetNodes.get(targetVersion));
                    start = targetVersion;
                    found = true;
                    break;
                }
            }
            if (!found) {
                return null;
            }
        }
        return result;
    }
}