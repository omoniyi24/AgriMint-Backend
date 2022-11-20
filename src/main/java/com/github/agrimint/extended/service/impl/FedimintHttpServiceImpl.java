package com.github.agrimint.extended.service.impl;

import com.github.agrimint.extended.dto.*;
import com.github.agrimint.extended.exception.FederationExecption;
import com.github.agrimint.extended.exception.MemberExecption;
import com.github.agrimint.extended.service.FedimintHttpService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
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

    @Value("${fedimint.guardianJoinFederationUrl}")
    private String guardianJoinFederationUrl;

    @Value("${fedimint.exchangeKeyUrl}")
    private String exchangeKeyUrl;

    @Value("${fedimint.createMemberUrl}")
    private String createMemberUrl;

    @Value("${fedimint.joinMemberUrl}")
    private String joinMemberUrl;

    @Value("${fedimint.memberHoldingInfoUrl}")
    private String memberHoldingInfoUrl;

    @Value("${fedimint.createInvoiceUrl}")
    private String createInvoiceUrl;

    @Value("${fedimint.payInvoiceUrl}")
    private String payInvoiceUrl;

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
                return gson.fromJson(responsePayload, GetConnectionFedimintHttpResponse.class);
            }
            throw new FederationExecption("Failed to get Federation Connection");
        } catch (Exception e) {
            e.printStackTrace();
            throw new FederationExecption("Error getting Federation Connection");
        }
    }

    @Override
    public CreateGuardianFedimintHttpResponse createGuardian(CreateGuardianFedimintHttpRequest createGuardianFedimintHttpRequest)
        throws FederationExecption {
        String payload = gson.toJson(createGuardianFedimintHttpRequest);
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

    @Override
    public boolean joinFederation(JoinFedimintHttpRequest joinFedimintHttpRequest, String guadianFedimintId) throws FederationExecption {
        String payload = gson.toJson(joinFedimintHttpRequest);
        try {
            String url = String.format(this.guardianJoinFederationUrl, guadianFedimintId);
            HttpEntity<String> entity = new HttpEntity<>(payload, getDefaultHeaders());
            log.info("joinFederation request payload {} on url: {} ", entity, url);
            ResponseEntity<String> postForEntity = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            log.info("joinFederation RAW response payload {} ", postForEntity);
            if (postForEntity.getStatusCode().equals(HttpStatus.OK)) {
                String responsePayload = postForEntity.getBody();
                log.info("joinFederation response body {} ", responsePayload);
                return true;
            }
            throw new FederationExecption("Failed to Join Federation");
        } catch (Exception e) {
            e.printStackTrace();
            throw new FederationExecption("Error Joining Federation");
        }
    }

    @Override
    public boolean exchangeKeys(JoinFedimintHttpRequest joinFedimintHttpRequest, String guadianFedimintId) throws FederationExecption {
        String payload = gson.toJson(joinFedimintHttpRequest);
        try {
            String url = String.format(this.exchangeKeyUrl, guadianFedimintId);
            HttpEntity<String> entity = new HttpEntity<>(payload, getDefaultHeaders());
            log.info("exchangeKeys request payload {} on url: {}", entity, url);
            ResponseEntity<String> postForEntity = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            log.info("exchangeKeys RAW response payload {} ", postForEntity);
            if (postForEntity.getStatusCode().equals(HttpStatus.OK)) {
                String responsePayload = postForEntity.getBody();
                log.info("exchangeKeys response body {} ", responsePayload);
                return true;
            }
            throw new FederationExecption("Failed to exchange Federation");
        } catch (Exception e) {
            e.printStackTrace();
            throw new FederationExecption("Error Exchanging Federation");
        }
    }

    @Override
    public boolean createMember(CreateFedimintMemberHttpRequest createFedimintMemberHttpRequest) {
        String payload = gson.toJson(createFedimintMemberHttpRequest);
        try {
            String url = this.createMemberUrl;
            HttpEntity<String> entity = new HttpEntity<>(payload, getDefaultHeaders());
            log.info("createMember request payload {} on url: {}", entity, url);
            ResponseEntity<String> postForEntity = restTemplate.postForEntity(url, entity, String.class);
            log.info("createMember RAW response payload {} ", postForEntity);
            if (postForEntity.getStatusCode().equals(HttpStatus.OK)) {
                return true;
            }
            throw new MemberExecption("Failed to create Member");
        } catch (Exception e) {
            e.printStackTrace();
            throw new MemberExecption("Error Creating Member");
        }
    }

    @Override
    public boolean joinMember(MemberJoinFedimintHttpRequest joinFedimintHttpRequest, String memberId) {
        String payload = gson.toJson(joinFedimintHttpRequest);
        try {
            String url = String.format(this.joinMemberUrl, memberId);
            HttpEntity<String> entity = new HttpEntity<>(payload, getDefaultHeaders());
            log.info("joinMember request payload {} on url: {}", entity, url);
            ResponseEntity<String> postForEntity = restTemplate.postForEntity(url, entity, String.class);
            log.info("joinMember RAW response payload {} ", postForEntity);
            if (postForEntity.getStatusCode().equals(HttpStatus.OK)) {
                return true;
            }
            throw new MemberExecption("Failed to Join Federation");
        } catch (Exception e) {
            e.printStackTrace();
            throw new MemberExecption("Error Joining Federation");
        }
    }

    @Override
    public MemberHoldingResponse getMemberHoldingInfo(String memberId, String federationId) {
        String url = String.format(memberHoldingInfoUrl, memberId, federationId);
        log.info("getMemberHoldingInfo request url {} ", url);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(getDefaultHeaders()),
                String.class
            );
            log.info("getMemberHoldingInfo RAW response payload {} ", responseEntity);
            if (responseEntity.getStatusCode().equals(HttpStatus.OK) && responseEntity.getBody() != null) {
                String responsePayload = responseEntity.getBody();
                log.info("getMemberHoldingInfo response body {} ", responsePayload);
                return gson.fromJson(responsePayload, MemberHoldingResponse.class);
            }
            throw new MemberExecption("Failed to get member info");
        } catch (Exception e) {
            e.printStackTrace();
            throw new MemberExecption("Error getting member info");
        }
    }

    @Override
    public CreateInvoiceResponse createInvoice(CreateInvoiceHttpRequest createInvoiceHttpRequest) {
        String payload = gson.toJson(createInvoiceHttpRequest);
        try {
            String url = this.createInvoiceUrl;
            HttpEntity<String> entity = new HttpEntity<>(payload, getDefaultHeaders());
            log.info("createInvoice request payload {} on url: {}", entity, url);
            ResponseEntity<String> postForEntity = restTemplate.postForEntity(url, entity, String.class);
            log.info("createInvoice RAW response payload {} ", postForEntity);
            if (postForEntity.getStatusCode().equals(HttpStatus.OK) && postForEntity.getBody() != null) {
                String responsePayload = postForEntity.getBody();
                log.info("createInvoice response body {} ", responsePayload);
                return gson.fromJson(responsePayload, CreateInvoiceResponse.class);
            }
            throw new MemberExecption("Failed to create invoice");
        } catch (Exception e) {
            e.printStackTrace();
            throw new MemberExecption("Error Creating Invoice");
        }
    }

    @Override
    public PayInvoiceResponse payInvoice(PayInvoiceHttpRequest payInvoiceHttpRequest) {
        String payload = gson.toJson(payInvoiceHttpRequest);
        try {
            String url = this.payInvoiceUrl;
            HttpEntity<String> entity = new HttpEntity<>(payload, getDefaultHeaders());
            log.info("payInvoice request payload {} on url: {}", entity, url);
            ResponseEntity<String> postForEntity = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            log.info("payInvoice RAW response payload {} ", postForEntity);
            if (postForEntity.getStatusCode().equals(HttpStatus.OK) && postForEntity.getBody() != null) {
                String responsePayload = postForEntity.getBody();
                log.info("payInvoice response body {} ", responsePayload);
                return gson.fromJson(responsePayload, PayInvoiceResponse.class);
            }
            throw new MemberExecption("Failed to pay invoice");
        } catch (Exception e) {
            e.printStackTrace();
            throw new MemberExecption("Error paying invoice");
        }
    }

    private HttpHeaders getDefaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
