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
1. Standardize terms (movie vs showtime, etc) across all projects
2. Keep track of bought seats
    a. Save any seats that are bought
        i. Bought seats exempt the showtime, seats, and film from cleanup
    b. Periodically check Alamo account for seats not bought through the app. Save them
    c. Able to list movies with showtimes with bought seats
        i. Must keep all seats in a bought showtime, regardless of if they are bought or not
3. Be able to assign people to seats
4. Be able to keep track of whether or not someone has paid for the tickets
5. Allow login from UI to purchase tickets
6. Schedule seat buy
7. Buy seats with algorithm to center around row 3, do not buy in front row
8. Allow parsing algorithm to buy seats
9. Write black box tests so we can be more confident that we're not breaking shit
10. Look into rxjs debounce to prevent people from continually spamming any of the UI buttons
11. Figure out how to not show links if a film showtime isn't being watched