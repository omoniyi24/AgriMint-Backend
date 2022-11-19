package com.github.agrimint.extended.resources;

import com.github.agrimint.extended.dto.InviteDTO;
import com.github.agrimint.extended.dto.SmsRequestDTO;
import com.github.agrimint.extended.exception.UserException;
import com.github.agrimint.extended.resources.vm.OtpRequestVM;
import com.github.agrimint.extended.resources.vm.OtpResponseVM;
import com.github.agrimint.extended.service.InviteService;
import com.github.agrimint.extended.util.ApplicationUrl;
import com.github.agrimint.security.SecurityUtils;
import com.github.agrimint.service.dto.AppUserDTO;
import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author OMONIYI ILESANMI
 */

@RestController
@RequestMapping(ApplicationUrl.BASE_CONTEXT_URL)
public class InviteResources {

    @Autowired
    private InviteService inviteService;

    @PostMapping("/invite")
    public ResponseEntity<Map<String, String>> invite(@Valid @RequestBody InviteDTO inviteDTO) throws Exception {
        Map<String, String> inviteId = inviteService.send(inviteDTO);
        if (inviteId != null && inviteId.get("id") != null) {
            return ResponseEntity.ok().body(inviteId);
        }
        return ResponseEntity.badRequest().build();
    }
}
