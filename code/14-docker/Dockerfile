ARG BUILD_IMAGE
ARG RUNTIME_IMAGE
FROM ${BUILD_IMAGE} AS build
WORKDIR /work
COPY ProbeServer.java .
RUN javac --release 21 --add-modules jdk.httpserver -d out ProbeServer.java
RUN java --add-modules jdk.httpserver -cp out ProbeServer --self-test

FROM ${RUNTIME_IMAGE}
WORKDIR /app
COPY --from=build /work/out /app/classes
USER 10001:10001
EXPOSE 8080
ENTRYPOINT ["java", "--add-modules", "jdk.httpserver", "-cp", "/app/classes", "ProbeServer"]