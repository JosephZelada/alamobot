# alamobot

http POST localhost:8142/alert film_name=='captain marvel' override_seating_algorithm=='false' earliest_showtime=='20:00:00' preferred_cinemas=='0003,0008' latest_showtime=='23:00:00' preferred_days_of_the_week=='MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY' seat_count=='20' 
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

Merchandise Buying Notes

POST https://drafthouse.com/s/mother/v1/app/seats/0008/25684/select
{"userSessionId":"e6e71c3f2bd24383ab67b4fdf4f909c6","seatSelections":[{"areaIndex":0,"rowIndex":0,"columnIndex":19}]}
RESPONSE: Look for data.isMerchandiseAvailable. If true, do the next one

GET https://drafthouse.com/s/mother/v1/app/merchandise/0008/25684?userSessionId=e6e71c3f2bd24383ab67b4fdf4f909c6
RESPONSE:
{  
   "data":{
      "order":{  
         "tickets":[  
            {  
               "vistaId":{  
                  "ticketTypeCode":"0083",
                  "barcode":null,
                  "loyaltyRecognitionId":null,
                  "loyaltyRecognitionSequence":0
               },
               "description":"Admission",
               "priceInCents":1028,
               "expectedSeatCount":1
            }
         ]
      },
      "merchandise":[  
         {  
            "contentId":"25045",
            "headOfficeCode":"A000008796",
            "vistaId":{  
               "id":"8796",
               "recognitionId":0,
               "recognitionSequenceNumber":0
            },
            "name":"SHAZAM Glass"
         }
      ]
   }
}

