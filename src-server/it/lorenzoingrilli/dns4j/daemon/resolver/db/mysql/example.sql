DELETE FROM soa WHERE name LIKE '%.example.net'
DELETE FROM soa WHERE name LIKE 'example.net'
DELETE FROM a WHERE name LIKE '%.example.net'
DELETE FROM a WHERE name LIKE 'example.net'
DELETE FROM aaaa WHERE name LIKE '%.example.net'
DELETE FROM aaaa WHERE name LIKE 'example.net'
DELETE FROM ns WHERE name LIKE '%.example.net'
DELETE FROM ns WHERE name LIKE 'example.net'
DELETE FROM cname WHERE name LIKE '%.example.net'
DELETE FROM cname WHERE name LIKE 'example.net'
DELETE FROM hinfo WHERE name LIKE '%.example.net'
DELETE FROM hinfo WHERE name LIKE 'example.net'
DELETE FROM txt WHERE name LIKE '%.example.net'
DELETE FROM txt WHERE name LIKE 'example.net'
DELETE FROM mx WHERE name LIKE '%.example.net'
DELETE FROM mx WHERE name LIKE 'example.net'
DELETE FROM rr WHERE name LIKE '%.example.net'
DELETE FROM rr WHERE name LIKE 'example.net'
DELETE FROM ptr WHERE name LIKE '%.example.net'
DELETE FROM ptr WHERE name LIKE 'example.net'
INSERT INTO zone(name) VALUES('example.net')
INSERT INTO soa(name, ttl, refresh, serial, retry, expire, minimum, email) VALUES('example.net', 100, 200, 300, 400, 500, 600, 'test@example.net')
INSERT INTO ns(name, ttl, host) VALUES('example.net', 120, 'ns1.example.net')
INSERT INTO a(name, ttl, address) VALUES('ns1.example.net', 200, '127.0.0.2')
INSERT INTO ns(name, ttl, host) VALUES('example.net', 120, 'ns2.example.net')
INSERT INTO a(name, ttl, address) VALUES('ns2.example.net', 200, '127.0.0.3')
INSERT INTO a(name, ttl, address) VALUES('example.net', 200, '127.0.0.1')
INSERT INTO aaaa(name, ttl, address) VALUES('example.net', 300, '::1')
INSERT INTO cname(name, ttl, host) VALUES('www.example.net', 'example.net')
INSERT INTO ptr(name, ttl, host) VALUES('127.0.0.7', 'myhost.example.net')
