CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS citext;
CREATE EXTENSION IF NOT EXISTS ltree;

CREATE TABLE customer (
    customer_id BIGSERIAL NOT NULL,
    title VARCHAR(5) NOT NULL,
    first_name VARCHAR NOT NULL,
    surname VARCHAR NOT NULL,
    contact_email VARCHAR(150) NOT NULL UNIQUE,
    contact_mobile VARCHAR(20)  NOT NULL UNIQUE,
    date_joined DATE NOT NULL DEFAULT CURRENT_DATE,
    comment TEXT,
    PRIMARY KEY(customer_id)
);

CREATE TABLE address(
    address_id BIGINT NOT NULL,
    street VARCHAR(200) NOT NULL,
    town VARCHAR(50),
    county VARCHAR(50) NOT NULL,
    post_code VARCHAR(15) NOT NULL,
    country VARCHAR(50) NOT NULL,
    PRIMARY KEY (address_id)
);

CREATE TABLE customer_address(
    customer_id BIGINT NOT NULL REFERENCES customer(customer_id),
    address_id BIGINT NOT NULL REFERENCES address(address_id),
    comment TEXT,
    PRIMARY KEY(customer_id, address_id),
    UNIQUE(customer_id, address_id)
);

CREATE TABLE car_models(
    model_id UUID NOT NULL,
    manufacturer VARCHAR(20) NOT NULL,
    model VARCHAR(20) NOT NULL,
    engine_description VARCHAR(50) NOT NULL,
    date_of_manufacture DATE NOT NULL,
    registration VARCHAR(15),
    PRIMARY KEY (model_id),
    UNIQUE(manufacturer, model, date_of_manufacture)
);

CREATE TABLE user_type (
    type_key VARCHAR(10) PRIMARY KEY,
    title VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(200)
);

CREATE TABLE users(
    user_id UUID NOT NULL DEFAULT gen_random_uuid(),
    user_name VARCHAR(150) UNIQUE NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    is_active BOOlEAN NOT NULL DEFAULT TRUE,
    is_expired BOOlEAN NOT NULL DEFAULT FALSE,
    last_login TIMESTAMP,
    customer_id BIGINT REFERENCES customer(customer_id) NOT NULL,
    user_type VARCHAR(20) REFERENCES user_type(type_key) NOT NULL,
    PRIMARY KEY(user_id)
);

CREATE TABLE user_cars(
    id BIGSERIAL NOT NULL,
    customer_id BIGINT REFERENCES customer(customer_id),
    model_id UUID NOT NULL REFERENCES car_models(model_id),
    date_purchased DATE,
    service_date DATE,
    comments TEXT,
    PRIMARY KEY(id)
);

