CREATE TABLE `rela_rela_file_role` (
  `token_id` bigint NOT NULL COMMENT '令牌Id',
  `target_id` bigint NOT NULL COMMENT '目标Id',
  `role_id` bigint NOT NULL COMMENT '权限Id',
  `type` int NOT NULL COMMENT '类型: 1 桶、2 文件夹 、100 文件',
  PRIMARY KEY (`token_id`,`target_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='令牌、目标(桶/文件夹/文件)、权限关系';

CREATE TABLE `res_bucket` (
  `id` bigint unsigned NOT NULL,
  `name` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '名称',
  `token_owner_id` bigint DEFAULT NULL COMMENT '所属令牌,如果为空就是公共桶',
  `status` int DEFAULT '1' COMMENT '状态',
  `is_deleted` bigint DEFAULT '0' COMMENT '是否删除',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`) /*!80000 INVISIBLE */,
  UNIQUE KEY `name_UNIQUE` (`name`,`token_owner_id`,`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='桶';

CREATE TABLE `res_file_info` (
  `id` bigint NOT NULL,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文件名称',
  `size` bigint NOT NULL DEFAULT '0' COMMENT '文件大小',
  `fingerprint` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文件指纹',
  `is_verify_sign` tinyint NOT NULL DEFAULT '0' COMMENT '是否验证签名 0:否 1:是',
  `suffix` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文件后缀',
  `status` int DEFAULT '1' COMMENT '状态',
  `is_deleted` bigint DEFAULT '0' COMMENT '是否删除',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fingerprint_index` (`fingerprint`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='文件信息';

CREATE TABLE `res_file_role` (
  `id` bigint unsigned NOT NULL,
  `name` varchar(20) COLLATE utf8mb4_bin NOT NULL,
  `permission_value` int NOT NULL COMMENT '对外数据权限:二进制index（0:查看, 1:下载, 2:修改/删除）',
  `permission_type` tinyint NOT NULL COMMENT '数据权限类型:0 默认, 1  向下继承,  2 被动向下继承',
  `status` int DEFAULT '1' COMMENT '状态',
  `is_deleted` bigint DEFAULT '0' COMMENT '是否删除',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='文件权限';

CREATE TABLE `res_folder` (
  `id` bigint unsigned NOT NULL,
  `name` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '名称',
  `bucket_id` bigint NOT NULL COMMENT '桶Id',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '上级Id',
  `deep` int NOT NULL DEFAULT '0' COMMENT '深度',
  `is_leaf` tinyint NOT NULL DEFAULT '1' COMMENT '是否是叶子节点',
  `status` int DEFAULT '1' COMMENT '状态',
  `is_deleted` bigint DEFAULT '0' COMMENT '是否删除',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='文件夹';

CREATE TABLE `res_image_info` (
  `id` bigint NOT NULL,
  `file_id` bigint NOT NULL COMMENT '文件id',
  `suffix` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文件后缀',
  `width` int NOT NULL,
  `height` int NOT NULL,
  `thumbnail_id` bigint DEFAULT NULL COMMENT '缩略图id',
  `status` int DEFAULT '1' COMMENT '状态',
  `is_deleted` bigint NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `file_id_index` (`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='图片信息';

CREATE TABLE `res_rela_file_bucket` (
  `file_id` bigint NOT NULL COMMENT '文件Id',
  `bucket_id` bigint NOT NULL COMMENT '桶Id',
  PRIMARY KEY (`file_id`,`bucket_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='文件和桶的关系';

CREATE TABLE `res_rela_file_folder` (
  `file_id` bigint NOT NULL COMMENT '文件Id',
  `folder_id` bigint NOT NULL COMMENT '文件夹Id',
  PRIMARY KEY (`file_id`,`folder_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='文件和文件夹的关系';

CREATE TABLE `res_token` (
  `id` bigint unsigned NOT NULL,
  `token` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '令牌',
  `public_key` varchar(256) COLLATE utf8mb4_bin NOT NULL COMMENT '公钥',
  `private_key` varchar(2048) COLLATE utf8mb4_bin NOT NULL COMMENT '私钥',
  `status` int DEFAULT '1' COMMENT '状态',
  `is_deleted` bigint NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `token_UNIQUE` (`token`,`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='令牌';

CREATE TABLE `res_video_info` (
  `id` bigint NOT NULL,
  `file_id` bigint NOT NULL COMMENT '文件id',
  `suffix` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文件后缀',
  `duration` bigint NOT NULL DEFAULT '0' COMMENT '视频时长(ms)',
  `screenshot_id` bigint DEFAULT NULL COMMENT '视频截图Id',
  `status` int DEFAULT '1' COMMENT '状态',
  `is_deleted` bigint NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `width` int DEFAULT '0',
  `height` int DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `file_id_index` (`file_id`),
  KEY `screenshot_id_index` (`screenshot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='视频信息';
