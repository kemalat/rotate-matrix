FROM openjdk:8-jre

MAINTAINER Kemal Atik <k.atik@oriontec.de>

ADD target/classes/application.yml /app/application.yml
ADD target/rotate-matrix-1.0.0.jar /app/rotate-matrix-1.0.0.jar
ADD start.sh /app/start.sh
WORKDIR /app
RUN mkdir -p /app/tmp
# Web port.
EXPOSE 2000
ENTRYPOINT ["./start.sh"]
