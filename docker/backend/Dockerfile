FROM eclipse-temurin:17-jdk-alpine

RUN apk add --no-cache bash procps curl tar tree

ENV MAVEN_HOME /usr/share/maven
ENV MY_PASSWORD=did9projet
ENV SECRET_KEY=leNomDuChienEstLongCarC'ESTainsiqu'ILlef@ut

COPY --from=maven:3.9.1-eclipse-temurin-17 ${MAVEN_HOME} ${MAVEN_HOME}
COPY --from=maven:3.9.1-eclipse-temurin-17 /usr/local/bin/mvn-entrypoint.sh /usr/local/bin/mvn-entrypoint.sh
COPY --from=maven:3.9.1-eclipse-temurin-17 /usr/share/maven/ref/settings-docker.xml /usr/share/maven/ref/settings-docker.xml

RUN ln -s ${MAVEN_HOME}/bin/mvn /usr/bin/mvn

ARG MAVEN_VERSION=3.9.1
ARG USER_HOME_DIR="/root"
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"
ENTRYPOINT ["/usr/local/bin/mvn-entrypoint.sh"]
COPY  pom.xml app/pom.xml
COPY backend app/backend
WORKDIR app/backend
RUN mvn  compile
CMD mvn exec:java


