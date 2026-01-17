FROM maven:3.9.9-eclipse-temurin-22-alpine

ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="$JAVA_HOME/bin:$PATH"

WORKDIR /app

COPY . .

RUN mvn clean package

# Правда теперь порт из конфига не играет особой роли
EXPOSE 8080

RUN java --enable-preview -jar target/yp-hmac-java-jar-with-dependencies.jar


# Я по честно изначально делал без сборщика, и оно работает,
# но не зря же умные люди придумали их

## build app
#CMD ["/opt/java/openjdk/bin/javac", \
# "-d", "bin", \
# "--enable-preview", \
# "-cp", "lib/*", \
# "--release", "22", \
# "src/**/*.java"]
#
## build tests
#CMD ["/opt/java/openjdk/bin/javac", \
# "-d", "bin", \
# "--enable-preview", \
# "-cp", "lib/*", \
# "--release", "22", \
# "test/**/*.java", \
# "src/**/*.java"]
#
## run tests
#CMD ["/opt/java/openjdk/bin/java", \
## "-ea", \
# "-cp", "bin/classes:bin/test-classes:lib/*", \
# "--enable-preview", \
# "org.junit.platform.console.ConsoleLauncher"]

# run app
#CMD ["/opt/java/openjdk/bin/java", \
#    "--enable-preview", \
#    "-cp", "bin:lib/*", \
#    "ru.yandex.practicum.ServerHMAC"]