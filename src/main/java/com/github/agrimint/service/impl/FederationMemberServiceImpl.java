package com.github.agrimint.service.impl;

import com.github.agrimint.domain.FederationMember;
import com.github.agrimint.repository.FederationMemberRepository;
import com.github.agrimint.service.FederationMemberService;
import com.github.agrimint.service.dto.FederationMemberDTO;
import com.github.agrimint.service.mapper.FederationMemberMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link FederationMember}.
 */
@Service
@Transactional
public class FederationMemberServiceImpl implements FederationMemberService {

    private final Logger log = LoggerFactory.getLogger(FederationMemberServiceImpl.class);

    private final FederationMemberRepository federationMemberRepository;

    private final FederationMemberMapper federationMemberMapper;

    public FederationMemberServiceImpl(
        FederationMemberRepository federationMemberRepository,
        FederationMemberMapper federationMemberMapper
    ) {
        this.federationMemberRepository = federationMemberRepository;
        this.federationMemberMapper = federationMemberMapper;
    }

    @Override
    public FederationMemberDTO save(FederationMemberDTO federationMemberDTO) {
        log.debug("Request to save FederationMember : {}", federationMemberDTO);
        FederationMember federationMember = federationMemberMapper.toEntity(federationMemberDTO);
        federationMember = federationMemberRepository.save(federationMember);
        return federationMemberMapper.toDto(federationMember);
    }

    @Override
    public Optional<FederationMemberDTO> partialUpdate(FederationMemberDTO federationMemberDTO) {
        log.debug("Request to partially update FederationMember : {}", federationMemberDTO);

        return federationMemberRepository
            .findById(federationMemberDTO.getId())
            .map(
                existingFederationMember -> {
                    federationMemberMapper.partialUpdate(existingFederationMember, federationMemberDTO);
                    return existingFederationMember;
                }
            )
            .map(federationMemberRepository::save)
            .map(federationMemberMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FederationMemberDTO> findAll(Pageable pageable) {
        log.debug("Request to get all FederationMembers");
        return federationMemberRepository.findAll(pageable).map(federationMemberMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FederationMemberDTO> findOne(Long id) {
        log.debug("Request to get FederationMember : {}", id);
        return federationMemberRepository.findById(id).map(federationMemberMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete FederationMember : {}", id);
        federationMemberRepository.deleteById(id);
    }
}
