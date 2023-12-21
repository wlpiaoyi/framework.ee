CREATE TABLE `biz_file_menu` (
  `id` bigint unsigned NOT NULL,
  `name` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文件名称',
  `size` bigint NOT NULL DEFAULT '0' COMMENT '文件大小',
  `token` varchar(128) COLLATE utf8mb4_bin NOT NULL,
  `fingerprint` varchar(128) COLLATE utf8mb4_bin NOT NULL COMMENT '文件指纹',
  `is_verify_sign` tinyint NOT NULL DEFAULT '0' COMMENT '是否验证签名 0:否 1:是',
  `suffix` varchar(10) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文件后缀',
  `status` int DEFAULT '1' COMMENT '状态',
  `is_deleted` int DEFAULT '0' COMMENT '是否删除',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='文件目录';
ALTER TABLE `biz_file_menu`
ADD INDEX `fingerprint_index` (`fingerprint` ASC) VISIBLE;
ALTER TABLE `biz_file_menu` ALTER INDEX `id_UNIQUE` INVISIBLE;
