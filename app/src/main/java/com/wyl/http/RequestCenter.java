package com.wyl.http;

/**
 * @author : wuchao5
 * @date : 2020/12/1 17:17
 * @desciption :
 */
public class RequestCenter {

  public static class HttpConstants {
    // public static final String ROOT_URL = "http://10.41.12.65:8000";
    public static final String ROOT_URL = "https://appsdkmonitor.biz.weibo.com";

    /**
     * SDK 监控日志
     */
    private static final String SDK_COLLECT_LOG = "https://mapi2.biz.weibo.com/collect/sdk/log";
    // private static final String SDK_COLLECT_LOG = "http://10.77.96.29:31000/collect/sdk/log";

    /**
     * SDK(联调，检测)流程与转化上报接口
     */
    private static final String SDK_ACTION = ROOT_URL + "/sdkaction";
    /**
     * SDK 心跳报文
     */
    private static final String SDK_HEART_BEAT = ROOT_URL + "/sdkheartbeat";

  }

  /**
   * 根据参数发送所有的get请求
   *
   * @param url
   * @param params
   * @param listener
   * @param clazz
   */
 /* public static void getRequest(String url, RequestParams params, DisposeDataListener listener, Class<?> clazz) {
    CommonOkHttpClient.get(CommonRequest.createGetRequest(url, params),
        new DisposeDataHandle(listener, clazz));
  }

  *//**
   * 根据参数发送所有的post请求
   *
   * @param url
   * @param params
   * @param listener
   * @param clazz
   *//*
  public static void postRequest(String url, RequestParams params, DisposeDataListener listener, Class<?> clazz) {
    CommonOkHttpClient.post(CommonRequest.createPostRequest(url, params),
        new DisposeDataHandle(listener, clazz));
  }

  public static void postRequest(String url, Object object, DisposeDataListener listener, Class<?> clazz) {
    CommonOkHttpClient.post(CommonRequest.createPostJsonRequest(url, object),
        new DisposeDataHandle(listener, clazz));
  }
*/

}
