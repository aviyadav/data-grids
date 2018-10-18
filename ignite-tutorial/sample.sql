CREATE USER 'ignite_user'@'localhost' IDENTIFIED BY 'password';

GRANT ALL PRIVILEGES ON *.* TO 'ignite_user'@'localhost' WITH GRANT OPTION;





-- CREATE USER 'ignite_user'@'%' IDENTIFIED BY 'password';
-- GRANT ALL PRIVILEGES ON *.* TO 'ignite_user'@'%' WITH GRANT OPTION;
-- CREATE USER 'admin'@'localhost' IDENTIFIED BY 'password';
-- GRANT RELOAD,PROCESS ON *.* TO 'admin'@'localhost';
-- CREATE USER 'dummy'@'localhost';



CREATE TABLE orders ( order_number INT NOT NULL primary key, order_type VARCHAR(30) NOT NULL , order_fulfillment_date DATE NOT NULL ) ENGINE=INNODB;
CREATE TABLE order_line( order_number INT not NULL , order_line_number INT NOT NULL , item_name VARCHAR(30) NOT NULL , item_qty INT NOT null ) ENGINE=INNODB;
ALTER TABLE order_line ADD CONSTRAINT order_num_fk FOREIGN KEY (order_number) REFERENCES orders(order_number) 
