CREATE DATABASE `twista` /*!40100 DEFAULT CHARACTER SET utf8 */;

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(100) NOT NULL,
  `login` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `profilePicPath` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `login_UNIQUE` (`login`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COMMENT='User table for twista app';

CREATE TABLE `friends` (
  `from_id` int(11) NOT NULL,
  `to_id` int(11) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`from_id`,`to_id`),
  KEY `user_id_idx` (`to_id`),
  CONSTRAINT `from_id` FOREIGN KEY (`from_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `to_id` FOREIGN KEY (`to_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Friends table for twista app';


CREATE TABLE `connection` (
  `connectionKey` varchar(32) NOT NULL,
  `id` int(11) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `root` tinyint(4) NOT NULL,
  PRIMARY KEY (`connectionKey`),
  KEY `id_idx` (`id`),
  KEY `connectionKey` (`connectionKey`,`id`,`timestamp`),
  CONSTRAINT `id` FOREIGN KEY (`id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
