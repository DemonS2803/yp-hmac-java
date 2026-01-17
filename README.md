
# HMAC server for java
### Dmitry Sudakov

## Requirments

- *nix operating system (Windows not personally recommended :-) )
- Java 22+
- Maven
- Docker (optional)

* All commands must run from repository root

## Simple way

Build docker container. It will build image, run tests and start application
```bash
docker build -t sudakov-yp-hmac-java:latest . 
```

PS: you should specify exposing port if want to change it

## Build application

You could build application with tests
```bash
mvn clean package -Djava.util.logging.config.file=logging.properties
```

Or without
```bash
mvn clean package -DskipTests -Djava.util.logging.config.file=logging.properties
```

There is also "True way"
```bash
javac -d bin --enable-preview -cp "lib/*" --release 22 src/**/*.java test/**/*.java
```

## Run HMAC server

```bash
java --enable-preview -Djava.util.logging.config.file=logging.properties -jar target/yp-hmac-java-jar-with-dependencies.jar
```

## Run tests

Running tests is available with maven
```bash
mvn test -Djava.util.logging.config.file=logging.properties
```

## Work with config

Config of application is JSON file:
```json
{
  "hmacAlg": "HmacSHA256",
  "secret": "bmV3LXNlY3JldA",
  "listenPort": 8080,
  "maxMsgSizeBytes": 1048576
}
```

Also available setting 'maxRequestBodySizeBytes' for limiting request body

Build cli-rotate-secret util
```bash
mvn clean install -Pcli-rotate-secret -DskipTests
```

Run cli-rotate-secret util
```bash
java --enable-preview -jar target/cli-rotate-secret-jar-with-dependencies.jar config.json new-secret
```

## API requests example

*Used signature from generated 'new-secret' key and maxMsgSizeBytes '100'*

Sign message
```bash
curl -sS -X GET http://localhost:8080/sign \
  -H 'Content-Type: application/json' \
  -d '{"msg":"hello"}'
  ```

Verify message. Valid
```bash
curl -sS -f -X GET http://localhost:8080/verify \
  -H 'Content-Type: application/json' \
  -d '{"msg":"hello","signature":"GBgHTPLL83wM935axaL96lKQ83eOKKUG2_MArxCp3Mc"}'
```

Sign message. Empty message
```bash
curl -sS -f -X GET http://localhost:8080/sign \
  -H 'Content-Type: application/json' \
  -d '{"msg":""}'
  ```

Sign message. Message too large
```bash
curl -sS -f -X GET http://localhost:8080/sign \
  -H 'Content-Type: application/json' \
  -d '{"msg":"aBcDeFgHiJkLmNoPqRsTuVwXyZ0123456789aBcDeFgHiJkLmNoPqRsTuVwXyZ0123456789aBcDeFgHiJkLmNoPqRsTuVwXyZ0123456789aBcDeFgHiJkLmNoPqRsTuVwXyZ0123456789"}'
  ```


Verify message. Invalid message: "hello" -> "hello!"
```bash
curl -sS -f -X GET http://localhost:8080/verify \
  -H 'Content-Type: application/json' \
  -d '{"msg":"hello!","signature":"GBgHTPLL83wM935axaL96lKQ83eOKKUG2_MArxCp3Mc"}'
```


Verify message. BrokenSignature
```bash
curl -sS -f -X GET http://localhost:8080/verify \
  -H 'Content-Type: application/json' \
  -d '{"msg":"hello!","signature":"@@@GBgHTPLL83wM935axaL96lKQ83eOKKUG2_MArxCp3Mc@@@"}'
```

## Ограничения учебной реализации
- HMAC ≠ асимметричная ЭП
- Нет многоключевой валидации
- Ротация простая


