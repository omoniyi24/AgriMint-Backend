package com.github.agrimint.extended.service;

import com.github.agrimint.extended.dto.CreatMemberRequestDTO;
import com.github.agrimint.extended.exception.FederationExecption;
import com.github.agrimint.extended.exception.MemberAlreadyExistExecption;
import com.github.agrimint.extended.exception.UserException;
import com.github.agrimint.service.dto.MemberDTO;

/**
 * @author OMONIYI ILESANMI
 */
public interface ExtendedGuardianService {
    MemberDTO create(CreatMemberRequestDTO memberDTO) throws MemberAlreadyExistExecption, FederationExecption, UserException;
}
