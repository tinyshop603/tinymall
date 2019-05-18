package com.attitude.tinymall.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.Data;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * @author zhaoguiyang on 2018/12/26.
 * @project Wechat
 */
public class HttpClientUtil {

  // 编码格式。发送编码格式统一用UTF-8
  private static final String ENCODING = "UTF-8";

  // 设置连接超时时间，单位毫秒。
  private static final int CONNECT_TIMEOUT = 6000;

  // 请求获取数据的超时时间(即响应时间)，单位毫秒。
  private static final int SOCKET_TIMEOUT = 6000;

  /**
   * 发送get请求；不带请求头和请求参数
   *
   * @param url 请求地址
   */
  public static HttpClientResult doGet(String url) throws Exception {
    return doGet(url, null, null);
  }

  /**
   * 发送get请求；带请求参数
   *
   * @param url 请求地址
   * @param params 请求参数集合
   */
  public static HttpClientResult doGet(String url, Map<String, String> params) throws Exception {
    return doGet(url, null, params);
  }

  /**
   * 发送get请求；带请求头和请求参数
   *
   * @param url 请求地址
   * @param headers 请求头集合
   * @param params 请求参数集合
   */
  public static HttpClientResult doGet(String url, Map<String, String> headers,
      Map<String, String> params) throws Exception {
    // 创建httpClient对象
    CloseableHttpClient httpClient = HttpClients.createDefault();

    // 创建访问的地址
    URIBuilder uriBuilder = new URIBuilder(url);
    if (params != null) {
      Set<Entry<String, String>> entrySet = params.entrySet();
      for (Entry<String, String> entry : entrySet) {
        uriBuilder.setParameter(entry.getKey(), entry.getValue());
      }
    }

    // 创建http对象
    HttpGet httpGet = new HttpGet(uriBuilder.build());
    /**
     * setConnectTimeout：设置连接超时时间，单位毫秒。
     * setConnectionRequestTimeout：设置从connect Manager(连接池)获取Connection
     * 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
     * setSocketTimeout：请求获取数据的超时时间(即响应时间)，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
     */
    RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT)
        .setSocketTimeout(SOCKET_TIMEOUT).build();
    httpGet.setConfig(requestConfig);

    // 设置请求头
    packageHeader(headers, httpGet);

    // 创建httpResponse对象
    CloseableHttpResponse httpResponse = null;

