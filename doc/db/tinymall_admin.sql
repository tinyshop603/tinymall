desc tinymall_admin;
alter table tinymall_admin add shop_name varchar(255);
alter table tinymall_admin add shop_address varchar(255);
alter table tinymall_admin add shop_fence_id int(11);
-- 第三方配送门店编号
alter table tinymall_admin add tpd_shop_no int(11) ;


update tinymall_admin set
shop_name ='烟酒茶行',
shop_address = '北京市昌平区回龙观镇龙禧三街北店嘉园南区底商10号',
shop_fence_id = 9,
tpd_shop_no = 9410766
where id = 1;
