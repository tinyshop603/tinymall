package com.attitude.tinymall.domain.dada.order;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class TransporterOrderAppointResult {
   private Integer id;
   private String name;
   private String phone;

   @JSONField(name = "city_id")
   private Integer cityId;
}
