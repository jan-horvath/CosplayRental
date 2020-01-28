-- schema-javadb.sql
-- DDL commands for JavaDB/Derby
CREATE TABLE Orders (
  id       INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  email   VARCHAR(50),
  fullname VARCHAR(50),
  phonenumber VARCHAR(30),
  returndate DATE
);

CREATE TABLE StoreProductStacks (
  id      INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  name VARCHAR(100),
  size VARCHAR(5),
  price DOUBLE,
  stackSize INT
);

CREATE TABLE OrderedProductStacks (
  id     INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  orderId INT REFERENCES Orders (id) ON DELETE CASCADE,
  storeId INT REFERENCES StoreProductStacks (id),
  stackSize INT
);