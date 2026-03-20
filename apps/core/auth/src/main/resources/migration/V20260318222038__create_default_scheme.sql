CREATE TABLE users (
       id          UUID        NOT NULL PRIMARY KEY,
       username    VARCHAR(255) NOT NULL,
       password    VARCHAR(255) NOT NULL,
       is_active   BOOLEAN     NOT NULL DEFAULT TRUE,
       create_at   TIMESTAMPTZ,
       update_at   TIMESTAMPTZ
);

CREATE INDEX idx_users_username ON users (username);

CREATE TABLE users_role (
        users_id    UUID        NOT NULL REFERENCES users (id),
        roles       VARCHAR(50) NOT NULL,
        PRIMARY KEY (users_id, roles)
);