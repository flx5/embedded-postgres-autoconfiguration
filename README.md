# Auto Configuration for Embedded Postgresql Database

A postgres database configuration for developing spring applications without an external database.

## Usage

Create a maven profile for development including the autoconfiguration:

```xml
<profiles>
    <profile>
        <id>develop</id>
        <dependencies>
            <dependency>
                <groupId>com.flx5.zonky</groupId>
                <artifactId>embedded-postgres-autoconfiguration</artifactId>
                <version>1.0-SNAPSHOT</version>
            </dependency>
        </dependencies>
    </profile>
</profiles>
```

Then activate that profile in your IDE. This way a JDBC Datasource as well as an R2DBC ConnectionFactory Bean is provided during development.