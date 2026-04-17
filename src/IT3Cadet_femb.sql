CREATE DATABASE IF NOT EXISTS IT3Cadet_femb
CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;
USE IT3Cadet_femb;

CREATE TABLE avatar(
  username VARCHAR(15) NOT NULL UNIQUE,
  word VARCHAR(15) NOT NULL DEFAULT 'apple',
  matches INT NOT NULL DEFAULT 0,
  rank VARCHAR(15) NOT NULL DEFAULT 'novice',
  
  PRIMARY KEY (username)
);

INSERT INTO avatar (username)
  VALUES ('brxcelo0016');
INSERT INTO avatar (username)
  VALUES ('ebuenga_kristel');
INSERT INTO avatar (username)
  VALUES ('dokitwo');
INSERT INTO avatar (username)
  VALUES ('whatmarvin');