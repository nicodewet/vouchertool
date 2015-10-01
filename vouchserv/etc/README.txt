	
	== PostgreSQL ==
	
	TODO

	== MySQL ==
	
	For MySQL integration testing, e.g. on localhost, you'll need to install MySQL 

	Ref: http://dev.mysql.com/doc/refman/5.1/en/adding-users.html

	= SETUP (DEV ONLY SINCE SUPER USER ACCOUNTS) =
	
	CREATE DATABASE vouchserv;
	CREATE USER 'vouchserv'@'localhost' IDENTIFIED BY 'vouchserv';
	GRANT ALL PRIVILEGES ON *.* TO 'vouchserv'@'localhost' WITH GRANT OPTION;
	CREATE USER 'vouchserv'@'%' IDENTIFIED BY 'vouchserv';
    GRANT ALL PRIVILEGES ON *.* TO 'vouchserv'@'%' WITH GRANT OPTION;
    
	= GENERAL =
	
	SHOW DATABASES;
	SHOW GRANTS FOR 'vouchservuser'@'localhost';
	SHOW GRANTS FOR 'vouchservuser'@'%';
	show databases;
	show tables in vouchserv;
	show columns from VOUCHER in vouchserv;