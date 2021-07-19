SET MODE MYSQL;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `user`;

CREATE TABLE `product` (
	`id` VARCHAR(255) NOT NULL,
	`business_id` TEXT NOT NULL ,
	`name` TEXT NOT NULL ,
	`description` TEXT NOT NULL ,
	`quantity` INT(10) NOT NULL DEFAULT '0',
	PRIMARY KEY (`id`)
);

CREATE TABLE `user`
(
    `id`                   VARCHAR(255) NOT NULL,
    `username`             VARCHAR(255) NOT NULL,
    `name`                 VARCHAR(255) NOT NULL,
    `can_modify_inventory` INT(10) NOT NULL DEFAULT '0' COMMENT '0 if false, 1 if true',
    `can_modify_products`  INT(10) NOT NULL DEFAULT '0' COMMENT '0 if false, 1 if true',
    `can_add_users`        INT(10) NOT NULL DEFAULT '0' COMMENT '0 if false, 1 if true',
    PRIMARY KEY (`id`)
);

INSERT INTO `user`(id, username, name, can_modify_inventory, can_modify_products, can_add_users) VALUES ('30559387-ef67-45f9-b4a6-8f5f799955ad', 'admin', 'Administrador', 1, 1, 1);