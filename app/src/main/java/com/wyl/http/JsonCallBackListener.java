package com.wyl.http;

import android.os.Handler;
import android.os.Looper;

import com.wyl.json.JsonUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author : wuchao5
 * @date : 3/26/21 11:33 AM
 * @desciption : 内部接口的实现类 将流转为用户想要的类型
 * 实现接口，则接口调用的时候，持有的实例就直接是接口的实现类
 */
public class JsonCallBackListener<T> implements CallBackListener {
  // 用户需要的请求结果的类型
  private Class<T> response;
  // 外部的回调接口类
  private IJsonDataListener iJsonDataListener;

  // 切换线程的Handler
  private Handler handler = new Handler(Looper.getMainLooper());

  /**
   * 构造方法
   * 在创建的时候就把需要的参数传递进来
   */
  public JsonCallBackListener(Class<T> response, IJsonDataListener iJsonDataListener) {
    this.response = response;
    this.iJsonDataListener = iJsonDataListener;
  }

  @Override
  public void onSuccess(InputStream inputStream) {
    // 解析结束 回调给外部的回调接口
    // 第一步 将流转化为json字符串
    String content = getContent(inputStream);
    try {
      // 第二步 使用原生json库将JSON字符串转化为想要的对象类型
      final T t = JsonUtils.jsonToObject(content, response);
      // 第三部 通过主线程将数据返回出去供外界使用
      handler.post(new Runnable() {
        @Override
        public void run() {
          iJsonDataListener.onSuccess(t);
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onFailed(InputStream inputStream) {
    // 将流转化为json字符串
    String content = getContent(inputStream);
    try {
      // 第二步 使用原生json库将JSON字符串转化为程序员想要的对象类型
      final T t = JsonUtils.jsonToObject(content, response);
      // 第三部 通过主线程将数据返回出去供外界使用
      handler.post(new Runnable() {
        @Override
        public void run() {
          iJsonDataListener.onFailed(t);
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 将inputStream转化为String类型
   */
  private String getContent(InputStream inputStream) {
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    StringBuilder sb = new StringBuilder();
    String line = null;
    try {
      while ((line = bufferedReader.readLine()) != null) {
        sb.append(line + "/n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        inputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return sb.toString().replace("/n", "");
  }
}