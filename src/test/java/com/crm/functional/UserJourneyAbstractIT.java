package com.crm.functional;

import com.crm.web.api.auth.LoginResponse;
import com.crm.web.api.lot.LotCreateRequest;
import com.crm.web.api.lot.LotResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.test.annotation.FlywayTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.Map;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@FlywayTest(locationsForMigrate = "filesystem:db/migration")
public abstract class UserJourneyAbstractIT {

    private static final String DEFAULT_USER_LOGIN = "admin";
    private static final String DEFAULT_USER_PASSWORD = "admin123";

    private static final String LOGIN_PATH = "/api/auth/login";
    private static final String LOTS_PATH = "/api/lots";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    String login() {

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(APPLICATION_JSON);
            String requestJson = objectMapper.writeValueAsString(Map.of("username", DEFAULT_USER_LOGIN, "password", DEFAULT_USER_PASSWORD));
            HttpEntity<String> httpEntity = new HttpEntity<>(requestJson, headers);

            LoginResponse loginResponse = this.restTemplate.postForObject(
                    "http://localhost:" + port + LOGIN_PATH,
                    httpEntity,
                    LoginResponse.class);

            return loginResponse.getToken();

        } catch(Exception e) {

            throw new RuntimeException(e);
        }
    }

    Integer createLot(String jwt, LotCreateRequest lotCreateRequest) {

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(APPLICATION_JSON);
            headers.setBearerAuth(jwt);
            String requestJson = objectMapper.writeValueAsString(lotCreateRequest);
            HttpEntity<String> httpEntity = new HttpEntity<>(requestJson, headers);

            LotResponse lotResponse = this.restTemplate.postForObject(
                    "http://localhost:" + port + LOTS_PATH,
                    httpEntity,
                    LotResponse.class);

            return lotResponse.getId();

        } catch(Exception e) {

            throw new RuntimeException(e);
        }
    }

    void deleteLot(String jwt, Integer lotId) {

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwt);
            HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

            restTemplate.exchange(
                    "http://localhost:" + port + LOTS_PATH + "/" + lotId,
                    HttpMethod.DELETE, httpEntity,
                    Void.class);

        } catch(Exception e) {

            throw new RuntimeException(e);
        }
    }
}
