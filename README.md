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
1. Whenever 'Watched' is clicked, immediately kick off retrieve scheduled job and refresh page
2. Show loading wheel when doing long things (purchasing)
1. Standardize terms (movie vs showtime, etc) across all projects
2. Be able to assign people to seats
3. Be able to keep track of whether or not someone has paid for the tickets
4. Allow login from UI to purchase tickets
5. Schedule seat buy
6. Buy seats with algorithm to center around row 3, do not buy in front row
7. Allow parsing algorithm to buy seats
8. Write black box tests so we can be more confident that we're not breaking shit
9. Look into rxjs debounce to prevent people from continually spamming any of the UI buttons
10. Figure out how to not show links if a film showtime isn't being watched