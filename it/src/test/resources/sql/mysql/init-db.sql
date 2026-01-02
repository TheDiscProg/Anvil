CREATE TABLE customer
(
    customer_id    BIGINT       NOT NULL AUTO_INCREMENT,
    title          VARCHAR(5)   NOT NULL,
    first_name     VARCHAR(50)  NOT NULL,
    surname        VARCHAR(50)  NOT NULL,
    contact_email  VARCHAR(150) NOT NULL UNIQUE,
    contact_mobile VARCHAR(20)  NOT NULL UNIQUE,
    date_joined    DATE         NOT NULL DEFAULT (CURRENT_DATE),
    comment        TEXT,
    PRIMARY KEY (customer_id)
);

CREATE TABLE address
(
    address_id BIGINT       NOT NULL,
    street     VARCHAR(200) NOT NULL,
    town       VARCHAR(50),
    county     VARCHAR(50)  NOT NULL,
    post_code  VARCHAR(15)  NOT NULL,
    country    VARCHAR(50)  NOT NULL,
    PRIMARY KEY (address_id)
);

CREATE TABLE customer_address
(
    customer_id BIGINT NOT NULL REFERENCES customer (customer_id),
    address_id  BIGINT NOT NULL REFERENCES address (address_id),
    comment     TEXT,
    PRIMARY KEY (customer_id, address_id),
    UNIQUE (customer_id, address_id)
);

CREATE TABLE car_models
(
    model_id            BINARY(16)  NOT NULL,
    manufacturer        VARCHAR(20) NOT NULL,
    model               VARCHAR(20) NOT NULL,
    engine_description  VARCHAR(50) NOT NULL,
    date_of_manufacture DATE        NOT NULL,
    registration        VARCHAR(15),
    PRIMARY KEY (model_id),
    UNIQUE (manufacturer, model, date_of_manufacture)
);

CREATE TABLE user_type
(
    type_key    VARCHAR(10) PRIMARY KEY,
    title       VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(200)
);

CREATE TABLE users
(
    user_id     BINARY(16)          NOT NULL DEFAULT (UUID_TO_BIN(UUID())),
    user_name   VARCHAR(150) UNIQUE NOT NULL,
    email       VARCHAR(150) UNIQUE NOT NULL,
    is_active   BOOlEAN             NOT NULL DEFAULT TRUE,
    is_expired  BOOlEAN             NOT NULL DEFAULT FALSE,
    last_login  TIMESTAMP,
    customer_id BIGINT              NOT NULL REFERENCES customer (customer_id),
    user_type   VARCHAR(20)         NOT NULL REFERENCES user_type (type_key),
    PRIMARY KEY (user_id)
);

CREATE TABLE user_cars
(
    id             BIGINT     NOT NULL AUTO_INCREMENT,
    customer_id    BIGINT REFERENCES customer (customer_id),
    model_id       BINARY(16) NOT NULL REFERENCES car_models (model_id),
    date_purchased DATE,
    service_date   DATE,
    comments       TEXT,
    PRIMARY KEY (id)
);

INSERT INTO user_type (type_key, title, description)
VALUES ('ADM', 'Admin', 'System Administrator'),
       ('SLF', 'Sales', 'Sales staff'),
       ('ENG', 'Engineering', 'Engineering staff'),
       ('CSP', 'Customer Support', 'Customer Support'),
       ('CST', 'Customer', 'Customer');

INSERT INTO car_models (model_id, manufacturer, model, engine_description, date_of_manufacture, registration)
VALUES (UNHEX(REPLACE('f7b4f080-ba57-46e7-8bd7-22f85d011bf6', '-', '')), 'BMW', 'Series 1', 'Coupe', '2013-12-13',
        NULL),
       (UNHEX(REPLACE('c483a731-6970-41a1-9354-29e23c132667', '-', '')), 'FIAT', '124 Spider', 'Coupe', '2019-06-25',
        NULL),
       (UNHEX(REPLACE('fb7b56b7-4fb8-4fd0-a561-74870d31d0d2', '-', '')), 'Chevrolet', '1500 Extended Cab', 'Pickup',
        '2021-10-15', NULL),
       (UNHEX(REPLACE('87dd82a8-31fa-41ac-bc6c-c4eec8b6386f', '-', '')), 'Nissan', '240SX', 'Coupe', '1998-11-16',
        NULL);

INSERT INTO customer (title, first_name, surname, contact_email, contact_mobile, date_joined, comment)
VALUES ('Mr', 'John', 'Smith', 'jsmith@gmail.com', '07777 123 456', '2025-05-13', 'Very important customer'),
       ('Mrs', 'Jo', 'Jackson', 'jj@gmail.com', '07777 222 333', '2024-06-10', NULL),
       ('Miss', 'Mary', 'McCackelsfield', 'mmac@test.com', '07776 444 555', '2025-07-29', 'Has more than one car');

