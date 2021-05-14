package com.wyl.db;

import com.wyl.db.bean.User;
import com.wyl.db.util.SQLUtil;

import junit.framework.TestCase;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/11
 * 描述    :
 */
public class SQLUtilTest extends TestCase {

    public void testCreateTableSQL() {
        String sql = SQLUtil.createTableSQL(User.class, null);
        System.out.println(sql);
    }
}