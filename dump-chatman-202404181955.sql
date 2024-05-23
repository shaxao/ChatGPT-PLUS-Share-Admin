-- MySQL dump 10.13  Distrib 8.0.26, for Win64 (x86_64)
--
-- Host: localhost    Database: chatman
-- ------------------------------------------------------
-- Server version	5.7.35-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `chat_access_token`
--

DROP TABLE IF EXISTS `chat_access_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_access_token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱，关联 chat_users 表的 chat_email 列',
  `token` varchar(500) DEFAULT NULL COMMENT 'token',
  `create_date` datetime DEFAULT NULL COMMENT '创建日期',
  `expire_date` datetime DEFAULT NULL COMMENT '过期日期',
  `creator` varchar(255) DEFAULT 'admin' COMMENT '创建人，默认 admin',
  `token_status` varchar(100) DEFAULT NULL COMMENT 'token状态',
  `user_count` int(10) DEFAULT NULL,
  `sid` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_email` (`email`),
  KEY `idx_token_status` (`token_status`),
  KEY `chat_access_token_FK` (`sid`),
  CONSTRAINT `chat_access_token_FK` FOREIGN KEY (`sid`) REFERENCES `chat_share_token` (`id`),
  CONSTRAINT `chat_access_token_ibfk_1` FOREIGN KEY (`email`) REFERENCES `chat_users` (`user_email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天会话Token表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_access_token`
--

LOCK TABLES `chat_access_token` WRITE;
/*!40000 ALTER TABLE `chat_access_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_access_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_account`
--

DROP TABLE IF EXISTS `chat_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '账号ID',
  `email` varchar(255) NOT NULL COMMENT '账号（邮箱）',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `create_time` datetime NOT NULL COMMENT '创建日期',
  `expire_time` datetime NOT NULL COMMENT '过期日期',
  `creator` varchar(255) NOT NULL COMMENT '创建人',
  `status` varchar(50) NOT NULL COMMENT '账号状态',
  `sid` bigint(20) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL COMMENT '用户名',
  PRIMARY KEY (`id`),
  KEY `chat_account_FK` (`sid`),
  CONSTRAINT `chat_account_FK` FOREIGN KEY (`sid`) REFERENCES `chat_session_token` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账号表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_account`
--

LOCK TABLES `chat_account` WRITE;
/*!40000 ALTER TABLE `chat_account` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_code`
--

DROP TABLE IF EXISTS `chat_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_code` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id主键',
  `qrcode_link` varchar(255) DEFAULT NULL COMMENT '二维码链接',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='聊天二维码表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_code`
--

LOCK TABLES `chat_code` WRITE;
/*!40000 ALTER TABLE `chat_code` DISABLE KEYS */;
INSERT INTO `chat_code` VALUES (1,'https://weixin.qq.com/g/AwYAAHcn3rvAvxGRy3-t_hu4kA2ZHyQASe3modOQyu54ezjewzBtG8dFVkDZZ-7shttps://weixin.qq.com/g/AwYAAHcn3rvAvxGRy3-t_hu4kA2ZHyQASe3modOQyu54ezjewzBtG8dFVkDZZ-7s','2024-01-04 17:05:56','2024-01-11 17:05:56'),(2,'https://weixin.qq.com/g/AwYAAAvplpoNnAU09PaKq2UMUQlIIs8skOTisbWGvpTRXkROxQNLxy64bVK3DFqO','2024-04-18 16:53:26','2024-04-25 16:53:26');
/*!40000 ALTER TABLE `chat_code` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_orders`
--

DROP TABLE IF EXISTS `chat_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_orders` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '订单id',
  `title` varchar(256) DEFAULT NULL COMMENT '订单标题',
  `order_no` varchar(50) DEFAULT NULL COMMENT '商户订单编号',
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `total_fee` int(11) DEFAULT NULL COMMENT '订单金额(分)',
  `code_url` varchar(50) DEFAULT NULL COMMENT '订单二维码连接',
  `order_status` varchar(10) DEFAULT NULL COMMENT '订单状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `expire_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '过期时间',
  `refresh_count` int(10) DEFAULT '5' COMMENT '每个订单的刷新次数',
  `pro_id` int(11) DEFAULT NULL,
  `quantity` int(11) DEFAULT '0' COMMENT '购买数量',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `chat_orders_FK` (`user_id`),
  KEY `chat_orders_FK_1` (`pro_id`),
  CONSTRAINT `chat_orders_FK` FOREIGN KEY (`user_id`) REFERENCES `chat_users` (`id`),
  CONSTRAINT `chat_orders_FK_1` FOREIGN KEY (`pro_id`) REFERENCES `chat_product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1713431514307 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_orders`
--

LOCK TABLES `chat_orders` WRITE;
/*!40000 ALTER TABLE `chat_orders` DISABLE KEYS */;
INSERT INTO `chat_orders` VALUES (1704285691610,'3天PLUS账号','1704285691611',15,800,NULL,'用户已取消','2024-01-03 20:41:32','2024-01-07 11:50:00',5,NULL,0),(1704290858688,'3天PLUS账号','1704290858688',15,800,NULL,'用户已取消','2024-01-03 22:07:39','2024-01-17 20:40:01',5,NULL,0),(1704338322275,'3天PLUS账号','1704338322276',15,800,NULL,'用户已取消','2024-01-04 11:18:42','2024-01-17 21:15:00',5,NULL,0),(1704339972388,'3天PLUS账号','1704339972388',15,800,NULL,'用户已取消','2024-01-04 11:46:12','2024-01-17 21:20:00',5,NULL,0),(1704340108848,'3天PLUS账号','1704340108849',15,800,NULL,'用户已取消','2024-01-04 11:48:29','2024-01-17 21:25:00',5,NULL,0),(1704340225251,'3天PLUS账号','1704340225251',15,800,NULL,'用户已取消','2024-01-04 11:50:25','2024-01-17 21:30:00',5,NULL,0),(1704340264416,'3天PLUS账号','1704340264417',15,800,NULL,'用户已取消','2024-01-04 11:51:04','2024-01-17 21:35:00',5,NULL,0),(1704340580135,'3天PLUS账号','1704340580135',15,800,NULL,'用户已取消','2024-01-04 11:56:20','2024-01-17 21:40:00',5,NULL,0),(1704340801600,'3天PLUS账号','1704340801600',15,800,NULL,'用户已取消','2024-01-04 12:00:02','2024-01-17 21:45:00',5,NULL,0),(1704341113852,'3天PLUS账号','1704341113852',15,1,NULL,'已支付','2024-01-04 12:05:14','2024-01-07 12:05:14',3,NULL,0),(1704341226409,'3天PLUS账号','1704341226409',15,800,NULL,'用户已取消','2024-01-04 12:07:06','2024-01-17 21:50:00',5,NULL,0),(1704341318112,'3天PLUS账号','1704341318112',15,800,NULL,'用户已取消','2024-01-04 12:08:38','2024-01-17 21:55:00',5,NULL,0),(1704341366068,'3天PLUS账号','1704341366069',15,800,NULL,'用户已取消','2024-01-04 12:09:26','2024-01-17 22:00:00',5,NULL,0),(1704341775973,'3天PLUS账号','1704341775973',15,800,NULL,'用户已取消','2024-01-04 12:16:16','2024-01-17 22:05:00',5,NULL,0),(1704341962216,'3天PLUS账号','1704341962216',15,800,NULL,'用户已取消','2024-01-04 12:19:22','2024-01-17 22:10:00',5,NULL,0),(1704342145430,'3天PLUS账号','1704342145430',15,800,NULL,'用户已取消','2024-01-04 12:22:25','2024-01-17 22:15:00',5,NULL,0),(1704342238158,'3天PLUS账号','1704342238158',15,800,NULL,'用户已取消','2024-01-04 12:23:58','2024-01-17 22:20:00',5,NULL,0),(1704342297139,'3天PLUS账号','1704342297139',15,2400,NULL,'用户已取消','2024-01-04 12:24:57','2024-01-17 22:25:00',5,NULL,0),(1704342300398,'3天PLUS账号','1704342300398',15,2400,NULL,'用户已取消','2024-01-04 12:25:00','2024-01-17 22:30:00',5,NULL,0),(1704342318871,'3天PLUS账号','1704342318871',15,2400,NULL,'用户已取消','2024-01-04 12:25:19','2024-01-17 22:35:00',5,NULL,0),(1704342405709,'3天PLUS账号','1704342405709',15,3200,NULL,'用户已取消','2024-01-04 12:26:46','2024-01-17 22:45:00',5,NULL,0),(1704342550307,'3天PLUS账号','1704342550315',15,2400,NULL,'用户已取消','2024-01-04 12:29:10','2024-01-18 08:45:00',5,NULL,0),(1704342999882,'3天PLUS账号','1704342999882',15,800,NULL,'用户已取消','2024-01-04 12:36:40','2024-01-18 08:50:00',5,NULL,0),(1704344025617,'3天PLUS账号','1704344025617',15,1600,NULL,'用户已取消','2024-01-04 12:53:46','2024-01-18 08:55:00',5,NULL,0),(1704344032214,'3天PLUS账号','1704344032214',15,1600,NULL,'用户已取消','2024-01-04 12:53:52','2024-01-18 09:00:00',5,NULL,0),(1704344037522,'3天PLUS账号','1704344037522',15,1600,NULL,'用户已取消','2024-01-04 12:53:58','2024-01-18 09:05:00',5,NULL,0),(1704345427908,'3天PLUS账号','1704345427908',15,800,NULL,'用户已取消','2024-01-04 13:17:08','2024-01-18 09:10:00',5,NULL,0),(1704345492743,'3天PLUS账号','1704345492743',15,800,NULL,'用户已取消','2024-01-04 13:18:13','2024-01-18 09:15:00',5,NULL,0),(1704359771289,'3天PLUS账号','1704359771289',15,800,NULL,'用户已取消','2024-01-04 17:16:11','2024-01-18 09:20:00',5,NULL,0),(1704375586652,'3天PLUS账号','1704375586653',15,800,NULL,'用户已取消','2024-01-04 21:39:47','2024-01-18 09:25:00',5,NULL,0),(1704375598619,'3天PLUS账号','1704375598619',15,800,NULL,'用户已取消','2024-01-04 21:39:59','2024-01-18 09:30:00',5,NULL,0),(1704375602100,'3天PLUS账号','1704375602100',15,800,NULL,'用户已取消','2024-01-04 21:40:02','2024-01-18 09:35:00',5,NULL,0),(1704380967569,'1天测试商品','1704380967576',15,1,NULL,'用户已取消','2024-01-04 23:09:28','2024-01-18 09:40:00',5,NULL,0),(1704381450473,'1天测试商品','1704381450473',15,1,NULL,'已支付','2024-01-04 23:17:30','2024-01-05 23:17:30',4,NULL,0),(1704422578085,'1天测试商品','1704422578086',15,1,NULL,'用户已取消','2024-01-05 10:42:58','2024-01-18 09:45:00',5,NULL,0),(1704423944585,'1天测试商品','1704423944585',15,1,NULL,'用户已取消','2024-01-05 11:05:45','2024-01-18 09:50:00',5,NULL,0),(1704424568179,'1天测试商品','1704424568180',15,1,NULL,'已支付','2024-01-05 11:16:08','2024-01-06 11:16:08',5,NULL,0),(1704424772333,'1天测试商品','1704424772334',15,1,NULL,'用户已取消','2024-01-05 11:19:32','2024-01-05 11:24:16',5,NULL,0),(1704427198863,'1天测试商品','1704427198864',15,1,NULL,'用户已取消','2024-01-05 11:59:59','2024-01-05 12:00:11',5,5,1),(1704427232775,'1天测试商品','1704427232775',15,1,NULL,'用户已取消','2024-01-05 12:00:33','2024-01-05 12:00:37',5,5,1),(1704427425293,'1天测试商品','1704427425294',15,1,NULL,'用户已取消','2024-01-05 12:03:45','2024-01-05 12:03:47',5,5,1),(1704427530509,'1天测试商品','1704427530509',15,1,NULL,'用户已取消','2024-01-05 12:05:31','2024-01-05 12:05:35',5,5,1),(1704427548193,'1天测试商品','1704427548193',15,1,NULL,'用户已取消','2024-01-05 12:05:48','2024-01-05 12:05:50',5,5,1),(1704427559787,'1天测试商品','1704427559787',15,1,NULL,'用户已取消','2024-01-05 12:06:00','2024-01-18 09:55:00',5,5,1),(1704427739566,'1天测试商品','1704427739566',15,1,NULL,'用户已取消','2024-01-05 12:09:00','2024-01-05 12:09:21',5,5,1),(1704427992133,'1天测试商品','1704427992133',15,1,NULL,'已支付','2024-01-05 12:13:12','2024-01-06 12:13:12',5,5,1),(1704428697168,'1天测试商品','1704428697168',15,1,NULL,'用户已取消','2024-01-05 12:24:57','2024-01-05 12:25:06',5,5,1),(1704428720609,'1天测试商品','1704428720609',15,1,NULL,'用户已取消','2024-01-05 12:25:21','2024-01-18 09:55:00',5,5,1),(1704445435971,'1天测试商品','1704445435971',17,1,NULL,'用户已取消','2024-01-05 17:03:56','2024-01-18 09:55:00',5,5,1),(1704454938666,'3天PLUS账号','1704454938666',17,800,NULL,'用户已取消','2024-01-05 19:42:19','2024-01-05 19:42:22',5,1,1),(1712222438604,'2天测试商品','1712222438604',17,900,NULL,'用户已取消','2024-04-04 17:20:39','2024-04-04 17:35:00',5,5,1),(1712222446942,'2天测试商品','1712222446942',17,900,NULL,'用户已取消','2024-04-04 17:20:47','2024-04-04 17:20:48',5,5,1),(1712222456247,'2天测试商品','1712222456247',17,900,NULL,'用户已取消','2024-04-04 17:20:56','2024-04-04 17:35:00',5,5,1),(1712376047035,'15天PLUS账号','1712376047035',17,4000,NULL,'未支付','2024-04-06 12:00:47','2024-04-21 12:00:47',5,3,1),(1712376051312,'15天PLUS账号','1712376051312',17,4000,NULL,'未支付','2024-04-06 12:00:51','2024-04-21 12:00:51',5,3,1),(1712376054229,'15天PLUS账号','1712376054229',17,4000,NULL,'未支付','2024-04-06 12:00:54','2024-04-21 12:00:54',5,3,1),(1712376056791,'15天PLUS账号','1712376056791',17,4000,NULL,'未支付','2024-04-06 12:00:57','2024-04-21 12:00:57',5,3,1),(1712376059739,'15天PLUS账号','1712376059739',17,4000,NULL,'未支付','2024-04-06 12:01:00','2024-04-21 12:01:00',5,3,1),(1712376064109,'15天PLUS账号','1712376064109',17,4000,NULL,'未支付','2024-04-06 12:01:04','2024-04-21 12:01:04',5,3,1),(1712376066908,'15天PLUS账号','1712376066908',17,4000,NULL,'未支付','2024-04-06 12:01:07','2024-04-21 12:01:07',5,3,1),(1712376070389,'15天PLUS账号','1712376070389',17,4000,NULL,'未支付','2024-04-06 12:01:10','2024-04-21 12:01:10',5,3,1),(1712376072744,'15天PLUS账号','1712376072744',17,4000,NULL,'未支付','2024-04-06 12:01:13','2024-04-21 12:01:13',5,3,1),(1712392357278,'3天PLUS账号','1712392357278',17,800,NULL,'未支付','2024-04-06 16:32:37','2024-04-09 16:32:37',5,1,1),(1712392463438,'3天PLUS账号','1712392463438',17,800,NULL,'用户已取消','2024-04-06 16:34:23','2024-04-06 16:34:42',5,1,1),(1712392731106,'3天PLUS账号','1712392731107',17,800,NULL,'用户已取消','2024-04-06 16:38:51','2024-04-18 10:06:39',5,1,1),(1713430460833,'2天测试商品','1713430460839',17,900,NULL,'用户已取消','2024-04-18 16:54:21','2024-04-18 16:59:22',5,5,1),(1713431514306,'2天测试商品','1713431514306',17,900,NULL,'用户已取消','2024-04-18 17:11:54','2024-04-18 17:16:55',5,5,1);
/*!40000 ALTER TABLE `chat_orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_permission`
--

DROP TABLE IF EXISTS `chat_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_permission` (
  `pid` int(11) NOT NULL AUTO_INCREMENT,
  `permissionName` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`pid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_permission`
--

LOCK TABLES `chat_permission` WRITE;
/*!40000 ALTER TABLE `chat_permission` DISABLE KEYS */;
INSERT INTO `chat_permission` VALUES (1,'分享界面','/pandora/share'),(2,'登录','/pandora/login'),(3,'无权限登录','/pandora/no'),(4,'真正的分享界面','/shared.html');
/*!40000 ALTER TABLE `chat_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_product`
--

DROP TABLE IF EXISTS `chat_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL COMMENT '商品标题',
  `stock` int(11) DEFAULT NULL COMMENT '商品库存',
  `price` decimal(10,2) DEFAULT NULL COMMENT '商品价格，显示两位小数',
  `image_url` varchar(255) DEFAULT NULL COMMENT '商品图片地址',
  `description` text COMMENT '商品描述',
  `chat_order_id` int(11) DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `pro_status` varchar(10) NOT NULL DEFAULT '下架' COMMENT '商品状态',
  `creator` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `chat_order_id` (`chat_order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='商品详情表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_product`
--

LOCK TABLES `chat_product` WRITE;
/*!40000 ALTER TABLE `chat_product` DISABLE KEYS */;
INSERT INTO `chat_product` VALUES (1,'3天PLUS账号',94,8.00,'/images/product.jpg','三天无限制plus账号，直接进入页面选择一个账号进入即可，一个账号限制了可以使用退出来用另一个，每个账号的聊天都是独立的，如果所有账号都限制了，请进入商品界面领取key登录即可',NULL,'2023-12-27 21:21:19','上架',NULL),(2,'7天PLUS账号',12,16.00,'/images/product.jpg','七天无限制plus账号，直接进入页面选择一个账号进入即可，一个账号限制了可以使用退出来用另一个，每个账号的聊天都是独立的，如果所有账号都限制了，请进入商品界面领取key登录即可',NULL,'2023-12-28 16:32:33','上架',NULL),(3,'15天PLUS账号',3,40.00,'/images/product.jpg','十五天无限制plus账号，直接进入页面选择一个账号进入即可，一个账号限制了可以使用退出来用另一个，每个账号的聊天都是独立的，如果所有账号都限制了，请进入商品界面领取key登录即可',NULL,'2023-12-28 16:34:16','上架',NULL),(4,'30天PLUS账号',12,60.00,'/images/product.jpg','三十天无限制plus账号，直接进入页面选择一个账号进入即可，一个账号限制了可以使用退出来用另一个，每个账号的聊天都是独立的，如果所有账号都限制了，请进入商品界面领取key登录即可',NULL,'2023-12-28 16:34:51','下架',NULL),(5,'2天测试商品',18,9.00,'/images/product.jpg','三天无限制plus账号',NULL,'2024-01-04 23:01:25','上架',NULL),(6,'1天测试商品',150,1999.00,'/images/product.jpg','这是产品的详细描述，介绍了产品的特点、用途等信息。',NULL,'2024-01-17 22:46:47','上架',NULL);
/*!40000 ALTER TABLE `chat_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_product_detail_img`
--

DROP TABLE IF EXISTS `chat_product_detail_img`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_product_detail_img` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_id` int(11) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `creator` varchar(255) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `chat_product_detail_img_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `chat_product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_product_detail_img`
--

LOCK TABLES `chat_product_detail_img` WRITE;
/*!40000 ALTER TABLE `chat_product_detail_img` DISABLE KEYS */;
INSERT INTO `chat_product_detail_img` VALUES (1,1,'/images/productDetail1.jpg','2024-01-05 19:16:38','',NULL),(2,1,'/images/productDetail2.jpg','2024-01-05 19:16:38',NULL,NULL),(3,1,'/images/productDetail3.jpg','2024-01-05 19:26:58',NULL,NULL),(4,1,'/images/productDetail4.jpg','2024-01-05 19:27:06',NULL,NULL),(5,6,'https://ttzi.top/group1/M00/00/00/L2N-bWWn5O6AL3m3AAANnYk4lc869..png','2024-01-17 22:46:47','admin',NULL),(6,6,'https://ttzi.top/group1/M00/00/00/L2N-bWWn5O6AZ5QFAAUzBZExECA33..jpg','2024-01-17 22:46:47','admin',NULL),(7,6,'https://ttzi.top/group1/M00/00/00/L2N-bWWn5O6ASWLfAASlGyW9q3E36..jpg','2024-01-17 22:46:47','admin',NULL);
/*!40000 ALTER TABLE `chat_product_detail_img` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_role`
--

DROP TABLE IF EXISTS `chat_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_role` (
  `rid` int(11) NOT NULL AUTO_INCREMENT,
  `roleName` varchar(255) DEFAULT NULL,
  `roleDesc` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_role`
--

LOCK TABLES `chat_role` WRITE;
/*!40000 ALTER TABLE `chat_role` DISABLE KEYS */;
INSERT INTO `chat_role` VALUES (1,'超级管理员','任意访问所有界面'),(2,'plus用户','访问share.html'),(3,'普通用户','只能进入登录界面');
/*!40000 ALTER TABLE `chat_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_session_token`
--

DROP TABLE IF EXISTS `chat_session_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_session_token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱，关联 chat_users 表的 chat_email 列',
  `token` varchar(500) DEFAULT NULL COMMENT 'token',
  `create_date` datetime DEFAULT NULL COMMENT '创建日期',
  `expire_date` datetime DEFAULT NULL COMMENT '过期日期',
  `creator` varchar(255) DEFAULT 'admin' COMMENT '创建人，默认 admin',
  `token_status` varchar(100) DEFAULT NULL COMMENT 'token状态',
  `user_count` int(10) DEFAULT NULL,
  `aid` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_email` (`email`),
  KEY `idx_token_status` (`token_status`),
  KEY `chat_session_token_FK` (`aid`),
  CONSTRAINT `chat_session_token_FK` FOREIGN KEY (`aid`) REFERENCES `chat_access_token` (`id`),
  CONSTRAINT `fk_chat_session_token_chat_users` FOREIGN KEY (`email`) REFERENCES `chat_users` (`user_email`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天会话Token表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_session_token`
--

LOCK TABLES `chat_session_token` WRITE;
/*!40000 ALTER TABLE `chat_session_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_session_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_share_token`
--

DROP TABLE IF EXISTS `chat_share_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_share_token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱，关联 chat_users 表的 chat_email 列',
  `token` varchar(500) DEFAULT NULL COMMENT 'token',
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  `expire_date` datetime DEFAULT NULL COMMENT '过期日期',
  `creator` varchar(255) DEFAULT 'admin' COMMENT '创建人，默认 admin',
  `token_status` varchar(11) DEFAULT NULL COMMENT 'token状态',
  `user_count` int(10) NOT NULL DEFAULT '0' COMMENT '使用人数',
  PRIMARY KEY (`id`),
  KEY `idx_email` (`email`),
  KEY `idx_token_status` (`token_status`),
  CONSTRAINT `chat_share_token_ibfk_1` FOREIGN KEY (`email`) REFERENCES `chat_users` (`user_email`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='聊天会话Token表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_share_token`
--

LOCK TABLES `chat_share_token` WRITE;
/*!40000 ALTER TABLE `chat_share_token` DISABLE KEYS */;
INSERT INTO `chat_share_token` VALUES (1,NULL,'fk-1n0spNSdCfv6hTrKUt0zSuTFH7gy2l0_AdCJEkPQBs4','2024-01-03 20:41:32','2024-01-06 20:41:32','admin','正常',0),(2,NULL,'fk-REO19Ho82HFXE7gXI8Qh_H1zt7Y0cGroy7SB5ltyehI','2024-01-02 20:41:32','2024-04-19 20:41:32','admin','正常',0),(3,NULL,'fk-V4CO4bDh_7f4Y4gEqL2bVQmNF-33320yILS-l62S8TI','2024-04-18 15:35:53','2024-04-19 00:00:00','admin','正常',0);
/*!40000 ALTER TABLE `chat_share_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_url`
--

DROP TABLE IF EXISTS `chat_url`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_url` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `route_path` varchar(255) DEFAULT NULL,
  `page_description` varchar(255) DEFAULT NULL,
  `route_status` varchar(11) DEFAULT '删除',
  `parent_menu` int(11) DEFAULT NULL,
  `child_menu` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_url`
--

LOCK TABLES `chat_url` WRITE;
/*!40000 ALTER TABLE `chat_url` DISABLE KEYS */;
INSERT INTO `chat_url` VALUES (1,'/pandora','pandora分享页面路径','正常',NULL,NULL),(2,'https://openai.qipusong.store','pandora登录url','正常',NULL,NULL),(3,'/product.html','商品页面','正常',NULL,NULL),(4,'https://chat.qipusong.site','chatNio','正常',NULL,NULL),(5,'https://lobe.qipusong.store','lobeChat','正常',NULL,NULL),(6,'https://bing.qipusong.store','bingAI','正常',NULL,NULL);
/*!40000 ALTER TABLE `chat_url` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_users`
--

DROP TABLE IF EXISTS `chat_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(11) unsigned DEFAULT NULL,
  `user_email` varchar(255) DEFAULT NULL COMMENT '用户邮箱',
  `user_phone` varchar(255) DEFAULT NULL,
  `user_status` varchar(255) DEFAULT NULL COMMENT '用户状态 0 未验证 1 普通用户 2 会员',
  `deleted` int(11) DEFAULT NULL COMMENT '用户状态 0 为删除 1 违规或者其他原因被删除',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL COMMENT '验证码',
  `code_expire_time` datetime DEFAULT NULL COMMENT '验证码过期时间',
  `user_password` varchar(255) DEFAULT NULL,
  `creator` varchar(255) DEFAULT 'admin',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  PRIMARY KEY (`id`),
  KEY `chat_users_FK` (`order_id`),
  KEY `idx_chat_users_chat_email` (`user_email`),
  CONSTRAINT `chat_users_FK` FOREIGN KEY (`order_id`) REFERENCES `chat_orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_users`
--

LOCK TABLES `chat_users` WRITE;
/*!40000 ALTER TABLE `chat_users` DISABLE KEYS */;
INSERT INTO `chat_users` VALUES (1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'muhuo',NULL,NULL,NULL,'admin',NULL),(15,NULL,'3803217870@qq.com',NULL,'会员',0,'2023-12-27 21:21:19','2024-01-04 23:19:38','mkhjnjn','625761','2023-12-27 21:21:19','$2a$10$Uyspdn0PoaKP3jpX7m1l8eKa9tnc8RfXB8HG1MJRkpc6bX/0d.2LS','admin',NULL),(16,NULL,'3803217817@qq.com',NULL,NULL,NULL,NULL,NULL,NULL,'357454','2023-12-27 19:36:07',NULL,'admin',NULL),(17,NULL,'415240147@qq.com',NULL,'管理员',0,'2024-01-05 16:45:45',NULL,'admin','838468','2024-01-05 16:45:45','$2a$10$sZQcY3aATrjSpgu0b3fcneFzLEpYOJbr/3BqKDwGW7slUUImtvBsy','admin','https://ttzi.top/group1/M00/00/00/L2N-bWWn4smAa1KDAAANnYk4lc803..png'),(18,NULL,'mhuimm62@gmail.com',NULL,'普通用户',0,'2024-04-18 15:56:41',NULL,'木火','508611','2024-04-18 15:56:41','$2a$10$rdkLdFwl/OMf6DvLZ.5hjeBGB8wxkye0G5oDUMP/3mUW9djXQQKP.','admin',NULL),(19,NULL,'466397328@qq.com',NULL,'会员',0,'2024-04-18 17:26:33',NULL,'阿斯顿','212241','2024-04-18 17:26:33','$2a$10$wrlRrcWntHH5OoW8pZmW8.mjM//uXmFe0bK1pO1M11f2oOXmDcYXe','admin',NULL);
/*!40000 ALTER TABLE `chat_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `persistent_logins`
--

DROP TABLE IF EXISTS `persistent_logins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `persistent_logins` (
  `username` varchar(64) NOT NULL,
  `series` varchar(64) NOT NULL,
  `token` varchar(64) NOT NULL,
  `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`series`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `persistent_logins`
--

LOCK TABLES `persistent_logins` WRITE;
/*!40000 ALTER TABLE `persistent_logins` DISABLE KEYS */;
INSERT INTO `persistent_logins` VALUES ('阿斯顿','A4HMx5rQYnQUskCaGgQiFQ==','IF1u4Svw/3rHezLhESF0EQ==','2024-04-18 11:11:35'),('admin','CL15zqhKsLAtaiorM+gdnw==','WbDT0lB3pBsBfWvRtEydaA==','2024-04-18 09:11:49'),('admin','FpRJD7oomxDc8M/FlDCdLw==','FxfMd+5YNEAcM8A7k2iHjQ==','2024-04-18 08:54:04'),('admin','nO7+n6/86V5UQ0PVRHEW8A==','FeCzN+3DByfXyaCZxmQ6TQ==','2024-04-18 08:08:58');
/*!40000 ALTER TABLE `persistent_logins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_permission`
--

DROP TABLE IF EXISTS `role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_permission` (
  `rid` int(11) NOT NULL,
  `pid` int(11) NOT NULL,
  PRIMARY KEY (`rid`,`pid`) USING BTREE,
  KEY `pid` (`pid`) USING BTREE,
  CONSTRAINT `role_permission_ibfk_1` FOREIGN KEY (`rid`) REFERENCES `chat_role` (`rid`),
  CONSTRAINT `role_permission_ibfk_2` FOREIGN KEY (`pid`) REFERENCES `chat_permission` (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_permission`
--

LOCK TABLES `role_permission` WRITE;
/*!40000 ALTER TABLE `role_permission` DISABLE KEYS */;
INSERT INTO `role_permission` VALUES (1,1),(2,1),(1,2),(2,2),(1,3),(3,3),(1,4),(2,4);
/*!40000 ALTER TABLE `role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users_role`
--

DROP TABLE IF EXISTS `users_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users_role` (
  `uid` int(255) NOT NULL,
  `rid` int(11) NOT NULL,
  PRIMARY KEY (`uid`,`rid`) USING BTREE,
  KEY `rid` (`rid`) USING BTREE,
  CONSTRAINT `users_role_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `chat_users` (`id`),
  CONSTRAINT `users_role_ibfk_2` FOREIGN KEY (`rid`) REFERENCES `chat_role` (`rid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users_role`
--

LOCK TABLES `users_role` WRITE;
/*!40000 ALTER TABLE `users_role` DISABLE KEYS */;
INSERT INTO `users_role` VALUES (1,1),(17,1),(15,2),(19,2),(18,3);
/*!40000 ALTER TABLE `users_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'chatman'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-04-18 19:55:52
