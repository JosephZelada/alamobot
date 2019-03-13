# alamobot

http PUT localhost:8142/alert film_name=='captain marvel' override_seating_algorithm=='false' earliest_showtime=='20:00:00' preferred_cinemas=='0003,0008' latest_showtime=='23:00:00' preferred_days_of_the_week=='WEDNESDAY' seat_count=='20' 
http GET localhost:8142/alert
http DELETE localhost:8142/alert/1

Alamobot Checklist:
1. Whenever 'Watched' is clicked, immediately kick off retrieve scheduled job and refresh page
2. Show loading wheel when doing long things (purchasing)
3. Standardize terms (movie vs showtime, etc) across all projects
4. Be able to assign people to seats
5. Be able to keep track of whether or not someone has paid for the tickets
6. Allow login from UI to purchase tickets
7. Schedule seat buy
8. Buy seats with algorithm to center around row 3, do not buy in front row
9. Allow parsing algorithm to buy seats
10. Write black box tests so we can be more confident that we're not breaking shit
11. Look into rxjs debounce to prevent people from continually spamming any of the UI buttons
12. Figure out how to not show links if a film showtime isn't being watched
13. Get refreshPostgresContainer task to show up/autocomplete when typing

Avengers Go-Live Checklist:
1. Able to list/set film names to scan and alert on
    a. Need to be able to do rudimentary pattern matching on film names
    b. Bolster film scheduled pull to scan every movie that comes in for the alert movie patterns
    c. Leave a hook in if a pattern gets matched to watch, buy the seats, and delete the alert
2. Able to buy seats based on a seating algorithm
    a. From row 3 or back, grab seats from the center out in groups of 3 or more, up to 10 per group
    b. Be sure to set the ticket limit within the alert
3. Able to pick preferred theater hierarchy
4. Able to add a time frame to buy tickets within (don't buy tickets before 7:00 PM or after 11:00 PM)
5. Alert buy grabs the earliest showing within the timeframe
    a. Maybe also analyze theater seats, don't buy from a theater if there aren't enough contiguous seats to fill the required amount of seats
6. Once ticket buy starts in a theater, don't auto-buy tickets for that alert in any other theater
7. If an alerted movie is detected, check if there are showtimes. If there are, break out of the loop, just grab the showtimes for that day