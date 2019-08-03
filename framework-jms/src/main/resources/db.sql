CREATE TABLE `jms_fail_msg` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(32) NOT NULL DEFAULT '' COMMENT '唯一 标识UUID',
  `message` blob NOT NULL COMMENT '消息内容',
  `message_type` int(4) NOT NULL DEFAULT '1' COMMENT '消息类型1:text 2:bytes 3:stream 4:map 5:object',
  `destination_name` varchar(50) NOT NULL DEFAULT 'Undefied' COMMENT '消息目的地',
  `destination_type` int(4) NOT NULL DEFAULT '1' COMMENT '目的地类型 1:queue 2:topic',
  `state` int(11) NOT NULL DEFAULT '0' COMMENT '消息状态 0:未处理 1:已处理',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

