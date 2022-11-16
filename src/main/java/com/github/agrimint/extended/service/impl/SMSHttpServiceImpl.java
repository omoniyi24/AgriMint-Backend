package com.github.agrimint.extended.service.impl;

import com.github.agrimint.extended.dto.*;
import com.github.agrimint.extended.exception.FederationExecption;
import com.github.agrimint.extended.exception.SmsException;
import com.github.agrimint.extended.service.FedimintHttpService;
import com.github.agrimint.extended.service.SMSHttpService;
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
public class SMSHttpServiceImpl implements SMSHttpService {

    private final RestTemplate restTemplate;
    private final Gson gson;

    @Value("${ussd.sendSmsUrl}")
    private String sendSmsUrl;

    public SMSHttpServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.gson = new Gson();
    }

    @Override
    public void send(SmsRequestDTO smsRequestDTO) throws FederationExecption {
        String payload = gson.toJson(smsRequestDTO);
        log.info("SMS request payload {} on url: {} ", payload, this.sendSmsUrl);
        try {
            HttpEntity<String> entity = new HttpEntity<>(payload, getDefaultHeaders());
            ResponseEntity<String> postForEntity = restTemplate.postForEntity(this.sendSmsUrl, entity, String.class);
            log.info("SMS service RAW response payload {} ", postForEntity);
            if (postForEntity.getStatusCode().equals(HttpStatus.OK) && postForEntity.getBody() != null) {
                String responsePayload = postForEntity.getBody();
                log.info("SMS response body {} ", responsePayload);
                //                SmsResponseDTO smsResponseDTO = gson.fromJson(responsePayload, SmsResponseDTO.class);
            }
            throw new SmsException("Failed to send sms");
        } catch (Exception e) {
            log.error("Error getting sending sms");
            e.printStackTrace();
        }
    }

    private HttpHeaders getDefaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
