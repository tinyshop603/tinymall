package com.attitude.tinymall.core.annotion;

import com.attitude.tinymall.core.serialize.AliyunImageAnnotionSerializer;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author zhaoguiyang on 2018/12/27.
 * @project Wechat
 */
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = AliyunImageAnnotionSerializer.class)
public @interface AliyunImage {

}
