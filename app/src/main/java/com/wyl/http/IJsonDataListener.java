package com.wyl.http;

/**
 * @author : wuchao5
 * @date : 3/25/21 4:37 PM
 * @desciption : 请求结果的回调接口
 */
public interface IJsonDataListener<T> {
  // 请求成功
  void onSuccess(T t);
  // 请求失败
  void onFailed(T t);
}