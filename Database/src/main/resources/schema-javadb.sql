-- schema-javadb.sql
-- DDL commands for JavaDB/Derby
CREATE TABLE ORDERS (
  id       INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  email   VARCHAR(50),
  creditcardnumber VARCHAR(20),
  fullname VARCHAR(50),
  phonenumber VARCHAR(30),
  returndate DATE
);

CREATE TABLE PRODUCTSTACKS (
  id     INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  orderid INT REFERENCES orders ON DELETE CASCADE,
  name VARCHAR(100),
  size VARCHAR(5),
  price DOUBLE,
  stackSize INT
);