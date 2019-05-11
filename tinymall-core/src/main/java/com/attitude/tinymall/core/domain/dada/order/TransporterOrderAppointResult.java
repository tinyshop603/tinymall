package com.attitude.tinymall.core.domain.dada.order;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class TransporterOrderAppointResult {
   private Integer id;

   private String name;

   @JSONField(name = "city_id")
   private Integer cityId;
}
