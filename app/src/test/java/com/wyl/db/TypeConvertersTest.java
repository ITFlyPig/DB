package com.wyl.db;

import com.wyl.db.bean.User;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/11
 * 描述    : 单元测试用到的类
 */
public class TypeConvertersTest {
//    public String test0(String s) {
//        return "";
//    }
//
//    public String test1(String s) {
//        return "";
//    }


//    public String test0(int i) {
//        return "";
//    }
//
//    public String test1(int i) {
//        return "";
//    }

//    public String test0(byte[] i) {
//        return "";
//    }
//
//    public String test1(byte[] i) {
//        return "";
//    }

//    public<T> String test0(T i) {
//        return "";
//    }
//
//    public<T> String test1(T i) {
//        return "";
//    }

//    public<T extends User> String test0(T i) {
//        return "";
//    }
//
//    public<T extends User> String test1(T i) {
//        return "";
//    }

    public String test0(ArrayList<? extends User> i) {
        return "";
    }

    public String test1(ArrayList<? extends Object> i) {
        return "";
    }


}
