drop table if exists `station`;
create table `station` (
  `id` bigint not null comment 'id',
  `name` varchar(20) not null comment '站名',
  `name_pinyin` varchar(50) not null comment '站名拼音',
  `name_py` varchar(50) not null comment '站名拼音首字母',
  `create_time` datetime(3) comment '新增时间',
  `update_time` datetime(3) comment '修改时间',
  primary key (`id`),
  unique key `name_unique` (`name`)
) engine=innodb default charset=utf8mb4 comment='车站';

drop table if exists `train`;
create table `train` (
  `id` bigint not null comment 'id',
  `code` varchar(20) not null comment '车次编号',
  `type` char(1) not null comment '车次类型|枚举[TrainTypeEnum]',
  `start` varchar(20) not null comment '始发站',
  `start_pinyin` varchar(50) not null comment '始发站拼音',
  `start_time` time not null comment '出发时间',
  `end` varchar(20) not null comment '终点站',
  `end_pinyin` varchar(50) not null comment '终点站拼音',
  `end_time` time not null comment '到站时间',
  `create_time` datetime(3) comment '新增时间',
  `update_time` datetime(3) comment '修改时间',
  primary key (`id`),
  unique key `code_unique` (`code`)
) engine=innodb default charset=utf8mb4 comment='车次';


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

