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


drop table if exists ticket;

create table train_member.ticket
(
    id             bigint      not null comment 'id'
        primary key,
    member_id      bigint      not null comment '会员id',
    passenger_id   bigint      not null comment '乘客id',
    passenger_name varchar(20) null comment '乘客姓名',
    date           date        not null comment '日期',
    train_code     varchar(20) not null comment '车次编号',
    carriage_index int         not null comment '箱序',
    row            char(2)     not null comment '排号|01,02',
    col            char        not null comment '列号|枚举[SeatEnum]',
    start          varchar(20) not null comment '出发站',
    start_time     time        not null comment '出发时间',
    end            varchar(20) not null comment '到达站',
    end_time       time        not null comment '到站时间',
    seat_type      char        not null comment '座位类型|枚举[SeatEnum]',
    create_time    datetime    null comment '新增时间',
    update_time    datetime    null comment '修改时间'
)
    comment '车票';

create index member_id_index
    on train_member.ticket (member_id);

