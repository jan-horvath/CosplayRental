-- test-data.sql

INSERT INTO ORDERS
(EMAIL, CREDITCARDNUMBER, FULLNAME, PHONENUMBER, RETURNDATE)
VALUES
('weaponreplica@enthusiast.org', '9184345167789991', 'No Name', '+658291912994', '2019-12-20'),
('fred.kirby@gmail.org', '9184345161019991', 'Fred Kirby', '+929123456994', '2019-12-21'),
('marc.blake@batmanfan.org', '4116852067789991', 'Marc Blake', '+444291912994', '2019-12-22');

INSERT INTO PRODUCTSTACKS
(ORDERID, NAME, SIZE, PRICE, STACKSIZE)
VALUES
(NULL, 'Asterix helmet', 'NA', 15.80, 3),
(NULL, 'Ironman suit', 'L', 120, 0),
(NULL, 'Batarang set', 'NA', 25, 10),
(1, 'Witcher silver sword', 'NA', 29, 3),
(1, 'Portal gun', 'NA', 42, 2),
(2, 'Ironman suit', 'L', 120, 1),
(2, 'Captain America suit', 'L', 109, 1),
(3, 'Batman suit', 'S', 100, 1),
(3, 'Batarang set', 'NA', 25, 2);

