CREATE TABLE IF NOT EXISTS `managerConfig` (
  `name` varchar(255) NOT NULL,
  `value` varchar(500) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8;