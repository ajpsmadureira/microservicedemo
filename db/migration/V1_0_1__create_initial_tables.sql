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

CREATE TYPE LOT_STATE AS ENUM ('CREATED', 'CANCELLED', 'AUCTIONED', 'SOLD', 'SHIPPED');

CREATE CAST (character varying AS LOT_STATE) with inout as assignment;

CREATE TABLE lots
(
   id                   SERIAL PRIMARY KEY,
   name                 VARCHAR(200) NOT NULL,
   surname              VARCHAR(200) NOT NULL,
   photo_url            VARCHAR(200),
   state                LOT_STATE DEFAULT 'CREATED',
   created_by           SERIAL references users(id) NOT NULL,
   last_modified_by     SERIAL references users(id) NOT NULL,
   created_at           TIMESTAMPTZ NOT NULL,
   updated_at           TIMESTAMPTZ NOT NULL
);

/*
CREATE TYPE BID_STATE AS ENUM ('OPENED', 'CANCELLED', 'ACCEPTED', 'OUTDATED');

CREATE TABLE bids
(
   id                   SERIAL PRIMARY KEY,
   amount               DECIMAL NOT NULL,
   lot                  SERIAL references lots(id) NOT NULL,
   state                BID_STATE NOT NULL,
   until                TIMESTAMPTZ,
   created_by           SERIAL references users(id) NOT NULL,
   last_modified_by     SERIAL references users(id) NOT NULL,
   created_at           TIMESTAMPTZ NOT NULL,
   updated_at           TIMESTAMPTZ NOT NULL
);
*/