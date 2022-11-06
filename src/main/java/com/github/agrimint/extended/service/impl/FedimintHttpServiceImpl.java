package com.github.agrimint.extended.service.impl;

import com.github.agrimint.extended.dto.CreateFedimintHttpRequest;
import com.github.agrimint.extended.dto.CreateFedimintHttpResponse;
import com.github.agrimint.extended.exeception.FederationExecption;
import com.github.agrimint.extended.service.FedimintHttpService;
import com.google.gson.Gson;
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

    @Value("${fedimint.createFedimintUrl}")
    String createFedimintUrl;

    private final RestTemplate restTemplate;
    private final Gson gson;

    public FedimintHttpServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.gson = new Gson();
    }

    @Override
    public CreateFedimintHttpResponse createFedimint(CreateFedimintHttpRequest createFedimintHttpRequest) throws FederationExecption {
        String payload = gson.toJson(createFedimintHttpRequest);
        log.info("createFederation request payload {} on url: {} ", payload, this.createFedimintUrl);
        HttpEntity<String> entity = new HttpEntity<>(payload, getDefaultHeaders());
        ResponseEntity<String> postForEntity = restTemplate.postForEntity(this.createFedimintUrl, entity, String.class);
        log.info("createFederation RAW response payload {} ", postForEntity);
        if (postForEntity.getStatusCode().equals(HttpStatus.OK) && postForEntity.getBody() != null) {
            String responsePayload = postForEntity.getBody();
            log.info("createFederation response body {} ", responsePayload);
            return gson.fromJson(responsePayload, CreateFedimintHttpResponse.class);
        }
        throw new FederationExecption("Federation Already Exist");
    }

    private HttpHeaders getDefaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
