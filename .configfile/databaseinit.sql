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