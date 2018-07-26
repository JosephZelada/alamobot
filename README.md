Steps to re-add the alamobot postgres DB:
```
cd ~/auth-losaml
gw refreshPostgres
//Password is postgres
psql -hdocker.localhost -p5432 -Upostgres
CREATE DATABASE alamobot;
\q
```



Need to add in postgres docker container# alamobot
# alamobot
