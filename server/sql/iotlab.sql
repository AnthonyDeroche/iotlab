-- phpMyAdmin SQL Dump
-- version 3.4.10.1deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Aug 08, 2014 at 04:23 PM
-- Server version: 5.5.38
-- PHP Version: 5.3.10-1ubuntu3.13

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `iotlab`
--

-- --------------------------------------------------------

--
-- Table structure for table `Alert`
--

CREATE TABLE IF NOT EXISTS `Alert` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `monitoringRuleId` int(11) DEFAULT NULL,
  `value` double DEFAULT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `monitoringRuleId` (`monitoringRuleId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `Anchor`
--

CREATE TABLE IF NOT EXISTS `Anchor` (
  `mote_id` int(11) NOT NULL,
  PRIMARY KEY (`mote_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `CalibrationData`
--

CREATE TABLE IF NOT EXISTS `CalibrationData` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `measure_id` int(11) NOT NULL,
  `rssi` double NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `measure_id` (`measure_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `CalibrationMeasure`
--

CREATE TABLE IF NOT EXISTS `CalibrationMeasure` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `first` int(11) NOT NULL,
  `second` int(11) NOT NULL,
  `wallNumber` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `first_2` (`first`,`second`),
  KEY `second` (`second`),
  KEY `first` (`first`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=211 ;

-- --------------------------------------------------------

--
-- Table structure for table `Config`
--

CREATE TABLE IF NOT EXISTS `Config` (
  `parameter` varchar(100) NOT NULL,
  `value` varchar(100) NOT NULL,
  PRIMARY KEY (`parameter`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `Config`
--

INSERT INTO `Config` (`parameter`, `value`) VALUES
('mail_receiver', ''),
('mail_sender', ''),
('smtp_auth', 'true'),
('smtp_host', ''),
('smtp_password', ''),
('smtp_port', '465'),
('smtp_starttls', 'true'),
('smtp_username', ''),
('timeBetAlert', '30'),
('ws_host', '"ws://127.0.0.1:8080/pidr/liveStream/0"'),
('ws_host_default', '"ws://" + window.location.host + "/pidr/liveStream/0"');

-- --------------------------------------------------------

--
-- Table structure for table `Data`
--

CREATE TABLE IF NOT EXISTS `Data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mote_id` int(11) NOT NULL,
  `label_id` int(11) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `value` double NOT NULL,
  `experiment` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `mote_id` (`mote_id`),
  KEY `experiment` (`experiment`),
  KEY `label_id` (`label_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `Error`
--

CREATE TABLE IF NOT EXISTS `Error` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` datetime NOT NULL,
  `title` varchar(50) NOT NULL,
  `message` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `Experiment`
--

CREATE TABLE IF NOT EXISTS `Experiment` (
  `id` int(11) NOT NULL,
  `comments` text NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '0',
  `description` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `Experiment`
--

INSERT INTO `Experiment` (`id`, `comments`, `active`, `description`) VALUES
(1, 'test', 1, '');

-- --------------------------------------------------------

--
-- Table structure for table `Filter`
--

CREATE TABLE IF NOT EXISTS `Filter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `offset` int(11) NOT NULL,
  `strategy` int(11) NOT NULL DEFAULT '1',
  `label_id` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `offset` (`offset`,`type`),
  KEY `label_id` (`label_id`),
  KEY `strategy` (`strategy`),
  KEY `type` (`type`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=121 ;

--
-- Dumping data for table `Filter`
--

INSERT INTO `Filter` (`id`, `offset`, `strategy`, `label_id`, `type`) VALUES
(19, 3, 1, 2, 0),
(55, 30, 1, 29, 0),
(56, 31, 1, 29, 0),
(57, 1, 1, 1, 0),
(58, 2, 1, 33, 0),
(63, 32, 1, 29, 0),
(64, 1, 1, 1, 1),
(65, 2, 1, 33, 1),
(66, 3, 1, 6, 1),
(80, 7, 1, 6, 0),
(94, 18, 1, 17, 0),
(96, 4, 1, 3, 0),
(97, 5, 1, 4, 0),
(98, 6, 1, 5, 0),
(99, 8, 1, 7, 0),
(100, 9, 1, 8, 0),
(101, 10, 6, 9, 0),
(102, 11, 1, 10, 0),
(103, 12, 1, 11, 0),
(104, 13, 1, 12, 0),
(105, 14, 1, 13, 0),
(106, 15, 1, 14, 0),
(107, 16, 1, 15, 0),
(108, 17, 1, 16, 0),
(109, 19, 8, 18, 0),
(110, 20, 1, 19, 0),
(111, 21, 1, 20, 0),
(112, 22, 1, 21, 0),
(113, 23, 7, 22, 0),
(114, 24, 1, 23, 0),
(115, 25, 4, 24, 0),
(116, 26, 5, 25, 0),
(117, 27, 2, 26, 0),
(118, 28, 3, 27, 0),
(119, 29, 1, 28, 0),
(120, 4, 1, 34, 1);

-- --------------------------------------------------------

--
-- Table structure for table `GeolocationData`
--

CREATE TABLE IF NOT EXISTS `GeolocationData` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `src` int(11) NOT NULL,
  `dest` int(11) NOT NULL,
  `rssi` int(11) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `src` (`src`,`dest`),
  KEY `dest` (`dest`),
  KEY `src_2` (`src`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=5 ;

-- --------------------------------------------------------

--
-- Table structure for table `Label`
--

CREATE TABLE IF NOT EXISTS `Label` (
  `label_id` int(11) NOT NULL AUTO_INCREMENT,
  `label` varchar(20) NOT NULL,
  PRIMARY KEY (`label_id`),
  UNIQUE KEY `label` (`label`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=35 ;

--
-- Dumping data for table `Label`
--

INSERT INTO `Label` (`label_id`, `label`) VALUES
(23, 'battery_indicator'),
(22, 'battery_voltage'),
(21, 'beacon_interval'),
(17, 'best_neighbor'),
(18, 'best_neightbor_ext'),
(11, 'clock'),
(2, 'data_len'),
(10, 'data_len2'),
(32, 'data_nb'),
(33, 'data_type'),
(34, 'dodag_version_number'),
(8, 'hops'),
(27, 'humidity'),
(9, 'latency'),
(24, 'light1'),
(25, 'light2'),
(6, 'node_id'),
(20, 'num_neighbors'),
(19, 'rmetric'),
(28, 'rssi'),
(7, 'seq_no'),
(26, 'temperature'),
(1, 'timestamp'),
(3, 'timestamp1'),
(4, 'timestamp2'),
(12, 'timesynchtime'),
(5, 'timesynctimestamp'),
(13, 'time_cpu'),
(16, 'time_listen'),
(14, 'time_lpm'),
(15, 'time_transmit'),
(29, 'undefined'),
(30, 'undefined2'),
(31, 'undefined3');

-- --------------------------------------------------------

--
-- Table structure for table `Log`
--

CREATE TABLE IF NOT EXISTS `Log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `member_username` varchar(50) DEFAULT NULL,
  `module` varchar(20) NOT NULL,
  `message` text NOT NULL,
  `level` int(11) NOT NULL,
  `datetime` datetime NOT NULL,
  `ip` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `member_username` (`member_username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `Member`
--

CREATE TABLE IF NOT EXISTS `Member` (
  `username` varchar(30) NOT NULL,
  `password` varchar(50) NOT NULL,
  `email` varchar(80) NOT NULL,
  `admin` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `Member`
--

INSERT INTO `Member` (`username`, `password`, `email`, `admin`) VALUES
('admin', '4d20d11baabf9ca23c1eb9eef336f165f9431b2f', 'admin@localhost', 1),
('gateway', '53d69c73bcf1656b6e24012ebc6a64f1366bfbf5', 'gateway@localhost', 0);

-- --------------------------------------------------------

--
-- Table structure for table `MonitoringRule`
--

CREATE TABLE IF NOT EXISTS `MonitoringRule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mote_id` int(11) NOT NULL,
  `label_id` int(11) NOT NULL,
  `minVal` double DEFAULT NULL,
  `maxVal` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `mote_id` (`mote_id`),
  KEY `label_id` (`label_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `Mote`
--

CREATE TABLE IF NOT EXISTS `Mote` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mac` varchar(17) NOT NULL,
  `ipv6` varchar(39) NOT NULL,
  `lat` double NOT NULL,
  `lon` double NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `mac` (`mac`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `Sender`
--

CREATE TABLE IF NOT EXISTS `Sender` (
  `mote_id` int(11) NOT NULL,
  PRIMARY KEY (`mote_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Sensor`
--

CREATE TABLE IF NOT EXISTS `Sensor` (
  `id` int(11) NOT NULL,
  `mote_id` int(11) NOT NULL,
  `label_id` int(11) NOT NULL,
  `correction` float NOT NULL,
  `welded_to_mote` tinyint(1) NOT NULL,
  `lat` double NOT NULL,
  `lon` double NOT NULL,
  PRIMARY KEY (`id`),
  KEY `mote_id` (`mote_id`),
  KEY `label_id` (`label_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Sink`
--

CREATE TABLE IF NOT EXISTS `Sink` (
  `mote_id` int(11) NOT NULL,
  `dodagVersionNumber` int(11) NOT NULL,
  PRIMARY KEY (`mote_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Strategy`
--

CREATE TABLE IF NOT EXISTS `Strategy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `className` varchar(30) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=10 ;

--
-- Dumping data for table `Strategy`
--

INSERT INTO `Strategy` (`id`, `className`) VALUES
(1, 'DoubleDefaultStrategy'),
(2, 'TemperatureStrategy'),
(3, 'HumidityStrategy'),
(4, 'Light1Strategy'),
(5, 'Light2Strategy'),
(6, 'LatencyStrategy'),
(7, 'BatteryVoltageStrategy'),
(8, 'BestNeighborETXStrategy'),
(9, 'BatteryRemaining');

-- --------------------------------------------------------

--
-- Table structure for table `tinyint_asc`
--

CREATE TABLE IF NOT EXISTS `tinyint_asc` (
  `value` tinyint(3) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`value`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `tinyint_asc`
--

INSERT INTO `tinyint_asc` (`value`) VALUES
(0),
(1),
(2),
(3),
(4),
(5),
(6),
(7),
(8),
(9),
(10),
(11),
(12),
(13),
(14),
(15),
(16),
(17),
(18),
(19),
(20),
(21),
(22),
(23),
(24),
(25),
(26),
(27),
(28),
(29),
(30),
(31),
(32),
(33),
(34),
(35),
(36),
(37),
(38),
(39),
(40),
(41),
(42),
(43),
(44),
(45),
(46),
(47),
(48),
(49),
(50),
(51),
(52),
(53),
(54),
(55),
(56),
(57),
(58),
(59),
(60),
(61),
(62),
(63),
(64),
(65),
(66),
(67),
(68),
(69),
(70),
(71),
(72),
(73),
(74),
(75),
(76),
(77),
(78),
(79),
(80),
(81),
(82),
(83),
(84),
(85),
(86),
(87),
(88),
(89),
(90),
(91),
(92),
(93),
(94),
(95),
(96),
(97),
(98),
(99),
(100),
(101),
(102),
(103),
(104),
(105),
(106),
(107),
(108),
(109),
(110),
(111),
(112),
(113),
(114),
(115),
(116),
(117),
(118),
(119),
(120),
(121),
(122),
(123),
(124),
(125),
(126),
(127),
(128),
(129),
(130),
(131),
(132),
(133),
(134),
(135),
(136),
(137),
(138),
(139),
(140),
(141),
(142),
(143),
(144),
(145),
(146),
(147),
(148),
(149),
(150),
(151),
(152),
(153),
(154),
(155),
(156),
(157),
(158),
(159),
(160),
(161),
(162),
(163),
(164),
(165),
(166),
(167),
(168),
(169),
(170),
(171),
(172),
(173),
(174),
(175),
(176),
(177),
(178),
(179),
(180),
(181),
(182),
(183),
(184),
(185),
(186),
(187),
(188),
(189),
(190),
(191),
(192),
(193),
(194),
(195),
(196),
(197),
(198),
(199),
(200),
(201),
(202),
(203),
(204),
(205),
(206),
(207),
(208),
(209),
(210),
(211),
(212),
(213),
(214),
(215),
(216),
(217),
(218),
(219),
(220),
(221),
(222),
(223),
(224),
(225),
(226),
(227),
(228),
(229),
(230),
(231),
(232),
(233),
(234),
(235),
(236),
(237),
(238),
(239),
(240),
(241),
(242),
(243),
(244),
(245),
(246),
(247),
(248),
(249),
(250),
(251),
(252),
(253),
(254),
(255);

-- --------------------------------------------------------

--
-- Table structure for table `Type`
--

CREATE TABLE IF NOT EXISTS `Type` (
  `id` int(11) NOT NULL,
  `description` text NOT NULL,
  `streamName` varchar(20) NOT NULL,
  `minDataNumber` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `Type`
--

INSERT INTO `Type` (`id`, `description`, `streamName`, `minDataNumber`) VALUES
(0, 'Data', 'DataStream', 7),
(1, 'Sink', 'SinkStream', 4),
(10, 'Geolocation', 'GeolocStream', 4);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `Alert`
--
ALTER TABLE `Alert`
  ADD CONSTRAINT `Alert_ibfk_1` FOREIGN KEY (`monitoringRuleId`) REFERENCES `MonitoringRule` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `Anchor`
--
ALTER TABLE `Anchor`
  ADD CONSTRAINT `Anchor_ibfk_1` FOREIGN KEY (`mote_id`) REFERENCES `Mote` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `CalibrationData`
--
ALTER TABLE `CalibrationData`
  ADD CONSTRAINT `CalibrationData_ibfk_1` FOREIGN KEY (`measure_id`) REFERENCES `CalibrationMeasure` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `CalibrationMeasure`
--
ALTER TABLE `CalibrationMeasure`
  ADD CONSTRAINT `CalibrationMeasure_ibfk_1` FOREIGN KEY (`first`) REFERENCES `Anchor` (`mote_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `CalibrationMeasure_ibfk_2` FOREIGN KEY (`second`) REFERENCES `Anchor` (`mote_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Data`
--
ALTER TABLE `Data`
  ADD CONSTRAINT `Data_ibfk_1` FOREIGN KEY (`label_id`) REFERENCES `Label` (`label_id`),
  ADD CONSTRAINT `Data_ibfk_2` FOREIGN KEY (`experiment`) REFERENCES `Experiment` (`id`),
  ADD CONSTRAINT `Data_ibfk_4` FOREIGN KEY (`mote_id`) REFERENCES `Mote` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Filter`
--
ALTER TABLE `Filter`
  ADD CONSTRAINT `Filter_ibfk_4` FOREIGN KEY (`strategy`) REFERENCES `Strategy` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `Filter_ibfk_5` FOREIGN KEY (`label_id`) REFERENCES `Label` (`label_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `Filter_ibfk_6` FOREIGN KEY (`type`) REFERENCES `Type` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `GeolocationData`
--
ALTER TABLE `GeolocationData`
  ADD CONSTRAINT `GeolocationData_ibfk_3` FOREIGN KEY (`src`) REFERENCES `Mote` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `GeolocationData_ibfk_4` FOREIGN KEY (`dest`) REFERENCES `Mote` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Log`
--
ALTER TABLE `Log`
  ADD CONSTRAINT `Log_ibfk_1` FOREIGN KEY (`member_username`) REFERENCES `Member` (`username`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `MonitoringRule`
--
ALTER TABLE `MonitoringRule`
  ADD CONSTRAINT `MonitoringRule_ibfk_1` FOREIGN KEY (`mote_id`) REFERENCES `Mote` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `MonitoringRule_ibfk_2` FOREIGN KEY (`label_id`) REFERENCES `Label` (`label_id`) ON DELETE CASCADE;

--
-- Constraints for table `Sender`
--
ALTER TABLE `Sender`
  ADD CONSTRAINT `Sender_ibfk_1` FOREIGN KEY (`mote_id`) REFERENCES `Mote` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Sink`
--
ALTER TABLE `Sink`
  ADD CONSTRAINT `Sink_ibfk_1` FOREIGN KEY (`mote_id`) REFERENCES `Mote` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
