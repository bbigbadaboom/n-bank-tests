FROM maven:3.9.9-eclipse-temurin-17

#дефолтные значения совпадают с конфиг пропертис
ARG TEST_PROFILE=api
ARG BASEAPIURL=http://localhost:4111
ARG BASEUIURL=http://localhost:3000

ENV TEST_PROFILE=${TEST_PROFILE}
ENV BASEAPIURL=${BASEAPIURL}
ENV BASEUIURL=${BASEUIURL}

WORKDIR /app

#копируется помник
COPY pom.xml .


#хагружаем зависимости и кэшируем
RUN mvn dependency:go-offline

#копируется весь проект
COPY . .

CMD /bin/bash -c "\
 mkdir -p /app/logs ; \
 { \
 echo '>>> Runnig tests ${TEST_PROFILE}' ; \
 mvn test -q -P${TEST_PROFILE} ; \
 \
 echo '>>> Running surefire-report' ;\
 mvn -DskipTests=true surefire-report:report ;\
 } 2>&1 | tee /app/logs/run.log"