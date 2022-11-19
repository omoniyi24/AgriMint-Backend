package com.github.agrimint.extended.resources;

import com.github.agrimint.extended.util.ApplicationUrl;
import com.github.agrimint.service.dto.MemberDTO;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.web.util.ResponseUtil;

/**
 * @author OMONIYI ILESANMI
 */

@RestController
@RequestMapping(ApplicationUrl.BASE_CONTEXT_URL + "/wallet")
public class WalletResource {

    private final Logger log = LoggerFactory.getLogger(ExtendedMemberResource.class);

    @GetMapping("federation/{federationId}/balance")
    public ResponseEntity<Map<String, BigDecimal>> getUserFederationWalletBalance(@PathVariable Long federationId) {
        log.debug("REST getUserFederationWalletBalance : {}", federationId);
        Map<String, BigDecimal> walletBalance = new HashMap<>();
        walletBalance.put("walletBalance", new BigDecimal("50000"));
        return ResponseEntity.ok(walletBalance);
    }

    @GetMapping("federation/{federationId}/invoice")
    public ResponseEntity<Map<String, String>> generateFederationUserInvoice(@PathVariable Long federationId) {
        log.debug("REST generateFederationUserInvoice : {}", federationId);
        String invoice =
            "lntb1u1pwz5w78pp5e8w8cr5c30xzws92v36sk45znhjn098rtc4pea6ertnmvu25ng3sdpywd6hyetyvf5hgueqv3jk6meqd9h8vmmfvdjsxqrrssy29mzkzjfq27u67evzu893heqex737dhcapvcuantkztg6pnk77nrm72y7z0rs47wzc09vcnugk2ve6sr2ewvcrtqnh3yttv847qqvqpvv398";
        Map<String, String> lnInvoiceMap = new HashMap<>();
        lnInvoiceMap.put("lnInvoice", invoice);
        return ResponseEntity.ok(lnInvoiceMap);
    }
}
