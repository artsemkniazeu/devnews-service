FROM openjdk:11
VOLUME /tmp
COPY build/libs/devnews-service-1.0.jar app.jar
EXPOSE 8080

ENV XMX="256m" \
    TIMEZONE="UTC"

ENV JAVA_OPTIONS="-Xmx${XMX} -Duser.timezone=${TIMEZONE}"

RUN touch newrelic.yml

CMD java ${JAVA_OPTIONS} ${OPTIONS} -jar /app.jar
