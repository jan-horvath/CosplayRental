-- schema-javadb.sql
-- DDL commands for JavaDB/Derby
CREATE TABLE RentOrder (
  id       INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  email   VARCHAR(50),
  fullname VARCHAR(50),
  phonenumber VARCHAR(30),
  returndate DATE
);

CREATE TABLE StoreProductStack (
  id      INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  name VARCHAR(100),
  size VARCHAR(5),
  price DOUBLE,
  stackSize INT
);

CREATE TABLE OrderedProductStack (
  id     INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  orderId INT REFERENCES RentOrder (id) ON DELETE CASCADE,
  storeId INT REFERENCES StoreProductStack (id),
  stackSize INT
);