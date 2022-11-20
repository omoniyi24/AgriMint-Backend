package com.github.agrimint.extended.service;

import com.github.agrimint.extended.dto.*;
import com.github.agrimint.extended.exception.FederationExecption;

/**
 * @author OMONIYI ILESANMI
 */
public interface SMSHttpService {
    boolean send(SmsRequestDTO smsRequestDTO);
}
