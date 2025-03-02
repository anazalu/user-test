## To run the tests with our REST API (in the 'api' Docker container):
```
docker-compose up --build
```

## To run the tests with your REST API (at localhost:8080/api):
```
API_BASE_URL=http://host.docker.internal:8080/api docker-compose up --build
```

## Test run results:
![alt text](image.png)