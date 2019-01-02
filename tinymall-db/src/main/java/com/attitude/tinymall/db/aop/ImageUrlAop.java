package com.attitude.tinymall.db.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

/**
 * @author zhaoguiyang on 2018/12/26.
 * @project Wechat
 */
@EnableAspectJAutoProxy
@Aspect
@Component
public class ImageUrlAop {

  @Pointcut("execution(public String com.attitude.tinymall.db.domain.*.*(..))")
  public void fetchImageOrginName() {
  }

  @Before("fetchImageOrginName()")
  public void doBefore(JoinPoint joinPoint) throws Throwable {
    // 省略日志记录内容
  }
}
