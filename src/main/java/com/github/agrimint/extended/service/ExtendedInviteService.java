package com.github.agrimint.extended.service;

import com.github.agrimint.extended.dto.ExtendedInviteDTO;
import java.util.Map;

/**
 * @author OMONIYI ILESANMI
 */
public interface ExtendedInviteService {
    Map<String, String> send(ExtendedInviteDTO extendedInviteDTO);
}
