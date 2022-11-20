package com.github.agrimint.extended.service;

import com.github.agrimint.extended.dto.CreateFederationRequestDTO;
import com.github.agrimint.extended.dto.GetConnectionFedimintHttpResponse;
import com.github.agrimint.extended.exception.FederationExecption;
import com.github.agrimint.service.criteria.FederationCriteria;
import com.github.agrimint.service.dto.FederationDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author OMONIYI ILESANMI
 */
public interface ExtendedFederationService {
    FederationDTO create(CreateFederationRequestDTO createFederationRequestDTO) throws FederationExecption;

    List<FederationDTO> getAll(FederationCriteria criteria, Pageable pageable);

    GetConnectionFedimintHttpResponse getFederationConnection(Long id) throws FederationExecption;
}
