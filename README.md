# mgr2022
Praca magisterska


## Local Dev - Uruchamianie

```shell
docker build --no-cache -f ./.docker/images/{APP}/Dockerfile -t localhost:5005/{APP}-app .
# change docker-compose app-server image
docker-compose up --force-recreate --remove-orphans -V grafana app-server
# NUM = 1..5
docker-compose run k6 run /scripts/scenario{NUM}.js
```

## Native Image - Budowanie

### Micronaut

./gradlew dockerBuildNative

Zbudowany zostanie obraz o nazwie micronaut-native:latest

### Quarkus

docker build --no-cache -f ./.docker/images/quarkus/Dockerfile.native -t localhost:5005/quarkus-native-app .