drop table if exists `train_carriage`;
create table `train_carriage` (
  `id` bigint not null comment 'id',
  `train_code` varchar(20) not null comment '车次编号',
  `index` int not null comment '厢号',
  `seat_type` char(1) not null comment '座位类型|枚举[SeatTypeEnum]',
  `seat_count` int not null comment '座位数',
  `row_count` int not null comment '排数',
  `col_count` int not null comment '列数',
  `create_time` datetime(3) comment '新增时间',
  `update_time` datetime(3) comment '修改时间',
  unique key `train_code_index_unique` (`train_code`, `index`),
  primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='火车车厢';

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

drop table if exists `daily_train_station`;
create table `daily_train_station` (
  `id` bigint not null comment 'id',
  `date` date not null comment '日期',
  `train_code` varchar(20) not null comment '车次编号',
  `index` int not null comment '站序|第一站是0',
  `name` varchar(20) not null comment '站名',
  `name_pinyin` varchar(50) not null comment '站名拼音',
  `in_time` time comment '进站时间',
  `out_time` time comment '出站时间',
  `stop_time` time comment '停站时长',
  `km` decimal(8, 2) not null comment '里程（公里）|从上一站到本站的距离',
  `create_time` datetime(3) comment '新增时间',
  `update_time` datetime(3) comment '修改时间',
  primary key (`id`),
  unique key `date_train_code_index_unique` (`date`, `train_code`, `index`),
  unique key `date_train_code_name_unique` (`date`, `train_code`, `name`)
) engine=innodb default charset=utf8mb4 comment='每日车站';



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

drop table if exists `daily_train_seat`;
create table `daily_train_seat` (
  `id` bigint not null comment 'id',
  `date` date not null comment '日期',
  `train_code` varchar(20) not null comment '车次编号',
  `carriage_index` int not null comment '箱序',
  `row` char(2) not null comment '排号|01, 02',
  `col` char(1) not null comment '列号|枚举[SeatColEnum]',
  `seat_type` char(1) not null comment '座位类型|枚举[SeatTypeEnum]',
  `carriage_seat_index` int not null comment '同车箱座序',
  `sell` varchar(50) not null comment '售卖情况|将经过的车站用01拼接，0表示可卖，1表示已卖',
  `create_time` datetime(3) comment '新增时间',
  `update_time` datetime(3) comment '修改时间',
  primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='每日座位';

drop table if exists `daily_train_ticket`;
CREATE TABLE `daily_train_ticket` (
                                      `id` bigint NOT NULL COMMENT 'id',
                                      `date` date NOT NULL COMMENT '日期',
                                      `train_code` varchar(20) NOT NULL COMMENT '车次编号',
                                      `start` varchar(20) NOT NULL COMMENT '出发站',
                                      `start_pinyin` varchar(50) NOT NULL COMMENT '出发站拼音',
                                      `start_time` time NOT NULL COMMENT '出发时间',
                                      `start_index` int NOT NULL COMMENT '出发站序|本站是整个车次的第几站',
                                      `end` varchar(20) NOT NULL COMMENT '到达站',
                                      `end_pinyin` varchar(50) NOT NULL COMMENT '到达站拼音',
                                      `end_time` time NOT NULL COMMENT '到站时间',
                                      `end_index` int NOT NULL COMMENT '到站站序|本站是整个车次的第几站',
                                      `ydz` int NOT NULL COMMENT '一等座余票',
                                      `ydz_price` decimal(8,2) NOT NULL COMMENT '一等座票价',
                                      `edz` int NOT NULL COMMENT '二等座余票',
                                      `edz_price` decimal(8,2) NOT NULL COMMENT '二等座票价',
                                      `rw` int NOT NULL COMMENT '软卧余票',
                                      `rw_price` decimal(8,2) NOT NULL COMMENT '软卧票价',
                                      `yw` int NOT NULL COMMENT '硬卧余票',
                                      `yw_price` decimal(8,2) NOT NULL COMMENT '硬卧票价',
                                      `create_time` datetime(3) DEFAULT NULL COMMENT '新增时间',
                                      `update_time` datetime(3) DEFAULT NULL COMMENT '修改时间',
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `daily_train_code_start_end_unique` (`date`,`train_code`,`start`,`end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='余票信息';

drop table if exists `confirm_order`;
create table `confirm_order` (
                                 `id` bigint not null comment 'id',
                                 `member_id` bigint not null comment '会员id',
                                 `date` date not null comment '日期',
                                 `train_code` varchar(20) not null comment '车次编号',
                                 `start` varchar(20) not null comment '出发站',
                                 `end` varchar(20) not null comment '到达站',
                                 `daily_train_ticket_id` bigint not null comment '余票ID',
                                 `tickets` json not null comment '车票',
                                 `status` char(1) not null comment '订单状态|枚举[ConfirmOrderStatusEnum]',
                                 `create_time` datetime(3) comment '新增时间',
                                 `update_time` datetime(3) comment '修改时间',
                                 primary key (`id`),
                                 index `date_train_code_index` (`date`, `train_code`)
) engine=innodb default charset=utf8mb4 comment='确认订单';

-- 注意此处0.3.0+ 增加唯一索引 ux_undo_log
drop table if exists `undo_log`;
CREATE TABLE `undo_log` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `branch_id` bigint(20) NOT NULL,
                            `xid` varchar(100) NOT NULL,
                            `context` varchar(128) NOT NULL,
                            `rollback_info` longblob NOT NULL,
                            `log_status` int(11) NOT NULL,
                            `log_created` datetime NOT NULL,
                            `log_modified` datetime NOT NULL,
                            `ext` varchar(100) DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

drop table if exists `sk_token`;
CREATE TABLE `sk_token` (
                            `id` bigint NOT NULL COMMENT 'id',
                            `date` date NOT NULL COMMENT '日期',
                            `train_code` varchar(20) NOT NULL COMMENT '车次编号',
                            `count` int NOT NULL COMMENT '令牌余量',
                            `create_time` datetime(3) DEFAULT NULL COMMENT '新增时间',
                            `update_time` datetime(3) DEFAULT NULL COMMENT '修改时间',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `date_train_code_unique` (`date`,`train_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='秒杀令牌'

-- Create station table (as provided in your request)
CREATE TABLE `station` (
                           `id` bigint NOT NULL COMMENT 'id',
                           `name` varchar(20) NOT NULL COMMENT '站名',
                           `name_pinyin` varchar(50) NOT NULL COMMENT '站名拼音',
                           `name_py` varchar(50) NOT NULL COMMENT '站名拼音首字母',
                           `create_time` datetime(3) COMMENT '新增时间',
                           `update_time` datetime(3) COMMENT '修改时间',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `name_unique` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车站';

-- Insert sample data into station table
INSERT INTO `station` (id, name, name_pinyin, name_py, create_time, update_time)
VALUES
    (1, '北京南', 'beijingnan', 'bjn', NOW(), NOW()),
    (2, '天津南', 'tianjinnan', 'tjn', NOW(), NOW()),
    (3, '济南西', 'jinanxi', 'jnx', NOW(), NOW()),
    (4, '南京南', 'nanjingnan', 'njn', NOW(), NOW()),
    (5, '上海虹桥', 'shanghaihongqiao', 'shhq', NOW(), NOW()),
    (6, '北京西', 'beijingxi', 'bjx', NOW(), NOW()),
    (7, '石家庄', 'shijiazhuang', 'sjz', NOW(), NOW()),
    (8, '郑州东', 'zhengzhoudong', 'zzd', NOW(), NOW()),
    (9, '武汉', 'wuhan', 'wh', NOW(), NOW()),
    (10, '长沙南', 'changshanan', 'csn', NOW(), NOW()),
    (11, '广州南', 'guangzhounan', 'gzn', NOW(), NOW()),
    (12, '保定', 'baoding', 'bd', NOW(), NOW()),
    (13, '郑州', 'zhengzhou', 'zz', NOW(), NOW()),
    (14, '西安', 'xian', 'xa', NOW(), NOW()),
    (15, '成都', 'chengdu', 'cd', NOW(), NOW()),
    (16, '武昌', 'wuchang', 'wc', NOW(), NOW()),
    (17, '长沙', 'changsha', 'cs', NOW(), NOW()),
    (18, '广州', 'guangzhou', 'gz', NOW(), NOW()),
    (19, '深圳北', 'shenzhenbei', 'szb', NOW(), NOW()),
    (20, '杭州东', 'hangzhoudong', 'hzd', NOW(), NOW());
