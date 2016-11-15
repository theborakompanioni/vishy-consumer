CREATE TABLE vishy_openmrc_request_test_table (
  id   BIGSERIAL,
  type VARCHAR(32)    NOT NULL,
  json VARCHAR(10000) NOT NULL,
  PRIMARY KEY (id)
);
