USE workstocks_java;

CREATE USER if not exists 'workstocks_user'@'localhost' IDENTIFIED BY 'workstocks_password';
GRANT ALL ON workstocks_java.* TO 'workstocks_user'@'localhost';
