#!/bin/bash
IMAGE_NAME=nbank-tests
DOCKERHUB_USERNAME=bbigbadaboom
TAG=${1:-latest}
#вынесли токен в переменну окружения export DOCKERHUB_TOKEN=
echo "Логин в Docker Hub с токеном"
echo "$DOCKERHUB_TOKEN" | docker login --username "$DOCKERHUB_USERNAME" --password-stdin
echo "Тегирование образа"
docker tag "$IMAGE_NAME:$TAG" "$DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG"

echo "Отправка образа в Docker Hub"
docker push "$DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG"