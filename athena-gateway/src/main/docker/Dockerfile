FROM openjdk:21-jdk-slim as builder

ENV application="/opt/athena"
ENV project_build_finalName="${project.build.finalName}.jar"

WORKDIR $application
COPY "maven/${project_build_finalName}" ./
RUN java -Djarmode=layertools -jar "${project_build_finalName}" extract

FROM openjdk:21-jdk-slim

ENV DB_PSQL_URL="jdbc:postgresql://localhost:5432/postgres" \
    DB_PSQL_USERNAME="postgres" \
    DB_PSQL_PASSWORD="password" \
    application="/opt/athena"

WORKDIR $application
COPY --from=builder "${application}/dependencies/" ./
RUN true
COPY --from=builder "${application}/spring-boot-loader/" ./
RUN true
COPY --from=builder "${application}/snapshot-dependencies/" ./
RUN true
COPY --from=builder "${application}/application/" ./
RUN true

EXPOSE 8080 9090

ENTRYPOINT ["java", \
"-Dhibernate.dialect=org.hibernate.dialect.PostgreSQLDialect", \
"-Dspring.datasource.url=${DB_PSQL_URL}", \
"-Dspring.datasource.username=${DB_PSQL_USERNAME}", \
"-Dspring.datasource.password=${DB_PSQL_PASSWORD}", \
"org.springframework.boot.loader.launch.JarLauncher"]