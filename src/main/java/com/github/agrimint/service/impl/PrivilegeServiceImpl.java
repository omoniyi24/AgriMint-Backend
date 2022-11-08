package com.github.agrimint.service.impl;

import com.github.agrimint.domain.Privilege;
import com.github.agrimint.repository.PrivilegeRepository;
import com.github.agrimint.service.PrivilegeService;
import com.github.agrimint.service.dto.PrivilegeDTO;
import com.github.agrimint.service.mapper.PrivilegeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Privilege}.
 */
@Service
@Transactional
public class PrivilegeServiceImpl implements PrivilegeService {

    private final Logger log = LoggerFactory.getLogger(PrivilegeServiceImpl.class);

    private final PrivilegeRepository privilegeRepository;

    private final PrivilegeMapper privilegeMapper;

    public PrivilegeServiceImpl(PrivilegeRepository privilegeRepository, PrivilegeMapper privilegeMapper) {
        this.privilegeRepository = privilegeRepository;
        this.privilegeMapper = privilegeMapper;
    }

    @Override
    public PrivilegeDTO save(PrivilegeDTO privilegeDTO) {
        log.debug("Request to save Privilege : {}", privilegeDTO);
        Privilege privilege = privilegeMapper.toEntity(privilegeDTO);
        privilege = privilegeRepository.save(privilege);
        return privilegeMapper.toDto(privilege);
    }

    @Override
    public Optional<PrivilegeDTO> partialUpdate(PrivilegeDTO privilegeDTO) {
        log.debug("Request to partially update Privilege : {}", privilegeDTO);

        return privilegeRepository
            .findById(privilegeDTO.getId())
            .map(
                existingPrivilege -> {
                    privilegeMapper.partialUpdate(existingPrivilege, privilegeDTO);
                    return existingPrivilege;
                }
            )
            .map(privilegeRepository::save)
            .map(privilegeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PrivilegeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Privileges");
        return privilegeRepository.findAll(pageable).map(privilegeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PrivilegeDTO> findOne(Long id) {
        log.debug("Request to get Privilege : {}", id);
        return privilegeRepository.findById(id).map(privilegeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Privilege : {}", id);
        privilegeRepository.deleteById(id);
    }
}
