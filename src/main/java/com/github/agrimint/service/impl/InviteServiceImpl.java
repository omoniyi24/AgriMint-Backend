package com.github.agrimint.service.impl;

import com.github.agrimint.domain.Invite;
import com.github.agrimint.repository.InviteRepository;
import com.github.agrimint.service.InviteService;
import com.github.agrimint.service.dto.InviteDTO;
import com.github.agrimint.service.mapper.InviteMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Invite}.
 */
@Service
@Transactional
public class InviteServiceImpl implements InviteService {

    private final Logger log = LoggerFactory.getLogger(InviteServiceImpl.class);

    private final InviteRepository inviteRepository;

    private final InviteMapper inviteMapper;

    public InviteServiceImpl(InviteRepository inviteRepository, InviteMapper inviteMapper) {
        this.inviteRepository = inviteRepository;
        this.inviteMapper = inviteMapper;
    }

    @Override
    public InviteDTO save(InviteDTO inviteDTO) {
        log.debug("Request to save Invite : {}", inviteDTO);
        Invite invite = inviteMapper.toEntity(inviteDTO);
        invite = inviteRepository.save(invite);
        return inviteMapper.toDto(invite);
    }

    @Override
    public Optional<InviteDTO> partialUpdate(InviteDTO inviteDTO) {
        log.debug("Request to partially update Invite : {}", inviteDTO);

        return inviteRepository
            .findById(inviteDTO.getId())
            .map(
                existingInvite -> {
                    inviteMapper.partialUpdate(existingInvite, inviteDTO);
                    return existingInvite;
                }
            )
            .map(inviteRepository::save)
            .map(inviteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InviteDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Invites");
        return inviteRepository.findAll(pageable).map(inviteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InviteDTO> findOne(Long id) {
        log.debug("Request to get Invite : {}", id);
        return inviteRepository.findById(id).map(inviteMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Invite : {}", id);
        inviteRepository.deleteById(id);
    }
}
