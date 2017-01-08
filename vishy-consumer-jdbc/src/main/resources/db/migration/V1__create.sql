CREATE TABLE ${table.name} (
  id   BIGSERIAL,
  type VARCHAR(32)    NOT NULL,
  json VARCHAR(10000) NOT NULL,
  PRIMARY KEY (id)
);
