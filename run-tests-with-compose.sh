#1/bin/bash
IMAGE_NAME=nbank-tests
COMPOSE_FILE=infra/docker_compose/docker-compose.yml
#собираем образ или проставляем в image имя контейнера и убираем build строку
docker build -t $IMAGE_NAME .
  TEST_PROFILE=${1:-api}

TIMESTAMP=$(date +"%Y%m%d_%H%M")
TEST_OUTPUT_DIR=./test-output/$TIMESTAMP
mkdir -p "$TEST_OUTPUT_DIR/logs"
mkdir -p "$TEST_OUTPUT_DIR/results"
mkdir -p "$TEST_OUTPUT_DIR/reports"

docker-compose -f $COMPOSE_FILE up -d

echo "Ждем пока сервисы стартанут."
sleep 5

#запуск контейнера
docker run --rm \
 -v $TEST_OUTPUT_DIR/logs:/app/logs \
 -v $TEST_OUTPUT_DIR/results:/app/target/surefire-reports \
 -v $TEST_OUTPUT_DIR/reports:/app/target/site \
 -e TEST_PROFILE="$TEST_PROFILE" \
 -e BASEAPIURL=http://192.168.1.65:4111 \
 -e BASEUIURL=http://192.168.1.65:80 \
 $IMAGE_NAME

 docker-compose -f $COMPOSE_FILE down

 echo "Сервисы остановлены"
 echo "есты завершены"
 echo "Лог файл: $TEST_OUTPUT_DIR/logs/run.log"
 echo "Результаты тестов: $TEST_OUTPUT_DIR/results"
 echo "Репорт: $TEST_OUTPUT_DIR/reports"
