
## Some commands

```
 mvn clean package
 docker build -t user-api .
 docker run -p 8080:8080 user-api
```
```
curl localhost:8080/api/users
```
```
curl -X POST localhost:8080/users -i -H 'Content-Type: application/json' -d '{}'
```
```
curl localhost:8080/api/users/{id}
```
```
curl -X PUT localhost:8080/users/{id} -i -H 'Content-Type: application/json' -d '{}'
```
```
curl -X DELETE localhost:8080/api/users/{id} -i
```
