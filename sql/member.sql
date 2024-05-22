create table member
(
    id     bigint      not null comment 'id',
    mobile varchar(11) null comment '手机号',
    constraint member_pk
        primary key (id),
    constraint member_pk_2
        unique (mobile)
)engine =innodb default charset =utf8mb4 comment '会员';

drop table if exists passenger;
create table passenger
(
    id          bigint      not null comment 'id',
    name        varchar(20) not null comment '姓名',
    id_card     varchar(18) null comment '身份证',
    type        char        not null comment '旅客类型|枚举[PassengerTypeEnum]',
    create_time datetime(3) null comment '新增时间',
    update_time datetime(3) null comment '修改时间',
    member_id   bigint      not null comment '会员id',
    constraint passenger_pk
        primary key (id),
    index member_id_index (member_id)
)engine =innodb default charset =utf8mb4 comment ='乘车人'
