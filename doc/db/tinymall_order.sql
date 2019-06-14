alter table tinymall_order modify order_status varchar(50);
alter table tinymall_order modify pay_status varchar(50);
alter table tinymall_order modify payment_way varchar(50);
alter table tinymall_order add tpd_status varchar(50);


Alter table tinymall_order add delivery_id varchar(50);
Alter table tinymall_order add deliver_fee decimal(10,2);

