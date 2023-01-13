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

## Pushing app image

```shell
docker login

docker build --no-cache -f ./.docker/images/{language}/{app}/Dockerfile -t rarhaeu/mgr2022:{app}-app .

docker push rarhaeu/mgr2022:{app}
```