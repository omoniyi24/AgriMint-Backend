package com.github.agrimint.extended.service.impl;

import com.github.agrimint.extended.dto.*;
import com.github.agrimint.extended.exeception.FederationExecption;
import com.github.agrimint.extended.service.FedimintHttpService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author OMONIYI ILESANMI
 */
@Service
@Slf4j
public class FedimintHttpServiceImpl implements FedimintHttpService {

    private final RestTemplate restTemplate;
    private final Gson gson;

    @Value("${fedimint.createFederationUrl}")
    private String createFedimintUrl;

    @Value("${fedimint.connectionUrl}")
    private String fedimintConnectionUrl;

    @Value("${fedimint.createGuardianUrl}")
    private String createGuardianUrl;

    public FedimintHttpServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.gson = new Gson();
    }

    @Override
    public CreateFedimintHttpResponse createFedimint(CreateFedimintHttpRequest createFedimintHttpRequest) throws FederationExecption {
        String payload = gson.toJson(createFedimintHttpRequest);
        log.info("createFederation request payload {} on url: {} ", payload, this.createFedimintUrl);
        try {
            HttpEntity<String> entity = new HttpEntity<>(payload, getDefaultHeaders());
            ResponseEntity<String> postForEntity = restTemplate.postForEntity(this.createFedimintUrl, entity, String.class);
            log.info("createFederation RAW response payload {} ", postForEntity);
            if (postForEntity.getStatusCode().equals(HttpStatus.OK) && postForEntity.getBody() != null) {
                String responsePayload = postForEntity.getBody();
                log.info("createFederation response body {} ", responsePayload);
                return gson.fromJson(responsePayload, CreateFedimintHttpResponse.class);
            }
            throw new FederationExecption("Failed to get Federation Connection");
        } catch (Exception e) {
            e.printStackTrace();
            throw new FederationExecption("Error getting Federation Connection");
        }
    }

    @Override
    public GetConnectionFedimintHttpResponse getFederationConnectionString(String federationId) throws FederationExecption {
        String url = String.format(fedimintConnectionUrl, federationId);
        log.info("getFederationConnection request url {} ", url);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(getDefaultHeaders()),
                String.class
            );
            log.info("getFederationConnection RAW response payload {} ", responseEntity);
            if (responseEntity.getStatusCode().equals(HttpStatus.OK) && responseEntity.getBody() != null) {
                String responsePayload = responseEntity.getBody();
                log.info("getFederationConnection response body {} ", responsePayload);
                Type targetClassType = new TypeToken<ArrayList<ArrayList<String>>>() {}.getType();
                return gson.fromJson(responsePayload, targetClassType);
            }
            throw new FederationExecption("Failed to get Federation Connection");
        } catch (Exception e) {
            e.printStackTrace();
            throw new FederationExecption("Error getting Federation Connection");
        }
    }

    @Override
    public CreateGuardianFedimintHttpResponse createGuardian(CreateGuardianFedimintHttpRequest createFedimintHttpRequest)
        throws FederationExecption {
        String payload = gson.toJson(createFedimintHttpRequest);
        log.info("createGuardian request payload {} on url: {} ", payload, this.createGuardianUrl);
        try {
            HttpEntity<String> entity = new HttpEntity<>(payload, getDefaultHeaders());
            ResponseEntity<String> postForEntity = restTemplate.postForEntity(this.createGuardianUrl, entity, String.class);
            log.info("createGuardian RAW response payload {} ", postForEntity);
            if (postForEntity.getStatusCode().equals(HttpStatus.OK) && postForEntity.getBody() != null) {
                String responsePayload = postForEntity.getBody();
                log.info("createGuardian response body {} ", responsePayload);
                return gson.fromJson(responsePayload, CreateGuardianFedimintHttpResponse.class);
            }
            throw new FederationExecption("Failed to get Federation Connection");
        } catch (Exception e) {
            e.printStackTrace();
            throw new FederationExecption("Error getting Federation Connection");
        }
    }

    private HttpHeaders getDefaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
