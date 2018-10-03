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

Alamobot Checklist:
1. Mock for payments, don't update seats with sold or new if bought
2. Keep track of bought seats
3. Be able to assign people to seats
4. Be able to keep track of whether or not someone has paid for the tickets
5. Allow login from UI to purchase tickets
6. Schedule seat buy
7. Buy seats with algorithm to center around row 3, do not buy in front row
8. Allow parsing algorithm to buy seats