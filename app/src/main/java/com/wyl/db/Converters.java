package com.wyl.db;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/10
 * 描述    :
 */
public class Converters implements IConverter{

    public String list2String(ArrayList<String> arrayList) {
        return new Gson().toJson(arrayList);
    }

}
