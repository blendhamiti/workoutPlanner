-- Schema CREATE statement

CREATE DATABASE `workout_planner`;

-- Table CREATE statements 

CREATE TABLE `workout_planner`.`exercises` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `duration` int DEFAULT '0',
  `repetitions` int DEFAULT '0',
  `sets` int DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `index_name` (`name`)
);

CREATE TABLE `workout_planner`.`routines` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `index_name` (`name`)
);

CREATE TABLE `workout_planner`.`routine_exercises` (
  `routine_id` int NOT NULL,
  `exercise_id` int NOT NULL,
  KEY `routine_exercises_fk_exercise_idx` (`exercise_id`),
  KEY `routine_exercises_fk_routine_idx` (`routine_id`),
  CONSTRAINT `routine_exercises_fk_exercise` FOREIGN KEY (`exercise_id`) REFERENCES `exercises` (`id`),
  CONSTRAINT `routine_exercises_fk_routine` FOREIGN KEY (`routine_id`) REFERENCES `routines` (`id`)
);