    try {
      // 执行请求并获得响应结果
      return getHttpClientResult(httpResponse, httpClient, httpGet);
    } finally {
      // 释放资源
      release(httpResponse, httpClient);
    }
  }

  /**
   * 发送post请求；不带请求头和请求参数
   *
   * @param url 请求地址
   */
  public static HttpClientResult doPost(String url) throws Exception {
    return doPost(url, null, null);
  }

  /**
   * 发送post请求；带请求参数
   *
   * @param url 请求地址
   * @param params 参数集合
   */
  public static HttpClientResult doPost(String url, Map<String, String> params) throws Exception {
    return doPost(url, null, params);
  }

  /**
   * 发送post请求；带请求头和请求参数
   *
   * @param url 请求地址
   * @param headers 请求头集合
   * @param params 请求参数集合
   */
  public static HttpClientResult doPost(String url, Map<String, String> headers,
      Map<String, String> params) throws Exception {
    // 创建httpClient对象
    CloseableHttpClient httpClient = HttpClients.createDefault();

    // 创建http对象
    HttpPost httpPost = new HttpPost(url);
    /**
     * setConnectTimeout：设置连接超时时间，单位毫秒。
     * setConnectionRequestTimeout：设置从connect Manager(连接池)获取Connection
     * 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
     * setSocketTimeout：请求获取数据的超时时间(即响应时间)，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
     */
    RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT)
        .setSocketTimeout(SOCKET_TIMEOUT).build();
    httpPost.setConfig(requestConfig);
    // 设置请求头
		/*httpPost.setHeader("Cookie", "");
		httpPost.setHeader("Connection", "keep-alive");
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
		httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
		httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");*/
    packageHeader(headers, httpPost);

    // 封装请求参数
    packageParam(params, httpPost);

    // 创建httpResponse对象
    CloseableHttpResponse httpResponse = null;

    try {
      // 执行请求并获得响应结果
      return getHttpClientResult(httpResponse, httpClient, httpPost);
    } finally {
      // 释放资源
      release(httpResponse, httpClient);
    }
  }

  /**
   * 发送put请求；不带请求参数
   *
   * @param url 请求地址
   */
  public static HttpClientResult doPut(String url) throws Exception {
    return doPut(url);
  }

  /**
   * 发送put请求；带请求参数
   *
   * @param url 请求地址
   * @param params 参数集合
   */
  public static HttpClientResult doPut(String url, Map<String, String> params) throws Exception {
    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpPut httpPut = new HttpPut(url);
    RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT)
        .setSocketTimeout(SOCKET_TIMEOUT).build();
    httpPut.setConfig(requestConfig);

    packageParam(params, httpPut);

    CloseableHttpResponse httpResponse = null;

    try {
      return getHttpClientResult(httpResponse, httpClient, httpPut);
    } finally {
      release(httpResponse, httpClient);
    }
  }

  /**
   * 发送delete请求；不带请求参数
   *
   * @param url 请求地址
   */
  public static HttpClientResult doDelete(String url) throws Exception {
    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpDelete httpDelete = new HttpDelete(url);
    RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT)
        .setSocketTimeout(SOCKET_TIMEOUT).build();
    httpDelete.setConfig(requestConfig);

    CloseableHttpResponse httpResponse = null;
    try {
      return getHttpClientResult(httpResponse, httpClient, httpDelete);
    } finally {
      release(httpResponse, httpClient);
    }
  }

  /**
   * 发送delete请求；带请求参数
   *
   * @param url 请求地址
   * @param params 参数集合
   */
  public static HttpClientResult doDelete(String url, Map<String, String> params) throws Exception {
    if (params == null) {
      params = new HashMap<String, String>();
    }

    params.put("_method", "delete");
    return doPost(url, params);
  }

  /**
   * Description: 封装请求头
   */
  public static void packageHeader(Map<String, String> params, HttpRequestBase httpMethod) {
    // 封装请求头
    if (params != null) {
      Set<Entry<String, String>> entrySet = params.entrySet();
      for (Entry<String, String> entry : entrySet) {
        // 设置到请求头到HttpRequestBase对象中
        httpMethod.setHeader(entry.getKey(), entry.getValue());
      }
    }
  }

  /**
   * Description: 封装请求参数
   */
  public static void packageParam(Map<String, String> params,
      HttpEntityEnclosingRequestBase httpMethod)
      throws UnsupportedEncodingException {
    // 封装请求参数
    if (params != null) {
      List<NameValuePair> nvps = new ArrayList<NameValuePair>();
      Set<Entry<String, String>> entrySet = params.entrySet();
      for (Entry<String, String> entry : entrySet) {
        nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
      }

      // 设置到请求的http对象中
      httpMethod.setEntity(new UrlEncodedFormEntity(nvps, ENCODING));
    }
  }

  /**
   * Description: 获得响应结果
   */
  public static HttpClientResult getHttpClientResult(CloseableHttpResponse httpResponse,
      CloseableHttpClient httpClient, HttpRequestBase httpMethod) throws Exception {
    // 执行请求
    httpResponse = httpClient.execute(httpMethod);

    // 获取返回结果
    if (httpResponse != null && httpResponse.getStatusLine() != null) {
      String content = "";
      if (httpResponse.getEntity() != null) {
        content = EntityUtils.toString(httpResponse.getEntity(), ENCODING);
      }
      return new HttpClientResult(httpResponse.getStatusLine().getStatusCode(), content);
    }
    return new HttpClientResult(HttpStatus.SC_INTERNAL_SERVER_ERROR);
  }

  /**
   * Description: 释放资源
   */
  public static void release(CloseableHttpResponse httpResponse, CloseableHttpClient httpClient)
      throws IOException {
    // 释放资源
    if (httpResponse != null) {
      httpResponse.close();
    }
    if (httpClient != null) {
      httpClient.close();
    }
  }


  @Data
  public static class HttpClientResult {

    private static final long serialVersionUID = 2168152194164783950L;

    /**
     * 响应状态码
     */
    private int code;

    /**
     * 响应数据
     */
    private String content;

    public HttpClientResult() {
    }

    public HttpClientResult(int code) {
      this.code = code;
    }

    public HttpClientResult(String content) {
      this.content = content;
    }

    public HttpClientResult(int code, String content) {
      this.code = code;
      this.content = content;
    }

    @Override
    public String toString() {
      return "HttpClientResult [code=" + code + ", content=" + content + "]";
    }
  }

  /**
   * 向指定URL发送GET方法的请求
   *
   * @param url
   *            发送请求的URL
   * @param param
   *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
   * @return URL 所代表远程资源的响应结果
   */
  public static String sendGet(String url, String param) {
    String result = "";
    BufferedReader in = null;
    try {
      String urlNameString = url + "?" + param;
      URL realUrl = new URL(urlNameString);
      // 打开和URL之间的连接
      URLConnection connection = realUrl.openConnection();
      // 设置通用的请求属性
      connection.setRequestProperty("accept", "*/*");
      connection.setRequestProperty("connection", "Keep-Alive");
      connection.setRequestProperty("user-agent",
              "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
      // 建立实际的连接
      connection.connect();
      // 获取所有响应头字段
      Map<String, List<String>> map = connection.getHeaderFields();
      // 遍历所有的响应头字段
      for (String key : map.keySet()) {
        System.out.println(key + "--->" + map.get(key));
      }
      // 定义 BufferedReader输入流来读取URL的响应
      in = new BufferedReader(new InputStreamReader(
              connection.getInputStream()));
      String line;
      while ((line = in.readLine()) != null) {
        result += line;
      }
    } catch (Exception e) {
      System.out.println("发送GET请求出现异常！" + e);
      e.printStackTrace();
    }
    // 使用finally块来关闭输入流
    finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (Exception e2) {
        e2.printStackTrace();
      }
    }
    return result;
  }


  /**
   * 向指定 URL 发送POST方法的请求
   *
   * @param url
   *            发送请求的 URL
   * @param param
   *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
   * @return 所代表远程资源的响应结果
   */
  public static String sendPost(String url, String param) {
    PrintWriter out = null;
    BufferedReader in = null;
    String result = "";
    try {
      URL realUrl = new URL(url);
      // 打开和URL之间的连接
      URLConnection conn = realUrl.openConnection();
      // 设置通用的请求属性
      conn.setRequestProperty("accept", "*/*");
      conn.setRequestProperty("connection", "Keep-Alive");
      conn.setRequestProperty("user-agent",
              "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
      // 发送POST请求必须设置如下两行
      conn.setDoOutput(true);
      conn.setDoInput(true);
      // 获取URLConnection对象对应的输出流
      out = new PrintWriter(conn.getOutputStream());
      // 发送请求参数
      out.print(param);
      // flush输出流的缓冲
      out.flush();
      // 定义BufferedReader输入流来读取URL的响应
      in = new BufferedReader(
              new InputStreamReader(conn.getInputStream()));
      String line;
      while ((line = in.readLine()) != null) {
        result += line;
      }
    } catch (Exception e) {
      System.out.println("发送 POST 请求出现异常！"+e);
      e.printStackTrace();
    }
    //使用finally块来关闭输出流、输入流
    finally{
      try{
        if(out!=null){
          out.close();
        }
        if(in!=null){
          in.close();
        }
      }
      catch(IOException ex){
        ex.printStackTrace();
      }
    }
    return result;
  }


}
