vishy-consumer-influxdb
=======

e.g. in your `application.yml`:
```
vishy.metrics.enabled: true

vishy.influxdb:
  enabled: true
  host: ${INFLUXDB_HOST}
  port: ${INFLUXDB_PORT}
  database: ${INFLUXDB_NAME}
  username: ${INFLUXDB_USER}
  password: ${INFLUXDB_PASS}
  skip-idle-metrics: true
  interval-in-seconds: 10
```
