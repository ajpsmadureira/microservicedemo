CREATE TABLE users
(
   id                   SERIAL PRIMARY KEY,
   active               BOOLEAN NOT NULL,
   is_admin             BOOLEAN NOT NULL,
   email                VARCHAR(200) NOT NULL,
   password             VARCHAR(200) NOT NULL,
   username             VARCHAR(200) NOT NULL,
   created_at           TIMESTAMPTZ NOT NULL,
   updated_at           TIMESTAMPTZ NOT NULL
);

CREATE TYPE LOT_STATE AS ENUM ('CREATED', 'CANCELLED', 'AUCTIONED', 'CLOSED');

CREATE CAST (character varying AS LOT_STATE) with inout as assignment;

CREATE TABLE lots
(
   id                   SERIAL PRIMARY KEY,
   name                 VARCHAR(200) NOT NULL,
   surname              VARCHAR(200) NOT NULL,
   photo_url            VARCHAR(200),
   state                LOT_STATE NOT NULL,
   created_by           SERIAL references users NOT NULL,
   last_modified_by     SERIAL references users NOT NULL,
   created_at           TIMESTAMPTZ NOT NULL,
   updated_at           TIMESTAMPTZ NOT NULL
);

CREATE TYPE BID_STATE AS ENUM ('CREATED', 'CANCELLED', 'ACCEPTED', 'OUTDATED', 'REJECTED');

CREATE CAST (character varying AS BID_STATE) with inout as assignment;

CREATE TABLE bids
(
   id                   SERIAL PRIMARY KEY,
   amount               DECIMAL NOT NULL,
   state                BID_STATE NOT NULL,
   until                TIMESTAMPTZ,
   lot                  SERIAL references lots NOT NULL,
   created_by           SERIAL references users NOT NULL,
   last_modified_by     SERIAL references users NOT NULL,
   created_at           TIMESTAMPTZ NOT NULL,
   updated_at           TIMESTAMPTZ NOT NULL
);