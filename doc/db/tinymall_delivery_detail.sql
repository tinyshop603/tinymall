drop table tinymall_delivery_detail;
CREATE TABLE `tinymall_delivery_detail` (
  delivery_id varchar(50) not null COMMENT'订单Id',
  client_id varchar(50) COMMENT'返回达达运单号，默认为空',
  dm_id varchar(50) COMMENT '达达配送员id，接单以后会传',
  dm_name varchar(50) COMMENT '达达配送员名字',
  dm_mobile varchar(20) COMMENT '配送员手机号，接单以后会传',
  update_time datetime DEFAULT NULL,
  create_time datetime DEFAULT now(),
  cancel_reason varchar(255) COMMENT '配送员手机号，接单以后会传',
  cancel_from varchar(50) COMMENT '订单取消原因来源',
  distance varchar(50) COMMENT '距离',
  fee int(10) COMMENT '实际运费',
  deliver_fee int(10) COMMENT '运费',
  PRIMARY KEY (delivery_id)
)