CREATE TABLE postgres_every_type (
    -- Numeric
    col_smallint          SMALLINT,
    col_integer           INTEGER,
    col_bigint            BIGINT,
    col_decimal           DECIMAL(10,2),
    col_numeric           NUMERIC(20,10),
    col_real              REAL,
    col_double            DOUBLE PRECISION,
    col_smallserial       SMALLSERIAL,
    col_serial            SERIAL,
    col_bigserial         BIGSERIAL,
    col_money             MONEY,
    -- Character
    col_char              CHAR(10),
    col_varchar           VARCHAR(100),
    col_text              TEXT,
    col_citext            CITEXT,
    col_name              NAME,
    -- Boolean
    col_boolean           BOOLEAN,
    -- Date / Time
    col_date              DATE,
    col_time              TIME,
    col_timetz            TIME WITH TIME ZONE,
    col_timestamp         TIMESTAMP,
    col_timestamptz       TIMESTAMP WITH TIME ZONE,
    col_interval          INTERVAL,
    -- Binary
    col_bytea             BYTEA,
    -- UUID
    col_uuid              UUID,
    -- JSON
    col_json              JSON,
    col_jsonb             JSONB,
    -- XML
    col_xml               XML,
    -- Arrays
    col_int_array         INTEGER[],
    col_text_array        TEXT[],
    -- Ranges
    col_int4range         INT4RANGE,
    col_int8range         INT8RANGE,
    col_numrange          NUMRANGE,
    col_daterange         DATERANGE,
    col_tsrange           TSRANGE,
    col_tstzrange         TSTZRANGE,
    -- Multiranges (PG 14+)
    col_int4multirange    INT4MULTIRANGE,
    col_nummultirange     NUMMULTIRANGE,
    col_tsmultirange      TSMULTIRANGE,
    -- Network
    col_inet              INET,
    col_cidr              CIDR,
    col_macaddr           MACADDR,
    col_macaddr8          MACADDR8,
    -- Bit strings
    col_bit               BIT(8),
    col_varbit            BIT VARYING(16),
    -- Text search
    col_tsvector          TSVECTOR,
    col_tsquery           TSQUERY,
    -- UUID / Object identifiers
    col_oid               OID,
    col_regclass          REGCLASS,
    col_regproc           REGPROC,
    col_regtype           REGTYPE,
    col_regrole           REGROLE,
    col_regnamespace      REGNAMESPACE,
    -- Transaction / system identifiers
    col_xid               XID,
    col_xid8              XID8,
    col_cid               CID,
    col_txid_snapshot     TXID_SNAPSHOT,
    col_pg_lsn            PG_LSN,
    -- Geometric
    col_point             POINT,
    col_line              LINE,
    col_lseg              LSEG,
    col_box               BOX,
    col_path              PATH,
    col_polygon           POLYGON,
    col_circle            CIRCLE,
    -- Full-text hierarchy
    col_ltree             LTREE
);


INSERT INTO user_type (type_key, title, description)
VALUES
    ('ADM', 'Admin', 'System Administrator'),
    ('SLF', 'Sales', 'Sales staff'),
    ('ENG', 'Engineering', 'Engineering staff'),
    ('CSP', 'Customer Support', 'Customer Support'),
    ('CST', 'Customer', 'Customer');

INSERT INTO car_models (model_id, manufacturer, model, engine_description, date_of_manufacture, registration)
VALUES
    ('f7b4f080-ba57-46e7-8bd7-22f85d011bf6', 'BMW', 'Series 1', 'Coupe', '12/13/2013', NULL),
    ('c483a731-6970-41a1-9354-29e23c132667', 'FIAT', '124 Spider', 'Coupe', '06/25/2019', NULL),
    ('fb7b56b7-4fb8-4fd0-a561-74870d31d0d2', 'Chevrolet', '1500 Extended Cab', 'Pickup', '10/15/2021', NULL),
    ('87dd82a8-31fa-41ac-bc6c-c4eec8b6386f', 'Nissan', '240SX', 'Coupe', '11/16/1998', NULL);


INSERT INTO customer (title, first_name, surname, contact_email, contact_mobile, date_joined, comment)
VALUES
    ('Mr', 'John', 'Smith', 'jsmith@gmail.com', '07777 123 456', '05/13/2025', 'Very important customer'),
    ('Mrs', 'Jo', 'Jackson', 'jj@gmail.com', '07777 222 333', '06/10/2024', NULL),
    ('Miss', 'Mary', 'McCackelsfield', 'mmac@test.com', '07776 444 555', '07/29/2025', 'Has more than one car');


INSERT INTO address (address_id, street, town, county, post_code, country)
VALUES
    (1, '1 The Road', 'Atown', 'Middlesex', 'AT1 0AA', 'United Kingdom'),
    (2, '34 The Street', 'BTown', 'Somerset', 'SS1 9BB', 'United Kingdom'),
    (3, 'The House on the Hill', 'CTown', 'Devon', 'DA1 0VN', 'United Kingdom');

