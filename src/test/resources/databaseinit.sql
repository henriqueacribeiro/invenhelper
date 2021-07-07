SET MODE MYSQL;
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
	`id` VARCHAR(255) NOT NULL,
	`business_id` TEXT NOT NULL ,
	`name` TEXT NOT NULL ,
	`description` TEXT NOT NULL ,
	`quantity` INT(10) NOT NULL DEFAULT '0',
	PRIMARY KEY (`id`)
)