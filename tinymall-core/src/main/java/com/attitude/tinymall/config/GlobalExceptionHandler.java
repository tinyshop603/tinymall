package com.attitude.tinymall.config;

import com.attitude.tinymall.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseBody
  public Object argumentHandler(MethodArgumentTypeMismatchException e,
      HandlerMethod handlerMethod) {
    String errorMsg = String.format("method name:%s invalid input error: %s",
        handlerMethod.getShortLogMessage(), e.getMessage());
    log.info(errorMsg);
    return ResponseUtil.badArgumentValue();
  }

  @ExceptionHandler(Exception.class)
  @ResponseBody
  public Object exceptionHandler(Exception e, HandlerMethod handlerMethod) {
    String errorMsg = String.format("method %s  un excepted error: %s",
        handlerMethod.getShortLogMessage(), e.getMessage());
    log.error(errorMsg, e);
    return ResponseUtil.serious();
  }

}
