
## Setup docker 
```
docker run --name mymysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=Test123 -d mysql:8.0.32
docker run --name myredis -p 6379:6379 -d redis:7.0.10
```