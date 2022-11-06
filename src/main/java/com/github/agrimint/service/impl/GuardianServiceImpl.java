package com.github.agrimint.service.impl;

import com.github.agrimint.domain.Guardian;
import com.github.agrimint.repository.GuardianRepository;
import com.github.agrimint.service.GuardianService;
import com.github.agrimint.service.dto.GuardianDTO;
import com.github.agrimint.service.mapper.GuardianMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Guardian}.
 */
@Service
@Transactional
public class GuardianServiceImpl implements GuardianService {

    private final Logger log = LoggerFactory.getLogger(GuardianServiceImpl.class);

    private final GuardianRepository guardianRepository;

    private final GuardianMapper guardianMapper;

    public GuardianServiceImpl(GuardianRepository guardianRepository, GuardianMapper guardianMapper) {
        this.guardianRepository = guardianRepository;
        this.guardianMapper = guardianMapper;
    }

    @Override
    public GuardianDTO save(GuardianDTO guardianDTO) {
        log.debug("Request to save Guardian : {}", guardianDTO);
        Guardian guardian = guardianMapper.toEntity(guardianDTO);
        guardian = guardianRepository.save(guardian);
        return guardianMapper.toDto(guardian);
    }

    @Override
    public Optional<GuardianDTO> partialUpdate(GuardianDTO guardianDTO) {
        log.debug("Request to partially update Guardian : {}", guardianDTO);

        return guardianRepository
            .findById(guardianDTO.getId())
            .map(
                existingGuardian -> {
                    guardianMapper.partialUpdate(existingGuardian, guardianDTO);
                    return existingGuardian;
                }
            )
            .map(guardianRepository::save)
            .map(guardianMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GuardianDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Guardians");
        return guardianRepository.findAll(pageable).map(guardianMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GuardianDTO> findOne(Long id) {
        log.debug("Request to get Guardian : {}", id);
        return guardianRepository.findById(id).map(guardianMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Guardian : {}", id);
        guardianRepository.deleteById(id);
    }
}
