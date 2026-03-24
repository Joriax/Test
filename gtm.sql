-- GTM MySQL Datenbank Setup
-- Import mit: mysql -u gtm -p gtm < gtm.sql
-- Oder in phpMyAdmin: Datenbank "gtm" auswählen -> SQL-Tab -> Datei importieren

CREATE DATABASE IF NOT EXISTS `gtm`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `gtm`;

-- --------------------------------------------------------
-- Spieler-Balances (Geld + Crowbars)
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
