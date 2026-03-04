CREATE TABLE IF NOT EXISTS dev.token_expiration(
   id SERIAL,
   token VARCHAR(255) NOT NULL UNIQUE,
   expiration TIMESTAMP NOT NULL,
   PRIMARY KEY(id)
);
