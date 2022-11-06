package com.github.agrimint.service.impl;

import com.github.agrimint.domain.Federation;
import com.github.agrimint.repository.FederationRepository;
import com.github.agrimint.service.FederationService;
import com.github.agrimint.service.dto.FederationDTO;
import com.github.agrimint.service.mapper.FederationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Federation}.
 */
@Service
@Transactional
public class FederationServiceImpl implements FederationService {

    private final Logger log = LoggerFactory.getLogger(FederationServiceImpl.class);

    private final FederationRepository federationRepository;

    private final FederationMapper federationMapper;

    public FederationServiceImpl(FederationRepository federationRepository, FederationMapper federationMapper) {
        this.federationRepository = federationRepository;
        this.federationMapper = federationMapper;
    }

    @Override
    public FederationDTO save(FederationDTO federationDTO) {
        log.debug("Request to save Federation : {}", federationDTO);
        Federation federation = federationMapper.toEntity(federationDTO);
        federation = federationRepository.save(federation);
        return federationMapper.toDto(federation);
    }

    @Override
    public Optional<FederationDTO> partialUpdate(FederationDTO federationDTO) {
        log.debug("Request to partially update Federation : {}", federationDTO);

        return federationRepository
            .findById(federationDTO.getId())
            .map(
                existingFederation -> {
                    federationMapper.partialUpdate(existingFederation, federationDTO);
                    return existingFederation;
                }
            )
            .map(federationRepository::save)
            .map(federationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FederationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Federations");
        return federationRepository.findAll(pageable).map(federationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FederationDTO> findOne(Long id) {
        log.debug("Request to get Federation : {}", id);
        return federationRepository.findById(id).map(federationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Federation : {}", id);
        federationRepository.deleteById(id);
    }
}
