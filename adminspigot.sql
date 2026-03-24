-- AdminSpigot MySQL Datenbank Setup
-- Import mit: mysql -u adminspigot -p adminspigot < adminspigot.sql
-- Oder in phpMyAdmin: Datenbank "adminspigot" auswählen -> SQL-Tab -> Datei importieren

CREATE DATABASE IF NOT EXISTS `adminspigot`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `adminspigot`;

-- --------------------------------------------------------
-- Spieler-Spielzeit (in Sekunden)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `player_playtime` (
  `player_uuid`    VARCHAR(36) NOT NULL,
  `total_playtime` BIGINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (`player_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
