-- test-data.sql

INSERT INTO ORDERS
(EMAIL, FULLNAME, PHONENUMBER, RETURNDATE)
VALUES
('weaponreplica@enthusiast.org', 'No Name', '+658291912994', '2019-12-20'),
('fred.kirby@gmail.org', 'Fred Kirby', '+929123456994', '2019-12-21'),
('marc.blake@batmanfan.org', 'Marc Blake', '+444291912994', '2019-12-22');

INSERT INTO STOREPRODUCTSTACKS
(NAME, SIZE, PRICE, STACKSIZE)
VALUES
('Asterix helmet', 'NA', 15.80, 3),
('Poseidon trident', 'NA', 21.90, 3),
('Deadpool suit', 'M', 42.20, 4),
('Witcher silver sword', 'NA', 29, 0),
('Portal gun', 'NA', 42, 1),
('BFG9000', 'NA', 65, 0),
('Ironman suit', 'L', 120, 0),
('Captain America suit', 'L', 109, 0),
('Batman suit', 'S', 100, 0),
('Batarang set', 'NA', 25, 10);

INSERT INTO ORDEREDPRODUCTSTACKS
(ORDERID, STOREID, NAME, SIZE, PRICE, STACKSIZE) VALUES
 (1, 4, 'Witcher silver sword', 'NA', 29, 3),
 (1, 5, 'Portal gun', 'NA', 42, 2),
 (1, 6, 'BFG9000', 'NA', 65, 1),
 (2, 7, 'Ironman suit', 'L', 120, 1),
 (2, 8, 'Captain America suit', 'L', 109, 1),
 (3, 9, 'Batman suit', 'S', 100, 1),
 (3, 10, 'Batarang set', 'NA', 25, 1);