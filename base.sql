

-- DROP DATABASE IF EXISTS book_store;

-- CREATE DATABASE book_store
--     WITH
--     OWNER = postgres
--     ENCODING = 'UTF8'
--     LC_COLLATE = 'English_United States.1251'
--     LC_CTYPE = 'English_United States.1251'
--     TABLESPACE = pg_default
--     CONNECTION LIMIT = -1
--     IS_TEMPLATE = False;


-- drop table books cascade

-- CREATE TYPE urole AS ENUM('Customer', 'Admin');
CREATE TABLE authors(
	author_id serial PRIMARY KEY,
	full_name VARCHAR(50) NOT NULL,
	description VARCHAR(1024) NOT NULL
);
CREATE TABLE genres(
	genre_id serial PRIMARY KEY,
	name VARCHAR(50) NOT NULL,
	description VARCHAR(1024) NOT NULL
);
CREATE TABLE books(
	book_id serial PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	author_id INT NOT NULL,
	genre_id INT NOT NULL,
	year_of_issue INT,
	description VARCHAR(1024),
	num_of_pages INT NOT NULL,
	amount INT NOT NULL,
	FOREIGN KEY (author_id) REFERENCES authors (author_id),
	FOREIGN KEY (genre_id) REFERENCES genres (genre_id)	
);
CREATE TABLE users(
	user_id serial PRIMARY KEY,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	username VARCHAR(50) NOT NULL,
	password VARCHAR(80) NOT NULL,
	enabled BOOLEAN NOT NULL,
	user_role VARCHAR(50) NOT NULL
);

