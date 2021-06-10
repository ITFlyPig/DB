package com.wyl.http;

import java.io.InputStream;

/**
 * @author : wuchao5
 * @date : 3/25/21 4:37 PM
 * @desciption : 请求接口的回调 给框架层调用的接口
 */
public interface CallBackListener {
  // 请求成功
  void onSuccess(InputStream inputStream);
  // 请求失败
  void onFailed(InputStream inputStream);
}