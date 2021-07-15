DROP DATABASE IF EXISTS invenhelper;
CREATE DATABASE IF NOT EXISTS invenhelper;
USE invenhelper;

CREATE TABLE `product` (
   `id` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
   `business_id` TEXT NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
   `name` TEXT NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
   `description` TEXT NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
   `quantity` INT(10) NOT NULL DEFAULT '0',
   PRIMARY KEY (`id`) USING BTREE
) COLLATE='utf8mb4_0900_ai_ci' ENGINE=InnoDB;

CREATE TABLE `user`
(
    `id`                   VARCHAR(255) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `username`             VARCHAR(255) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `name`                 VARCHAR(255) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `can_modify_inventory` INT(10) NOT NULL DEFAULT '0' COMMENT '0 if false, 1 if true',
    `can_modify_products`  INT(10) NOT NULL DEFAULT '0' COMMENT '0 if false, 1 if true',
    `can_add_users`        INT(10) NOT NULL DEFAULT '0' COMMENT '0 if false, 1 if true',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB
;

INSERT INTO user(id, username, name, can_modify_inventory, can_modify_products, can_add_users) VALUES ('30559387-ef67-45f9-b4a6-8f5f799955ad', 'admin', 'Administrador', 1, 1, 1)
;