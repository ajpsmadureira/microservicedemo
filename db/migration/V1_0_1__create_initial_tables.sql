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

CREATE TABLE lots
(
   id                   SERIAL PRIMARY KEY,
   name                 VARCHAR(200) NOT NULL,
   surname              VARCHAR(200) NOT NULL,
   photo_url            VARCHAR(200),
   created_by           SERIAL references users(id) NOT NULL,
   last_modified_by     SERIAL references users(id) NOT NULL,
   created_at           TIMESTAMPTZ NOT NULL,
   updated_at           TIMESTAMPTZ NOT NULL
);