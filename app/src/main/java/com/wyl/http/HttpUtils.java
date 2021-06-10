package com.wyl.http;

/**
 * @author : wuchao5
 * @date : 3/26/21 3:45 PM
 * @desciption :
 */
public class HttpUtils {
  // url
  // 请求参数
  // 接收结果的回调接口
  // 接收结果的参数类型
  public static <T, M> void senRequest(String url, T requestParams, Class<M> response, IJsonDataListener iJsonDataListener) {
    // 请求对象的封装
    // 为什么使用父类呢？通过接口的父类的引用去指向子类的实例
    IHttpRequest httpRequest = new JsonHttpRequest();
    // 创建一个内部回调接口的实现类
    CallBackListener callBackListener = new JsonCallBackListener(response, iJsonDataListener);
    // 请求对象的二次封装，将请求对象封装成线程
    HttpTask httpTask = new HttpTask(httpRequest, callBackListener, url, requestParams);
    // 将请求线程加入到请求队列中
    ThreadManager.getInstance().addTask(httpTask);
  }
}