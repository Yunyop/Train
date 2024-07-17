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