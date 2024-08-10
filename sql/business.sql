CREATE TABLE `station`
(
    id          BIGINT      NOT NULL COMMENT 'id',
    name        VARCHAR(20) NOT NULL COMMENT '站名',
    name_pinyin VARCHAR(50) NOT NULL COMMENT '站名拼音',
    name_py     VARCHAR(50) NOT NULL COMMENT '站名拼音首字母',
    create_time DATETIME(3) NULL COMMENT '新增时间',
    update_time DATETIME(3) NULL COMMENT '修改时间',
    PRIMARY KEY (id),
    UNIQUE KEY `name_unique` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车站';

create table train
(
    id           bigint      not null comment 'id',
    code         varchar(20) not null comment '车次编号',
    type      char        not null comment '车次类型|枚举[TrainTypeEnum]',
    start        varchar(20) not null comment '始发站',
    start_pinyin varchar(50) not null comment '始发站拼音',
    start_time   time        not null comment '出发时间',
    end          varchar(20) not null comment '终点站',
    end_pinyin   varchar(50) not null comment '终点站拼音',
    end_time     time        not null comment '到站时间',
    create_time  datetime(3) null comment '新增时间',
    update_time  datetime(3) null comment '修改时间',
    constraint train_pk
        primary key (id),
    unique key `code_unique`(`code`)
)engine =innodb default charset utf8mb4 comment '车次';

-- auto-generated definition
create table train_carriage
(
    id          bigint      not null comment 'id'
        primary key,
    train_code  varchar(20) not null comment '车次编号',
    `index`     int         not null comment '厢号',
    seat_type   char        not null comment '座位类型|枚举[SeatTypeEnum]',
    seat_count  int         not null comment '座位数',
    row_count   int         not null comment '排数',
    col_count   int         not null comment '列数',
    create_time datetime(3) null comment '新增时间',
    update_time datetime(3) null comment '修改时间',
    constraint train_code_index_unique
        unique (train_code, `index`)
)
    comment '火车车厢';


drop table if exists train_station;
create table train_station
(
    id          bigint           not null comment 'id',
    train_code  varchar(20)   not null comment '车次编号',
    `index`     int           not null comment '站序',
    name        varchar(20)   not null comment '站名',
    name_pinyin varchar(50)   not null comment '站名拼音',
    in_time     time          null comment '进站时间',
    out_time    time          null comment '出站时间',
    stop_time   time          null comment '停留时间',
    km          decimal(8, 2) not null comment '里程(公里)|从上一站到本站的距离',
    create_time datetime(3)   null comment '新增时间',
    update_time datetime(3)   null comment '修改时间',
    constraint train_station_pk
        primary key (id),
    constraint train_code_index_unique
        unique (train_code, `index`),
    constraint train_code_name_unique
        unique (train_code, name)
)engine =innodb default charset = utf8mb4 comment ='火车车站';

create table train_station
(
    id          bigint           not null comment 'id',
    train_code  varchar(20)   not null comment '车次编号',
    `index`     int           not null comment '站序',
    name        varchar(20)   not null comment '站名',
    name_pinyin varchar(50)   not null comment '站名拼音',
    in_time     time          null comment '进站时间',
    out_time    time          null comment '出站时间',
    stop_time   time          null comment '停留时间',
    km          decimal(8, 2) not null comment '里程(公里)|从上一站到本站的距离',
    create_time datetime(3)   null comment '新增时间',
    update_time datetime(3)   null comment '修改时间',
    constraint train_station_pk
        primary key (id),
    constraint train_code_index_unique
        unique (train_code, `index`),
    constraint train_code_name_unique
        unique (train_code, name)
)engine =innodb default charset = utf8mb4 comment ='火车车站';


CREATE TABLE `train_seat` (
                              id BIGINT NOT NULL COMMENT 'id',
                              train_code VARCHAR(20) NOT NULL COMMENT '车次编号',
                              carriage_index INT NOT NULL COMMENT '箱序',
                              `row` CHAR(2) NOT NULL COMMENT '排号|01,02',
                              col CHAR(1) NOT NULL COMMENT '列号|枚举[SeatColEnum]',
                              seat_type CHAR(1) NOT NULL COMMENT '座位类型|枚举[SeatTypeEnum]',
                              carriage_seat_index INT NOT NULL COMMENT '同车厢座序',
                              create_time DATETIME(3) NULL COMMENT '新增时间',
                              update_time DATETIME(3) NULL COMMENT '修改时间',
                              CONSTRAINT train_seat_pk PRIMARY KEY (id)
) COMMENT '座位'
    ENGINE = InnoDB
    CHARSET = utf8mb4;

drop table if exists `daily_train`;
create table daily_train
(
    id           bigint      not null comment 'id',
    date         date        not null comment '日期',
    code         varchar(20) not null comment '车次编号',
    type         char        not null comment '车次类型|枚举[TrainTypeEnum]',
    start        varchar(20) not null comment '始发站',
    start_pinyin varchar(50) not null comment '始发站拼音',
    start_time   time        not null comment '出发时间',
    end          varchar(20) not null comment '终点站',
    end_pinyin   varchar(50) not null comment '终点站拼音',
    end_time     time        not null comment '到站时间',
    create_time  datetime(3) null comment '新增时间',
    update_time  datetime(3) null comment '修改时间',
    constraint train_pk
        primary key (id),
    unique key `date_code_unique`(`code`,`date`)
)engine =innodb default charset utf8mb4 comment '车次';

-- auto-generated definition
drop table if exists `daily_train_carriage`;

create table train_business.daily_train_carriage
(
    id          bigint      not null comment 'id'
        primary key,
    date        date        not null comment '日期',
    train_code  varchar(20) not null comment '车次编号',
    `index`     int         not null comment '厢号',
    seat_type   char        not null comment '座位类型|枚举[SeatTypeEnum]',
    seat_count  int         not null comment '座位数',
    row_count   int         not null comment '排数',
    col_count   int         not null comment '列数',
    create_time datetime(3) null comment '新增时间',
    update_time datetime(3) null comment '修改时间',
    constraint date_train_code_index_unique
        unique (train_code, `index`, date)
)
    comment '火车车厢';

