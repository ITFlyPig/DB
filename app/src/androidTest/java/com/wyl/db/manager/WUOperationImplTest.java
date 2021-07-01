package com.wyl.db.manager;

import android.text.TextUtils;

import com.wyl.db.DBInitUtil;
import com.wyl.db.bean.User;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author : yuelinwang
 * @time : 7/1/21
 * @desc : 测试
 */
public class WUOperationImplTest extends TestCase {
    private WUOperationImpl wuOperation;
    private User user;

    public void setUp() throws Exception {
        DBInitUtil.init();
        wuOperation = new WUOperationImpl();
        user = new User();
        user.custom = "自定义字段";
        user.aByte = 0;
        user.aShort = 0;
        user.anInt = 0;
        user.aLong = 0;
        user.aFloat = 0;
        user.aDouble = 0;
        user.id = 1;
        user.aDouble = 100000000.0;
        user.anInt = 88888;
        user.arrayList = new ArrayList<>();
        user.arrayList.add("wang");
        user.arrayList.add("yue");
        user.arrayList.add("lin");

        user.hashMap = new HashMap<>();
        user.hashMap.put("name", "wang");



    }


    public void testInsert() {
        long id = wuOperation.insert(user);
        assertTrue(id > 0);
        user.id = (int) id;
        User newUser = wuOperation.query(id, User.class);
        contentEquals(user, newUser);

    }

    public void testTestInsert() {

    }

    public void testQuery() {
        List<User> users = wuOperation.query("select * from User", null, User.class);
        assertTrue(users.size() > 0);
    }

    public void testTestQuery() {
        User user = wuOperation.query(1, User.class);
        assertNotNull(user);
    }

    public void testDelete() {
        User user = wuOperation.query(1, User.class);
        if (user != null) {
            long num = wuOperation.delete(user);
            assertTrue(num > 0);
        }
    }

    public void testTestDelete() {
        List<User> users = wuOperation.query("select * from User", null, User.class);
        wuOperation.delete(users);
        List<User> newUsers = wuOperation.query("select * from User", null, User.class);
        int len = newUsers == null ? 0 : newUsers.size();
        assertEquals(0, len);

    }

    public void testUpdate() {
        long id = wuOperation.insert(user);
        assertTrue(id > 0);
        user.id = (int) id;
        user.aaLong = 100L;
        user.hashMap = new HashMap<>();
        user.hashMap.put("name", "xiaoming");
        long num = wuOperation.update(user);
        assertTrue(num > 0);
        User updatedUser = wuOperation.query(id, User.class);
        contentEquals(updatedUser, user);

    }

    public void testTestUpdate() {
        List<User> users = wuOperation.query("select * from User", null, User.class);
        int len = users == null ? 0 : users.size();
        if (len == 0) return;
        User temp = users.get(0);
        temp.custom = "条件修改Map";
        temp.aaLong = 22L;
        user.arrayList = new ArrayList<>();
        user.arrayList.add("条件修改List");

        // 将id >= 0的所有的数据都修改为temp这样的
        long num = wuOperation.update(temp, "_id  >= ?", new String[]{"0"});
        assertTrue(num > 0);
        List<User> updatedUsers = wuOperation.query("select * from User", null, User.class);
        assertTrue(updatedUsers.size() > 0);
        for (User updatedUser : updatedUsers) {
            if (updatedUser.id >= 0) {
                contentEqualsExcludeId(updatedUser, temp);
            }
        }
    }

    public void testCount() {
        List<User> users = wuOperation.query("select * from User", null, User.class);
        int len = users == null ? 0 : users.size();
        long count = wuOperation.count(User.class, "_id >= ?", new String[]{"0"});
        assertEquals(len, count);

    }

    public void testTestDelete1() {
        List<User> users = wuOperation.query("select * from User", null, User.class);
        int len = users == null ? 0 : users.size();
        if (len == 0) return;
        int deleteId = users.get(0).id;
        long num = wuOperation.delete(User.class, "_id = ?", new String[]{String.valueOf(deleteId)});
        assertEquals(num, 1);
        List<User> deletedUsers = wuOperation.query("select * from User", null, User.class);
        for (User u : deletedUsers) {
            assertTrue(u.id != deleteId);
        }

    }

    public void testTestUpdate1() {
    }

    private void contentEquals(User a, User b) {
        assertEquals(a.id, b.id);
        assertEquals(a.anInt ,b.anInt);
        assertTrue(TextUtils.equals(a.custom, b.custom));
        assertEquals(a.aShort , b.aShort);
        assertEquals(a.aShort , b.aShort);
        assertEquals(a.aByte , b.aByte);
        assertEquals(a.aLong , b.aLong);
//        assertEquals(a.aDouble , b.aDouble); 这是忽略字段，两个不相等也是合理的
        assertEquals(a.aFloat , b.aFloat);
        assertArrayEquals(a.aBytes, b.aBytes);
        assertEquals(a.aaLong, b.aaLong);
        assertEquals(a.aBoolean , b.aBoolean);
        assertEquals(a.arrayList, b.arrayList);
        assertEquals(a.hashMap, b.hashMap);

    }

    private void contentEqualsExcludeId(User a, User b) {
//        assertEquals(a.id, b.id);
        assertEquals(a.anInt ,b.anInt);
        assertTrue(TextUtils.equals(a.custom, b.custom));
        assertEquals(a.aShort , b.aShort);
        assertEquals(a.aShort , b.aShort);
        assertEquals(a.aByte , b.aByte);
        assertEquals(a.aLong , b.aLong);
//        assertEquals(a.aDouble , b.aDouble); 这是忽略字段，两个不相等也是合理的
        assertEquals(a.aFloat , b.aFloat);
        assertArrayEquals(a.aBytes, b.aBytes);
        assertEquals(a.aaLong, b.aaLong);
        assertEquals(a.aBoolean , b.aBoolean);
        assertEquals(a.arrayList, b.arrayList);
        assertEquals(a.hashMap, b.hashMap);

    }


}