INSERT INTO address (address_id, street, town, county, post_code, country)
VALUES (1, '1 The Road', 'Atown', 'Middlesex', 'AT1 0AA', 'United Kingdom'),
       (2, '34 The Street', 'BTown', 'Somerset', 'SS1 9BB', 'United Kingdom'),
       (3, 'The House on the Hill', 'CTown', 'Devon', 'DA1 0VN', 'United Kingdom');

INSERT INTO customer_address (customer_id, address_id, comment)
VALUES (1, 1, 'Moved to this address in 2022'),
       (2, 2, 'Lived here for a very long time'),
       (3, 3, ' Nice view');

INSERT INTO users (user_name, email, customer_id, user_type)
VALUES ('JohnSmith', 'jsmith@gmail.com', 1, 'CST'),
       ('JoJackson', 'jj@gmail.com', 2, 'CST'),
       ('MaryM', 'mmac@test.com', 3, 'CST');

INSERT INTO user_cars (customer_id, model_id, date_purchased, service_date, comments)
VALUES (2, UNHEX(REPLACE('c483a731-6970-41a1-9354-29e23c132667', '-', '')), '2020-04-13', NULL, NULL);


CREATE TABLE mysql_all_types
(
    -- Numeric types
    col_tinyint    TINYINT,
    col_smallint   SMALLINT,
    col_mediumint  MEDIUMINT,
    col_int        INT,
    col_bigint     BIGINT,
    col_decimal    DECIMAL(10, 2),
    col_numeric    NUMERIC(10, 2),
    col_float      FLOAT,
    col_double     DOUBLE,
    col_bit        BIT(8),
    col_boolean    BOOLEAN,
    -- Date and time types
    col_date       DATE,
    col_time       TIME,
    col_datetime   DATETIME,
    col_timestamp  TIMESTAMP,
    col_year       YEAR,
    -- Character / string types
    col_char       CHAR(10),
    col_varchar    VARCHAR(255),
    col_tinytext   TINYTEXT,
    col_text       TEXT,
    col_mediumtext MEDIUMTEXT,
    col_longtext   LONGTEXT,
    -- Binary types
    col_binary     BINARY(8),
    col_varbinary  VARBINARY(255),
    col_tinyblob   TINYBLOB,
    col_blob       BLOB,
    col_mediumblob MEDIUMBLOB,
    col_longblob   LONGBLOB,
    -- ENUM and SET
    col_enum       ENUM ('RED', 'GREEN', 'BLUE'),
    col_set        SET ('A', 'B', 'C'),
    -- JSON (MySQL 5.7+)
    col_json       JSON,
    -- Spatial types (require SRID support in MySQL 8+)
    col_point      POINT,
    col_linestring LINESTRING,
    col_polygon    POLYGON,
    -- Auto-increment / primary key
    col_id         BIGINT AUTO_INCREMENT PRIMARY KEY
);

INSERT INTO mysql_all_types (col_tinyint,
                             col_smallint,
                             col_mediumint,
                             col_int,
                             col_bigint,
                             col_decimal,
                             col_numeric,
                             col_float,
                             col_double,
                             col_bit,
                             col_boolean,
                             col_date,
                             col_time,
                             col_datetime,
                             col_timestamp,
                             col_year,
                             col_char,
                             col_varchar,
                             col_tinytext,
                             col_text,
                             col_mediumtext,
                             col_longtext,
                             col_binary,
                             col_varbinary,
                             col_tinyblob,
                             col_blob,
                             col_mediumblob,
                             col_longblob,
                             col_enum,
                             col_set,
                             col_json,
                             col_point,
                             col_linestring,
                             col_polygon)
VALUES (
           -- Numeric
           1,
           123,
           12345,
           123456,
           1234567890123,
           1234.56,
           7890.12,
           3.14,
           2.718281828,
           b'10101010',
           TRUE,
           -- Date & time
           '2025-01-01',
           '12:34:56',
           '2025-01-01 12:34:56',
           CURRENT_TIMESTAMP,
           2025,
           -- Strings
           'CHAR_VAL',
           'VARCHAR value',
           'Tiny text',
           'Regular text',
           'Medium text example',
           'Long text example',
           -- Binary
           x'0102030405060708',
           x'0A0B0C',
           x'01',
           x'0203',
           x'040506',
           x'0708090A',
           -- ENUM / SET
           'GREEN',
           'A,B',
           -- JSON
           JSON_OBJECT('key1', 'value1', 'key2', 123),
           -- Spatial
           ST_GeomFromText('POINT(10 20)'),
           ST_GeomFromText('LINESTRING(0 0, 10 10, 20 25)'),
           ST_GeomFromText('POLYGON((0 0, 10 0, 10 10, 0 10, 0 0))'));
