vishy-consumer-kafka
=======

e.g. in your `application.yml`:
```
vishy.kafka:
  enabled: true
  brokers: ${KAFKA_HOST}:${KAFKA_PORT}
  retries: 0
  batchSize: 16384
  lingerMs: 1
  bufferMemory: 33554432
```
