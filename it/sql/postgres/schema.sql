CREATE DATABASE anvil
    ENCODING = UTF8;

CREATE TABLE customer (
    customer_id BIGINT NOT NULL,
    title VARCHAR(5) NOT NULL
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
    title VARCHAR(50) UNIQUE,
    description VARCHAR(200)
);

CREATE TABLE users(
    user_id UUID NOT NULL,
    user_name VARCHAR(150) UNIQUE NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    is_active BOOlEAN NOT NULL DEFAULT TRUE,
    is_expired BOOlEAN NOT NULL DEFAULT FALSE,
    last_login TIMESTAMP,
    customer_id BIGINT REFERENCES customer(customer_id),
    user_type VARCHAR(20) REFERENCES user_type(type_key),
    PRIMARY KEY(user_id)
);