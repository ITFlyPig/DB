package com.wyl.db.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wyl.db.bean.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Handler;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/10
 * 描述    : 复杂类型转换器
 *
 * @author yuelinwang
 */
public class TypeConverters implements ITypeConverter {

    public String convert(ArrayList<String> list) {
        return new Gson().toJson(list);
    }

    public String convert(HashMap<String, String> map) {
        return new Gson().toJson(map);
    }

    public HashMap<String, String> convertMap(String s) {
        return new Gson().fromJson(s, new TypeToken<HashMap<String, String>>(){}.getType());
    }

    public ArrayList<String> convert(String s) {
        ArrayList<String> list = new Gson().fromJson(s, new TypeToken<ArrayList<String>>(){}.getType());
        return list;
    }


}
