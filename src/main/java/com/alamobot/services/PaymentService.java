package com.alamobot.services;

import com.alamobot.core.AlamoUrls;
import com.alamobot.core.api.Seat;
import com.alamobot.core.api.SeatEssentials;
import com.alamobot.core.api.consume.payment.CardWalletDataContainer;
import com.alamobot.core.api.consume.payment.LoyaltyMember;
import com.alamobot.core.api.consume.payment.PaymentDataRequest;
import com.alamobot.core.api.consume.payment.PaymentDataResponseContainer;
import com.alamobot.core.api.consume.payment.SeatClaimDataContainer;
import com.alamobot.core.api.consume.payment.UserSessionDataContainer;
import com.alamobot.core.api.consume.payment.scheduled.BoughtSeat;
import com.alamobot.core.api.consume.payment.scheduled.BoughtSeatDataResponseContainer;
import com.alamobot.core.api.consume.payment.scheduled.PaymentHistoryDataResponseContainer;
import com.alamobot.core.api.consume.payment.scheduled.Purchase;
import com.alamobot.core.domain.FilmEntity;
import com.alamobot.core.domain.MarketEntity;
import com.alamobot.core.domain.MovieEntity;
import com.alamobot.core.domain.SeatEntity;
import com.alamobot.core.persistence.FilmRepository;
import com.alamobot.core.persistence.MarketRepository;
import com.alamobot.core.persistence.MovieRepository;
import com.alamobot.core.persistence.SeatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class PaymentService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HttpHeaders httpHeaders;
    @Autowired
    private HttpEntity<String> noBodyHttpEntity;
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    MarketRepository marketRepository;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    SeatService seatService;
    @Autowired
    FilmRepository filmRepository;
    @Autowired
    AlamoScheduler alamoScheduler;
    @Value("${alamo.user-name}")
    private String userName;
    @Value("${alamo.password}")
    private String password;

    //TODO: Add tests around this
    //TODO: Do a confirmation through checking accounts
    public boolean buySeats(int sessionId, List<Seat> seatsToBuy) {
        String cinemaId = movieRepository.findBySessionId(sessionId).getCinemaId();
        LoyaltyMember loyaltyMember = logInWithWebSession();
        if(loyaltyMember == null) {
            return false;
        }
        String userSessionId = loyaltyMember.getUserSessionId();
        String cardWalletToken = getCardWalletToken(userSessionId);
        if(!assignSeatsToAccount(sessionId, seatsToBuy, userSessionId, cinemaId)) {
            return false;
        }
        if(!primeSeatsForSale(sessionId, userSessionId, cinemaId)) {
            return false;
        }
        boolean seatsBought = buyTicketsForClaimedSeats(cardWalletToken, loyaltyMember, cinemaId, sessionId);
        if(seatsBought) {
            seatService.markSeatsAsBought(seatsToBuy);
        }
        return  seatsBought;
    }

    private boolean buyTicketsForClaimedSeats(String walletAccessToken, LoyaltyMember loyaltyMember, String cinemaId, int sessionId) {
        String buyTicketUrl = AlamoUrls.CHECKOUT_BASE_URL + cinemaId + "/" + sessionId;

        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new LoggingRequestInterceptor());
        restTemplate.setInterceptors(interceptors);

        PaymentDataRequest paymentDataRequest = PaymentDataRequest.builder()
                .billFullOutstandingAmount(true)
                .saveCardToCardWallet(false)
                .useAsBookingRef(true)
                .paymentValueCents(0)
                .walletAccessToken(walletAccessToken)
                .build();
        List<PaymentDataRequest> paymentDataRequestList = new ArrayList<>();
        paymentDataRequestList.add(paymentDataRequest);
        Map<String, Object> body = new HashMap<>();
        body.put("customerEmail", loyaltyMember.getEmail());
        body.put("customerName", loyaltyMember.getFullName());
        body.put("joinVictory", false);
        body.put("userSessionId", loyaltyMember.getUserSessionId());
        body.put("payments", paymentDataRequestList);
        HttpEntity<?> httpEntity = new HttpEntity<>(body, httpHeaders);

        ResponseEntity<PaymentDataResponseContainer> paymentDataResponse =
                restTemplate.exchange(
                        buyTicketUrl,
                        HttpMethod.POST,
                        httpEntity,
                        PaymentDataResponseContainer.class
                );
        PaymentDataResponseContainer paymentDataResponseContainer = paymentDataResponse.getBody();
        //TODO: Extract to method called validateSeatsBought
        return paymentDataResponseContainer != null && paymentDataResponseContainer.getData() != null && paymentDataResponseContainer.getError() == null && !paymentDataResponseContainer.getData().getPaymentResult().getVistaBookingId() .equals("");
    }

    private boolean primeSeatsForSale(int sessionId, String userSessionId, String cinemaId) {
        String checkoutUrl = AlamoUrls.CHECKOUT_BASE_URL + cinemaId + "/" + sessionId + "?userSessionId=" +userSessionId;

        ResponseEntity<SeatClaimDataContainer> seatClaimResponse =
                restTemplate.exchange(
                        checkoutUrl,
                        HttpMethod.GET,
                        noBodyHttpEntity,
                        SeatClaimDataContainer.class
                );
        SeatClaimDataContainer seatClaimDataContainer = seatClaimResponse.getBody();
        //TODO: Extract to method called validateSeatsPrimedForPurchase
        return seatClaimDataContainer != null && seatClaimDataContainer.getError() == null && seatClaimDataContainer.getData().getOrder() != null;
    }

    private boolean assignSeatsToAccount(int sessionId, List<Seat> seatsToBuy, String userSessionId, String cinemaId) {
        String seatChartBaseUrl = AlamoUrls.SEAT_CHART_BASE_URL + cinemaId + "/" + sessionId + "/select?userSessionId=" +userSessionId;
        ArrayList<SeatEssentials> seatEssentialsList = new ArrayList<>();
        seatsToBuy.forEach(seat -> seatEssentialsList.add(SeatEssentials.builder().areaIndex(seat.getAreaIndex()).rowIndex(seat.getRowIndex()).columnIndex(seat.getColumnIndex()).build()));

        Map<String, Object> body = new HashMap<>();
        body.put("seatSelections", seatEssentialsList);
        body.put("userSessionId", userSessionId);
        HttpEntity<?> httpEntity = new HttpEntity<>(body, httpHeaders);

        ResponseEntity<SeatClaimDataContainer> seatClaimResponse =
                restTemplate.exchange(
                        seatChartBaseUrl,
                        HttpMethod.POST,
                        httpEntity,
                        SeatClaimDataContainer.class
                );
        SeatClaimDataContainer seatClaimDataContainer = seatClaimResponse.getBody();
        //TODO: Extract to method called validateSeatsAssocicatedToAccount
        return seatClaimDataContainer != null && seatClaimDataContainer.getError() == null && seatClaimDataContainer.getData().getOrder() != null;
    }

    private String getCardWalletToken(String userSessionId) {
        String loginUrl = AlamoUrls.WALLET_BASE_URL + "?userSessionId=" +userSessionId;

        ResponseEntity<CardWalletDataContainer> cardWalletResponse =
                restTemplate.exchange(
                        loginUrl,
                        HttpMethod.GET,
                        noBodyHttpEntity,
                        CardWalletDataContainer.class
                );
        CardWalletDataContainer cardWalletDataContainer = cardWalletResponse.getBody();
        if(cardWalletDataContainer == null || cardWalletDataContainer.getData() == null) {
            return "";
        }
        return cardWalletDataContainer.getData().getCardWallet().getCards().get(0).getAccessToken();
    }

    private LoyaltyMember logInWithWebSession() {
        String loginUrl = AlamoUrls.LOGIN_BASE_URL;
        String userSessionId = generateUserSessionId();

        Map<String, String> body = new HashMap<>();
        body.put("email", userName);
        body.put("password", password);
        body.put("userSessionId", userSessionId);
        HttpEntity<?> httpEntity = new HttpEntity<>(body, httpHeaders);

        ResponseEntity<UserSessionDataContainer> loginResponse =
                restTemplate.exchange(
                        loginUrl,
                        HttpMethod.POST,
                        httpEntity,
                        UserSessionDataContainer.class
                );
        UserSessionDataContainer userSessionDataContainer = loginResponse.getBody();
        if(userSessionDataContainer != null && userSessionDataContainer.getData().getLoginSuccess()) {
            userSessionDataContainer.getData().getLoyaltyMember().setUserSessionId(userSessionId);
            return userSessionDataContainer.getData().getLoyaltyMember();
        }
        return null;
    }

    private String generateUserSessionId() {
        String userSessionIdFormat = "xxxxxxxxxxxx4xxxyxxxxxxxxxxxxxxx";
        StringBuilder stringBuilder = new StringBuilder();
        for(char character: userSessionIdFormat.toCharArray()) {
            stringBuilder.append(convertUserSessionIdChar(character));
        }
        return stringBuilder.toString();
    }

    private char convertUserSessionIdChar(char characterToConvert) {
        if(characterToConvert == '4') {
            return characterToConvert;
        }
        Integer random16NoDecimal = (int)(Math.random() * 16);
        int newCharacter = 'x' == characterToConvert ? random16NoDecimal : 3 & random16NoDecimal | 8;
        return Integer.toHexString(newCharacter).charAt(0);
    }

    public void getBoughtMovieSeatsFromServerAndPersist() {
        //Log in to Alamo to get userSessionId
        LoyaltyMember loyaltyMember = logInWithWebSession();
        if(loyaltyMember == null) {
            return;
        }

        String userSessionId = loyaltyMember.getUserSessionId();
        //Get list of bought showtimes
        String paymentHistoryUrl = AlamoUrls.PAYMENT_HISTORY_BASE_URL + "?userSessionId=" +userSessionId;

        Map<String, String> body = new HashMap<>();
        HttpEntity<?> httpEntity = new HttpEntity<>(body, httpHeaders);

        ResponseEntity<PaymentHistoryDataResponseContainer> purchaseHistoryResponse =
                restTemplate.exchange(
                        paymentHistoryUrl,
                        HttpMethod.GET,
                        httpEntity,
                        PaymentHistoryDataResponseContainer.class
                );
        PaymentHistoryDataResponseContainer paymentHistoryDataResponseContainer = purchaseHistoryResponse.getBody();

        //Convert to usable list
        List<Purchase> futureShowtimePurchases = new ArrayList<>();
        for(Purchase purchase: paymentHistoryDataResponseContainer.getData().getPurchaseHistory().getPurchases()) {
            //In conversion, shave off any showtimes that have already passed
            if(showtimeHasNotPassedOrBeenRefunded(purchase)) {
                futureShowtimePurchases.add(purchase);
            }
        }

        //Iterate over all remaining showtimes
        for(Purchase purchase: futureShowtimePurchases) {
            //Get seats for showtime (different endpoint)
            String seatPurchaseConfirmationUrl = AlamoUrls.TICKET_SEATING_CONFIRMATION_BASE_URL + "/" + purchase.getCinemaId() + "/" + purchase.getBookingId() + "/" + purchase.getFilmSlug() + "?userSessionId=" +userSessionId;
            ResponseEntity<BoughtSeatDataResponseContainer> seatPurchaseConfirmationResponseContainer =
                    restTemplate.exchange(
                            seatPurchaseConfirmationUrl,
                            HttpMethod.GET,
                            httpEntity,
                            BoughtSeatDataResponseContainer.class
                    );
            BoughtSeatDataResponseContainer seatPurchaseConfirmationResponse = seatPurchaseConfirmationResponseContainer.getBody();
            Integer sessionId = seatPurchaseConfirmationResponse.getData().getBooking().getSessionId();

            try {
                populateAndMarkMarketAsWatched(purchase.getMarketId());
                populateAndMarkFilmAsWatched(purchase.getFilmHoCode());
                populateAndMarkShowtimeAsWatched(sessionId);
            } catch(Exception e) {
                continue;
            }


            //Mark seats as bought, maintaining seatStatus if TAKEN and personInSeat if set
            //TODO: Do a null check on seatPurchaseConfirmationResponse all the way down before doing getData
            List<BoughtSeat> boughtSeats = seatPurchaseConfirmationResponse.getData().getBooking().getSeats();
            for(BoughtSeat boughtSeat: boughtSeats) {
                SeatEntity seatEntity = seatRepository.findBySessionIdAndRowNumberAndSeatNumber(sessionId, boughtSeat.getRowNumber(), boughtSeat.getSeatNumber());
                if(seatEntity == null) {
                    alamoScheduler.getSeatsFromAlamoAndPersist();
                    seatEntity = seatRepository.findBySessionIdAndRowNumberAndSeatNumber(sessionId, boughtSeat.getRowNumber(), boughtSeat.getSeatNumber());
                    if(seatEntity == null) {
                        log.error("Unable to find session " + sessionId +  ", row " + boughtSeat.getRowNumber() + ", seat " + boughtSeat.getSeatNumber() + " for film " + purchase.getFilmSlug());
                        continue;
                    }
                }
                seatEntity.setSeatBought(true);
                seatRepository.save(seatEntity);
            }
        }
    }

    private void populateAndMarkShowtimeAsWatched(Integer sessionId) throws Exception {
        MovieEntity movieEntity = movieRepository.findBySessionId(sessionId);
        if(movieEntity == null) {
            alamoScheduler.getSeatsFromAlamoAndPersist();
            movieEntity = movieRepository.findBySessionId(sessionId);
            if(movieEntity == null) {
                log.error("Unable to find showtime by sessionId " + sessionId + ", skipping purchase");
                throw new Exception();
            }
            //Get movie from server before continuing. Do it like AlamoScheduler does, get and persist
            //Grab the movie again from the database when you're done, put it in the entity
        }
        movieEntity.setWatched(true);
        movieRepository.save(movieEntity);
    }

    private void populateAndMarkFilmAsWatched(String filmId) throws Exception {
        Optional<FilmEntity> filmEntityOptional = filmRepository.findById(filmId);
        if(!filmEntityOptional.isPresent()) {
            alamoScheduler.getMoviesFromAlamoAndPersist();
            filmEntityOptional = filmRepository.findById(filmId);
            if(!filmEntityOptional.isPresent()) {
                log.error("Unable to find movie by filmId " + filmId + ", skipping purchase");
                throw new Exception();
            }
            //Get film from server before continuing. Do it like AlamoScheduler does, get and persist
            //Grab the film again from the database when you're done, put it in the optional
        }
        FilmEntity filmEntity = filmEntityOptional.get();
        filmEntity.setWatched(true);
        filmRepository.save(filmEntity);
    }

    private void populateAndMarkMarketAsWatched(String marketId) throws Exception {
        Optional<MarketEntity> marketEntityOptional = marketRepository.findById(marketId);
        if(!marketEntityOptional.isPresent()) {
            alamoScheduler.getMarketsFromAlamoAndPersist();
            marketEntityOptional = marketRepository.findById(marketId);
            if(!marketEntityOptional.isPresent()) {
                log.error("Unable to find market by marketId " + marketId + ", skipping purchase");
                throw new Exception();
            }
            //Get market from server before continuing. Do it like AlamoScheduler does, get and persist
            //Grab the market again from the database when you're done, put it in the optional
        }
        MarketEntity marketEntity = marketEntityOptional.get();
        marketEntity.setWatched(true);
        marketRepository.save(marketEntity);
    }

    private boolean showtimeHasNotPassedOrBeenRefunded(Purchase purchase) {
        return !purchase.isRefunded() && (purchase.getSessionDateTimeClt().isAfter(LocalDateTime.now()));
    }
}
