package com.github.agrimint.extended.resources;

import com.github.agrimint.extended.dto.ExtendedInviteDTO;
import com.github.agrimint.extended.service.ExtendedInviteService;
import com.github.agrimint.extended.util.ApplicationUrl;
import java.util.Map;
import javax.validation.Valid;
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
    private ExtendedInviteService extendedInviteService;

    @PostMapping("/invite")
    public ResponseEntity<Map<String, String>> invite(@Valid @RequestBody ExtendedInviteDTO extendedInviteDTO) throws Exception {
        Map<String, String> inviteId = extendedInviteService.send(extendedInviteDTO);
        if (inviteId != null && inviteId.get("invitationCode") != null) {
            return ResponseEntity.ok().body(inviteId);
        }
        return ResponseEntity.badRequest().build();
    }
}
