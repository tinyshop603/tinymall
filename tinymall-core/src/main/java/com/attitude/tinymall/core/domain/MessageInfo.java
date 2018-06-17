package com.attitude.tinymall.core.domain;

import com.attitude.tinymall.core.util.JacksonUtil;
import java.io.Serializable;

public class  MessageInfo<T> implements Serializable {



  //源客户端id
  private String sourceClientId;
  //目标客户端id
  private String targetClientId;
  //消息类型
  private String msgType;
  //消息内容
  private String msgContent;

  private T domainData;

  public MessageInfo() {
  }

  public MessageInfo(String sourceClientId, String targetClientId, String msgType,
      String msgContent, T domainData) {
    this.sourceClientId = sourceClientId;
    this.targetClientId = targetClientId;
    this.msgType = msgType;
    this.msgContent = msgContent;
    this.domainData = domainData;
  }

  public T getDomainData() {
    return domainData;
  }

  public void setDomainData(T domainData) {
    this.domainData = domainData;
  }

  public String getSourceClientId() {
    return sourceClientId;
  }

  public void setSourceClientId(String sourceClientId) {
    this.sourceClientId = sourceClientId;
  }

  public String getTargetClientId() {
    return targetClientId;
  }

  public void setTargetClientId(String targetClientId) {
    this.targetClientId = targetClientId;
  }

  public String getMsgType() {
    return msgType;
  }

  public void setMsgType(String msgType) {
    this.msgType = msgType;
  }

  public String getMsgContent() {
    return msgContent;
  }

  public void setMsgContent(String msgContent) {
    this.msgContent = msgContent;
  }

  @Override
  public String toString() {
    return JacksonUtil.stringifyObject(this);
  }
}
