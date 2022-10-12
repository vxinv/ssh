DROP TABLE IF EXISTS `ecs`;
CREATE TABLE IF NOT EXISTS `ecs`
(
    `id`             int(11)   NOT NULL AUTO_INCREMENT,
    `name`           varchar(255) NOT NULL,
    `description`    varchar(255) DEFAULT NULL,
    `config`         text ,
    `type`           varchar(10)  NOT NULL, -- '类型 FOLDER:文件夹, NODE:叶子节点'
    `parent_id`      bigint(20)   NOT NULL ,--  '父文件夹ID'
    `create_time`    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP , -- '创建时间'
    `update_time`    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP , -- '修改时间'
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `theme`;
create table  if not exists `theme`
(
    `id` int not null auto_increment,
    `fontsize` int not null default 12 comment '字体大小',
    `hearttime` int not null default 3000 comment '心跳检测时间'
);

DROP TABLE IF EXISTS `command`;
create table  if not exists `command`
(
    `id` varchar(32) not null comment 'id',
    `name` varchar(100) not null default '' comment '名字',
    `content` varchar(200) not null default '' comment '内容',
    `has_delete` varchar(1) not null default '1' comment '1没删除, 0删除'
)