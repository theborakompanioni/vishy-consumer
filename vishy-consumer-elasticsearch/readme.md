vishy-consumer-elasticsearch
=======

e.g. in your `application.yml`:
```
vishy.elasticsearch:
  enabled: true

# ELASTICSEARCH
spring.data.elasticsearch:
  cluster-name: elasticsearch-test-cluster
  cluster-nodes: # The address(es) of the server node (comma-separated; if not specified starts a client node)
  repositories.enabled: true
  properties:
    http.enabled: true
    index.refresh_interval: 60s
    path.data: /tmp/data-test/
    threadpool:
      index:
        type: fixed
        size: 1
        queue_size: 1000
      search:
        type: fixed
        size: 1
        queue_size: 1000
      refresh:
        type: scaling
        size: 1
        queue_size: 1000
```
