package com.github.agrimint.service.impl;

import com.github.agrimint.domain.OtpRequest;
import com.github.agrimint.repository.OtpRequestRepository;
import com.github.agrimint.service.OtpRequestService;
import com.github.agrimint.service.dto.OtpRequestDTO;
import com.github.agrimint.service.mapper.OtpRequestMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link OtpRequest}.
 */
@Service
@Transactional
public class OtpRequestServiceImpl implements OtpRequestService {

    private final Logger log = LoggerFactory.getLogger(OtpRequestServiceImpl.class);

    private final OtpRequestRepository otpRequestRepository;

    private final OtpRequestMapper otpRequestMapper;

    public OtpRequestServiceImpl(OtpRequestRepository otpRequestRepository, OtpRequestMapper otpRequestMapper) {
        this.otpRequestRepository = otpRequestRepository;
        this.otpRequestMapper = otpRequestMapper;
    }

    @Override
    public OtpRequestDTO save(OtpRequestDTO otpRequestDTO) {
        log.debug("Request to save OtpRequest : {}", otpRequestDTO);
        OtpRequest otpRequest = otpRequestMapper.toEntity(otpRequestDTO);
        otpRequest = otpRequestRepository.save(otpRequest);
        return otpRequestMapper.toDto(otpRequest);
    }

    @Override
    public Optional<OtpRequestDTO> partialUpdate(OtpRequestDTO otpRequestDTO) {
        log.debug("Request to partially update OtpRequest : {}", otpRequestDTO);

        return otpRequestRepository
            .findById(otpRequestDTO.getId())
            .map(
                existingOtpRequest -> {
                    otpRequestMapper.partialUpdate(existingOtpRequest, otpRequestDTO);
                    return existingOtpRequest;
                }
            )
            .map(otpRequestRepository::save)
            .map(otpRequestMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OtpRequestDTO> findAll(Pageable pageable) {
        log.debug("Request to get all OtpRequests");
        return otpRequestRepository.findAll(pageable).map(otpRequestMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OtpRequestDTO> findOne(Long id) {
        log.debug("Request to get OtpRequest : {}", id);
        return otpRequestRepository.findById(id).map(otpRequestMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete OtpRequest : {}", id);
        otpRequestRepository.deleteById(id);
    }
}
