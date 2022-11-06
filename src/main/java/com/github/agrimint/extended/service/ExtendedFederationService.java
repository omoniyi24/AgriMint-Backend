package com.github.agrimint.extended.service;

import com.github.agrimint.extended.dto.CreatMemberRequestDTO;
import com.github.agrimint.extended.dto.CreateFederationRequestDTO;
import com.github.agrimint.service.criteria.FederationCriteria;
import com.github.agrimint.service.dto.FederationDTO;
import com.github.agrimint.service.dto.MemberDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author OMONIYI ILESANMI
 */
public interface ExtendedFederationService {
    FederationDTO create(CreateFederationRequestDTO createFederationRequestDTO);

    Page<FederationDTO> getAll(FederationCriteria criteria, Pageable pageable);
}
