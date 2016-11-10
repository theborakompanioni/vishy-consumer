[![Build Status](https://travis-ci.org/theborakompanioni/vishy-consumer.svg)](https://travis-ci.org/theborakompanioni/vishy-consumer)
[![License](https://img.shields.io/github/license/theborakompanioni/vishy-consumer.svg?maxAge=2592000)](https://github.com/theborakompanioni/vishy-consumer/blob/master/LICENSE)
vishy-consumer
===

### Download

#### Maven
e.g.
```xml
<dependency>
    <groupId>com.github.theborakompanioni</groupId>
    <artifactId>vishy-consumer-metrics</artifactId>
    <version>${vishy-consumer.version}</version>
</dependency>
<dependency>
    <groupId>com.github.theborakompanioni</groupId>
    <artifactId>vishy-consumer-kafka</artifactId>
    <version>${vishy-consumer.version}</version>
</dependency>
```

Enable snapshot repositories:
```xml
<profiles>
    <profile>
        <id>allow-snapshots</id>
        <activation><activeByDefault>true</activeByDefault></activation>
        <repositories>
            <repository>
                <id>snapshots-repo</id>
                <url>https://oss.sonatype.org/content/repositories/snapshots</url>
                <releases><enabled>false</enabled></releases>
                <snapshots><enabled>true</enabled></snapshots>
            </repository>
        </repositories>
    </profile>
</profiles>
```

License
-------
The project is licensed under the Apache License. See [LICENSE](LICENSE) for details.
