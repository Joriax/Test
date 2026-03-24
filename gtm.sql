-- GTM MySQL Datenbank Setup
-- Import mit: mysql -u root -p < gtm.sql

CREATE DATABASE IF NOT EXISTS `gtm`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `gtm`;

-- --------------------------------------------------------
-- Spieler-Balances (Economy: Geld + Crowbars)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `player_balances` (
  `uuid`     VARCHAR(36) NOT NULL,
  `username` VARCHAR(16) NOT NULL DEFAULT '',
  `balance`  DOUBLE      NOT NULL DEFAULT 0.0,
  `crowbars` INT         NOT NULL DEFAULT 0,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- Spieler-Level (XP & Level)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `player_levels` (
  `uuid`     VARCHAR(36) NOT NULL,
  `username` VARCHAR(16) NOT NULL DEFAULT '',
  `xp`       INT         NOT NULL DEFAULT 0,
  `level`    INT         NOT NULL DEFAULT 1,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- Spieler-Spielzeit (Playtime in Sekunden)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `player_playtime` (
  `player_uuid`    VARCHAR(36) NOT NULL,
  `total_playtime` BIGINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (`player_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
