CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users
(
    id                              UUID          PRIMARY KEY,
    username                        TEXT          NOT NULL UNIQUE,
    email                           TEXT          NOT NULL UNIQUE,
    password                        TEXT          NOT NULL,
    phone                           TEXT          UNIQUE,
    image_url                       TEXT,
    role                            TEXT          NOT NULL,
    first_name                      TEXT          NOT NULL,
    last_name                       TEXT          NOT NULL,
    profile_bg_url                  TEXT,
    birthday                        TEXT,
    country                         TEXT,
    city                            TEXT,
    timezone                        TEXT,
    created_at                      TIMESTAMPTZ,
    updated_at                      TIMESTAMPTZ,
    deleted_at                      TIMESTAMPTZ
);