INSERT INTO customer_address (customer_id, address_id, comment)
VALUES
    (1, 1, 'Moved to this address in 2022'),
    (2, 2, 'Lived here for a very long time'),
    (3, 3, ' Nice view');

INSERT INTO users (user_name, email, customer_id, user_type)
VALUES
    ('JohnSmith', 'jsmith@gmail.com', 1, 'CST'),
    ('JoJackson', 'jj@gmail.com', 2, 'CST'),
    ('MaryM', 'mmac@test.com', 3, 'CST');

INSERT INTO user_cars (customer_id, model_id, date_purchased, service_date, comments)
VALUES
    (2, 'c483a731-6970-41a1-9354-29e23c132667', 'April 13, 2020', NULL, NULL);

INSERT INTO postgres_every_type (
    col_smallint,
    col_integer,
    col_bigint,
    col_decimal,
    col_numeric,
    col_real,
    col_double,
    col_money,
    col_char,
    col_varchar,
    col_text,
    col_citext,
    col_name,
    col_boolean,
    col_date,
    col_time,
    col_timetz,
    col_timestamp,
    col_timestamptz,
    col_interval,
    col_bytea,
    col_uuid,
    col_json,
    col_jsonb,
    col_xml,
    col_int_array,
    col_text_array,
    col_int4range,
    col_int8range,
    col_numrange,
    col_daterange,
    col_tsrange,
    col_tstzrange,
    col_int4multirange,
    col_nummultirange,
    col_tsmultirange,
    col_inet,
    col_cidr,
    col_macaddr,
    col_macaddr8,
    col_bit,
    col_varbit,
    col_tsvector,
    col_tsquery,
    col_oid,
    col_regclass,
    col_regproc,
    col_regtype,
    col_regrole,
    col_regnamespace,
    col_xid,
    col_xid8,
    col_cid,
    col_txid_snapshot,
    col_pg_lsn,
    col_point,
    col_line,
    col_lseg,
    col_box,
    col_path,
    col_polygon,
    col_circle,
    col_ltree
) VALUES (
    1,
    100,
    10000000000,
    123.45,
    987654.1234567890,
    3.14,
    2.718281828,
    19.99,
    'char',
    'varchar',
    'text value',
    'CaseInsensitive',
    'pg_user',
    TRUE,
    CURRENT_DATE,
    CURRENT_TIME,
    CURRENT_TIME,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    INTERVAL '1 year 2 days',
    E'\\xDEADBEEF',
    uuid_generate_v4(),
    '{"a":1}',
    '{"b":2}',
    '<root><value>xml</value></root>',
    ARRAY[1,2,3],
    ARRAY['a','b','c'],
    '[1,10)',
    '[100,1000)',
    '[1.1,9.9]',
    '[2024-01-01,2025-01-01)',
    '[2024-01-01 00:00,2025-01-01 00:00)',
    '[2024-01-01 00:00+00,2025-01-01 00:00+00)',
    '{[1,5),[10,20)}',
    '{[1.1,2.2)}',
    '{[2024-01-01 00:00,2024-06-01 00:00)}',
    '192.168.1.1',
    '192.168.0.0/24',
    '08:00:2b:01:02:03',
    '08:00:2b:ff:fe:01:02:03',
    B'10101010',
    B'101010101010',
    to_tsvector('english', 'PostgreSQL full text search'),
    to_tsquery('postgresql & search'),
    12345,
    'pg_type',
    'now',
    'integer',
    current_user::regrole,
    'pg_catalog',
    pg_current_xact_id()::xid,
    pg_current_xact_id()::xid8,
    null,
    txid_current_snapshot(),
    pg_current_wal_lsn(),
    '(1,2)',
    '((1,1),(2,2))',
    '[(0,0),(1,1)]',
    '((0,0),(2,2))',
    '((0,0),(1,1),(2,2))',
    '((0,0),(1,0),(1,1),(0,1))',
    '<(0,0),5>',
    'Top.Science.Database.PostgreSQL'
);
