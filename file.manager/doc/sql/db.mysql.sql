CREATE TABLE `biz_file_info` (
  `id` BIGINT(20) unsigned NOT NULL,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文件名称',
  `size` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '文件大小',
  `token` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `fingerprint` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文件指纹',
  `is_verify_sign` tinyint NOT NULL DEFAULT '0' COMMENT '是否验证签名 0:否 1:是',
  `suffix` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文件后缀',
  `status` int DEFAULT '1' COMMENT '状态',
  `is_deleted` int DEFAULT '0' COMMENT '是否删除',
  `create_user` BIGINT(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_dept` BIGINT(20) DEFAULT NULL COMMENT '创建部门',
  `update_user` BIGINT(20) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`) /*!80000 INVISIBLE */,
  KEY `fingerprint_index` (`fingerprint`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='文件信息';

CREATE TABLE `biz_image_info` (
  `id` BIGINT(20) UNSIGNED NOT NULL,
  `file_id` BIGINT(20) NOT NULL,
  `suffix` VARCHAR(10) NOT NULL COMMENT '文件后缀',
  `width` INT NOT NULL,
  `height` INT NOT NULL,
  `thumbnail_id` BIGINT(20) NULL DEFAULT NULL COMMENT '缩略图',
  `status` INT NULL DEFAULT '1' COMMENT '状态',
  `is_deleted` INT NULL DEFAULT '0' COMMENT '是否删除',
  `create_user` BIGINT(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_dept` BIGINT(20) NULL DEFAULT NULL COMMENT '创建部门',
  `update_user` BIGINT(20) NULL DEFAULT NULL COMMENT '修改人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  INDEX `file_id_index` (`file_id` ASC) VISIBLE)
COMMENT = '图片信息';

