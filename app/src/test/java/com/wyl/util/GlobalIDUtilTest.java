package com.wyl.util;

import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * @author : yuelinwang
 * @time : 7/13/21
 * @desc :
 */
public class GlobalIDUtilTest extends TestCase {

    public void testGetID() {
        ArrayList<String> ids = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            String id = GlobalIDUtil.getID();
            // 验证生成的id是唯一的
            assertFalse(ids.contains(id));
            ids.add(id);
            System.out.println(id);
        }
    }
}