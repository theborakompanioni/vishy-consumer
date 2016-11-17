vishy-consumer-jdbc
=======

e.g. in your `application.yml`:
```
vishy.jdbc:
  enabled: true
  jdbcUrl: ${POSTGRES_URL}
  username: ${POSTGRES_USER}
  password: ${POSTGRES_PASS}
  driverClassName: org.postgresql.Driver
  tableName: vishy_openmrc_request_table_test
```
