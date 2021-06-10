package com.wyl.http;


import com.wyl.json.JsonUtils;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author : wuchao5
 * @date : 3/26/21 2:01 PM
 * @desciption : 请求对象的二次封装，将请求对象封装成线程，在线程中来执行请求
 */
public class HttpTask<T> implements Runnable, Delayed {

  // 请求对象
  private IHttpRequest iHttpRequest;

  // 重试的延迟时间
  private long delayTime;

  // 当前失败的次数
  private int failNum;

  public IHttpRequest getiHttpRequest() {
    return iHttpRequest;
  }

  public void setiHttpRequest(IHttpRequest iHttpRequest) {
    this.iHttpRequest = iHttpRequest;
  }

  public long getDelayTime() {
    return delayTime;
  }

  public void setDelayTime(long delayTime) {
    // 把当前的时间以时间戳的形式记录下来
    this.delayTime = delayTime + System.currentTimeMillis();
  }

  public int getFailNum() {
    return failNum;
  }

  public void setFailNum(int failNum) {
    this.failNum = failNum;
  }

  /**
   * @param iHttpRequest 请求对象
   * @param callBackListener 请求结果的回调接口
   * @param url 请求路径
   * @param requestData 请求数据的对象
   */
  public HttpTask(IHttpRequest iHttpRequest, CallBackListener callBackListener, String url, T requestData) {
    this.iHttpRequest = iHttpRequest;
    this.iHttpRequest.setUrl(url);
    this.iHttpRequest.setLisener(callBackListener);

    // 判断是否拥有请求数据
    if (requestData != null) {
      try {
        // 将请求对象转为Json字符串
        // String dataStr = JSON.toJSONString(requestData);
        String dataStr = JsonUtils.toJSONString(requestData);
        this.iHttpRequest.setRequestParams(dataStr.getBytes(StandardCharsets.UTF_8));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void run() {
    try {
      this.iHttpRequest.execute();
    } catch (Exception e) {
      // 捕捉到异常，然后将请求线程丢入到重试机制中
      // ThreadManager.getInstance().addFailedTask(this);
    }
    // this.iHttpRequest.execute();
  }

  @Override
  public int compareTo(Delayed o) {
    return 0;
  }

  /**
   * 队列的执行间隔时间
   */
  @Override
  public long getDelay(TimeUnit unit) {
    // 取出时间 减去时间戳
    return unit.convert(getDelayTime() - System.currentTimeMillis(), TimeUnit.SECONDS);
  }
}