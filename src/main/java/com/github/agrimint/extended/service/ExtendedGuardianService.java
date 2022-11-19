package com.github.agrimint.extended.service;

import com.github.agrimint.extended.dto.CreatMemberRequestDTO;
import com.github.agrimint.extended.exception.FederationExecption;
import com.github.agrimint.extended.exception.MemberExecption;
import com.github.agrimint.extended.exception.UserException;
import com.github.agrimint.service.dto.MemberDTO;

/**
 * @author OMONIYI ILESANMI
 */
public interface ExtendedGuardianService {
    MemberDTO create(CreatMemberRequestDTO memberDTO, boolean active, boolean guardian, String invitationCode)
        throws MemberExecption, FederationExecption, UserException;
}
