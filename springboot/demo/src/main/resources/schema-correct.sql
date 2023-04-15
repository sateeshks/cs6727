CREATE DATABASE  IF NOT EXISTS `dpm` ;
USE `dpm`;

-- ------------------------------------------------------
-- Server version	8.0.32



--
-- Table structure for table `app_user`
--

DROP TABLE IF EXISTS `app_user`;
CREATE TABLE `app_user` (
  `user_id` bigint NOT NULL,
  `create_time` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `handle` tinyblob NOT NULL,
  `name` varchar(255) NOT NULL,
  `updated_date_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_1j9d9a06i600gd43uu3km82jw` (`email`)
) ;
--
-- Table structure for table `app_user_seq`
--

DROP TABLE IF EXISTS `app_user_seq`;

CREATE TABLE `app_user_seq` (
  `next_val` bigint DEFAULT NULL
) ;

--
-- Table structure for table `authenticator`
--

DROP TABLE IF EXISTS `authenticator`;
CREATE TABLE `authenticator` (
  `id` bigint NOT NULL,
  `aaguid` tinyblob,
  `count` bigint NOT NULL,
  `credential_id` tinyblob NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `public_key` tinyblob NOT NULL,
  `user_user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrpqmldy2dc1hntc0ql5x3wyc9` (`user_user_id`),
  CONSTRAINT `FKrpqmldy2dc1hntc0ql5x3wyc9` FOREIGN KEY (`user_user_id`) REFERENCES `app_user` (`user_id`)
) ;
--
-- Table structure for table `authenticator_seq`
--

DROP TABLE IF EXISTS `authenticator_seq`;
CREATE TABLE `authenticator_seq` (
  `next_val` bigint DEFAULT NULL
) ;


--
-- Table structure for table `user_profile`
--

DROP TABLE IF EXISTS `user_profile`;
CREATE TABLE `user_profile` (
  `p_id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `digits` bit(1) NOT NULL,
  `domain` varchar(255) DEFAULT NULL,
  `exclude` varchar(255) DEFAULT NULL,
  `length` int NOT NULL,
  `lower_case` bit(1) NOT NULL,
  `revision` int NOT NULL,
  `symbols` bit(1) NOT NULL,
  `updated_date_time` datetime(6) DEFAULT NULL,
  `upper_case` bit(1) NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`p_id`),
  KEY `FKpdmw33px6fmevqhcy2lpstu4w` (`user_id`),
  CONSTRAINT `FKpdmw33px6fmevqhcy2lpstu4w` FOREIGN KEY (`user_id`) REFERENCES `app_user` (`user_id`)
) ;

INSERT INTO app_user_seq (next_val) VALUES (1);
INSERT INTO authenticator_seq (next_val) VALUES (1);
