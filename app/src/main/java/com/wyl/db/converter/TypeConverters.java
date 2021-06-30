package com.wyl.db.converter;

import com.google.gson.Gson;
import com.wyl.db.bean.User;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/10
 * 描述    : 复杂类型转换器
 * @author yuelinwang
 */
public class TypeConverters implements ITypeConverter {

    public String convertS(ArrayList<String> arrayList) {
        return "将arrayList String转为String";
    }

    public String convertI(ArrayList<Integer> arrayList) {
        return "将arrayList Integer转为String";
    }

    public String mapStringString(HashMap<String, String> map) {
        return "Map转为String";
    }

    public String mapStringInteger(HashMap<String, Integer> map) {
        return "Map转为String";
    }

    public ArrayList<String> convertRetStringList(String s) {
        ArrayList<String> list = new ArrayList<>();
        list.add("这是转换过来的");
        return list;

    }
    public ArrayList<Integer> convertRetIntegerList(String s) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        return list;

    }

    public HashMap<String, String> covert(String s) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("map", "测试map");
        return hashMap;

    }

    public int convert(HashMap<String, User> hashMap) {
        return 1;
    }

    public String convert1(HashMap<String, String> hashMap) {
        return new Gson().toJson(hashMap);
    }



}
