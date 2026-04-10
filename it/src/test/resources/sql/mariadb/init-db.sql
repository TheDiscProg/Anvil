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
    model_id UUID NOT NULL,
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
    user_id UUID NOT NULL DEFAULT UUID_v7(),
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
    id             BIGINT NOT NULL AUTO_INCREMENT,
    customer_id    BIGINT REFERENCES customer (customer_id),
    model_id UUID NOT NULL REFERENCES car_models (model_id),
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

CREATE TABLE mariadb_all_types
(
    t_id         BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    -- Integer types
    t_tinyint    TINYINT,
    t_smallint   SMALLINT,
    t_mediumint  MEDIUMINT,
    t_int        INT,
    t_bigint     BIGINT,

    -- Floating point / fixed point
    t_float      FLOAT,
    t_double     DOUBLE,
    t_decimal    DECIMAL(10, 2),

    -- Boolean (alias of TINYINT)
    t_boolean    BOOLEAN,

    -- Bit
    t_bit        BIT(8),

    -- Date and time
    t_date       DATE,
    t_datetime   DATETIME,
    t_timestamp  TIMESTAMP,
    t_time       TIME,
    t_year       YEAR,

    -- Character / string
    t_char       CHAR(10),
    t_varchar    VARCHAR(255),
    t_text       TEXT,
    t_tinytext   TINYTEXT,
    t_mediumtext MEDIUMTEXT,
    t_longtext   LONGTEXT,

    -- Binary / blob
    t_binary     BINARY(10),
    t_varbinary  VARBINARY(255),
    t_blob       BLOB,
    t_tinyblob   TINYBLOB,
    t_mediumblob MEDIUMBLOB,
    t_longblob   LONGBLOB,

    -- Enum and Set
    t_enum       ENUM ('small', 'medium', 'large'),
    t_set        SET ('a', 'b', 'c'),

    -- JSON (alias for LONGTEXT in MariaDB)
    t_json       JSON,

    -- Spatial (requires InnoDB with spatial support)
    t_point      POINT,

    -- UUID (stored as CHAR)
    t_uuid       CHAR(36)
);

INSERT INTO mariadb_all_types (t_tinyint,
                               t_smallint,
                               t_mediumint,
                               t_int,
                               t_bigint,
                               t_float,
                               t_double,
                               t_decimal,
                               t_boolean,
                               t_bit,
                               t_date,
                               t_datetime,
                               t_timestamp,
                               t_time,
                               t_year,
                               t_char,
                               t_varchar,
                               t_text,
                               t_tinytext,
                               t_mediumtext,
                               t_longtext,
                               t_binary,
                               t_varbinary,
                               t_blob,
                               t_tinyblob,
                               t_mediumblob,
                               t_longblob,
                               t_enum,
                               t_set,
                               t_json,
                               t_point,
                               t_uuid)
VALUES (
           -- Integers
           1,
           100,
           10000,
           1000000,
           12345678901,

           -- Floating
           3.14,
           2.718281828,
           12345.67,

           -- Boolean
           TRUE,

           -- Bit
           b'10101010',

           -- Date/time
           '2026-01-01',
           '2026-01-01 12:34:56',
           CURRENT_TIMESTAMP,
           '12:34:56',
           2026,

           -- Strings
           'char_test',
           'varchar test string',
           'This is TEXT',
           'tiny text',
           'medium text example',
           'long text example',

           -- Binary
           'binary123',
           'varbinary123',
           'blob data',
           'tiny blob',
           'medium blob',
           'long blob',

           -- Enum / Set
           'medium',
           'a,b',

           -- JSON
           '{
             "key": "value",
             "number": 123
           }',

           -- Spatial
           ST_GeomFromText('POINT(1 1)'),

           -- UUID
           '550e8400-e29b-41d4-a716-446655440000');