POST https://drafthouse.com/s/mother/v1/app/merchandise/0008/25684
{"userSessionId":"e6e71c3f2bd24383ab67b4fdf4f909c6","merchandise":[{"vistaId":{"id":"8796","recognitionId":0,"recognitionSequenceNumber":0},"quantity":1}]}
RESPONSE:
{  
   "data":{  
      "film":{  
         "headOfficeCode":"A000020498",
         "slug":"2d-shazam",
         "title":"2D SHAZAM!",
         "meta":{  
            "description":"Initially created as a Superman knockoff called Captain Marvel (it's a long story), Shazam was the most popular comic book character of the 1940s. And it's easy to see why – the premise is perfect for kids. Instead of merely being a superhero's sidekick, imagine you could actually become a superhero.\n\nIn the film, a mysterious being chooses foster kid Billy Batson (Asher Angel) as a vessel for its extraordinary powers. When Billy says this being’s name, he transforms from a 14 year-old boy into the grown-up superhero Shazam (Zachary Levi, THOR: RAGNAROK). With his friend Freddy (Jack Dylan Grazer, IT) in tow, he works to make sense of his powers, which include being able to fly, resist bullets, and purchase beer without getting carded.\n\nWith a great comic actor like Zachary Levi wearing the cape, SHAZAM! manages to give us something we haven't seen from superhero cinema in far too long – a funny, action-packed adventure film that the whole family will love.",
            "image":"https://s3.drafthouse.com/images/made/SHAZAM-STILL-01_1050_591_81_s_c1.jpg"
         },
         "year":"2019",
         "runtimeMinutes":132,
         "showtimeMinutes":147,
         "rating":"PG-13",
         "seriesName":null,
         "seriesSummary":null,
         "headline":"What’s in a name? A legendary hero, for starters.",
         "description":"<p>Like a combination between SUPERMAN and BIG, SHAZAM! is a goofy, lovable, family-friendly twist on the superhero genre. It&rsquo;s a refreshing reminder that as much as we love watching films about dark, brooding, morally-compromised crusaders, comic book films can also be bright, bold, and just plain fun.</p>\n\n<p>Initially created as a Superman knockoff called Captain Marvel (it&#39;s a long story), Shazam was the most popular comic book character of the 1940s. And it&#39;s easy to see why &ndash; the premise is perfect for kids. Instead of merely being a superhero&#39;s sidekick, imagine you could actually become a superhero.</p>\n\n<p>In the film, a mysterious being chooses foster kid Billy Batson (Asher Angel) as a vessel for its extraordinary powers. When Billy says this being&rsquo;s name, he transforms from a 14 year-old boy into the grown-up superhero Shazam (Zachary Levi, THOR: RAGNAROK). With his friend Freddy (Jack Dylan Grazer, IT) in tow, he works to make sense of his powers, which include being able to fly, resist bullets, and purchase beer without getting carded.</p>\n\n<p>With a great comic actor like Zachary Levi wearing the cape, SHAZAM! manages to give us something we haven&#39;t seen from superhero cinema in far too long &ndash; a funny, action-packed adventure film that the whole family will love.</p>",
         "poster":"https://s3.drafthouse.com/images/made/Shazam-poster-2D_500_741_81_s_c1.jpg",
         "stills":[  
            "https://s3.drafthouse.com/images/made/SHAZAM-STILL-01_758_426_81_s_c1.jpg",
            "https://s3.drafthouse.com/images/made/SHAZAM-STILL-02_758_426_81_s_c1.jpg"
         ],
         "trailer":"https://www.youtube.com/watch?v=go6GEIrcvFY",
         "director":"David F. Sandberg",
         "cast":"Lovina Yavari, Mark Strong, Zachary Levi",
         "agePolicyLabel":"Age Policy",
         "agePolicy":"18 and up; Children 6 and up will be allowed only with an adult. No children under the age of 6 will be allowed.",
         "landscapeHeroImage":"cloudinary:shows/SHAZAM-STILL-01.jpg",
         "portraitHeroImage":"cloudinary:shows/SHAZAM-STILL-01.jpg",
         "posterImage":"cloudinary:shows/Shazam-poster-2D.jpg",
         "trailerImage":"cloudinary:shows/SHAZAM-STILL-02.jpg",
         "gallery":[  

         ],
         "openingDateClt":null,
         "specialShowType":null,
         "isClosedCaptioningAvailable":true,
         "isDescriptiveAudioAvailable":true,
         "isAssistedListeningAvailable":true
      },
      "market":{  
         "id":"0000",
         "slug":"austin",
         "name":"Austin, TX",
         "status":"UNKNOWN",
         "marketStrings":[  
            {  
               "key":"closedCaptioning",
               "value":"The personal closed captioning device displays feature film dialogue and other audio in the form of English-language text for individuals who are deaf or hard of hearing. Other foreign-language options can also be made available with 48-hour advanced notice."
            },
            {  
               "key":"descriptiveAudio",
               "value":"The descriptive audio headset provides individuals who are blind or have low vision with an English-language narration of the details of the feature presentation that can't be understood by the soundtrack alone."
            },
            {  
               "key":"assistedListening",
               "value":"The assisted listening headset provides an amplified audio signal for those individuals who are hard of hearing. A 3.5mm headphone jack is available to drive hearing aids, neck loops, or cochlear implants."
            },
            {  
               "key":"latePolicy",
               "value":"If you show up to your desired screening late, we will happily exchange your ticket for a different showtime or for a raincheck to a future show."
            }
         ]
      },
      "cinema":{  
         "id":"0008",
         "loyaltyCinemaId":"32",
         "slug":"mueller",
         "name":"Mueller",
         "status":"OPEN",
         "timeZoneName":"America/Chicago",
         "latitude":30.298706,
         "longitude":-97.704233,
         "street1":"1911 Aldrich Street, Suite 120",
         "street2":null,
         "city":"Austin",
         "state":"TX",
         "postalCode":"78723",
         "phone":"512-572-1425",
         "email":"",
         "heroImageUrl":"https://s3.drafthouse.com/images/made/alamo-drafthouse-mueller_350_336_81_s_c1.jpg",
         "googleMapsUrl":"https://www.google.com/maps/place/Alamo+Drafthouse+Mueller/@30.2983927,-97.7068959,17z/data=!4m12!1m6!3m5!1s0x8644b5f8e25fdee7:0x590e493b822ce388!2sAlamo+Drafthouse+Mueller!8m2!3d30.2983881!4d-97.7047072!3m4!1s0x8644b5f8e25fdee7:0x590e493b822ce388!8m2!3d30.2983881!4d-97.7047072",
         "appleMapsUrl":"https://maps.apple.com/?address=1911%20Aldrich%20St,%20Unit%20120,%20Austin,%20TX%20%2078723,%20United%20States&auid=16338817463053804478&ll=30.298648,-97.704281&lsp=9902&q=Alamo%20Drafthouse%20Cinema&t=r",
         "attachedBar":{  
            "name":"Barrel O' Fun",
            "websiteUrl":"http://barrelofunatx.com/"
         },
         "isClosedCaptioningAvailable":true,
         "isDescriptiveAudioAvailable":true,
         "isAssistedListeningAvailable":true
      },
      "session":{  
         "cinemaId":"0008",
         "sessionId":"25684",
         "status":"ONSALE",
         "businessDateClt":"2019-04-06",
         "showTimeClt":"2019-04-06T15:40:00",
         "cinemaTimeZoneName":"America/Chicago",
         "showTimeUtc":"2019-04-06T20:40:00",
         "ticketTypesLoyaltyCount":1,
         "ticketTypesVoucherCount":1,
         "ticketTypesNormalCount":3,
         "reservedSeating":true,
         "attributes":[  

         ],
         "screenNumber":"4",
         "theaterNumber":"4",
         "formatName":"Digital",
         "recognitionIds":[  
            "0",
            "0",
            "11",
            "350",
            "353"
         ]
      },
      "sessionAttributes":[  

      ],
      "loyaltyMember":null,
      "order":{  
         "userSessionId":"e6e71c3f2bd24383ab67b4fdf4f909c6",
         "lastUpdatedUtc":"2019-03-14T07:50:18.806",
         "cinemaId":"0008",
         "sessionId":"25684",
         "screenNumber":"4",
         "theaterNumber":"4",
         "seats":[  
            {  
               "rowId":"1",
               "rowNumber":"1",
               "seatNumber":"19"
            }
         ],
         "tickets":[  
            {  
               "vistaId":{  
                  "ticketTypeCode":"0083",
                  "barcode":null,
                  "loyaltyRecognitionId":null,
                  "loyaltyRecognitionSequence":0
               },
               "description":"Admission",
               "priceInCents":1028,
               "expectedSeatCount":1
            }
         ],
         "merchandise":[  
            {  
               "contentId":"25045",
               "headOfficeCode":"A000008796",
               "vistaId":{  
                  "id":"8796",
                  "recognitionId":0,
                  "recognitionSequenceNumber":0
               },
               "name":"SHAZAM Glass",
               "description":"<p>Art by C&eacute;sar Moreno</p>",
               "imageUrls":[  
                  "https://s3.drafthouse.com/images/made/Shazam_ShoppingCart_640x432_640_432_81_s_c1.jpg"
               ],
               "grossPriceInCents":1100,
               "taxInCents":91,
               "netPriceInCents":1191,
               "priceInCents":1100,
               "quantityAvailable":99,
               "quantityOnOrder":1,
               "modifiers":[  

               ],
               "useModifierDeltaPricing":true,
               "modifierButtonText":null,
               "minCountPerSeat":0,
               "maxCountPerSeat":0,
               "minCentsPerSeat":0,
               "maxCentsPerSeat":0
            }
         ],
         "receipt":{  
            "isTaxItemized":true,
            "lineItems":[  
               {  
                  "id":"T-0083",
                  "isRemoveable":false,
                  "lineItemType":"PURCHASE",
                  "lineItemSubtype":"TICKET",
                  "quantity":1,
                  "description":"Admission",
                  "unitPriceCents":950,
                  "totalPriceCents":950
               },
               {  
                  "id":"M-A000008796",
                  "isRemoveable":false,
                  "lineItemType":"PURCHASE",
                  "lineItemSubtype":"MERCHANDISE",
                  "quantity":1,
                  "description":"SHAZAM Glass",
                  "unitPriceCents":1100,
                  "totalPriceCents":1100
               },
               {  
                  "id":"SUBTOT-001",
                  "isRemoveable":false,
                  "lineItemType":"SUBTOTAL",
                  "lineItemSubtype":"NONE",
                  "quantity":1,
                  "description":"Subtotal",
                  "unitPriceCents":2050,
                  "totalPriceCents":2050
               },
               {  
                  "id":"BF-001",
                  "isRemoveable":false,
                  "lineItemType":"FEE",
                  "lineItemSubtype":"BOOKING_FEE",
                  "quantity":1,
                  "description":"Convenience Fee",
                  "unitPriceCents":149,
                  "totalPriceCents":149
               },
               {  
                  "id":"BF-001",
                  "isRemoveable":false,
                  "lineItemType":"TAX",
                  "lineItemSubtype":"NONE",
                  "quantity":1,
                  "description":"Tax",
                  "unitPriceCents":181,
                  "totalPriceCents":181
               },
               {  
                  "id":"TOT-001",
                  "isRemoveable":false,
                  "lineItemType":"TOTAL",
                  "lineItemSubtype":"NONE",
                  "quantity":1,
                  "description":"Total",
                  "unitPriceCents":2380,
                  "totalPriceCents":2380
               }
            ]
         },
         "payments":[  

         ],
         "bookingFeeGrossPriceInCents":149,
         "bookingFeeTaxInCents":12,
         "bookingFeeNetPriceInCents":161,
         "bookingFeeValueCents":149,
         "totalGrossPriceInCents":2199,
         "totalTaxInCents":181,
         "totalNetPriceInCents":2380,
         "totalValueCents":2380
      },
      "merchandise":[  
         {  
            "contentId":"25045",
            "headOfficeCode":"A000008796",
            "vistaId":{  
               "id":"8796",
               "recognitionId":0,
               "recognitionSequenceNumber":0
            },
            "name":"SHAZAM Glass",
            "description":"<p>Art by C&eacute;sar Moreno</p>",
            "imageUrls":[  
               "https://s3.drafthouse.com/images/made/Shazam_ShoppingCart_640x432_640_432_81_s_c1.jpg"
            ],
            "grossPriceInCents":1100,
            "taxInCents":91,
            "netPriceInCents":1191,
            "priceInCents":1100,
            "quantityAvailable":99,
            "quantityOnOrder":1,
            "modifiers":[  

            ],
            "useModifierDeltaPricing":true,
            "modifierButtonText":null,
            "minCountPerSeat":0,
            "maxCountPerSeat":0,
            "minCentsPerSeat":0,
            "maxCentsPerSeat":0
         }
      ],
      "merchandiseAd":null,
      "merchandiseGroups":[  
         {  
            "title":"Add Merchandise",
            "description":null,
            "imageUri":null,
            "merchandise":[  
               {  
                  "contentId":"25045",
                  "headOfficeCode":"A000008796",
                  "vistaId":{  
                     "id":"8796",
                     "recognitionId":0,
                     "recognitionSequenceNumber":0
                  },
                  "name":"SHAZAM Glass",
                  "description":"<p>Art by C&eacute;sar Moreno</p>",
                  "imageUrls":[  
                     "https://s3.drafthouse.com/images/made/Shazam_ShoppingCart_640x432_640_432_81_s_c1.jpg"
                  ],
                  "grossPriceInCents":1100,
                  "taxInCents":91,
                  "netPriceInCents":1191,
                  "priceInCents":1100,
                  "quantityAvailable":99,
                  "quantityOnOrder":1,
                  "modifiers":[  

                  ],
                  "useModifierDeltaPricing":true,
                  "modifierButtonText":null,
                  "minCountPerSeat":0,
                  "maxCountPerSeat":0,
                  "minCentsPerSeat":0,
                  "maxCentsPerSeat":0
               }
            ]
         }
      ]
   }
}

Windows installation
Install Docker Toolbox
Install IntelliJ
Install Python 
Install Node version 6
Install curl
Install pip
Install httpie
Add docker.localhost to etc/hosts
docker inspect -f "{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}"" postgres-alamobot
Get IP address from docker-machine ls
192.168.99.101
192.168.99.101			docker.localhost
C:\Windows\System32\drivers\etc\hosts

http POST localhost:8142/alert film_name=="shazam" override_seating_algorithm=="false" earliest_showtime=="15:00:00" preferred_cinemas=="0008" latest_showtime=="16:00:00" preferred_days_of_the_week=="SATURDAY" seat_count=="17" 
http POST localhost:8142/alert film_name=="endgame" override_seating_algorithm=="false" earliest_showtime=="19:30:00" preferred_cinemas=="0008,0003" latest_showtime=="21:00:00" preferred_days_of_the_week=="THURSDAY" seat_count=="50" 
http GET localhost:8142/alert