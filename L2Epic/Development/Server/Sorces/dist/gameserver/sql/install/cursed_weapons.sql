-- ----------------------------
-- Table structure for `cursed_weapons`
-- ----------------------------
DROP TABLE IF EXISTS `cursed_weapons`;
CREATE TABLE `cursed_weapons` (
  `itemId` INT,
  `playerId` INT DEFAULT 0,
  `playerKarma` INT DEFAULT 0,
  `playerPkKills` INT DEFAULT 0,
  `nbKills` INT DEFAULT 0,
  `endTime` DECIMAL(20,0) DEFAULT 0,
  PRIMARY KEY (`itemId`)
) ;
