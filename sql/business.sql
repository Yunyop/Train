create table member
(
    id     bigint      not null comment 'id',
    mobile varchar(11) null comment '手机号',
    constraint member_pk
        primary key (id),
    constraint member_pk_2
        unique (mobile)
)engine =innodb default charset =utf8mb4 comment '会员';
