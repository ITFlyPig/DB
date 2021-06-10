package com.wyl.http;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

/**
 * @author : wuchao5
 * @date : 3/25/21 6:58 PM
 * @desciption : 请求接口的实现类 请求参数的封装类
 */
public class JsonHttpRequest implements IHttpRequest {
  private String url;
  private byte[] data;
  private CallBackListener callBackListener;
  // 执行请求的对象
  private HttpURLConnection httpURLConnection;

  @Override
  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public void setRequestParams(byte[] data) {
    this.data = data;
  }

  @Override
  public void setLisener(CallBackListener callBackListener) {
    this.callBackListener = callBackListener;
  }

  @Override public void execute() {
    URL url = null;
    try {
      SSLContext sc = SSLContext.getInstance("TLS");
      sc.init(null, new TrustManager[] { HttpTrustUtils.initTrustManager() },
          new SecureRandom());
      HttpsURLConnection
          .setDefaultSSLSocketFactory(sc.getSocketFactory());
      HttpsURLConnection
          .setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override public boolean verify(String hostname, SSLSession session) {
              return true;
            }
          });

      url = new URL(this.url);

      httpURLConnection = (HttpURLConnection) url.openConnection();
      // 连接超时时间
      httpURLConnection.setConnectTimeout(6000);
      // 不使用缓存
      httpURLConnection.setUseCaches(false);
      // 是成员变量 仅作用域当前函数，设置当前这个对象
      httpURLConnection.setInstanceFollowRedirects(false);
      // 响应超时的时间
      httpURLConnection.setReadTimeout(3000);
      // 设置这个链接是否可以写入数据
      httpURLConnection.setDoInput(true);
      // 设置这个链接是否可以输出数据
      httpURLConnection.setDoOutput(true);
      // 设置这个请求的方法
      httpURLConnection.setRequestMethod("POST");
      httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
      // 建立链接
      httpURLConnection.connect();

      // --------------------使用字节流发送数据---------------------
      OutputStream out = httpURLConnection.getOutputStream();
      // 缓冲字节流 包装字节流
      BufferedOutputStream bos = new BufferedOutputStream(out);
      // 把字节流数组写入缓冲区中
      if (data != null) {
        bos.write(data);
      }
      // 刷新缓冲区 发送数据
      bos.flush();
      out.close();
      bos.close();

      // 如果响应码为200代表访问成功
      if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
        InputStream in = httpURLConnection.getInputStream();
        // 先回调内部的回调接口 让内部接口去转换为程序员需要的Class
        if (callBackListener != null) {
          callBackListener.onSuccess(in);
        }
      } else {
        // if (httpURLConnection.getResponseCode() == 500) {
        InputStream errorStream = httpURLConnection.getErrorStream();
        if (callBackListener != null) {
          // callBackListener.onSuccess(errorStream);
          callBackListener.onFailed(errorStream);
        }
        // }
        // throw new RuntimeException("请求失败！");
      }
    } catch (Exception e) {
      e.printStackTrace();
      // if (callBackListener != null) {
      //   callBackListener.onFailed(null);
      // }
      throw new RuntimeException("请求失败！");
    } finally {
      // 关闭 HTTPConnection 对象
      httpURLConnection.disconnect();
    }
  }

  public String getUrl() {
    return url;
  }

  public byte[] getData() {
    return data;
  }

  public CallBackListener getCallBackListener() {
    return callBackListener;
  }

  public HttpURLConnection getHttpURLConnection() {
    return httpURLConnection;
  }

}