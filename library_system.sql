/*
 Navicat Premium Data Transfer

 Source Server         : 118.25.99.38
 Source Server Type    : MySQL
 Source Server Version : 50736
 Source Host           : 118.25.99.38:3306
 Source Schema         : library_system

 Target Server Type    : MySQL
 Target Server Version : 50736
 File Encoding         : 65001

 Date: 22/11/2024 15:07:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for books
-- ----------------------------
DROP TABLE IF EXISTS `books`;
CREATE TABLE `books`  (
  `book_id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `author` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `publication_date` date DEFAULT NULL,
  `isbn` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `location` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `added_date` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`book_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of books
-- ----------------------------
INSERT INTO `books` VALUES (1, '红楼梦', '曹雪芹', '1791-01-01', '9787505715660', 10, '第1排，A架', '2024-11-22 10:20:35');
INSERT INTO `books` VALUES (2, '西游记', '吴承恩', '1592-01-01', '9787101003068', 8, '第1排，B架', '2024-11-22 10:20:35');
INSERT INTO `books` VALUES (3, '水浒传', '施耐庵', '1589-01-01', '9787101003051', 7, '第2排，A架', '2024-11-22 10:20:35');
INSERT INTO `books` VALUES (4, '三国演义', '罗贯中', '1522-01-01', '9787101003044', 5, '第2排，B架', '2024-11-22 10:20:35');
INSERT INTO `books` VALUES (6, 'c语言', 'ls', '2021-03-04', 'abec', 30, '图书馆西楼1', '2024-11-22 11:07:04');

-- ----------------------------
-- Table structure for borrow_records
-- ----------------------------
DROP TABLE IF EXISTS `borrow_records`;
CREATE TABLE `borrow_records`  (
  `borrow_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `book_id` int(11) NOT NULL,
  `borrow_date` date NOT NULL,
  `return_date` date DEFAULT NULL,
  PRIMARY KEY (`borrow_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for borrowrecords
-- ----------------------------
DROP TABLE IF EXISTS `borrowrecords`;
CREATE TABLE `borrowrecords`  (
  `borrow_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `book_id` int(11) DEFAULT NULL,
  `borrow_date` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `due_date` date DEFAULT NULL,
  `return_date` timestamp(0) DEFAULT NULL,
  PRIMARY KEY (`borrow_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `book_id`(`book_id`) USING BTREE,
  CONSTRAINT `borrowrecords_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `borrowrecords_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`book_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for returnrecords
-- ----------------------------
DROP TABLE IF EXISTS `returnrecords`;
CREATE TABLE `returnrecords`  (
  `return_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `book_id` int(11) DEFAULT NULL,
  `return_date` timestamp(0) DEFAULT NULL,
  PRIMARY KEY (`return_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `book_id`(`book_id`) USING BTREE,
  CONSTRAINT `returnrecords_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `returnrecords_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`book_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `password` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `gender` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `phone` varchar(15) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `role` enum('admin','user') CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT 'user',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '1234567', '男', '1234567890', 'admin@example.com', 'admin');
INSERT INTO `users` VALUES (2, 'John', 'password123', '女', '13800138000', 'johndoe@example.com', 'user');
INSERT INTO `users` VALUES (3, 'Jane', 'password456', '女', '13800138001', 'janedoe@example.com', 'user');
INSERT INTO `users` VALUES (4, 'abc', '123456', '男', '114566', '1@qq.com', 'admin');

SET FOREIGN_KEY_CHECKS = 1;
