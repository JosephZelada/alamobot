package com.alamobot.services;

import com.alamobot.core.AlamoUrls;
import com.alamobot.core.api.Seat;
import com.alamobot.core.api.consume.payment.PaymentDataContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HttpHeaders httpHeaders;

    public boolean buySeats(int sessionId, ArrayList<Seat> seatsToBuy) {
        String userSessionId = logInWithWebSession();
        return !userSessionId.equals("");
    }



//    private boolean addTicketsToSession(int sessionId, ArrayList<Seat> seatsToBuy) {
//
//    }
//
//    private purchaseSeats() {
//
//    }

    private String logInWithWebSession() {
        String loginUrl = AlamoUrls.LOGIN_BASE_URL;
        String userSessionId = generateUserSessionId();

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "chrome");
        ArrayList<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(MediaType.ALL);
        headers.setAccept(mediaTypes);
        headers.setConnection("keep-alive");
        headers.setContentLength(110);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("email", "bibblebobbleboo2@yahoo.com");
        body.put("password", "bibblebobbleboo2");
        body.put("userSessionId", userSessionId);
        HttpEntity<?> httpEntity = new HttpEntity<>(body, headers);

        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new LoggingRequestInterceptor());
        restTemplate.setInterceptors(interceptors);

        ResponseEntity<PaymentDataContainer> loginResponse =
                restTemplate.exchange(
                        loginUrl,
                        HttpMethod.POST,
                        httpEntity,
                        PaymentDataContainer.class
                );
        PaymentDataContainer paymentDataContainer = loginResponse.getBody();
        if(paymentDataContainer != null && paymentDataContainer.getData().getLoginSuccess()) {
            return userSessionId;
        }
        return "";
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
}
