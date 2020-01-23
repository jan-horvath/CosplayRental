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

CREATE TABLE STOREPRODUCTSTACKS (
  id      INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  name VARCHAR(100),
  size VARCHAR(5),
  price DOUBLE,
  stackSize INT
  );

CREATE TABLE ORDEREDPRODUCTSTACKS (
  id     INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  orderId INT REFERENCES orders (id) ON DELETE CASCADE,
  productStackId INT REFERENCES storeproductstacks (id) ON DELETE CASCADE,
  stackSize INT
);