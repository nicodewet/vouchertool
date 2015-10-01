-- SHOW GRANTS FOR 'vouchservuser'@'localhost';
-- SHOW GRANTS FOR 'vouchservuser'@'%';
DROP USER 'vouchservuser'@'localhost';
DROP USER 'vouchservuser'@'%';
CREATE USER 'vouchservuser'@'localhost' IDENTIFIED BY 'smartchristiaankoln';
CREATE USER 'vouchservuser'@'%' IDENTIFIED BY 'smartchristiaankoln';
GRANT SELECT,INSERT,UPDATE ON vouchserv.* TO 'vouchservuser'@'localhost';
GRANT SELECT,INSERT,UPDATE ON vouchserv.* TO 'vouchservuser'@'%';